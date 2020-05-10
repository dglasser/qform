/* ====================================================================
 * The QueryForm License, Version 1.1
 *
 * Copyright (c) 1998 - 2004 David F. Glasser.  All rights
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
 * $Source: /cvsroot/qform/qform/src/org/glasser/sql/Column.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2004/09/28 01:37:57 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.sql;


import java.util.*;
import java.sql.*;
import java.lang.reflect.*;
import org.glasser.util.*;



public class Column implements java.io.Serializable {

    

    protected String tableCatalog = null;

    protected String tableSchema = null;
                                  
    protected String tableName = null;

    protected String columnName = null;

    protected int dataType = 0;

    protected String typeName = null;

    protected int columnSize = 0;

    protected int decimalDigits = 0;

    protected int radix = 0;

    protected boolean nullable = true;

    protected String remarks = null;

    protected String columnDefaultValue = null;

    protected int ordinal = -1;

    protected boolean pkComponent = false;

    protected boolean isForeignKey = false;


    // setters

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public void setRadix(int radix) {
        this.radix = radix;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setColumnDefaultValue(String columnDefaultValue) {
        this.columnDefaultValue = columnDefaultValue;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public void setPkComponent(boolean pkComponent) {
        this.pkComponent = pkComponent;
    }

    public void setIsForeignKey(boolean isForeignKey) {
        this.isForeignKey = isForeignKey;
    }


    // getters

    public String getTableCatalog() {
        return tableCatalog;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getDataType() {
        return dataType;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public int getRadix() {
        return radix;
    }

    public boolean getNullable() {
        return nullable;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getColumnDefaultValue() {
        return columnDefaultValue;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public boolean getPkComponent() {
        return pkComponent;
    }

    public boolean getIsForeignKey() {
        return isForeignKey;
    }



    /**
     * Returns the columnName enclosed in the provided "quote" characters, if
     * the columnName contains a space or a hyphen, otherwise returns the columnName as-is.
     * Normally, the openQuote and closeQuote args will both be regular double quotation 
     * marks ("), however, Microsoft databases use square brackets ([ and ]).
     */
	public String maybeQuoteColumnName(char openQuote, char closeQuote) {
		if(columnName == null) return null;
		if(columnName.trim().length() == 0) return "";
		if(columnName.indexOf(' ') < 0 && columnName.indexOf('-') < 0) return columnName;
		StringBuffer buffer = new StringBuffer(columnName.length() + 4);
		return buffer.append(openQuote).append(columnName).append(closeQuote).toString();
	}

    private static Object[][] sqlTypes =
    {
         {"ARRAY", new Integer(java.sql.Types.ARRAY) }
        ,{"BIGINT", new Integer(java.sql.Types.BIGINT) }
        ,{"BINARY", new Integer(java.sql.Types.BINARY) }
        ,{"BIT", new Integer(java.sql.Types.BIT) }
        ,{"BLOB", new Integer(java.sql.Types.BLOB) }
        ,{"CHAR", new Integer(java.sql.Types.CHAR) }
        ,{"CLOB", new Integer(java.sql.Types.CLOB) }
        ,{"DATE", new Integer(java.sql.Types.DATE) }
        ,{"DECIMAL", new Integer(java.sql.Types.DECIMAL) }
        ,{"DISTINCT", new Integer(java.sql.Types.DISTINCT) }
        ,{"DOUBLE", new Integer(java.sql.Types.DOUBLE) }
        ,{"FLOAT", new Integer(java.sql.Types.FLOAT) }
        ,{"INTEGER", new Integer(java.sql.Types.INTEGER) }
        ,{"JAVA_OBJECT", new Integer(java.sql.Types.JAVA_OBJECT) }
        ,{"LONGVARBINARY", new Integer(java.sql.Types.LONGVARBINARY) }
        ,{"LONGVARCHAR" , new Integer(java.sql.Types.LONGVARCHAR) }
        ,{"NULL", new Integer(java.sql.Types.NULL) }
        ,{"NUMERIC", new Integer(java.sql.Types.NUMERIC) }
        ,{"OTHER", new Integer(java.sql.Types.OTHER) }
        ,{"REAL", new Integer(java.sql.Types.REAL) }
        ,{"REF", new Integer(java.sql.Types.REF) }
        ,{"SMALLINT", new Integer(java.sql.Types.SMALLINT) }
        ,{"STRUCT", new Integer(java.sql.Types.STRUCT) }
        ,{"TIME", new Integer(java.sql.Types.TIME) }
        ,{"TIMESTAMP", new Integer(java.sql.Types.TIMESTAMP) }
        ,{"TINYINT", new Integer(java.sql.Types.TINYINT) }
        ,{"VARBINARY", new Integer(java.sql.Types.VARBINARY) }
        ,{"VARCHAR", new Integer(java.sql.Types.VARCHAR) }
    };

    private final static Hashtable typeStrings = new Hashtable();

    static {
        for(int j=0; j<sqlTypes.length; j++) { 
            typeStrings.put(sqlTypes[1], sqlTypes[0]);
        }
    }

}

