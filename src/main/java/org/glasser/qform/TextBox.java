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

import java.util.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import org.glasser.swing.*;
import org.glasser.sql.DBUtil;

public class TextBox extends JTextArea {

    private static boolean debug = System.getProperty("TextBox.debug") != null;

    private Set editableTypes = null;

    private String columnName;
    private Integer dataType;
    private String typeName;
    private int length;
    private boolean nullable;
    private boolean isNull = false;
    private int ordinal;
    private boolean constructorDone = false;

    private boolean isPKComponent = false;

    private boolean dirty = false;

    private JLabel label = null;

    protected String dbEncoding = null;

    protected final static String APP_ENCODING = System.getProperty("file.encoding");

    final Border redBorder = new LineBorder(Color.red);


    class RequiredFieldBorder extends CompoundBorder {
        RequiredFieldBorder(Border innerBorder) {
            super(redBorder, innerBorder);
        }
    };


    
    


    private static java.awt.Color DISABLED = new java.awt.Color(0xE1E1E1);
    private static java.awt.Color WHITE = java.awt.Color.white;


    private String decode(String s) {
        try {
            if(dbEncoding == null) return s;
            String decoded = new String( s.getBytes(dbEncoding), APP_ENCODING);
            if(debug) System.out.println("encoded: " + s + "\ndecoded: " + decoded);
            return decoded;
        }
        catch(java.io.UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return s;
        }
    }

    private String encode(String s) {
        try {
            if(dbEncoding == null) return s;
            String encoded = new String( s.getBytes(APP_ENCODING), dbEncoding);
            if(debug) System.out.println("decoded: " + s + "\nencoded: " + encoded);
            return encoded;

        }
        catch(java.io.UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return s;
        }
    }


    
    public TextBox(String ColumnName, int DataType, String TypeName, int Length, boolean Nullable, int Ordinal, JLabel lab) {
        super();
        setText("");
        columnName = ColumnName;
        dataType = new Integer(DataType);

        typeName = TypeName;
        length = Length;
        nullable = Nullable;
        ordinal = Ordinal;
        label = lab;
        String toolTip = columnName + ", type=" + dataType+ " (" + typeName
                         + "), Length=" + length + ", nullable=" + nullable;
        label.setToolTipText(toolTip);

        setBorder(normalBorder());

        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        this.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    dirty = true;
                }
                public void removeUpdate(DocumentEvent e) {
                    dirty = true;
                }
                public void changedUpdate(DocumentEvent e) {
                    dirty = true;
                }
            });

        // in Java versions 1.4 and later, this will cause the tab key to
        // shift focus away from a JTextArea.
		GUIHelper.setTabTraversal(this);

        dbEncoding = System.getProperty("qform.db.encode." + typeName);

        constructorDone = true;


        
    }

    public boolean isDirty() {
        return dirty;
    }


    public void setDirty(boolean b) {
        dirty = b;
    }

    public void setRequiredFieldBorder(boolean b) {
        if(b && nullable == false) {
            setBorder(new RequiredFieldBorder( normalBorder()));
        }
        else {
            setBorder(normalBorder());
        }
    }

    public void setLabel(JLabel l) {
        label = l;
    }

    public JLabel getLabel() {
        return label;
    }

    public String getColumnName() {
        if(columnName == null) return "";
        else return columnName;
    }

    public Integer getDataType() {
        return dataType;
    }

    public int getLength() {
        return length;
    }

    public boolean isNullable() {
        return nullable;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public boolean isTypeDisplayable() {
        return (editableTypes != null && editableTypes.contains(dataType))
            || DBUtil.isDisplayableType(dataType.intValue());
    }

    public boolean isTypeNumeric() {
        return DBUtil.isNumericType(dataType.intValue());
    }

    public void setEditable(boolean b) {
        if(constructorDone == false) {
            super.setEditable(b);
            return;
        }
        if(isTypeDisplayable()) {
            super.setEditable(b);
            setColor(b);
        }
        else {
            super.setEditable(false);
            setColor(false);
        
        }
    
    }

    public void setPkComponent(boolean isPKComponent) {
        this.isPKComponent = isPKComponent;
        if(isPKComponent) {
            if(label != null) {
                label.setForeground(java.awt.Color.red);
                String text = label.getText();
                if(text != null) label.setText("* " + text);
            }
        }
    }

    public boolean isPkComponent() {
        return isPKComponent;
    }

    private void setColor(boolean b) {
        if(b) setBackground(WHITE);
        else setBackground(DISABLED);
    }

    private Border backupBorder = new ThinBevelBorder(ThinBevelBorder.LOWERED);

    private Border normalBorder() {
        Border b = (Border) UIManager.getBorder("TextField.border");
        if(b == null) {
            return backupBorder;
        }
        else {
            return b;
        }
    }



    public void setValue(Object s) {



        // if the value to be displayed is null, we'll turn the label
        // gray to tell the user it's null rather than an empty string
        // or a string of blanks.
        if(s == null) {
            isNull = true;
            if(label != null) label.setEnabled(false);
        }
        else {
            isNull = false;
            label.setEnabled(true);
            
        }

        // if the data type is displayable, display it.
        if(isTypeDisplayable()) {
            if(s != null) {
                //s = s.trim();
                super.setText(decode(s.toString()));
            }
            else {
                super.setText(null);
            }
        }
        else {
            if(s == null) {
                super.setText(null);
            }
            else {
                super.setText("<" + typeName + " value >");
            }
        }

        dirty = false;
    }

    public void clear() {
        super.setText(null);
        label.setEnabled(true);
    }

    public static String escapeQuotes(String s) {
        if(s == null) return s;
        if(s.indexOf('\'') < 0) return s;
        StringBuffer buffer = new StringBuffer(s.length() + 10);
        char[] chars = s.toCharArray();
        for(int j=0; j<chars.length; j++) {
            if( chars[j] == '\'' ) buffer.append('\'');
            buffer.append(chars[j]);
        }
        return buffer.toString();
    }

    public String getValueClause(boolean trim) {

        String s = getText();
        if(s == null || s.length() == 0) return null;

        if(trim) s = s.trim();

        if(this.isTypeNumeric()) {
           return s.trim();
        }
        else {
            switch(dataType.intValue()) {
                case Types.CHAR :
                case Types.VARCHAR :        
                    return ("'" + escapeQuotes(encode(s)) + "'");
                case 9 :    
                case Types.DATE :
                    return ("{d '" + escapeQuotes(s) + "'}");
                case 10 :   
                case Types.TIME :
                    return ("{t '" + escapeQuotes(s) + "'}");
                case 11 :   
                case Types.TIMESTAMP :
                    return ("{ts '" + escapeQuotes(s) + "'}");
                default :
                    return ("'" + escapeQuotes(encode(s)) + "'");
            }
        }
    }

    private String columnNameForQuery = null;

    public String getColumnNameForQuery() {

        String temp = columnNameForQuery;
        if(temp != null) return temp;

        // check to see if the column name has embedded spaces or hyphens. 
        // if any are found, enclose the column name in double quotation marks.
        temp = columnName;
        if(columnName.indexOf(' ') > -1 || columnName.indexOf('-') > -1) temp = "\"" + columnName + "\"";
        columnNameForQuery = temp;
        return temp;
    }

    public String getCondition() {
        return getCondition(true);
    }

    public String getCondition(boolean useWildcards) {
        
        if(getText().trim().length() == 0  || isTypeDisplayable() == false) {
            return null;
        }

        String columnNameTemp = getColumnNameForQuery();

        String valueClause = getValueClause(true);

        if(useWildcards && isTypeNumeric() == false
           && valueClause != null
           && valueClause.indexOf("%") > 0) {
            return columnNameTemp + " LIKE " + valueClause;
        }
        else if(useWildcards && isTypeNumeric()
           && valueClause != null
           && (valueClause.indexOf(">") == 0 || valueClause.indexOf("<") == 0 || valueClause.toUpperCase().indexOf("BETWEEN") == 0)) {
            return columnNameTemp + " " + valueClause;
        }
        else {
            return columnNameTemp + " = " + valueClause;
        }
    }

    public String getSetClause() {
        
        return getColumnNameForQuery() + " = " + getValueClause(false);
    }

    public void scrollRectToVisible(Rectangle r) {}

    public void setUI(javax.swing.plaf.TextUI ui) {
        
        boolean isEditingRequired = getBorder() instanceof RequiredFieldBorder;
        super.setUI(ui);
        if(isEditingRequired) {
            setBorder(new RequiredFieldBorder(normalBorder()));
        }
        else {
            setBorder(normalBorder());
        }
    }

    /**
     * This takes as its argument a HashSet of Integer objects that represent the SQL types
     * that should be considered displayable and editable by this TextBox. This is for databases
     * that have data types that are not one of the standard ones defined in java.sql.Types.
     */
    public void setEditableTypes(Set editableTypes) {
        this.editableTypes = editableTypes;
    }

}

