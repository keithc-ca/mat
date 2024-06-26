/*******************************************************************************
 * Copyright (c) 2008, 2021 SAP AG, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *    Andrew Johnson - bug fix for missing classes
 *    Netflix (Jason Koch) - refactors for increased performance and concurrency
 *******************************************************************************/
package org.eclipse.mat.hprof;

import java.io.IOException;
import java.util.List;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.collect.ArrayLong;
import org.eclipse.mat.parser.IPreliminaryIndex;
import org.eclipse.mat.parser.index.IIndexReader.IOne2LongIndex;
import org.eclipse.mat.parser.model.ClassImpl;
import org.eclipse.mat.parser.model.XSnapshotInfo;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.util.IProgressListener;

public interface IHprofParserHandler
{
    String IDENTIFIER_SIZE = "ID_SIZE"; //$NON-NLS-1$
    String CREATION_DATE = "CREATION_DATE"; //$NON-NLS-1$
    String VERSION = "VERSION";//$NON-NLS-1$
    String REFERENCE_SIZE = "REF_SIZE"; //$NON-NLS-1$
    String STREAM_LENGTH = "LENGTH"; //$NON-NLS-1$
    String HEAP_POSITION = "HEAP_POS"; //$NON-NLS-1$

    public class HeapObject
    {
        public long objectAddress;
        public ClassImpl clazz;
        public long usedHeapSize;
        public ArrayLong references;
        public boolean isObjectArray = false;
        public boolean isPrimitiveArray = false;
        public long filePosition;
        public long classIdOrElementType;
        public int arraySize;
        public long[] ids;
        public byte[] instanceData;
        public int idSize;

        public static HeapObject forPrimitiveArray(long objectAddress, byte elementType, int arraySize, long filePosition)
        {
            HeapObject o = new HeapObject(1);
            o.objectAddress = objectAddress;
            o.isPrimitiveArray = true;
            o.classIdOrElementType = elementType;
            o.arraySize = arraySize;
            o.filePosition = filePosition;
            return o;
        }

        public static HeapObject forObjectArray(long objectAddress, long classID, int arraySize, long[] ids, long filePosition)
        {
            HeapObject o = new HeapObject(1 + ids.length);
            o.objectAddress = objectAddress;
            o.isObjectArray = true;
            o.classIdOrElementType = classID;
            o.arraySize = arraySize;
            o.filePosition = filePosition;
            o.ids = ids;
            return o;
        }

        public static HeapObject forInstance(long objectAddress, long classID, byte[] instanceData, long filePosition, int idSize)
        {
            int estSize = Math.min(10, 1 + instanceData.length / idSize);
            HeapObject o = new HeapObject(estSize);
            o.objectAddress = objectAddress;
            o.classIdOrElementType = classID;
            o.instanceData = instanceData;
            o.filePosition = filePosition;
            o.idSize = idSize;
            return o;
        }

        public HeapObject()
        {
            references = new ArrayLong();
        }

        public HeapObject(int refs)
        {
            references = new ArrayLong(refs);
        }

        public HeapObject(long objectAddress, ClassImpl clazz, long usedHeapSize)
        {
            this(0);
            this.objectAddress = objectAddress;
            this.clazz = clazz;
            this.usedHeapSize = usedHeapSize;
        }
    }

    // //////////////////////////////////////////////////////////////
    // lifecycle
    // //////////////////////////////////////////////////////////////

    void beforePass1(XSnapshotInfo snapshotInfo) throws IOException;

    void beforePass2(IProgressListener monitor) throws IOException, SnapshotException;

    IOne2LongIndex fillIn(IPreliminaryIndex index, IProgressListener listener) throws IOException;

    void cancel();

    // //////////////////////////////////////////////////////////////
    // report parsed entities
    // //////////////////////////////////////////////////////////////

    void addProperty(String name, String value) throws IOException;

    void addGCRoot(long id, long referrer, int rootType) throws IOException;

    void addClass(ClassImpl clazz, long filePosition, int idSize, int instsize) throws IOException;

    void addObject(HeapObject object) throws IOException;

    void reportInstanceWithClass(long id, long filePosition, long classID, int size);

    void reportInstanceOfObjectArray(long id, long filePosition, long arrayClassID);

    void reportInstanceOfPrimitiveArray(long id, long filePosition, int arrayType);

    // //////////////////////////////////////////////////////////////
    // lookup heap infos
    // //////////////////////////////////////////////////////////////

    int getIdentifierSize();

    IClass lookupClass(long classId);

    IClass lookupClassByName(String name, boolean failOnMultipleInstances);

    IClass lookupClassByIndex(int objIndex);

    IClass lookupPrimitiveArrayClassByType(byte elementType);

    List<IClass> resolveClassHierarchy(long classId);

    int mapAddressToId(long address);

    XSnapshotInfo getSnapshotInfo();

    long getObjectArrayHeapSize(ClassImpl arrayType, int size);

    long getPrimitiveArrayHeapSize(byte elementType, int size);
}
