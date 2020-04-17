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
 * $Source: /cvsroot/qform/qform/src/org/glasser/sql/ForeignKey.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/01/25 23:35:03 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.sql;

import java.util.*;
import org.glasser.util.*;


public class ForeignKey implements java.io.Serializable {

    private String foreignKeyName = null;

    /**
     * This is the name of the schema (or table owner) to which
     * the table containing this foreign key belongs.
     */
    private String localTableSchema = null;


    /**
     * This is the name of the table to which this foreign key belongs.
     */
    private String localTableName = null;


    /**
     * This is the name of the schema (or table owner) to which the foreign
     * table (the table that is referenced by this foreign key) belongs.
     */
    private String foreignTableSchema = null;

    /**
     * This is the name of the table that is referenced by this foreign key.
     */
    private String foreignTableName = null;

    /**
     * This is a Vector that is used to hold the ForeignKeyColumns that participate
     * in this ForeignKey.
     */
    private Vector foreignKeyColumns = new Vector();

    public ForeignKey(String foreignTableName) {
        if(Util.isNothing(foreignTableName))
            throw new IllegalArgumentException("foreignTableName argument cannot be null.");
        this.foreignTableName = foreignTableName.trim();
    }


    public void setForeignKeyName(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }

    /**
     * This is the name of the schema (or table owner) to which
     * the table containing this foreign key belongs.
     */
    public void setLocalTableSchema(String localTableSchema) {
        this.localTableSchema = localTableSchema;
    }


    /**
     * This is the name of the table to which this foreign key belongs.
     */
    public void setLocalTableName(String localTableName) {
        this.localTableName = localTableName;
    }


    /**
     * This is the name of the schema (or table owner) to which the foreign
     * table (the table that is referenced by this foreign key) belongs.
     */
    public void setForeignTableSchema(String foreignTableSchema) {
        this.foreignTableSchema = foreignTableSchema;
    }

    /**
     * This is the name of the table that is referenced by this foreign key.
     */
    public void setForeignTableName(String foreignTableName) {
        this.foreignTableName = foreignTableName;
    }

    public void setForeignKeyColumns(Vector foreignKeyColumns) {
        this.foreignKeyColumns = (Vector) foreignKeyColumns.clone();
    }

    public void addForeignKeyColumn(ForeignKeyColumn col) {
        foreignKeyColumns.add(col);
    }

    public String getForeignKeyName() {
        return foreignKeyName;
    }

    /**
     * This is the name of the schema (or table owner) to which
     * the table containing this foreign key belongs.
     */
    public String getLocalTableSchema() {
        return localTableSchema;
    }


    /**
     * This is the name of the table to which this foreign key belongs.
     */
    public String getLocalTableName() {
        return localTableName;
    }


    /**
     * This is the name of the schema (or table owner) to which the foreign
     * table (the table that is referenced by this foreign key) belongs.
     */
    public String getForeignTableSchema() {
        return foreignTableSchema;
    }

    /**
     * This is the name of the table that is referenced by this foreign key.
     */
    public String getForeignTableName() {
        return foreignTableName;
    }
    

    public Vector getForeignKeyColumns() {
        return (Vector) foreignKeyColumns.clone();
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer(200);
        buffer.append("ForeignKeyColumn[");
        buffer.append("localTableSchema=");
        buffer.append(localTableSchema);
        buffer.append(",localTablename=");
        buffer.append(localTableName);
        buffer.append(",foreignTableSchema=");
        buffer.append(foreignTableSchema);
        buffer.append(",foreignTableName=");
        buffer.append(foreignTableName);
        buffer.append(",foreignKeyColumns.size()=");
        buffer.append(foreignKeyColumns);
        buffer.append("]");
        return buffer.toString();
    }

}
