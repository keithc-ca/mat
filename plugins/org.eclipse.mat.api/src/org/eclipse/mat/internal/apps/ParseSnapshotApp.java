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
package org.eclipse.mat.internal.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.internal.MATPlugin;
import org.eclipse.mat.internal.snapshot.SnapshotQueryContext;
import org.eclipse.mat.report.Spec;
import org.eclipse.mat.report.SpecFactory;
import org.eclipse.mat.report.TestSuite;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.SnapshotFactory;
import org.eclipse.mat.util.ConsoleProgressListener;
import org.eclipse.mat.util.MessageUtil;

public class ParseSnapshotApp implements IApplication
{

    public Object start(IApplicationContext context) throws Exception
    {
        String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);

        if (args == null || args.length < 1)
            throw new IllegalArgumentException("Usage: <snapshot> [(<report id>)*]");

        File file = new File(args[0]);
        if (!file.exists())
            throw new FileNotFoundException(MessageUtil.format("File not found: {0}", file.getAbsolutePath()));

        List<Spec> reports = new ArrayList<Spec>();

        SpecFactory factory = SpecFactory.instance();
        for (int ii = 1; ii < args.length; ii++)
        {
            Spec spec = null;

            File specFile = new File(args[ii]);
            if (specFile.exists())
            {
                spec = factory.create(specFile);
            }
            else
            {
                spec = factory.create(args[ii]);
            }

            if (spec != null)
            {
                factory.resolve(spec);
                reports.add(spec);
            }
            else
            {
                System.err.println(MessageUtil.format("Report not found: {0}", args[ii]));
            }
        }

        parse(file, reports);

        return IApplication.EXIT_OK;
    }

    public void stop()
    {}

    private void parse(File file, List<Spec> reports) throws SnapshotException
    {
        ConsoleProgressListener listener = new ConsoleProgressListener(System.out);
        ISnapshot snapshot = SnapshotFactory.openSnapshot(file, listener);
        listener.done();

        try
        {
            for (Spec report : reports)
            {
                try
                {
                    runReport(snapshot, report);
                }
                catch (SnapshotException e)
                {
                    MATPlugin.log(e);
                }
                catch (IOException e)
                {
                    MATPlugin.log(e);
                }
            }
        }
        finally
        {
            SnapshotFactory.dispose(snapshot);
        }
    }

    private void runReport(ISnapshot snapshot, Spec report) throws SnapshotException, IOException
    {
        TestSuite suite = new TestSuite.Builder(report) //
                        .build(new SnapshotQueryContext(snapshot));

        ConsoleProgressListener listener = new ConsoleProgressListener(System.out);
        suite.execute(listener);
        listener.done();
    }

}
