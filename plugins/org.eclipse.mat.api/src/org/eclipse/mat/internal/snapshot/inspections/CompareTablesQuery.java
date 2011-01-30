/*******************************************************************************
 * Copyright (c) 2010, 2011 SAP AG and IBM Corporation. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: SAP AG - initial API and implementation
 * Andrew Johnson - conversion to proper query, set operations via contexts
 ******************************************************************************/
package org.eclipse.mat.internal.snapshot.inspections;

import java.net.URL;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.mat.collect.ArrayInt;
import org.eclipse.mat.internal.Messages;
import org.eclipse.mat.query.Column;
import org.eclipse.mat.query.ContextProvider;
import org.eclipse.mat.query.IContextObject;
import org.eclipse.mat.query.IContextObjectSet;
import org.eclipse.mat.query.IIconProvider;
import org.eclipse.mat.query.IQuery;
import org.eclipse.mat.query.IQueryContext;
import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.IResultTable;
import org.eclipse.mat.query.ResultMetaData;
import org.eclipse.mat.query.annotations.Argument;
import org.eclipse.mat.query.annotations.Icon;
import org.eclipse.mat.query.annotations.Menu;
import org.eclipse.mat.query.annotations.Menu.Entry;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.query.Icons;
import org.eclipse.mat.util.IProgressListener;
import org.eclipse.mat.util.MessageUtil;

import com.ibm.icu.text.DecimalFormat;

@Icon("/META-INF/icons/compare.gif")
@Menu({ @Entry(options = "-setop ALL")
})
public class CompareTablesQuery implements IQuery
{
	@Argument
	public IResultTable[] tables;

    @Argument
    public IQueryContext queryContext;
	
	@Argument
	public IQueryContext[] queryContexts;

	@Argument(isMandatory = false)
	public Mode mode = Mode.ABSOLUTE;

	@Argument(isMandatory = false)
	public Operation setOp = Operation.NONE;

    private boolean[] sameSnapshot;

	public enum Mode
	{
		ABSOLUTE("ABSOLUTE"), // //$NON-NLS-1$
		DIFF_TO_FIRST("DIFF_TO_FIRST"), // //$NON-NLS-1$
		DIFF_TO_PREVIOUS("DIFF_TO_PREVIOUS"); //$NON-NLS-1$

		String label;

		private Mode(String label)
		{
			this.label = label;
		}

		public String toString()
		{
			return label;
		}
	}

	public enum Operation
	{
	    NONE,
	    ALL,
	    INTERSECTION,
	    UNION,
	    DIFFERENCE
	}

	public IResult execute(IProgressListener listener) throws Exception
	{
		if (tables == null) return null;

		// Length 1 table is valid, and we need to process it in case it is from a different snapshot

		IResultTable base = tables[0];
		Column[] baseColumns = base.getColumns();
		Column key = baseColumns[0];

		sameSnapshot = new boolean[tables.length];
		ISnapshot sn = (ISnapshot)queryContext.get(ISnapshot.class, null);
		for (int i = 0; i < tables.length; ++i)
		{
		    sameSnapshot[i] = (queryContexts[i] == null || sn.equals((ISnapshot)queryContexts[i].get(ISnapshot.class, null)));
		}

		List<ComparedColumn> attributes = new ArrayList<ComparedColumn>();
		for (int i = 1; i < baseColumns.length; i++)
		{
			int[] indexes = new int[tables.length];
			for (int j = 0; j < indexes.length; j++)
			{
				indexes[j] = getColumnIndex(baseColumns[i].getLabel(), tables[j]);
			}
			attributes.add(new ComparedColumn(baseColumns[i], indexes, true));
		}

		return new TableComparisonResult(mergeKeys(), key, attributes, mode, setOp);
	}

	private int getColumnIndex(String name, IResultTable table)
	{
		Column[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++)
		{
			if (columns[i].getLabel().equals(name)) return i;
		}
		return -1;
	}

	private List<ComparedRow> mergeKeys()
	{
		Map<Object, Object[]> map = new HashMap<Object, Object[]>();
		for (int i = 0; i < tables.length; i++)
		{
			for (int j = 0; j < tables[i].getRowCount(); j++)
			{
				Object row = tables[i].getRow(j);
				Object key = tables[i].getColumnValue(row, 0);
				Object[] rows = map.get(key);
				if (rows == null)
				{
					rows = new Object[tables.length];
					map.put(key, rows);
				}
				rows[i] = row;
			}
		}

		List<ComparedRow> result = new ArrayList<ComparedRow>(map.size());
		for (Map.Entry<Object, Object[]> entry : map.entrySet())
		{
			result.add(new ComparedRow(entry.getKey(), entry.getValue()));
		}

		return result;
	}

	public class ComparedColumn
	{
		Column description;
		int[] columnIndexes;
		boolean displayed;

		public ComparedColumn(Column description, int[] columnIndexes, boolean displayed)
		{
			super();
			this.displayed = displayed;
			this.description = description;
			this.columnIndexes = columnIndexes;
		}

		public Column getDescription()
		{
			return description;
		}

		public void setDescription(Column description)
		{
			this.description = description;
		}

		public int[] getColumnIndexes()
		{
			return columnIndexes;
		}

		public void setColumnIndexes(int[] columnIndexes)
		{
			this.columnIndexes = columnIndexes;
		}

		public boolean isDisplayed()
		{
			return displayed;
		}

		public void setDisplayed(boolean displayed)
		{
			this.displayed = displayed;
		}

	}

	class ComparedRow
	{
		Object key;
		Object[] rows;

		public ComparedRow(Object key, Object[] rows)
		{
			super();
			this.key = key;
			this.rows = rows;
		}

		public Object getKey()
		{
			return key;
		}

		public void setKey(Object key)
		{
			this.key = key;
		}

		public Object[] getRows()
		{
			return rows;
		}

		public void setRows(Object[] rows)
		{
			this.rows = rows;
		}
	}

	public class TableComparisonResult implements IResultTable, IIconProvider
	{
		private Column key;
		private List<ComparedRow> rows;
		private List<ComparedColumn> comparedColumns;
		private List<ComparedColumn> displayedColumns;
		private Column[] columns;
		private Mode mode;
		private Operation setOp;

		public TableComparisonResult(List<ComparedRow> rows, Column key, List<ComparedColumn> comparedColumns, Mode mode, Operation setOp)
		{
			this.key = key;
			this.mode = mode;
			this.rows = rows;
			this.comparedColumns = comparedColumns;
			updateColumns();
			setMode(mode);
			this.setOp = setOp;
		}

		public Object getRow(int rowId)
		{
			return rows.get(rowId);
		}

		public int getRowCount()
		{
			return rows.size();
		}

		public List<ComparedColumn> getComparedColumns()
		{
			return comparedColumns;
		}

		public void setComparedColumns(List<ComparedColumn> comparedColumns)
		{
			this.comparedColumns = comparedColumns;
		}

		public Object getColumnValue(Object row, int columnIndex)
		{
			ComparedRow cr = (ComparedRow) row;
			if (columnIndex == 0) return cr.getKey();

			int comparedColumnIdx = (columnIndex - 1) / tables.length;
			int tableIdx = (columnIndex - 1) % tables.length;

			if (tableIdx == 0) return getAbsoluteValue(cr, comparedColumnIdx, tableIdx);

			switch (mode)
			{
			case ABSOLUTE:
				return getAbsoluteValue(cr, comparedColumnIdx, tableIdx);
			case DIFF_TO_FIRST:
				return getDiffToFirst(cr, comparedColumnIdx, tableIdx);
			case DIFF_TO_PREVIOUS:
				return getDiffToPrevious(cr, comparedColumnIdx, tableIdx);

			default:
				break;
			}

			return null;

		}

		public Column[] getColumns()
		{
			return columns;
		}

		public IContextObject getContext(Object row)
		{
            // Find a context from one of the tables
            IContextObject ret = null;
            for (int i = 0; i < tables.length && ret == null; ++i)
            {
                ret = getContextFromTable(i, row);
            }
            return ret;
		}

		public ResultMetaData getResultMetaData()
		{
            ResultMetaData.Builder bb = new ResultMetaData.Builder();
            int previous = -1;
            for (int i = 0; i < tables.length; ++i)
            {
                if (!sameSnapshot[i])
                    continue;

                if (setOp != Operation.NONE && previous >= 0)
                {
                    for (int op = 0; op < 4; ++op)
                    {
                        if (setOp == Operation.INTERSECTION && op != 0) continue;
                        if (setOp == Operation.UNION && op != 1) continue;
                        if (setOp == Operation.DIFFERENCE && op != 2) continue;
                        final int op1 = op;
                        // intersection, union, difference, difference
                        String title1;
                        final LinkedList<Integer> toDo = new LinkedList<Integer>();
                        if (mode == Mode.ABSOLUTE)
                        {
                            toDo.add(previous);
                            if (op == 3)
                            {
                                for (int k = previous + 1; k <= i; ++k)
                                {
                                    toDo.addFirst(k);
                                }
                            }
                            else
                            {
                                for (int k = previous + 1; k <= i; ++k)
                                {
                                    toDo.add(k);
                                }
                            }
                        }
                        else
                        {
                            if (op == 3)
                            {
                                toDo.add(i);
                                toDo.add(previous);
                            }
                            else
                            {
                                toDo.add(previous);
                                toDo.add(i);
                            }
                        }
                        // Convert the list of tables to a readable menu item
                        if (toDo.size() == 2)
                        {
                            switch (op)
                            {
                            case 0:
                                title1 = MessageUtil.format(Messages.CompareTablesQuery_IntersectionOf2, toDo.get(0)+1, toDo.get(1)+1);
                                break;
                            case 1:
                                title1 = MessageUtil.format(Messages.CompareTablesQuery_UnionOf2, toDo.get(0)+1, toDo.get(1)+1);
                                break;
                            case 2:
                            case 3:
                            default:
                                title1 = MessageUtil.format(Messages.CompareTablesQuery_DifferenceOf2, toDo.get(0)+1, toDo.get(1)+1);
                                break;
                            }
                        } else {
                            String soFar;
                            switch (op)
                            {
                                case 0:
                                    soFar = MessageUtil.format(Messages.CompareTablesQuery_IntersectionFirst, toDo.get(0)+1, toDo.get(1)+1);
                                break;
                                case 1:
                                    soFar = MessageUtil.format(Messages.CompareTablesQuery_UnionFirst, toDo.get(0)+1, toDo.get(1)+1);
                                    break;
                                case 2:
                                case 3:
                                default:
                                    soFar = MessageUtil.format(Messages.CompareTablesQuery_DifferenceFirst, toDo.get(0)+1, toDo.get(1)+1);
                                    break;
                            }
                            for (int t = 2; t < toDo.size() - 1; ++t)
                            {
                                switch (op)
                                {
                                case 0:
                                    soFar = MessageUtil.format(Messages.CompareTablesQuery_IntersectionMiddle, soFar, toDo.get(t)+1);
                                break;
                                case 1:
                                    soFar = MessageUtil.format(Messages.CompareTablesQuery_UnionMiddle, soFar, toDo.get(t)+1);
                                    break;
                                case 2:
                                case 3:
                                default:
                                    soFar = MessageUtil.format(Messages.CompareTablesQuery_DifferenceMiddle, soFar, toDo.get(t)+1);
                                    break;
                                }
                            }
                            int t = toDo.size() - 1;
                            switch (op)
                            {
                            case 0:
                                title1 = MessageUtil.format(Messages.CompareTablesQuery_IntersectionLast, soFar, toDo.get(t)+1);
                            break;
                            case 1:
                                title1 = MessageUtil.format(Messages.CompareTablesQuery_UnionLast, soFar, toDo.get(t)+1);
                                break;
                            case 2:
                            case 3:
                            default:
                                title1 = MessageUtil.format(Messages.CompareTablesQuery_DifferenceLast, soFar, toDo.get(t)+1);
                                break;
                            }
                        }

                        ContextProvider prov2 = new ContextProvider(title1)
                        {
                            public IContextObject getContext(final Object row)
                            {
                                int nullContexts = 0;
                                for (int i = 0; i < toDo.size(); ++i)
                                {
                                    if (getContextFromTable(i, row) == null)
                                    {
                                        ++nullContexts;
                                    }
                                    else
                                    {
                                        break;
                                    }
                                }
                                // If all the contexts were null then skip this generated context too
                                if (nullContexts == toDo.size())
                                    return null;
                                // First non-null context
                                final IContextObject cb = getContextFromTable(nullContexts, row);
                                
                                return new IContextObjectSet()
                                {
                                    public int getObjectId()
                                    {
                                        return cb.getObjectId();
                                    }

                                    public String getOQL()
                                    {
                                        return null;
                                    }
                                    public int[] getObjectIds()
                                    {
                                        LinkedList<Integer> toDo2 = new LinkedList<Integer>(toDo);
                                        int j = toDo2.remove();
                                        final IContextObject cb = getContextFromTable(j, row);
                                        int b[] = getObjectIdsFromContext(cb, row);
                                        ArrayInt bb;
                                        if (b == null)
                                        {
                                            bb = new ArrayInt();
                                        }
                                        else
                                        {
                                            bb = new ArrayInt(b);
                                            bb.sort();
                                        }

                                        while (!toDo2.isEmpty())
                                        {
                                            j = toDo2.remove();
                                            IContextObject ca = getContextFromTable(j, row);
                                            int a[] = getObjectIdsFromContext(ca, row);
                                            ArrayInt aa;
                                            if (a == null)
                                            {
                                                aa = new ArrayInt();
                                            }
                                            else
                                            {
                                                aa = new ArrayInt(a);
                                                aa.sort();
                                            }
                                            switch (op1)
                                            {
                                                case 0:
                                                    bb = intersectionArray(aa, bb);
                                                    break;
                                                case 1:
                                                    bb = unionArray(aa, bb);
                                                    break;
                                                case 2:
                                                case 3:
                                                    bb = diffArray(aa, bb);
                                                    break;
                                            }
                                        }

                                        final int res[] = bb.toArray();
                                        return res;
                                    }

                                    /**
                                     * Union of aa from bb
                                     * 
                                     * @param aa
                                     * @param bb
                                     * @return
                                     */
                                    private ArrayInt unionArray(ArrayInt aa, ArrayInt bb)
                                    {
                                        if (aa.size() == 0)
                                            return bb;
                                        if (bb.size() == 0)
                                            return aa;
                                        ArrayInt cc = new ArrayInt();
                                        int j = 0;
                                        for (int i = 0; i < bb.size(); ++i)
                                        {
                                            while (j < aa.size() && aa.get(j) < bb.get(i))
                                            {
                                                cc.add(aa.get(j));
                                                ++j;
                                            }
                                            cc.add(bb.get(i));
                                            if (j < aa.size() && aa.get(j) == bb.get(i))
                                            {
                                                ++j;
                                            }
                                        }
                                        return cc;
                                    }

                                    /**
                                     * Remove aa from bb
                                     * 
                                     * @param aa
                                     * @param bb
                                     * @return
                                     */
                                    private ArrayInt diffArray(ArrayInt aa, ArrayInt bb)
                                    {
                                        if (bb.size() == 0)
                                            return bb;
                                        if (aa.size() == 0)
                                            return bb;
                                        ArrayInt cc = new ArrayInt();
                                        int j = 0;
                                        for (int i = 0; i < bb.size(); ++i)
                                        {
                                            while (j < aa.size() && aa.get(j) < bb.get(i))
                                                ++j;
                                            if (j < aa.size() && aa.get(j) == bb.get(i))
                                            {
                                                ++j;
                                            }
                                            else
                                            {
                                                cc.add(bb.get(i));
                                            }
                                        }
                                        return cc;
                                    }

                                    private ArrayInt intersectionArray(ArrayInt aa, ArrayInt bb)
                                    {
                                        if (aa.size() == 0)
                                            return aa;
                                        if (bb.size() == 0)
                                            return bb;
                                        ArrayInt cc = new ArrayInt();
                                        int j = 0;
                                        for (int i = 0; i < bb.size(); ++i)
                                        {
                                            while (j < aa.size() && aa.get(j) < bb.get(i))
                                                ++j;
                                            if (j < aa.size() && aa.get(j) == bb.get(i))
                                            {
                                                cc.add(bb.get(i));
                                                ++j;
                                            }
                                        }
                                        return cc;
                                    }

                                    private int[] getObjectIdsFromContext(IContextObject b, Object row)
                                    {
                                        int bobjs[];
                                        if (b instanceof IContextObjectSet)
                                        {
                                            bobjs = ((IContextObjectSet) b).getObjectIds();
                                        }
                                        else if (b != null)
                                        {
                                            int id = b.getObjectId();
                                            if (id >= 0)
                                                bobjs = new int[] { id };
                                            else
                                                bobjs = null;
                                        }
                                        else
                                        {
                                            bobjs = null;
                                        }
                                        return bobjs;
                                    }
                                };
                            }
                        };
                        bb.addContext(prov2);
                    }
                }
                if (setOp == Operation.NONE || setOp == Operation.ALL)
                {
                    final int i2 = i;
                    String title = MessageUtil.format(Messages.CompareTablesQuery_Table, i + 1);
                    ContextProvider prov = new ContextProvider(title)
                    {
                        public IContextObject getContext(Object row)
                        {
                            return getContextFromTable(i2, row);
                        }
                    };
                    bb.addContext(prov);
                }
                if (mode == Mode.DIFF_TO_PREVIOUS || previous == -1)
                    previous = i;
            }
            return bb.build();
		}

		private Object getAbsoluteValue(ComparedRow cr, int comparedColumnIdx, int tableIdx)
		{
			Object tableRow = cr.getRows()[tableIdx];
			if (tableRow == null) return null;

			int tableColumnIdx = displayedColumns.get(comparedColumnIdx).getColumnIndexes()[tableIdx];
			if (tableColumnIdx == -1) return null;

			return tables[tableIdx].getColumnValue(tableRow, tableColumnIdx);
		}

		private Object getDiffToFirst(ComparedRow cr, int comparedColumnIdx, int tableIdx)
		{
			Object tableRow = cr.getRows()[tableIdx];
			if (tableRow == null) return null;

			int tableColumnIdx = displayedColumns.get(comparedColumnIdx).getColumnIndexes()[tableIdx];
			if (tableColumnIdx == -1) return null;

			Object value = tables[tableIdx].getColumnValue(tableRow, tableColumnIdx);
			Object firstTableValue = getAbsoluteValue(cr, comparedColumnIdx, 0);

			if (value == null && firstTableValue == null) return null;

			if (value == null && firstTableValue instanceof Number) return null;

			if (value instanceof Number && firstTableValue == null) return value;

			if (value instanceof Number && firstTableValue instanceof Number)
			{
				return computeDiff((Number) firstTableValue, (Number) value);
			}
			return null;
		}

		private Object getDiffToPrevious(ComparedRow cr, int comparedColumnIdx, int tableIdx)
		{
			Object tableRow = cr.getRows()[tableIdx];
			if (tableRow == null) return null;

			int tableColumnIdx = displayedColumns.get(comparedColumnIdx).getColumnIndexes()[tableIdx];
			if (tableColumnIdx == -1) return null;

			Object value = tables[tableIdx].getColumnValue(tableRow, tableColumnIdx);
			Object previousTableValue = getAbsoluteValue(cr, comparedColumnIdx, tableIdx - 1);

			if (value == null && previousTableValue == null) return null;

			if (value == null && previousTableValue instanceof Number) return null;

			if (value instanceof Number && previousTableValue == null) return value;

			if (value instanceof Number && previousTableValue instanceof Number)
			{
				return computeDiff((Number) previousTableValue, (Number) value);
			}
			return null;
		}

		private Object computeDiff(Number o1, Number o2)
		{
			if (o1 instanceof Long && o2 instanceof Long) return (o2.longValue() - o1.longValue());

			if (o1 instanceof Integer && o2 instanceof Integer) return (o2.intValue() - o1.intValue());

			if (o1 instanceof Short && o2 instanceof Short) return (o2.shortValue() - o1.shortValue());

			if (o1 instanceof Byte && o2 instanceof Byte) return (o2.byteValue() - o1.byteValue());

			if (o1 instanceof Float && o2 instanceof Float) return (o2.floatValue() - o1.floatValue());

			if (o1 instanceof Double && o2 instanceof Double) return (o2.doubleValue() - o1.doubleValue());

			return null;
		}

		/**
		 * Get the icon for the row.
		 * Chose the icon from the underlying tables if they all agree,
		 * others choose a special compare icon.
		 */
		public URL getIcon(Object row)
		{
			URL ret = null;
            final ComparedRow cr = (ComparedRow) row;
            // Find the rows from the tables
            boolean foundIcon = false;
            for (int i = 0; i < tables.length; ++i)
            {
                Object r = cr.getRows()[i];
                if (r != null)
                {
                    URL tableIcon = (tables[i] instanceof IIconProvider) ? ((IIconProvider) tables[i]).getIcon(r)
                                    : null;
                    if (foundIcon)
                    {
                        if (ret == null ? tableIcon != null : !ret.equals(tableIcon))
                        {
                            // Mismatch, so use compare icon instead
                            ret = Icons.getURL("compare.gif"); //$NON-NLS-1$
                            break;
                        }
                    }
                    else
                    {
                        ret = tableIcon;
                        foundIcon = true;
                    }
                }
            }
			return ret;
		}

		public Mode getMode()
		{
			return mode;
		}

		public void setMode(Mode mode)
		{
			this.mode = mode;
			setFormatter();
		}

		private void setFormatter()
		{
			int i = 1;
			Format formatter = new DecimalFormat("+#,##0;-#,##0"); //$NON-NLS-1$

			for (ComparedColumn comparedColumn : displayedColumns)
			{
				Column c = comparedColumn.description;
				for (int j = 0; j < comparedColumn.getColumnIndexes().length; j++)
				{
					if (mode != Mode.ABSOLUTE && j > 0)
					{
						columns[i].formatting(formatter);
					}
					else
					{
						columns[i].formatting(c.getFormatter());
					}
					i++;
				}
			}
		}

		public void updateColumns()
		{
			List<Column> result = new ArrayList<Column>();
			result.add(new Column(key.getLabel(), key.getType(), key.getAlign(), null, key.getFormatter(), null));

			displayedColumns = new ArrayList<ComparedColumn>();

			for (ComparedColumn comparedColumn : comparedColumns)
			{
				Column c = comparedColumn.description;
				if (comparedColumn.isDisplayed())
				{
					displayedColumns.add(comparedColumn);
					for (int j = 0; j < comparedColumn.getColumnIndexes().length; j++)
					{
						result.add(new Column(c.getLabel() + " #" + (j + 1), c.getType(), c.getAlign(), c.getSortDirection(), c.getFormatter(), null)); //$NON-NLS-1$
					}
				}
			}

			columns = result.toArray(new Column[result.size()]);
			setFormatter();
		}

        IContextObject getContextFromTable(int i, Object row)
        {
            if (!sameSnapshot[i])
                return null;
            final ComparedRow cr = (ComparedRow) row;
            IContextObject ret = null;
            Object r = cr.getRows()[i];
            if (r != null)
            {
                ret = tables[i].getContext(r);
            }
            return ret;
        }
    }
}
