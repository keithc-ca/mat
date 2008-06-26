/*******************************************************************************
 * Copyright (c) 2008 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.mat.hprof;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.collect.HashMapLongObject;
import org.eclipse.mat.parser.io.PositionInputStream;
import org.eclipse.mat.parser.model.ClassImpl;
import org.eclipse.mat.snapshot.model.Field;
import org.eclipse.mat.snapshot.model.FieldDescriptor;
import org.eclipse.mat.snapshot.model.GCRootInfo;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IPrimitiveArray;
import org.eclipse.mat.util.IProgressListener;
import org.eclipse.mat.util.SimpleMonitor;

public class Pass1Parser extends AbstractParser
{
    private static final Pattern PATTERN_OBJ_ARRAY = Pattern.compile("^(\\[+)L(.*);$");
    private static final Pattern PATTERN_PRIMITIVE_ARRAY = Pattern.compile("^(\\[+)(.)$");

    private HashMapLongObject<String> class2name = new HashMapLongObject<String>();
    private HashMapLongObject<Long> thread2id = new HashMapLongObject<Long>();
    private IHprofParserHandler handler;
    private SimpleMonitor.Listener monitor;

    public Pass1Parser(IHprofParserHandler handler, SimpleMonitor.Listener monitor) throws IOException
    {
        this.handler = handler;
        this.monitor = monitor;
    }

    public void read(File file) throws SnapshotException, IOException
    {
        in = new PositionInputStream(new BufferedInputStream(new FileInputStream(file)));

        try
        {
            // header & version
            version = readVersion(in);
            handler.addProperty(IHprofParserHandler.VERSION, version.toString());

            // identifierSize (32 or 64 bit)
            idSize = in.readInt();
            if (idSize != 4 && idSize != 8)
                throw new SnapshotException("Only 32bit and 64bit dumps are supported.");
            handler.addProperty(IHprofParserHandler.IDENTIFIER_SIZE, String.valueOf(idSize));

            // creation date
            long date = in.readLong();
            handler.addProperty(IHprofParserHandler.CREATION_DATE, String.valueOf(date));

            long fileSize = file.length();
            long curPos = in.position();

            while (curPos < fileSize)
            {
                if (monitor.isProbablyCanceled())
                    throw new IProgressListener.OperationCanceledException();
                monitor.totalWorkDone(curPos / 1000);

                int record = in.readUnsignedByte();

                in.skipBytes(4); // time stamp

                long length = readUnsignedInt();
                if (length < 0)
                    throw new SnapshotException(MessageFormat
                                    .format("Illegal record length at byte {0}", in.position()));

                switch (record)
                {
                    case Constants.Record.STRING_IN_UTF8:
                        readString(length);
                        break;
                    case Constants.Record.LOAD_CLASS:
                        readLoadClass();
                        break;
                    case Constants.Record.HEAP_DUMP:
                    case Constants.Record.HEAP_DUMP_SEGMENT:
                        readDumpSegments(length);
                        break;
                    default:
                        in.skipBytes(length);
                        break;
                }

                curPos = in.position();
            }
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException ignore)
            {}
        }
    }

    private void readString(long length) throws IOException
    {
        long id = readID();
        byte[] chars = new byte[(int) (length - idSize)];
        in.readFully(chars);
        handler.getConstantPool().put(id, new String(chars));
    }

    private void readLoadClass() throws IOException
    {
        in.skipBytes(4);
        long classID = readID();
        in.skipBytes(4);
        long nameID = readID();

        String className = getStringConstant(nameID).replace('/', '.');
        class2name.put(classID, className);
    }

    private void readDumpSegments(long length) throws IOException, SnapshotException
    {
        long segmentStartPos = in.position();
        long segmentsEndPos = segmentStartPos + length;

        while (segmentStartPos < segmentsEndPos)
        {
            long workDone = segmentStartPos / 1000;
            if (this.monitor.getWorkDone() < workDone)
            {
                if (this.monitor.isProbablyCanceled())
                    throw new IProgressListener.OperationCanceledException();
                this.monitor.totalWorkDone(workDone);
            }

            int segmentType = in.readUnsignedByte();
            switch (segmentType)
            {
                case Constants.DumpSegment.ROOT_UNKNOWN:
                    readGC(GCRootInfo.Type.UNKNOWN, 0);
                    break;
                case Constants.DumpSegment.ROOT_THREAD_OBJECT:
                    readGCThreadObject(GCRootInfo.Type.THREAD_OBJ);
                    break;
                case Constants.DumpSegment.ROOT_JNI_GLOBAL:
                    readGC(GCRootInfo.Type.NATIVE_STACK, idSize);
                    break;
                case Constants.DumpSegment.ROOT_JNI_LOCAL:
                    readGCWithThreadContext(GCRootInfo.Type.NATIVE_LOCAL, 4);
                    break;
                case Constants.DumpSegment.ROOT_JAVA_FRAME:
                    readGCWithThreadContext(GCRootInfo.Type.JAVA_LOCAL, 4);
                    break;
                case Constants.DumpSegment.ROOT_NATIVE_STACK:
                    readGCWithThreadContext(GCRootInfo.Type.NATIVE_STACK, 0);
                    break;
                case Constants.DumpSegment.ROOT_STICKY_CLASS:
                    readGC(GCRootInfo.Type.SYSTEM_CLASS, 0);
                    break;
                case Constants.DumpSegment.ROOT_THREAD_BLOCK:
                    readGC(GCRootInfo.Type.THREAD_BLOCK, 4);
                    break;
                case Constants.DumpSegment.ROOT_MONITOR_USED:
                    readGC(GCRootInfo.Type.BUSY_MONITOR, 0);
                    break;
                case Constants.DumpSegment.CLASS_DUMP:
                    readClassDump(segmentStartPos);
                    break;
                case Constants.DumpSegment.INSTANCE_DUMP:
                    readInstanceDump(segmentStartPos);
                    break;
                case Constants.DumpSegment.OBJECT_ARRAY_DUMP:
                    readObjectArrayDump(segmentStartPos);
                    break;
                case Constants.DumpSegment.PRIMITIVE_ARRAY_DUMP:
                    readPrimitiveArrayDump(segmentStartPos);
                    break;
                default:
                    throw new SnapshotException(MessageFormat.format("Error: Invalid heap dump file.\n"
                                    + "Unsupported segment type {0} at position {1}", segmentType, segmentStartPos));
            }

            segmentStartPos = in.position();
        }
    }

    private void readGCThreadObject(int gcType) throws IOException
    {
        long id = readID();
        int threadSerialNo = in.readInt();
        thread2id.put(threadSerialNo, id);
        handler.addGCRoot(id, 0, gcType);

        in.skipBytes(4);
    }

    private void readGC(int gcType, int skip) throws IOException
    {
        long id = readID();
        handler.addGCRoot(id, 0, gcType);

        if (skip > 0)
            in.skipBytes(skip);
    }

    private void readGCWithThreadContext(int gcType, int skip) throws IOException
    {
        long id = readID();
        int threadSerialNo = in.readInt();
        handler.addGCRoot(id, thread2id.get(threadSerialNo), gcType);

        if (skip > 0)
            in.skipBytes(skip);
    }

    private void readClassDump(long segmentStartPos) throws IOException
    {
        long address = readID();
        in.skipBytes(4); // stack trace serial number
        long superClassObjectId = readID();
        long classLoaderObjectId = readID();

        // skip signers, protection domain, reserved ids (2), instance size
        in.skipBytes(this.idSize * 4 + 4);

        // constant pool: u2 ( u2 u1 value )*
        int constantPoolSize = in.readUnsignedShort();
        for (int ii = 0; ii < constantPoolSize; ii++)
        {
            in.skipBytes(2); // index
            skipValue(); // value
        }

        // static fields: u2 num ( name ID, u1 type, value)
        int numStaticFields = in.readUnsignedShort();
        Field[] statics = new Field[numStaticFields];

        for (int ii = 0; ii < numStaticFields; ii++)
        {
            long nameId = readID();
            String name = getStringConstant(nameId);

            byte type = in.readByte();

            Object value = readValue(null, type);
            statics[ii] = new Field(name, type, value);
        }

        // instance fields: u2 num ( name ID, u1 type )
        int numInstanceFields = in.readUnsignedShort();
        FieldDescriptor[] fields = new FieldDescriptor[numInstanceFields];

        for (int ii = 0; ii < numInstanceFields; ii++)
        {
            long nameId = readID();
            String name = getStringConstant(nameId);

            byte type = in.readByte();
            fields[ii] = new FieldDescriptor(name, type);
        }

        // get name
        String className = (String) class2name.get(address);
        if (className == null)
            className = "unknown-name@0x" + Long.toHexString(address);

        if (className.charAt(0) == '[') // quick check if array at hand
        {
            // fix object class names
            Matcher matcher = PATTERN_OBJ_ARRAY.matcher(className);
            if (matcher.matches())
            {
                int l = matcher.group(1).length();
                className = matcher.group(2);
                for (int ii = 0; ii < l; ii++)
                    className += "[]";
            }

            // primitive arrays
            matcher = PATTERN_PRIMITIVE_ARRAY.matcher(className);
            if (matcher.matches())
            {
                int count = matcher.group(1).length() - 1;
                className = "unknown[]";

                char signature = matcher.group(2).charAt(0);
                for (int ii = 0; ii < IPrimitiveArray.SIGNATURES.length; ii++)
                {
                    if (IPrimitiveArray.SIGNATURES[ii] == (byte) signature)
                    {
                        className = IPrimitiveArray.TYPE[ii];
                        break;
                    }
                }

                for (int ii = 0; ii < count; ii++)
                    className += "[]";
            }
        }

        ClassImpl clazz = new ClassImpl(address, className, superClassObjectId, classLoaderObjectId, statics, fields);
        handler.addClass(clazz, segmentStartPos);
    }

    private void readInstanceDump(long segmentStartPos) throws IOException
    {
        long address = readID();
        handler.reportInstance(address, segmentStartPos);
        in.skipBytes(idSize + 4);
        int payload = in.readInt();
        in.skipBytes(payload);
    }

    private void readObjectArrayDump(long segmentStartPos) throws IOException
    {
        long address = readID();
        handler.reportInstance(address, segmentStartPos);

        in.skipBytes(4);
        int size = in.readInt();
        long arrayClassObjectID = readID();

        // check if class needs to be created
        IClass arrayType = handler.lookupClass(arrayClassObjectID);
        if (arrayType == null)
            handler.reportRequiredObjectArray(arrayClassObjectID);

        in.skipBytes(size * idSize);
    }

    private void readPrimitiveArrayDump(long segmentStartPos) throws SnapshotException, IOException
    {
        long address = readID();
        handler.reportInstance(address, segmentStartPos);

        in.skipBytes(4);
        int size = in.readInt();
        byte elementType = in.readByte();

        if ((elementType < IPrimitiveArray.Type.BOOLEAN) || (elementType > IPrimitiveArray.Type.LONG))
            throw new SnapshotException("Illegal primitive object array type");

        // check if class needs to be created
        String name = IPrimitiveArray.TYPE[(int) elementType];
        IClass clazz = handler.lookupClassByName(name, true);
        if (clazz == null)
            handler.reportRequiredPrimitiveArray((int) elementType);

        int elementSize = IPrimitiveArray.ELEMENT_SIZE[(int) elementType];
        in.skipBytes(elementSize * size);
    }

    private String getStringConstant(long address) throws IOException
    {
        if (address == 0L)
            return "";

        String result = handler.getConstantPool().get(address);
        return result == null ? "Unresolved Name 0x" + Long.toHexString(address) : result;
    }

}
