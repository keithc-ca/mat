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
package org.eclipse.mat.ui.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mat.query.refined.Filter;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class Copy
{
    static CopyToClipboardData copyData;
    static Clipboard clipboard;
    static Map<Integer, Integer> columnLengths = new HashMap<Integer, Integer>();

    public Copy()
    {}

    private static final class CopyToClipboardData
    {
        Control control;
        private Item[] selection;
        Display display;

        private CopyToClipboardData(Control control, Item[] selection)
        {
            this.control = control;
            this.display = control.getDisplay();
            this.selection = selection;
        }

        private Item[] getSelection()
        {
            if (selection != null && selection.length != 0)
                return selection;
            return null;
        }
    }

    public static void copyToClipboard(Control control)
    {
        copyData = new CopyToClipboardData(control, getSelection(control));
        doCopy(copyData, null);
    }

    public static void exportToTxtFile(Control control, String fileName)
    {
        copyData = new CopyToClipboardData(control, null);
        try
        {
            PrintWriter writer = new PrintWriter(new FileWriter(fileName));
            doCopy(copyData, writer);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error in export to .txt: data type not supported for export.", e);
        }
    }

    private static void doCopy(CopyToClipboardData data, PrintWriter writer)
    {
        StringBuffer resultBuffer = new StringBuffer();
        StringBuffer rowBuffer = new StringBuffer();
        int numberOfColumns = getColumnCount();

        Object[] items;
        if (copyData.getSelection() != null)
            items = copyData.getSelection();
        else if (copyData.control instanceof Table)
            items = ((Table) copyData.control).getItems();
        else
            // if (copyData.control instanceof Tree)
            items = ((Tree) copyData.control).getItems();

        Item[] columns = getColumns(copyData.control);

        if (numberOfColumns == 0)
        {
            resultBuffer = copySimpleStructure(items, resultBuffer);
        }
        else
        {
            // find the length of column 0 by running through all the
            // entries and finding the longest one
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++)
            {
                getColumnLength(items, columns, columnIndex);
            }

            // add column names to the result buffer
            for (int i = 0; i < numberOfColumns; i++)
            {
                if (i == 0)
                    resultBuffer.append(align(columns[i].getText(), true, columnLengths.get(0)));
                else
                    resultBuffer.append("|" + align(columns[i].getText(), false, columnLengths.get(i))); //$NON-NLS-1$ 
            }
            resultBuffer.append("\r\n"); //$NON-NLS-1$

            for (Object item : items)
            {
                boolean addLineBreak = true;
                for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++)
                {
                    String value = "";
                    if (item instanceof TableItem)
                    {
                        value = ((TableItem) item).getText(columnIndex);
                    }
                    else if ((item instanceof TreeItem) && toPrint((TreeItem) item))
                    {
                        value = ((TreeItem) item).getText(columnIndex);
                    }
                    else if ((item instanceof TreeItem) && !toPrint((TreeItem) item))
                    {
                        addLineBreak = false;
                        break;
                    }
                    for (String filterName : Filter.FILTER_TYPES)
                    {
                        if (value.equals(filterName))
                            value = "";
                    }

                    if (columnIndex == 0)
                        rowBuffer.append(align(value, true, columnLengths.get(0)));
                    else
                        rowBuffer.append("|" + align(value, false, columnLengths.get(columnIndex)));

                }
                if (addLineBreak)
                    rowBuffer.append("\r\n");//$NON-NLS-1$
                if (item instanceof TreeItem && ((TreeItem) item).getExpanded() && toPrint((TreeItem) item))
                {
                    addNextLineToClipboard(new StringBuilder(), (TreeItem) item, rowBuffer, numberOfColumns,
                                    columnLengths.get(0));
                }
            }

            String dashedLine = getDashedLine(numberOfColumns);
            columnLengths.clear();
            resultBuffer.append(dashedLine + "\r\n"); //$NON-NLS-1$
            resultBuffer.append(rowBuffer);
            resultBuffer.append(dashedLine + "\r\n"); //$NON-NLS-1$
        }
        if (writer != null)
        {
            writer.append(resultBuffer.toString());
            writer.flush();
            writer.close();
        }
        else
        {
            TextTransfer textTransfer = TextTransfer.getInstance();
            Transfer[] transfers = new Transfer[] { textTransfer };
            Object[] dataToCopy = new Object[] { resultBuffer.toString() };
            if (clipboard == null || clipboard.isDisposed())
                clipboard = new Clipboard(copyData.display);
            clipboard.setContents(dataToCopy, transfers);
            clipboard.dispose();
        }

    }

    private static StringBuffer copySimpleStructure(Object[] items, StringBuffer resultBuffer)
    {
        for (Object item : items)
        {
            boolean addLineBreak = true;

            String value = "";
            if (item instanceof TableItem)
            {
                value = ((TableItem) item).getText();
            }
            else if ((item instanceof TreeItem) && toPrint((TreeItem) item))
            {
                value = ((TreeItem) item).getText();
            }
            else if ((item instanceof TreeItem) && !toPrint((TreeItem) item))
            {
                addLineBreak = false;
                continue;
            }

            resultBuffer.append(align(value, true, value.length()));
            if (addLineBreak)
                resultBuffer.append("\r\n");//$NON-NLS-1$

            if (item instanceof TreeItem && ((TreeItem) item).getExpanded() && toPrint((TreeItem) item))
            {
                addNextLineToClipboard(new StringBuilder(), (TreeItem) item, resultBuffer, 0, ((TreeItem) item)
                                .getText().length());
            }
        }
        return resultBuffer;
    }

    private static void addNextLineToClipboard(StringBuilder level, TreeItem item, StringBuffer rowBuffer,
                    int numberOfColumns, int length)
    {

        TreeItem[] children = item.getItems();
        for (int j = 0; j < children.length; j++)
        {
            level = getLevel(level, children.length, j);
            Item[] selection = copyData.getSelection();
            if (selection == null || (selection != null && !skip(children[j])))
            {
                if (numberOfColumns == 0)
                {
                    rowBuffer.append(level + align(children[j].getText(), true, length - level.length()));
                }
                else
                {
                    for (int i = 0; i < numberOfColumns; i++)
                    {
                        if (i == 0)
                            rowBuffer.append(level + align(children[j].getText(i), true, length - level.length()));
                        else
                            rowBuffer.append("|" + align(children[j].getText(i), false, columnLengths.get(i))); //$NON-NLS-1$ 

                    }
                }
                rowBuffer.append("\r\n"); //$NON-NLS-1$

                if (children[j].getExpanded())
                {
                    addNextLineToClipboard(level, children[j], rowBuffer, numberOfColumns, length);
                }

                if (level.length() >= 3)
                    level.delete(level.length() - 3, level.length());
            }
        }
    }

    private static Item[] getColumns(Control control)
    {
        if (control instanceof Table)
            return ((Table) control).getColumns();
        else
            // Tree
            return ((Tree) control).getColumns();
    }

    private static int getColumnCount()
    {
        if (copyData.control instanceof Table)
            return ((Table) copyData.control).getColumnCount();
        else if (copyData.control instanceof Tree)
            return ((Tree) copyData.control).getColumnCount();
        else
            return 0;
    }

    private static String align(String s, boolean left, int length)
    {
        StringBuffer buf = new StringBuffer(length);
        if (s != null)
        {
            if (s.length() > length)
                return s;

            int blanks = length - s.length();
            if (left)
            {
                buf.append(s);
            }

            for (int i = 0; i < blanks; i++)
                buf.append(' ');
            if (!left)
            {
                // always add a space in the beginning of the line for right
                // alignment
                buf.append(' ');
                buf.append(s);
                buf.append(' ');
            }
        }
        else
        {
            for (int i = 0; i < length; i++)
                buf.append(' ');
            if (!left)
                buf.append(' ').append(' ');
        }
        return buf.toString();
    }

    private static String getDashedLine(int numberOfColumns)
    {
        StringBuffer dashes = new StringBuffer();
        int dashesLength = 0;
        for (int i = 0; i < numberOfColumns; i++)
            dashesLength = dashesLength + columnLengths.get(i);

        // length of all the columns included empty spaces
        for (int i = 0; i < dashesLength + numberOfColumns * 2; i++)
            dashes.append('-'); //$NON-NLS-1$
        return dashes.toString();
    }

    private static int getColumnLength(Object[] items, Item[] columns, int columnNumber)
    {
        int lengthToCompare = 0;
        String header = columns[columnNumber].getText();
        int length = header != null ? header.length() : 0;

        for (int i = 0; i < items.length; i++)
        {
            if (items[i] instanceof TableItem)
                lengthToCompare = ((TableItem) items[i]).getText(columnNumber).length();

            else
                lengthToCompare = ((TreeItem) items[i]).getText(columnNumber).length();

            if (lengthToCompare > length)
                length = lengthToCompare;

            if (items[i] instanceof TreeItem && ((TreeItem) items[i]).getExpanded())
            {
                if (columnNumber == 0)
                    lengthToCompare = compare((TreeItem) items[i], length, new StringBuilder());
                else
                    lengthToCompare = getOtherColumnLength((TreeItem) items[i], length, columnNumber);

            }
            if (lengthToCompare > length)
                length = lengthToCompare;
        }

        if (!columnLengths.containsKey(columnNumber)
                        || (columnLengths.containsKey(columnNumber) && columnLengths.get(columnNumber) < length))
            columnLengths.put(columnNumber, length);
        return length;
    }

    private static int getOtherColumnLength(TreeItem item, int length, int columnNumber)
    {
        int lengthToCompare = 0;
        TreeItem[] children = item.getItems();
        for (int i = 0; i < children.length; i++)
        {
            if (copyData.getSelection() != null && skip(children[i]))
                continue;
            String columnText = children[i].getText(columnNumber);
            if (columnText != null)
                lengthToCompare = columnText.length();
            if (lengthToCompare > length)
                length = lengthToCompare;

            if (children[i].getExpanded())
            {
                lengthToCompare = getOtherColumnLength(children[i], length, columnNumber);
                if (lengthToCompare > length)
                    length = lengthToCompare;
            }

        }
        return length;
    }

    private static int compare(TreeItem item, int length, StringBuilder level)
    {
        int lengthToCompare = 0;
        TreeItem[] children = item.getItems();
        for (int i = 0; i < children.length; i++)
        {
            if (copyData.getSelection() != null && skip(children[i]))
                continue;
            level = getLevel(level, children.length, i);
            lengthToCompare = children[i].getText(0).length() + level.length();
            if (lengthToCompare > length)
                length = lengthToCompare;

            if (children[i].getExpanded())
            {
                lengthToCompare = compare(children[i], length, level);
                if (lengthToCompare > length)
                    length = lengthToCompare;
            }
            if (level.length() >= 3)
                level.delete(level.length() - 3, level.length());

        }
        return length;
    }

    private static boolean toPrint(TreeItem item)
    {
        TreeItem[] selection = (TreeItem[]) copyData.getSelection();
        if (selection == null)
            return true;
        for (TreeItem treeItem : selection)
        {
            if (treeItem.equals(item.getParentItem()))
                return false;
        }
        return true;
    }

    private static boolean skip(TreeItem item)
    {
        TreeItem[] selection = (TreeItem[]) copyData.getSelection();
        for (TreeItem treeItem : selection)
        {
            if (treeItem.equals(item))
                return false;
        }
        return true;
    }

    private static StringBuilder getLevel(StringBuilder level, int length, int counter)
    {
        int k = level.indexOf("'-"); //$NON-NLS-1$
        if (k != -1)
        {
            level.replace(k, k + 2, "  "); //$NON-NLS-1$
        }
        else
        {
            k = level.indexOf("|-"); //$NON-NLS-1$
            if (k != -1)
            {
                level.replace(k + 1, k + 2, " "); //$NON-NLS-1$
            }
        }
        if (counter == length - 1)
        {
            level.append('\'');
        }
        else
        {
            level.append('|');
        }
        level.append("- "); //$NON-NLS-1$
        return level;
    }

    private static Item[] getSelection(Control control)
    {
        if (control instanceof Table)
            return ((Table) control).getSelection();
        else
            // Tree
            return ((Tree) control).getSelection();
    }
}
