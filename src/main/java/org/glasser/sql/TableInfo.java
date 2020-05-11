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
 * $Source: /cvsroot/qform/qform/src/org/glasser/sql/TableInfo.java,v $
 * $Revision: 1.7 $
 * $Author: dglasser $
 * $Date: 2005/01/26 02:22:58 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.sql;


import org.glasser.util.*;
import org.glasser.util.comparators.*;
import java.util.*;

public class TableInfo {

    private String cachedString = null;

    public final static MethodComparator<TableInfo> NAME_COMPARATOR =
        new MethodComparator<>(org.glasser.sql.TableInfo.class, "getTableName");


    protected String tableCat = null;

    protected String tableSchem = null;

    protected String tableName = null;

    protected String tableType = null;

    protected String remarks = null;

    protected String typeCat = null;

    protected String typeSchem = null;

    protected String typeName = null;

    protected Column[] columns = null;

    protected ForeignKey[] foreignKeys = null;

    protected ForeignKey[] exportedKeys = null;

    protected HashMap<String, Column> columnMap = null;

    protected String qualifiedTableName = null;


    public void setTableCat(String tableCat) {
        this.tableCat = trimToNull(tableCat);
    }

    public void setTableSchem(String tableSchem) {
        qualifiedTableName = null;
        this.tableSchem = trimToNull(tableSchem);
    }

    public void setTableName(String tableName) {
        qualifiedTableName = null;
        this.tableName = trimToNull(tableName);
    }

    public void setTableType(String tableType) {
        this.tableType = trimToNull(tableType);
    }

    public void setRemarks(String remarks) {
        this.remarks = trimToNull(remarks);
    }

    public void setTypeCat(String typeCat) {
        this.typeCat = trimToNull(typeCat);
    }

    public void setTypeSchem(String typeSchem) {
        this.typeSchem = trimToNull(typeSchem);
    }

    public void setTypeName(String typeName) {
        this.typeName = trimToNull(typeName);
    }

    public void setColumns(Column[] columns) {
        columnMap = null;
        this.columns = columns;
    }

    public void setForeignKeys(ForeignKey[] foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public void setExportedKeys(ForeignKey[] exportedKeys) {
        this.exportedKeys = exportedKeys;
    }



    public String getTableCat() {
        return tableCat;
    }

    public String getTableSchem() {
        return tableSchem;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableType() {
        return tableType;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getTypeCat() {
        return typeCat;
    }

    public String getTypeSchem() {
        return typeSchem;
    }

    public String getTypeName() {
        return typeName;
    }

    public Column[] getColumns() {
        return columns;
    }

    public ForeignKey[] getForeignKeys() {
        return foreignKeys;
    }

    public ForeignKey[] getExportedKeys() {
        return exportedKeys;
    }


    protected synchronized HashMap<String, Column> getColumnMap() {
        HashMap<String, Column> tempMap = columnMap; // thead safety
        if(tempMap == null) {
            tempMap = new HashMap<String, Column>(); 
            Column[] temp = columns; // thread safety
            for(int j=0; temp != null && j<temp.length; j++) {
                tempMap.put(temp[j].getColumnName(), temp[j]);
            }
            columnMap = tempMap;
        }
        return tempMap;
    }

    public Column getColumn(String columnName) {
        return (Column) getColumnMap().get(columnName);
    }


    public String debugString() {

        StringBuffer buffer = new StringBuffer(200);
        buffer.append("TableInfo[");
        buffer.append("tableCat=");
        buffer.append(tableCat);
        buffer.append(",tableSchem=");
        buffer.append(tableSchem);
        buffer.append(",tableName=");
        buffer.append(tableName);
        buffer.append(",tableType=");
        buffer.append(tableType);
        buffer.append(",remarks=");
        buffer.append(remarks);
        buffer.append(",typeCat=");
        buffer.append(typeCat);
        buffer.append(",typeSchem=");
        buffer.append(typeSchem);
        buffer.append(",typeName=");
        buffer.append(typeName);
        buffer.append("]");
        return buffer.toString();

    }


    public String toString() {
        if(tableName != null) return tableName;
        else return "TableInfo[tableName=null]";
    }


    /**
     * Returns the tableName enclosed in the provided "quote" characters, if
     * the tableName contains a space or a hyphen, otherwise returns the tableName as-is.
     * Normally, the openQuote and closeQuote args will both be regular double quotation 
     * marks ("), however, Microsoft databases use square brackets ([ and ]).
     */
    public String maybeQuoteTableName(char openQuote, char closeQuote) {

		if(tableName == null) return null;

		if(tableName.indexOf(' ') < 0 && tableName.indexOf('-') < 0) {
			return tableName;
		}

		StringBuffer buffer = new StringBuffer(tableName.length() + 4);

		return buffer.append(openQuote).append(tableName).append(closeQuote).toString();

	}

    /**
     * Returns the tableSchem field enclosed in the provided "quote" characters, if
     * the tableName contains a space or a hyphen, otherwise returns the tableSchem as-is.
     * Normally, the openQuote and closeQuote args will both be regular double quotation 
     * marks ("), however, Microsoft databases use square brackets ([ and ]).
     */
	public String maybeQuoteTableSchem(char openQuote, char closeQuote) {

		if(tableSchem == null || tableSchem.trim().length() == 0) return null;

		if(tableSchem.indexOf(' ') < 0 && tableSchem.indexOf('-') < 0) {
			return tableSchem;
		}

		StringBuffer buffer = new StringBuffer(tableSchem.length() + 4);

		return buffer.append(openQuote).append(tableSchem).append(closeQuote).toString();

	}


    /**
     * Returns the tableName prefixed by the tableSchem field and a dot, if the tableSchem field
     * contains non-whitespace characters.  If the tableSchem field is null or whitespace, then only 
     * the tableName is returned. Either the tableSchem or tableName may be enclosed in
     * the provided quoting characters, if they contain spaces or hyphens.
     */
    public String getQualifiedTableName(char openQuote, char closeQuote) {

		
		String table = maybeQuoteTableName(openQuote, closeQuote);
		if(table == null) return null;

		String schem = maybeQuoteTableSchem(openQuote, closeQuote);
		if(schem == null) return table;

		StringBuffer buffer = new StringBuffer(table.length() + schem.length() + 2);
		return buffer.append(schem).append('.').append(table).toString();

	}



		




    /**
     * Calls getQualifiedTableName(char, char) using double quotation marks as
     * the openQuote and closeQuote.
     */
    public String getQualifiedTableName() {
		return getQualifiedTableName('"', '"');
    }

    /**
     * Trims whitespace, and returns null if the String is zero-length
     * or all whitespace.
     */
    public String trimToNull(String s) {
        if(s == null || (s = s.trim()).length() == 0) return null;
        return s;
    }

}
 
