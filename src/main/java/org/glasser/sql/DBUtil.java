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
 * $Source: /cvsroot/qform/qform/src/org/glasser/sql/DBUtil.java,v $
 * $Revision: 1.3 $
 * $Author: dglasser $
 * $Date: 2005/04/29 05:02:45 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.sql;


import java.sql.*;
import java.util.*;


public class DBUtil implements java.io.Serializable {


    public static class COLUMN {


        public final static int TABLE_CAT = 1;
        public final static int TABLE_SCHEM = 2;
        public final static int TABLE_NAME = 3;
        public final static int COLUMN_NAME = 4;
        public final static int DATA_TYPE = 5;
        public final static int TYPE_NAME = 6;
        public final static int COLUMN_SIZE = 7;
        public final static int BUFFER_LENGTH = 8;
        public final static int DECIMAL_DIGITS = 9;
        public final static int NUM_PREC_RADIX = 10;
        public final static int NULLABLE = 11;
        public final static int REMARKS = 12;
        public final static int COLUMN_DEF = 13;
        public final static int SQL_DATA_TYPE = 14;
        public final static int SQL_DATETIME_SUB = 15;
        public final static int CHAR_OCTET_LENGTH = 16;
        public final static int ORDINAL_POSITION = 17;
        public final static int IS_NULLABLE = 18;

    }

    public static Column[] getColumns(ResultSet rs) 
        throws SQLException
    {
        ArrayList<Column> columns = new ArrayList<>();

        while(rs.next()) {

            Column col = new Column();
            col.setTableCatalog(rs.getString(COLUMN.TABLE_CAT));
            col.setTableSchema(rs.getString(COLUMN.TABLE_SCHEM));
            col.setTableName(rs.getString(COLUMN.TABLE_NAME));
            col.setColumnName(rs.getString(COLUMN.COLUMN_NAME));
            col.setDataType(rs.getInt(COLUMN.DATA_TYPE));
            col.setTypeName(rs.getString(COLUMN.TYPE_NAME));
            col.setColumnSize(rs.getInt(COLUMN.COLUMN_SIZE));
            col.setDecimalDigits(rs.getInt(COLUMN.DECIMAL_DIGITS));
            col.setRadix(rs.getInt(COLUMN.NUM_PREC_RADIX));
            col.setNullable(DatabaseMetaData.columnNoNulls != rs.getInt(COLUMN.NULLABLE));
            col.setRemarks(rs.getString(COLUMN.REMARKS));
            col.setColumnDefaultValue(rs.getString(COLUMN.COLUMN_DEF));
            col.setOrdinal(rs.getInt(COLUMN.ORDINAL_POSITION));
            columns.add(col);
        }


        return columns.toArray(new Column[columns.size()]);

    }


//  1  TABLE_CAT String => table catalog (may be null) 
//  2  TABLE_SCHEM String => table schema (may be null) 
//  3  TABLE_NAME String => table name 
//  4  TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". 
//  5  REMARKS String => explanatory comment on the table 
//  6  TYPE_CAT String => the types catalog (may be null) 
//  7  TYPE_SCHEM String => the types schema (may be null) 
//  8  TYPE_NAME String => type name (may be null) 
//  9  SELF_REFERENCING_COL_NAME String => name of the designated "identifier" column of a typed table (may be null) 
//  10  REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null) 
//

    public static TableInfo[] getTableInfos(ResultSet rs)
        throws SQLException
    {

        ArrayList<TableInfo> list = new ArrayList<>(100);
        while(rs.next()) {
            TableInfo ti = new TableInfo();
            ti.setTableCat(rs.getString("TABLE_CAT"));
            ti.setTableSchem(rs.getString(2));
            ti.setTableName(rs.getString(3));
            ti.setTableType(rs.getString(4));
            ti.setRemarks(rs.getString(5));
            list.add(ti);
        }

        return list.toArray(new TableInfo[list.size()]);

    }


    public static HashMap getTableInfoLists(TableInfo[] tables, String defaultSchemaString) {

        // first sort the list of tables by their name so the individual
        // lists will be sorted.
//        Arrays.sort(tables, TableInfo.NAME_COMPARATOR);

        HashMap map = new HashMap();

        // if there are now tables in the list, we'll create
        // an empty list for the default schema
        if(tables == null || tables.length == 0) {
            map.put(defaultSchemaString, new Vector());
        }
        for(int j=0; j<tables.length; j++) {
            TableInfo ti = tables[j];
            String schema = ti.getTableSchem();
            if(schema == null) schema = defaultSchemaString;
            Vector v = (Vector) map.get(schema);
            if(v == null) {
                v = new Vector();
                map.put(schema, v);
            }
            v.add(ti);
//            System.out.println(ti.debugString());
        }

        return map;
    }


    public static class FK {
        public final static int PKTABLE_CAT = 1;
        public final static int PKTABLE_SCHEM = 2;
        public final static int PKTABLE_NAME = 3;
        public final static int PKCOLUMN_NAME = 4;
        public final static int FKTABLE_CAT = 5;
        public final static int FKTABLE_SCHEM = 6;
        public final static int FKTABLE_NAME = 7;
        public final static int FKCOLUMN_NAME = 8;
        public final static int KEY_SEQ = 9;
        public final static int UPDATE_RULE = 10;
        public final static int DELETE_RULE = 11;
        public final static int FK_NAME = 12;
        public final static int PK_NAME = 13;
        public final static int DEFERRABILITY = 14;


    }


    public static ForeignKey[] getForeignKeys(ResultSet fk) 
        throws SQLException
    {
        Hashtable fKeys = new Hashtable(); 
        while(fk != null && fk.next()) {

            // we're supposed to read the result set in column-order
            // to insure portability.
            String foreignTableSchema = fk.getString(FK.PKTABLE_SCHEM);
            String foreignTableName = fk.getString(FK.PKTABLE_NAME);
            String foreignColumnName = fk.getString(FK.PKCOLUMN_NAME);
            String localTableSchema = fk.getString(FK.FKTABLE_SCHEM);
            String localTableName = fk.getString(FK.FKTABLE_NAME);
            String localColumnName = fk.getString(FK.FKCOLUMN_NAME);
            int keySeq = fk.getInt(FK.KEY_SEQ);
            String fkName = fk.getString(FK.FK_NAME);


            // the actual name of the foreign key ("FK_NAME") is the better item to use as a hashkey,
            // however, the API docs for getImportedKeys() says that might be null.
            // if it is null, we'll use the foreignTableName, which might produce incorrect results
            // when a table has more than one foreign key into another table.
            String hashkey = fkName;
            if(fkName == null) hashkey = foreignTableName;

            // see if we've already found a component of this table's key.
            ForeignKey fkey = (ForeignKey) fKeys.get(hashkey);

            // if not, create a new ForeignKey object and add it to the
            // hashtable
            if(fkey == null) {
                fkey = new ForeignKey(foreignTableName);
                fkey.setForeignKeyName(fkName);
                fkey.setLocalTableSchema(localTableSchema);
                fkey.setLocalTableName(localTableName);
                fkey.setForeignTableSchema(foreignTableSchema);
                fkey.setForeignTableName(foreignTableName);
                fKeys.put(hashkey, fkey);
            }

            // now create a ForeignKeyColumn object for the column we're
            // examining
            ForeignKeyColumn fcol = new ForeignKeyColumn();
            fcol.setLocalTableSchema(localTableSchema);
            fcol.setLocalTableName(localTableName);
            fcol.setLocalColumnName(localColumnName);
            fcol.setForeignTableSchema(foreignTableSchema);
            fcol.setForeignTableName(foreignTableName);
            fcol.setForeignColumnName(foreignColumnName);
            
            // add it to this fk's collection of columns.
            fkey.addForeignKeyColumn(fcol);

        }


        ForeignKey[] results = new ForeignKey[fKeys.size()];
        Iterator i = fKeys.values().iterator();
        for(int j=0; j<results.length; j++)  {
            results[j] = (ForeignKey) i.next();
        }

        return results;

    }


    /**
     * This reads each row of a ResultSet and stores it in a HashMap object, with the
     * column names, force to upper case, used as keys for each value. This will break
     * if a ResultSet contains duplicate column names.
     */
    public static Map[] readResultSet(ResultSet rs) 
        throws SQLException 
    {
        return readResultSet(rs, -1);

    }


    /**
     * This reads each row of a ResultSet and stores it in a HashMap object, with the
     * column names, forced to upper case, used as keys for each value. This will break
     * if a ResultSet contains duplicate column names.
     * 
     * @param rs the ResultSet that will be read. It will NOT be closed after being read.
     * @param maxRows the maximum number of rows to read; 0 or below indicates no limit.
     */
    public static Map[] readResultSet(ResultSet rs, int maxRows) 
        throws SQLException 
    {


        // handle the trival case of an empty ResultSet quickly.
        boolean flag = rs.next();

        // if ResultSet is empty, return a zero-length array.
        if(flag == false) return new HashMap[0];

        boolean hasLimit = maxRows > 0;

        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        String[] columnNames = new String[numColumns];
        for(int j=0; j<numColumns; j++) {
            // column indexes are 1-based.
            String columnName = rsmd.getColumnName(j+1);
            if(columnName != null) columnName = columnName.toUpperCase();
            columnNames[j] = columnName;
        }

        ArrayList results = new ArrayList(40);

        // the first time the loop is entered, rs.next() won't get
        // called because the flag will short-circuit it.
        int rowsRead = 0;
        while(flag || rs.next()) {
            flag = false;

            HashMap map = new HashMap(columnNames.length + 10);

            for(int j=0; j<columnNames.length; j++) {
                map.put(columnNames[j], rs.getObject(j+1));
            }

            results.add(map);
            if(hasLimit) {
                rowsRead++;
                if(rowsRead >= maxRows) {
                    break;
                }
            }

        }

        return (HashMap[]) results.toArray(new HashMap[results.size()]);

    }

    /**
     * This reads each row of a ResultSet and stores it in an List object, with each element
     * of the list representing the corresponding column from the ResultSet row.      
     */
    public static List[] readResultSet2(ResultSet rs) 
        throws SQLException 
    {
        return readResultSet2(rs, -1);

    }


    /**
     * This reads each row of a ResultSet and stores it in an List object, with each element
     * of the list representing the corresponding column from the ResultSet row.     
     * 
     * @param rs the ResultSet that will be read. It will NOT be closed after being read.
     * @param maxRows the maximum number of rows to read; 0 or below indicates no limit.
     */
    public static List<Object>[] readResultSet2(ResultSet rs, int maxRows) 
        throws SQLException 
    {


        // handle the trival case of an empty ResultSet quickly.
        boolean flag = rs.next();

        // if ResultSet is empty, return a zero-length array.
        if(flag == false) return new ArrayList[0];

        boolean hasLimit = maxRows > 0;

        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();

        ArrayList<List<Object>> results = new ArrayList(40);

        // the first time the loop is entered, rs.next() won't get
        // called because the flag will short-circuit it.
        int rowsRead = 0;
        while(flag || rs.next()) {
            flag = false;

            ArrayList<Object> rowList = new ArrayList<>(numColumns);

            for(int j=0; j<numColumns; j++) {
                rowList.add( rs.getObject(j+1) );
            }

            results.add(rowList);
            if(hasLimit) {
                rowsRead++;
                if(rowsRead >= maxRows) {
                    break;
                }
            }

        }

        return results.toArray(new ArrayList[results.size()]);

    }


    /**
     * This reads each row of a ResultSet and stores it in an List object, with each element
     * of the list representing the corresponding column from the ResultSet row.      
     */
    public static Object[] readResultSet3(ResultSet rs) 
        throws SQLException 
    {
        return readResultSet3(rs, -1);

    }


    /**
     * This reads each row of a ResultSet and stores it in an List object, with each element
     * of the list representing the corresponding column from the ResultSet row.     
     * 
     * @param rs the ResultSet that will be read. It will NOT be closed after being read.
     * @param maxRows the maximum number of rows to read; 0 or below indicates no limit.
     */
    public static Object[] readResultSet3(ResultSet rs, int maxRows) 
        throws SQLException 
    {


        // handle the trival case of an empty ResultSet quickly.
        boolean flag = rs.next();

        // if ResultSet is empty, return a zero-length array.
        if(flag == false) return new Object[0];

        boolean hasLimit = maxRows > 0;


        ArrayList<Object> results = new ArrayList<>(40);

        // the first time the loop is entered, rs.next() won't get
        // called because the flag will short-circuit it.
        int rowsRead = 0;
        while(flag || rs.next()) {
            flag = false;
            results.add(rs.getObject(1));

            if(hasLimit) {
                rowsRead++;
                if(rowsRead >= maxRows) {
                    break;
                }
            }

        }

        return results.toArray();

    }

    public static void closeConnection(Connection conn) {
        if(conn == null) return;
        try {
            conn.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void rollback(Connection conn) {
        if(conn == null) return;
        try {
            conn.rollback();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void setAutoCommit(Connection conn, boolean autoCommit) {
        if(conn == null) return;
        try {
            conn.setAutoCommit(autoCommit);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void closeResultSet(ResultSet rs) {
        if(rs == null) return;
        try {
            rs.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void closeStatement(Statement s) {
        if(s == null) return;
        try {
            s.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }



    private static final int[] CHAR_TYPES =
    {
         java.sql.Types.CHAR
        ,java.sql.Types.VARCHAR
        ,java.sql.Types.LONGVARCHAR
    };



    private static final int[] NUMERIC_TYPES = 
    {
         java.sql.Types.BIT
        ,java.sql.Types.TINYINT
        ,java.sql.Types.SMALLINT
        ,java.sql.Types.INTEGER
        ,java.sql.Types.BIGINT
        ,java.sql.Types.FLOAT
        ,java.sql.Types.REAL
        ,java.sql.Types.DOUBLE
        ,java.sql.Types.NUMERIC
        ,java.sql.Types.DECIMAL
    };

    private final static int[] DATETIME_TYPES =
    {
         java.sql.Types.DATE
        ,java.sql.Types.TIME
        ,java.sql.Types.TIMESTAMP
    };

    private static final int[] BINARY_TYPES = 
    {
         java.sql.Types.BINARY
        ,java.sql.Types.BLOB
        ,java.sql.Types.DISTINCT
        ,java.sql.Types.JAVA_OBJECT
        ,java.sql.Types.LONGVARBINARY
        ,java.sql.Types.NULL
        ,java.sql.Types.OTHER
        ,java.sql.Types.STRUCT
        ,java.sql.Types.VARBINARY
     };

     private static final int[] DISPLAYABLE_TYPES 
         = new int[NUMERIC_TYPES.length + CHAR_TYPES.length + DATETIME_TYPES.length];
        

    
    static {

//      DISPLAYABLE_TYPES 

        System.arraycopy(NUMERIC_TYPES, 0, DISPLAYABLE_TYPES, 0, NUMERIC_TYPES.length);
        System.arraycopy(CHAR_TYPES, 0, DISPLAYABLE_TYPES, NUMERIC_TYPES.length, CHAR_TYPES.length);
        System.arraycopy(DATETIME_TYPES, 0, DISPLAYABLE_TYPES, NUMERIC_TYPES.length + CHAR_TYPES.length, DATETIME_TYPES.length);

        Arrays.sort(DISPLAYABLE_TYPES);
        Arrays.sort(NUMERIC_TYPES);
        Arrays.sort(BINARY_TYPES);
        Arrays.sort(CHAR_TYPES);
        Arrays.sort(DATETIME_TYPES);

    }

    private static boolean arrayContains(int[] array, int number) {

        // a linear search is used instead of a binary
        // search because the list is so small that the
        // division operations needed by the binary sort
        // would probably negate any gains.
        for(int j=0; j<array.length; j++) {

            if(number == array[j]) return true;
            if(number < array[j]) return false;
        }

        return false;
    }


    public static boolean isBinaryType(int sqlType) {

        return arrayContains(BINARY_TYPES, sqlType);
    }


    public static boolean isNumericType(int sqlType) {

        return arrayContains(NUMERIC_TYPES, sqlType);    
    }

    public static boolean isCharType(int sqlType) {

        return arrayContains(CHAR_TYPES, sqlType);    
    }

    public static boolean isDateTimeType(int sqlType) {

        return arrayContains(DATETIME_TYPES, sqlType);    
    }

    public static boolean isDisplayableType(int sqlType) {

        return arrayContains(DISPLAYABLE_TYPES, sqlType);    
    }


    public static String escape(String s) {
        if(s.indexOf('\'') < 0) return s;
        StringBuffer buffer = new StringBuffer(s.length() + 10);
        char[] characters = s.toCharArray();
        for(int j=0; j<characters.length; j++) {
            if(characters[j] == '\'') buffer.append('\'');
            buffer.append(characters[j]);
        }
        return buffer.toString();
    }

}
