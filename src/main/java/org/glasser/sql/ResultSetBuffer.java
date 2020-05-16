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
package org.glasser.sql;


import java.util.*;
import java.sql.*;
import org.glasser.util.*;
import org.glasser.util.comparators.MutableListComparator;


public class ResultSetBuffer extends Vector<List<Object>> {


    public static boolean debug = System.getProperty("ResultSetBuffer.debug") != null;

    protected final static int DEFAULT_READAHEAD = 200;

    protected int readAhead = DEFAULT_READAHEAD;

    protected int columnCount = 0;

    protected ResultSet resultSet = null;

    protected Connection conn = null;

    protected boolean endOfResultsReached = false;

    protected SmartEventListenerList listeners = new SmartEventListenerList();

    protected String[] columnNames = null;

    protected Class[] columnClasses = null;

    protected int[] columnTypes = null;

    protected String[] nonDisplayableColumnStrings = null;

    protected int cursor = -1;

    protected boolean readAheadEnabled = true;

    protected MutableListComparator<Object> comparator = new MutableListComparator<>();

    /**
     * This is the index of the last column on which the buffer was sorted. 
     * Its value is -1 if the buffer has not yet been sorted.
     */
    protected int sortColumn  = -1;


    /**
     * This indicates whether the last sort applied to the buffer was in 
     * descending order or not.
     */
    protected boolean descendingSort = false;

    public void replaceCurrentRow(ResultSet rs) 
        throws SQLException
    {
        List<Object> newRow = readRow(rs);
        set(cursor, newRow);
    }

    /**
     * Constructs an empty ResultSetBuffer.
     */
    public ResultSetBuffer(Column[] columns, Class[] columnClasses) {
        columnCount = columns.length;
        columnNames = new String[columnCount];
        columnTypes = new int[columnCount];
        this.columnClasses = columnClasses;
        nonDisplayableColumnStrings = new String[columnCount];
        for(int j=0; j<columnCount; j++) {
            Column column = columns[j];
            columnNames[j] = column.getColumnName();
            columnTypes[j] = column.getDataType();
            if(false == DBUtil.isDisplayableType(columnTypes[j])) {
                String columnTypeName = column.getTypeName();
                nonDisplayableColumnStrings[j] = "<" + columnTypeName + ">";
            }
        }

        endOfResultsReached = true;
        readAheadEnabled = false;
    }


    /**
     * Constructs an empty ResultSetBuffer.
     */
    public ResultSetBuffer(Column[] columns) {
        this(columns, null);
        columnClasses = new Class[columnCount];
        for(int j=0; j<columnCount; j++) {
            columnClasses[j] = this.getClassForSqlType(columnTypes[j]);
        }
    }

    public ResultSetBuffer(ResultSet rs, int readAhead) 
        throws SQLException
    {
        super(readAhead * 2, readAhead);
        if(readAhead < 1) {
            throw new IllegalArgumentException("ResultSetBuffer(ResultSet rs, int readAhead): readAhead argument must be 1 or greater.");
        }
        this.readAhead = readAhead;
        this.resultSet = rs;
        ResultSetMetaData rsmd = resultSet.getMetaData();
        columnCount = rsmd.getColumnCount();
        columnNames = new String[columnCount];
        columnTypes = new int[columnCount];
        columnClasses = new Class[columnCount];
        nonDisplayableColumnStrings = new String[columnCount];
        for(int j=0; j<columnCount; j++) {
            columnNames[j] = rsmd.getColumnName(j+1);
            try {

                columnTypes[j] = rsmd.getColumnType(j+1);

                // get the Class name for this column.
                String columnClassName = null;
                try {
                    columnClassName = rsmd.getColumnClassName(j+1);
                    if(debug) System.out.println("columnClassName for " + columnNames[j] + " is " + columnClassName);
                }
                catch(SQLException sqlex) {
                    System.out.println("WARNING: ResultSetMetaData.getColumnClassName() failed for column " + (j+1) + ", SQL Type = " + columnTypes[j] + ": " + sqlex);
                    columnClassName = this.getClassForSqlType(columnTypes[j]).getName();
                }

                // now get the Class for this column
                boolean classNotFound = false; 
                try {
                    columnClasses[j] = Class.forName(columnClassName);
                    if(debug) System.out.println("column class for " + columnNames[j] + " found: " + columnClasses[j].getName());
                }
                catch(Exception ex) {
                    System.err.println("column class ("
                        + columnClassName + ") for " + columnNames[j] + " NOT FOUND.");
                    if(DBUtil.isDisplayableType(columnTypes[j])) {
                        columnClasses[j] = getClassForSqlType(columnTypes[j]);
                    }
                    else {
                        classNotFound = true;
                    }
                };

                // handle non-displayable columns
                if(classNotFound || false == DBUtil.isDisplayableType(columnTypes[j]) || columnClasses[j].isArray()) {

                    String columnTypeName = rsmd.getColumnTypeName(j+1);

                    // use String.class as the column class, because non-displayable values are not stored in the buffer,
                    // only their nonDisplayableColumnString, to indicate that the column is non-null for
                    // that row.
                    columnClasses[j] = java.lang.String.class;
                    nonDisplayableColumnStrings[j] = "<" + columnTypeName + ">";
                }

            }
            catch(Exception ex) {
                ex.printStackTrace();
                columnClasses[j] = Object.class;
            }
        }
        readMoreRows(2*readAhead);
        if(size() > 0) cursor = 0;

    }

    public void addResultSetBufferListener(ResultSetBufferListener listener) {
        listeners.add(ResultSetBufferListener.class, listener);
    }

    public void removeResultSetBufferListener(ResultSetBufferListener listener) {
        listeners.remove(ResultSetBufferListener.class, listener);
    }

    public void setReadAheadEnabled(boolean b) {
        this.readAheadEnabled = b;
    }

    public boolean isReadAheadEnabled() {
        return readAheadEnabled;
    }

    public ResultSetBuffer(ResultSet rs) 
        throws SQLException
    {
        this(rs, DEFAULT_READAHEAD);
    }

    public String[] getColumnNames() {
        return (String[]) columnNames.clone();
    }

    public Class[] getColumnClasses() {
        return columnClasses;
    }


    public boolean isEndOfResultsReached() {
        return endOfResultsReached;
    }


    private void readMoreRows() 
        throws SQLException
    {
        readMoreRows(readAhead);
    }


    private synchronized int readMoreRows(int numRowsToRead) 
        throws SQLException
    {

        // when the ResultSetBuffer is "locked", 
        // then no more rows will be read from the resultset.
        if(readAheadEnabled == false) return 0;

        if(endOfResultsReached) return 0;
        double begin = System.currentTimeMillis();
        int j=0, rowsRead = 0;
        for(j=0; j<numRowsToRead; j++) {
            if(resultSet.next()) {
                super.add(readRow(resultSet));
                rowsRead++;
            }
            else {
                endOfResultsReached = true;
                break;
            }
        }
        double end = System.currentTimeMillis();
        if(debug) System.out.println("Reading ahead " + j + " records took " + ((end - begin) / 1000.0d) + " seconds.");
        if(rowsRead > 0) {
            ResultSetBufferEvent e = new ResultSetBufferEvent(this, ResultSetBufferEvent.MORE_ROWS_READ);
            listeners.fireSmartEvent(e);
        }
        if(endOfResultsReached) {
            ResultSetBufferEvent e = new ResultSetBufferEvent(this, ResultSetBufferEvent.END_OF_RESULTS_REACHED);
            listeners.fireSmartEvent(e);
        }

        return rowsRead;
    }

    protected final List<Object> readRow(ResultSet rs)
        throws SQLException
    {
        Vector<Object> row = new Vector<>(columnCount);
        for(int col=1; col<=columnCount; col++) {
            Object val = null;
            try {
            val = rs.getObject(col);
            }
            catch(SQLException ex) {
                System.out.println("COL: " + col);
                ex.printStackTrace();
                val = null;
//                throw ex;
            }
                
            if(nonDisplayableColumnStrings[col-1] != null && val != null) {
                row.add(nonDisplayableColumnStrings[col-1]);
            }
            else {
                row.add(val);
            }
        }
        return row;
    }


    
    public List<Object> getPreviousRow() {
        if(cursor < 1) return null;
        cursor--;
        return get(cursor);
    }

    public List<Object> getCurrentRow() {
        if(cursor < 0) return null;
        return get(cursor);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getCurrentRowClone() {
        List<Object> v = getCurrentRow();
        if(v == null) return null;
        return (List<Object>) ((Vector<Object>)v).clone();
    }

    /**
     * Removes the row at the cursor and repositions the cursor
     * if necessary. If the buffer is empty after the remove, the cursor
     * is set at -1.
     */
    public void removeCurrentRow() 
        throws SQLException
    {
        if(cursor < 0) return;
        remove(cursor);

        // if the cursor is beyond the end of the buffer, attempt
        // to read more rows
        if(cursor >= size()) {
            readMoreRows();
            if(cursor >= size()) { 
                // if cursor is still beyond the end of the buffer, it means
                // no more rows could be read, so move the cursor back one position.
                // if the buffer is now empty, it means the row at position 0 was
                // removed and the cursor will now be at -1.
                cursor = size() - 1;
            }
        }
    }


    public List<Object> getNextRow() {
        //System.out.println("getNextRow - cursor = " + cursor + "  size = " + size());
        if(endOfResultsReached && (cursor >= (size()-1))) return null;
        int temp = cursor;
        try {
            cursor++;
            return getRowAt(cursor);
        }
        catch(RuntimeException ex) {
            cursor = temp;
            throw ex;
        }
    }

    public List<Object> getRowAt(int row) {
//        System.out.println("getRowAt(" + row + ")");
        if(endOfResultsReached == false && (size() - readAhead) <= row) {
            try {
                readMoreRows();
            }
            catch(SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException("An SQLException occurred in ResultSetBuffer.getRowAt("
                    + row + ")");
            }
        }
        return super.get(row);
    }

    public List<Object> get(int index) {
        return getRowAt(index);
    }

    public List<Object> elementAt(int index) {
        return getRowAt(index);
    }


    public List<Object> getFirstRow() {
        if(isEmpty()) return null;
        cursor = 0;
        return get(cursor);
    }

    public boolean isAtBeginning() {
        return (cursor == 0);
    }

    public boolean isAtEnd() {
        return (cursor >= size() - 1);
    }

    public int getCursor() {
        return cursor;
    }

    @SuppressWarnings("unchecked")
    public List<Object>[] getCurrentRowset() {
        return (List<Object>[]) toArray(new Vector[0]);
    }

    public void setCursor(int value) {
        if(value > size() - 1) {
            new Throwable().printStackTrace();
            throw new ArrayIndexOutOfBoundsException("ResultSetBuffer.setCursor(): Cursor value "
                + value + " is out of range. (Size is " + size() + ".)");
        }
        cursor = value;
    }

    /**
     * This method can be called with values that are greater than the current
     * number of records in the buffer. If the value is out of range, it will
     * attempt to read in enough records from the ResultSet so that the cursor
     * can be set to the new value. If it is successful, true is returned, if
     * not, false is returned.
     */
    public synchronized boolean maybeSetCursor(int value) 
    {
        int size = size();
        if(value < size) {
            cursor = value;
            return true;
        }
        else if(endOfResultsReached) {
            return false;
        }

        // at this point we know there are unread rows in
        // the resultset, so calculate how many we'll need 
        // to read in.

        int rowsNeeded = value - size + 1;

        // attempt to read in some multiple of the "readAhead" value
        // that will bring in the needed number of rows, while keeping
        // the cursor location more than "readAhead" rows from
        // the end of the buffer.

        int multiple = (rowsNeeded / readAhead) + 2;
        int rowsToRead = multiple * readAhead;

        try {
            int rowsRead = readMoreRows(rowsToRead);
            if(rowsRead >= rowsNeeded) {
                cursor = value;
                return true;
            }
            else {
                return false;
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("An SQLException occurred in ResultSetBuffer.maybeSetCusror("
                + value + ")");
        }
    }

    public void sort(int columnIndex, boolean sortDescending) {

        if(debug) System.out.println("sort(" + columnIndex + ", " + sortDescending + ")");

        boolean saveReadAheadEnabled = readAheadEnabled;

        try {
            
            setReadAheadEnabled(false);
            this.comparator.setElementIndex(columnIndex);
            this.comparator.setSortDescending(sortDescending);

            
            List<? extends Object> selectedRow = null;
            if(cursor > -1) { 
                selectedRow = this.getRowAt(cursor);
                if(debug) {
                    System.out.println("Before sort, selected row is " + cursor);
                }

            }
            Collections.sort(this, comparator);

            if(selectedRow != null) {
                int newCursor = indexOf(selectedRow);
                if(newCursor > -1) {
                    cursor = newCursor;
                    if(debug) {
                        System.out.println("After sort, selected row is " + cursor);
                    }
                }
            }

            // remember the parameters of this sort
            sortColumn = columnIndex;
            descendingSort = sortDescending;

            

            // notify listeners.
            ResultSetBufferEvent e = new ResultSetBufferEvent(this, ResultSetBufferEvent.BUFFER_SORTED);
            listeners.fireSmartEvent(e);
        }
        finally {
            setReadAheadEnabled(saveReadAheadEnabled);
        }
    }


    /**
     * Sorts the buffer on the indicated column, and returns true if the sort was in
     * descending order, or false if it was in ascending order. The order
     * will usually be ascending, unless the same column is sorted multiple times
     * consecutively, in which case the sort order is reversed each time.
     */
    public boolean sort(int columnIndex) {
        boolean desc = false;

        // if the column to be sorted is the same as the last column
        // that was sorted, then reverse the last sort order.
        if(this.sortColumn == columnIndex) desc = !descendingSort;

        sort(columnIndex, desc);

        return desc;

    }

    /**
     * This is called when the Java Class corresponding to a particular columns SQL type
     * cannot be determined through ResultSetMetaData.getColumnClassName(). If the general types
     * returned by this method (String, Number, java.util.Date and Object) are used with
     * actual data, for instance, by a TableModel, then problems could occur if the
     * types are not actually correct.
     */
    private Class getClassForSqlType(int sqlType) {
        if(DBUtil.isCharType(sqlType)) return java.lang.String.class;
        else if(DBUtil.isNumericType(sqlType)) return java.lang.Number.class;
        else if(DBUtil.isDateTimeType(sqlType)) return java.util.Date.class;
        else return Object.class;
    }

}




