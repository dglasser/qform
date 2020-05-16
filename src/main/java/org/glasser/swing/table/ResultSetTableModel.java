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
 */
package org.glasser.swing.table;

import org.glasser.sql.*;
import java.util.*;
import org.glasser.util.*;


public class ResultSetTableModel extends ListTableModel<List<Object>> implements ResultSetBufferListener {


    public ResultSetTableModel(ResultSetBuffer buffer) {
        super(new ListColumnManager<Object>(buffer.getColumnNames(), buffer.getColumnClasses()), buffer);
        buffer.addResultSetBufferListener(this);
    }


    /**
     * This constructor allows an instance to be constructed before any query
     * results are available to display.
     */
    public ResultSetTableModel(String[] columnNames) {
        super();
        super.setColumnManager(new ListColumnManager<Object>(columnNames, null));
    }

    public void setColumnManager(ColumnManager<List<Object>> columnManager) {
        throw new UnsupportedOperationException("The ColumnManager cannot be set for this TableModel.");
    }


    private boolean[] displayableColumns = null;


    public void setDataList(List<List<Object>> newData) {
//        if(newData instanceof ResultSetBuffer == false) {
//            throw new IllegalArgumentException("newData argument must be a ResultSetBuffer.");
//        }

        List<List<Object>> currentBuffer = (List<List<Object>>) this.getDataList();
        if(currentBuffer != null && currentBuffer instanceof ResultSetBuffer) {
            ((ResultSetBuffer) currentBuffer).removeResultSetBufferListener(this);
        }
        if(newData == null) {
            super.setDataList(null);
            return;
        }
        ResultSetBuffer buffer = (ResultSetBuffer) newData;
        buffer.addResultSetBufferListener(this);
        
        ListColumnManager listColumnManager = (ListColumnManager) super.getColumnManager();

        if(listColumnManager != null) {
            listColumnManager.setColumnNames(buffer.getColumnNames());
            listColumnManager.setColumnClasses(buffer.getColumnClasses());
        }
        super.setDataList(newData);

    }

    protected ResultSetBuffer getResultSetBuffer() {
        List list = this.getDataList();
        if(list instanceof ResultSetBuffer) {
            return (ResultSetBuffer) list;
        }
        else {
            return null;
        }

    }


    /**
     * Sorts the model's contents on the given column.
     * 
     * @param columnIndex the zero-based index of the column on which the data
     * will be sorted.
     * 
     * @param sortDescending if true, the data is sorted in descending order, otherwise
     * it will be sorted in ascending order.
     */
    public void sort(int columnIndex, boolean sortDescending) {
        ResultSetBuffer buffer = getResultSetBuffer();
        if(buffer == null) return;
        buffer.sort(this.getMappedColumnIndex(columnIndex), sortDescending);
    }

    /**
     * Sorts the buffer on the indicated column, and returns true if the sort was in
     * descending order, or false if it was in ascending order. The order
     * will usually be ascending, unless the same column is sorted multiple times
     * consecutively, in which case the sort order is reversed each time.
     */
    public boolean sort(int columnIndex) {
        ResultSetBuffer buffer = getResultSetBuffer();
        if(buffer == null) return false;
        return buffer.sort(getMappedColumnIndex(columnIndex));
    }


    public void moreRowsRead(ResultSetBufferEvent e) {
        fireTableDataChanged();
    }

    public void endOfResultsReached(ResultSetBufferEvent e) {

    }

    public void bufferSorted(ResultSetBufferEvent e) {
        fireTableDataChanged();
    }
}
