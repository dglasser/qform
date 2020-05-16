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


/**
 * An instance of this class is used to hold the metadata for a table column that serves as
 * all or part of a foreign key.
 */
public class ForeignKeyColumn implements java.io.Serializable {


    private String localTableSchema = null;

    private String localTableName = null;

    private String localColumnName = null;

    private String foreignTableSchema = null;

    private String foreignTableName = null;

    private String foreignColumnName = null;

    private int keySeq = -1;

    private Column localColumn = null;

    
    /**
     * This is the schema (or table owner) for the table to which
     * this column belongs.
     */
    public void setLocalTableSchema(String localTableSchema) {
        this.localTableSchema = localTableSchema;
    }

    /**
     * This is from the column FKTABLE_NAME in the metadata ResultSet.
     */
    public void setLocalTableName(String localTableName) {
        this.localTableName = localTableName;
    }

    /**
     * This is from the column FKCOLUMN_NAME in the metadata ResultSet.
     */
    public void setLocalColumnName(String localColumnName) {
        this.localColumnName = localColumnName;
    }

    /**
     * This is the schema to which the foreign table belongs.
     */
    public void setForeignTableSchema(String foreignTableSchema) {
        this.foreignTableSchema = foreignTableSchema;
    }

    /**
     * This is from the column PKTABLE_NAME in the metadata ResultSet.
     */
    public void setForeignTableName(String foreignTableName) {
        this.foreignTableName = foreignTableName;
    }

    /**
     * This is from the column PKCOLUMN_NAME in the metadata ResultSet.
     */
    public void setForeignColumnName(String foreignColumnName) {
        this.foreignColumnName = foreignColumnName;
    }    

    public void setKeySeq(int keySeq) {
        this.keySeq = keySeq;
    }

    public void setLocalColumn(Column localColumn) {
        this.localColumn = localColumn;
    }

    public String getLocalTableSchema() {
        return localTableSchema;
    }

    public String getLocalTableName() {
        return localTableName;
    }

    public String getLocalColumnName() {
        return localColumnName;
    }

    public String getForeignTableSchema() {
        return foreignTableSchema;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }

    public String getForeignColumnName() {
        return foreignColumnName;
    }

    public int getKeySeq() {
        return keySeq;
    }

    public Column getLocalColumn() {
        return localColumn;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(150);
        buffer.append("ForeignKeyColumn[");
        buffer.append("localTableSchema=");
        buffer.append(localTableSchema);
        buffer.append(",localTableName=");
        buffer.append(localTableName);
        buffer.append(",localColumnName=");
        buffer.append(localColumnName);
        buffer.append(",foreignTableSchema=");
        buffer.append(foreignTableSchema);
        buffer.append(",foreignColumnName=");
        buffer.append(foreignColumnName);
        buffer.append(",keySeq=");
        buffer.append(keySeq);
        buffer.append(",localColumn=");
        buffer.append(localColumn);
        return buffer.toString();
    }

}
