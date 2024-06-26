/*******************************************************************************
 * Copyright (c) 2008, 2020 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.mat.inspections;

import org.eclipse.mat.query.IQuery;
import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.annotations.Argument;
import org.eclipse.mat.query.annotations.Category;
import org.eclipse.mat.query.annotations.CommandName;
import org.eclipse.mat.query.annotations.HelpUrl;
import org.eclipse.mat.query.annotations.Icon;
import org.eclipse.mat.query.annotations.Menu;
import org.eclipse.mat.query.annotations.Menu.Entry;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.query.IHeapObjectArgument;
import org.eclipse.mat.snapshot.query.ObjectListResult;
import org.eclipse.mat.util.IProgressListener;

@Category(Category.HIDDEN)
@CommandName("list_objects")
@Icon("/META-INF/icons/heapobjects/instance_obj.gif")
@Menu( { @Entry(icon = "/META-INF/icons/list_outbound.gif"), //
                @Entry(options = "-inbound", icon = "/META-INF/icons/list_inbound.gif") //
})
@HelpUrl("/org.eclipse.mat.ui.help/reference/querymatrix.html#ref_querymatrix__list_objects")
public class ObjectListQuery implements IQuery
{
    @Argument
    public ISnapshot snapshot;

    @Argument(flag = Argument.UNFLAGGED)
    public IHeapObjectArgument objects;

    @Argument(isMandatory = false)
    public boolean inbound = false;

    public IResult execute(IProgressListener listener) throws Exception
    {
        return inbound ? new ObjectListResult.Inbound(snapshot, objects.getIds(listener)) //
                        : new ObjectListResult.Outbound(snapshot, objects.getIds(listener));
    }

}
