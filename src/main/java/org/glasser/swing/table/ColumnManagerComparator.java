/* ====================================================================
 * The QueryForm License, Version 1.1
 *
 * Copyright (c) 1998 - 2003 David F. Glasser.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by 
 *        David F. Glasser."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "QueryForm" and "David F. Glasser" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact dglasser@pobox.com.
 *
 * 5. Products derived from this software may not be called "QueryForm",
 *    nor may "QueryForm" appear in their name, without prior written
 *    permission of David F. Glasser.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL DAVID F. GLASSER, THE APACHE SOFTWARE 
 * FOUNDATION OR ITS CONTRIBUTORS, OR ANY AUTHORS OR DISTRIBUTORS
 * OF THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 *
 * ==================================================================== 
 *
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/table/ColumnManagerComparator.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2003/07/16 02:32:47 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing.table;



import org.glasser.util.comparators.BaseComparator;
import java.util.Comparator;

/**
* This comparator is used by a ListTableModel to sort its rows on a particular
* column. It uses the ColumnManager that has been set for the ListTableModel
* to extract the column values for each of two given rows, and then compares those
* values to establish an ordering for the rows.<p>
* 
* <strong>Objects of this class are not thread-safe, however, as used by the ListTableModel
* in a single-threaded GUI, it should not be a problem.</strong>
* 
* @author Dave Glasser
*/
public class ColumnManagerComparator extends BaseComparator {


    /**
     * This is the column manager used to extract column values from row objects.
     */
    protected ColumnManager columnManager = null;

    protected int sortColumn = -1;

    protected Comparator[] columnComparators = null;

    public ColumnManagerComparator(ColumnManager columnManager) {
        this.columnManager = columnManager;
    }


    /**
     * This is the column index on which two row objects will be compared.
     */
    public void setSortColumn(int sortColumn) {
        this.sortColumn = sortColumn;
    }

    /**
     * Returns the column index which is used to compare two row objects.
     */
    public int getSortColumn() {
        return sortColumn;
    }


    public void setColumnComparator(int columnIndex, Comparator comparator) {
        if(columnComparators == null) {
            columnComparators = new Comparator[columnIndex + 20]; // leave some room for growth
        } else if(columnComparators.length <= columnIndex) {
            Comparator[] temp = new Comparator[columnIndex + 20];
            System.arraycopy(columnComparators, 0, temp, 0, columnComparators.length);
        }

        columnComparators[columnIndex] = comparator;
    }

    /**
     * The two objects passed to this method are rows in a ListTableModel. The ColumnManager
     * is used to get the correct column value from each row object, and compares the two
     * column values to establish an ordering for the two rows.
     */
    protected int doCompare(Object o1, Object o2) {
        Object val1 = columnManager.getValueAt(0, sortColumn, o1);
        Object val2 = columnManager.getValueAt(0, sortColumn, o2);

        if(val1 == null && val2 == null) return 0;

        int retVal = compareForNulls(val1, val2);

        if(retVal != 0) return retVal;


        if(columnComparators != null && columnComparators[sortColumn] != null) {
            return columnComparators[sortColumn].compare(val1, val2);
        }

        if(val1 instanceof Comparable && val2 instanceof Comparable) {
            return((Comparable) val1).compareTo((Comparable) val2);
        }

        return val1.toString().compareTo(val2.toString());


    }

    /**
     * Sets the value of the nullIsGreater field, which determines whether
     * null objects are considered "greater than" or "less than" non-null objects
     * for sorting purposes. The default is false.
     */
    protected void setNullIsGreater(boolean nullIsGreater) {
        super.setNullIsGreater(nullIsGreater);
    }
    /**
     * Sets the value of the sortDescending field. If true, the sign of the
     * value of the compare() method is "flipped" before it's returned to
     * the caller, which will cause collections of objects to be sorted
     * in reverse order.
     */
    protected void setSortDescending(boolean sortDescending) {
        super.setSortDescending(sortDescending);
    }


}
