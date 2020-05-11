
/* ====================================================================
 * Copyright (c) 1998 - 2020 David F. Glasser.  All rights
 * reserved.
 *
 * This file is part of the QueryForm Database Tool.
 *
 * The QueryForm Database Tool is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The QueryForm Database Tool is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the QueryForm Database Tool; if not, write to:
 *
 *      The Free Software Foundation, Inc.,
 *      59 Temple Place, Suite 330
 *      Boston, MA  02111-1307  USA
 *
 * or visit http://www.gnu.org.
 *
 * ====================================================================
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 *
 * ==================================================================== 
 */
package org.glasser.qform;


import org.glasser.util.*;
import org.glasser.sql.ForeignKeyColumn;
import org.glasser.sql.Column;
import org.glasser.swing.table.*;


/**
 * This ColumnManager is used for the Exported Key Columns table on an Exported Keys panel.
 */
public class ExportedKeyColumnManager extends AbstractColumnManager<ForeignKeyColumn> {


    protected final static String[] COLUMN_NAMES = {"Key Seq", "Foreign Column", "Local Column", "Data Type (Local)", "SQL Type (Local)"};

    protected final static Class[] COLUMN_CLASSES = {Integer.class, String.class, String.class, String.class, Integer.class};

    public ExportedKeyColumnManager() {
        super();
        setColumnNames(COLUMN_NAMES);
        setColumnClasses(COLUMN_CLASSES);
    }

    public Object getValueAt(int row, int column, ForeignKeyColumn rowObject) {

        ForeignKeyColumn fc = rowObject;
        if(fc == null) return null;
        Column lc = fc.getLocalColumn();
        switch(column) {
            case 0 :
                return new Integer(fc.getKeySeq());
            case 1 :
                return fc.getLocalColumnName();
            case 2 :
                return fc.getForeignColumnName();
            case 3 :
                return lc.getTypeName();
            case 4 :
                return new Integer(lc.getDataType());
            default :
                return null;
        }
    }
}
