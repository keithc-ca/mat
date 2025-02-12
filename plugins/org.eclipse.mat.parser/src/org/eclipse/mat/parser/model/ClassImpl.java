/*******************************************************************************
 * Copyright (c) 2008, 2025 SAP AG, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *    Andrew Johnson - documentation update
 *    Netflix (Jason Koch) - refactors for increased performance and concurrency
 *******************************************************************************/
package org.eclipse.mat.parser.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.collect.ArrayInt;
import org.eclipse.mat.collect.ArrayLong;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.Field;
import org.eclipse.mat.snapshot.model.FieldDescriptor;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.NamedReference;
import org.eclipse.mat.snapshot.model.ObjectReference;
import org.eclipse.mat.snapshot.model.PseudoReference;
import org.eclipse.mat.util.IProgressListener;
import org.eclipse.mat.util.VoidProgressListener;

/**
 * Implementation of a Java object representing a java.lang.Class object.
 * As well as some standard object information it contains information about the class
 * and summary details about instances of this class.
 * @noextend
 */
public class ClassImpl extends AbstractObjectImpl implements IClass, Comparable<ClassImpl>
{
    private static final long serialVersionUID = 22L;
    private static final transient AtomicIntegerFieldUpdater<ClassImpl> instanceCountUpdater =
                    AtomicIntegerFieldUpdater.newUpdater(ClassImpl.class, "instanceCount"); //$NON-NLS-1$
    private static final transient AtomicLongFieldUpdater<ClassImpl> totalSizeUpdater =
                    AtomicLongFieldUpdater.newUpdater(ClassImpl.class, "totalSize"); //$NON-NLS-1$

    /** Shortcut for java.lang.Class */
    public static final String JAVA_LANG_CLASS = IClass.JAVA_LANG_CLASS;

    protected String name;
    protected int superClassId = -1;
    protected long superClassAddress;
    protected int classLoaderId = -1;
    protected long classLoaderAddress;
    protected Field[] staticFields;
    protected FieldDescriptor[] fields;
    protected int usedHeapSize;
    protected int instanceSize;
    protected volatile int instanceCount;
    protected volatile long totalSize;
    protected boolean isArrayType;

    private Collection<IClass> subClasses;

    private Serializable cacheEntry;

    /**
     * Construct a class object based on name, address and fields.
     * @param address the address of the class object
     * @param name the class name, using '.' as package separator
     * @param superAddress the address of the superclass, or 0 if none.
     * @param loaderAddress the address of the class loader
     * @param staticFields all the static fields, with values
     * @param fields all the instance fields as descriptors
     */
    public ClassImpl(long address, String name, long superAddress, long loaderAddress, Field[] staticFields,
                    FieldDescriptor[] fields)
    {
        super(-1, address, null);

        this.name = name;
        this.superClassAddress = superAddress;
        this.classLoaderAddress = loaderAddress;
        this.staticFields = staticFields;
        this.fields = fields;
        this.instanceSize = -1;

        this.totalSize = 0;
        this.isArrayType = name.endsWith("[]");//$NON-NLS-1$
    }

    /**
     * Gets the key for extra information about this class.
     * @return the key
     */
    public Serializable getCacheEntry()
    {
        return cacheEntry;
    }

    /**
     * Sets the key for extra information about this class.
     * @param cacheEntry the key
     */
    public void setCacheEntry(Serializable cacheEntry)
    {
        this.cacheEntry = cacheEntry;
    }

    /**
     * Sets the superclass index.
     * May need to be changed after reindexing of a snapshot.
     * @param superClassIndex the new index
     */
    public void setSuperClassIndex(int superClassIndex)
    {
        this.superClassId = superClassIndex;
    }

    /**
     * Sets the class loader index.
     * May need to be changed after reindexing of a snapshot.
     * @param classLoaderIndex the new index
     */
    public void setClassLoaderIndex(int classLoaderIndex)
    {
        this.classLoaderId = classLoaderIndex;
    }

    public int[] getObjectIds() throws UnsupportedOperationException, SnapshotException
    {
        try
        {
            return source.getIndexManager().c2objects().getObjectsOf(this.cacheEntry);
        }
        catch (IOException e)
        {
            throw new SnapshotException(e);
        }
    }

    public long getRetainedHeapSizeOfObjects(boolean calculateIfNotAvailable, boolean approximation,
                    IProgressListener listener) throws SnapshotException
    {
        long answer = this.source.getRetainedSizeCache().get(getObjectId());

        if (answer > 0 || !calculateIfNotAvailable)
            return answer;

        if (answer < 0 && approximation)
            return answer;

        if (listener == null)
            listener = new VoidProgressListener();

        ArrayInt ids = new ArrayInt(1 + getNumberOfObjects());
        ids.add(getObjectId());
        ids.addAll(getObjectIds());

        int[] retainedSet;
        long retainedSize = 0;

        if (!approximation)
        {
            retainedSet = source.getRetainedSet(ids.toArray(), listener);
            if (listener.isCanceled())
                return 0;
            retainedSize = source.getHeapSize(retainedSet);
        }
        else
        {
            retainedSize = source.getMinRetainedSize(ids.toArray(), listener);
            if (listener.isCanceled())
                return 0;
        }

        if (approximation)
            retainedSize = -retainedSize;

        this.source.getRetainedSizeCache().put(getObjectId(), retainedSize);
        return retainedSize;

    }

    @Override
    public long getUsedHeapSize()
    {
        return usedHeapSize;
    }

    @Override
    public ArrayLong getReferences()
    {
        ArrayLong answer = new ArrayLong(staticFields.length);

        answer.add(classInstance.getObjectAddress());
        if (superClassAddress != 0)
            answer.add(superClassAddress);
        answer.add(classLoaderAddress);

        for (int ii = 0; ii < staticFields.length; ii++)
        {
            if (staticFields[ii].getValue() instanceof ObjectReference)
            {
                ObjectReference ref = (ObjectReference) staticFields[ii].getValue();
                answer.add(ref.getObjectAddress());
            }
        }

        return answer;
    }

    public List<NamedReference> getOutboundReferences()
    {
        List<NamedReference> answer = new LinkedList<NamedReference>();
        answer.add(new PseudoReference(source, classInstance.getObjectAddress(), "<class>"));//$NON-NLS-1$
        if (superClassAddress != 0)
            answer.add(new PseudoReference(source, superClassAddress, "<super>"));//$NON-NLS-1$
        answer.add(new PseudoReference(source, classLoaderAddress, "<classloader>"));//$NON-NLS-1$

        for (int ii = 0; ii < staticFields.length; ii++)
        {
            if (staticFields[ii].getValue() instanceof ObjectReference)
            {
                ObjectReference ref = (ObjectReference) staticFields[ii].getValue();
                String fieldName = staticFields[ii].getName();
                if (fieldName.startsWith("<")) //$NON-NLS-1$
                    answer.add(new PseudoReference(source, ref.getObjectAddress(), fieldName));
                else
                    answer.add(new NamedReference(source, ref.getObjectAddress(), fieldName));
            }
        }

        return answer;
    }

    @Override
    public long getClassLoaderAddress()
    {
        return classLoaderAddress;
    }

    /**
     * Sets the class loader for this class.
     * @param address the address of the loader
     */
    public void setClassLoaderAddress(long address)
    {
        this.classLoaderAddress = address;
    }

    @Override
    public List<FieldDescriptor> getFieldDescriptors()
    {
        return Arrays.asList(fields);
    }

    @Override
    public int getNumberOfObjects()
    {
        return instanceCount;
    }

    /**
	 * @since 1.0
	 */
    @Override
    public long getHeapSizePerInstance()
    {
        return instanceSize;
    }

    /**
     * Sets the size per instance for the class
	 * @param size the size in bytes
     * @since 1.0
	 */
    public void setHeapSizePerInstance(long size)
    {
        instanceSize = (int)Math.min(size, Integer.MAX_VALUE);
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Sets the class name.
     * @param name the class name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public List<Field> getStaticFields()
    {
        return Arrays.asList(staticFields);
    }

    /**
     * Gets the address of the superclass.
     * @return the superclass address
     */
    public long getSuperClassAddress()
    {
        return superClassAddress;
    }

    @Override
    public int getSuperClassId()
    {
        return superClassId;
    }

    @Override
    public ClassImpl getSuperClass()
    {
        try
        {
            return superClassAddress != 0 ? (ClassImpl) this.source.getObject(superClassId) : null;
        }
        catch (SnapshotException e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The size of all the instances of this class.
     * @return the size in bytes
     */
    public long getTotalSize()
    {
        return totalSize;
    }

    @Override
    public boolean hasSuperClass()
    {
        return this.superClassAddress != 0;
    }

    @Override
    public int compareTo(ClassImpl other)
    {
        final long myAddress = getObjectAddress();
        final long otherAddress = other.getObjectAddress();
        return myAddress > otherAddress ? 1 : myAddress == otherAddress ? 0 : -1;
    }

    /**
     * Note another instance to this class.
	 * @param usedHeapSize the size in bytes of this instance
     * @since 1.0
	 */
    public void addInstance(long usedHeapSize)
    {
        instanceCountUpdater.getAndAdd(this, 1);
        totalSizeUpdater.getAndAdd(this, usedHeapSize);
    }

    /**
     * Remove an instance of this class.
     * Reverses the effect of {@link #addInstance(long)}
	 * @param heapSizePerInstance the size in bytes of this instance
     * @since 1.0
	 */
    public void removeInstance(long heapSizePerInstance)
    {
        instanceCountUpdater.getAndAdd(this, -1);
        totalSizeUpdater.getAndAdd(this, -heapSizePerInstance);
    }

    void removeInstanceBulk(int instanceCount, long heapSize)
    {
        instanceCountUpdater.getAndAdd(this, -instanceCount);
        totalSizeUpdater.getAndAdd(this, heapSize);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<IClass> getSubclasses()
    {
        return (subClasses == null) ? Collections.emptyList() : new ArrayList<>(subClasses);
    }

    @Override
    public List<IClass> getAllSubclasses()
    {
        if (subClasses == null || subClasses.isEmpty())
            return Collections.emptyList();

        List<IClass> answer = new ArrayList<IClass>(subClasses.size() * 2);
        answer.addAll(this.subClasses);
        for (IClass subClass : this.subClasses)
            answer.addAll(subClass.getAllSubclasses());
        return answer;
    }

    @Override
    protected StringBuffer appendFields(StringBuffer buf)
    {
        return super.appendFields(buf).append(";name=").append(getName());//$NON-NLS-1$
    }

    @Override
    public boolean isArrayType()
    {
        return isArrayType;
    }

    @Override
    public String getTechnicalName()
    {
        StringBuilder builder = new StringBuilder(256);
        builder.append("class ");//$NON-NLS-1$
        builder.append(getName());
        builder.append(" @ 0x");//$NON-NLS-1$
        builder.append(Long.toHexString(getObjectAddress()));
        return builder.toString();
    }

    @Override
    protected Field internalGetField(String name)
    {
        for (Field f : staticFields)
            if (f.getName().equals(name))
                return f;
        return null;
    }

    @Override
    public int getClassLoaderId()
    {
        return classLoaderId;
    }

    /**
     * Add a subclass of this class.
     * @param clazz the subclass
     */
    public void addSubClass(ClassImpl clazz)
    {
        if (subClasses == null)
            subClasses = new LinkedHashSet<IClass>();
        subClasses.add(clazz);
    }

    /**
     * Remove a subclass of this class.
     * Reverses the effect of {@link #addSubClass(ClassImpl)}
     * @param clazz the subclass
     */
    public void removeSubClass(ClassImpl clazz)
    {
        subClasses.remove(clazz);
    }

    /**
     * Sets the used heap size for this particular class.
     * Does not include instances.
	 * @param usedHeapSize the size in bytes
     * @since 1.0
	 */
    public void setUsedHeapSize(long usedHeapSize)
    {
        this.usedHeapSize = (int)Math.min(usedHeapSize, Integer.MAX_VALUE);
    }

    @Override
    public boolean doesExtend(String className) throws SnapshotException
    {
        if (className.equals(this.name))
            return true;

        return hasSuperClass() ? ((ClassImpl) source.getObject(this.superClassId)).doesExtend(className) : false;
    }

    @Override
    public void setSnapshot(ISnapshot dump)
    {
        super.setSnapshot(dump);

        // object reference need (for convenience) a reference to the snapshot
        // inject after restoring class objects

        for (Field f : this.staticFields)
        {
            if (f.getValue() instanceof ObjectReference)
            {
                ObjectReference ref = (ObjectReference) f.getValue();
                f.setValue(new ObjectReference(dump, ref.getObjectAddress()));
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        // TODO intent is to remove this code at some point and simply use Set
        // For backwards compatibility serialize as List - https://github.com/eclipse-mat/mat/issues/89
        final Collection<IClass> originalSubclasses = subClasses;
        try
        {
            if (originalSubclasses != null)
            {
                subClasses = new ArrayList<>(originalSubclasses);
            }
            out.defaultWriteObject();
        }
        finally
        {
            subClasses = originalSubclasses;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // For backwards compatibility serialize as List - https://github.com/eclipse-mat/mat/issues/89
        in.defaultReadObject();
        if (subClasses instanceof List)
        {
            subClasses = new LinkedHashSet<>(subClasses);
        }
    }

}
