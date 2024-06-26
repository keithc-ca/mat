/*******************************************************************************
 * Copyright (c) 2008, 2022 SAP AG and IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *    Andrew Johnson (IBM Corporation) - tidy up of snapshot
 *******************************************************************************/
package org.eclipse.mat.internal.snapshot;

import java.io.Closeable;
import java.io.File;
import java.lang.ref.WeakReference;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.query.registry.ArgumentDescriptor;
import org.eclipse.mat.query.registry.ArgumentFactory;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.SnapshotFactory;
import org.eclipse.mat.util.VoidProgressListener;

public class SnapshotArgument implements ArgumentFactory, Closeable
{
    String filename;
    WeakReference<ISnapshot> snapref;

    public SnapshotArgument(String filename)
    {
        this.filename = filename;
    }

    public String getFilename()
    {
        return filename;
    }

    public void appendUsage(StringBuilder buf)
    {
        buf.append(filename);
    }

    public Object build(ArgumentDescriptor descriptor) throws SnapshotException
    {
        if (!ISnapshot.class.isAssignableFrom(descriptor.getType()))
            throw new SnapshotException();

        ISnapshot snapshot = SnapshotFactory.openSnapshot(new File(filename), new VoidProgressListener());
        snapref = new WeakReference<ISnapshot>(snapshot);
        return snapshot;
    }

    /**
     * Tidy up and dispose of any snapshot obtained from build.
     */
    @Override
    public void close()
    {
        if (snapref != null)
        {
            SnapshotFactory.dispose(snapref.get());
            snapref.clear();
            snapref = null;
        }
    }

}
