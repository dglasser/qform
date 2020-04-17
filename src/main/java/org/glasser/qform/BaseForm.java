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


import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import org.glasser.sql.*;
import org.glasser.util.*;
import org.glasser.swing.PopupMenuManager;
import org.glasser.swing.GUIHelper;


/**
 * An object of this class provides the form through which query parameters are input
 * and query results are displayed (one record at a time) in the QueryForm app.
 */
public class BaseForm extends JPanel {

    protected int textRowHeight = 28;

    protected int margin = 2;

    protected TextBox[] fields = null;
                                                      
    protected ResultSetBuffer resultSetBuffer = null;

    protected boolean hasPK = false;

    protected boolean endReached = false;

    int minWidth = 0;

    public ResultSetBuffer getResultSetBuffer() {
        return resultSetBuffer;
    }

    public boolean setResultSetBuffer(ResultSetBuffer resultSetBuffer) throws SQLException {
        
        this.resultSetBuffer = resultSetBuffer;

        
        endReached = false;
//        this.setFieldsEditable(false);                                                               
//        clearFields();

        if(resultSetBuffer.isEndOfResultsReached() && resultSetBuffer.size() == 0) {
            endReached = true;
            return false;
        }

        this.setFieldsEditable(false);                                                               
        clearFields();
        this.current();
        return true;

    }

    public void removeResultSetBuffer() {
        this.resultSetBuffer = null;
    }

    
    public BaseForm(Vector fieldList) {

        fields = (TextBox[]) fieldList.toArray(new TextBox[fieldList.size()]);        

        int vgap = 5;

        GridBagLayout gbLayout = new GridBagLayout();
        this.setLayout(gbLayout);
        GridBagConstraints gc = new GridBagConstraints();

        Insets labelInsets = new Insets(0,0,0,0);
        Insets fieldInsets = new Insets(0,5,vgap,0);
        gc.anchor = gc.NORTH;
        gc.fill = gc.HORIZONTAL;



        this.setBorder(new EmptyBorder(10,10,10,10));
        for(int row=0; row<fields.length; row++) {

            TextBox textBox = fields[row];
            JLabel label = textBox.getLabel();

            if(fields[row].isPkComponent()) {
                hasPK = true;
            }

            // add the label
            gc.gridx = 0;
            gc.gridy = row;
            gc.weightx = 0;
            gc.ipadx = 0;
            gc.ipady = 0;
            gc.insets = labelInsets;

            this.add(label, gc);
            label.setForeground(java.awt.Color.black);
            label.setHorizontalAlignment(JLabel.RIGHT);

            // now add the textfield
            gc.gridx = 1;
            gc.weightx = 1;
            gc.ipadx = 300;
            gc.insets = fieldInsets;

            // we need to see how many lines high the JTextArea needs to be to display
            // the field's contents, assuming that it is 40 characters wide. This only
            // applys to fields with displayable (non-binary) data types.
            int numlines = 1;
            if(textBox.getLength() > 40   && textBox.isTypeDisplayable()) {
                numlines = (textBox.getLength() / 40) + 1;
            }

            // if the number of lines required to display this field is more than
            // 7, we'll only make it 7 high, but put it in a scrollpane so all of
            // the field's data can be viewed.
            if(numlines > 7) {
                numlines = 7;
                gc.ipady = (textRowHeight * numlines) / 2; 

                JScrollPane sp = new JScrollPane(textBox);
                sp.setBorder(null);  // use the JTextArea's border
                sp.getHorizontalScrollBar().setUnitIncrement(textRowHeight + margin);
                this.add(sp, gc);
            } else if(numlines > 1) {
                gc.ipady = (textRowHeight * numlines) / 2; 
                this.add(textBox, gc);
            } else {
                // otherwise 
                this.add(textBox, gc);
            }
        }

        // now add a row to the bottom of the form for some 
        // padding, and make it vertically stretchable to
        // absorb any excess height
        gc.gridx = 0;
        gc.gridy = fields.length;
        gc.weightx = 0;
        gc.weighty = 1;
        gc.ipadx = 0;
        gc.ipady = 0;
        gc.insets = labelInsets;

        this.add(new JLabel("  "), gc);

        fieldInsets.bottom = 0;
        gc.gridx = 1;
        gc.weightx = 1;
        gc.insets = fieldInsets;
        this.add(new JLabel("  "), gc);

        for(int k=0; k<fields.length - 1; k++) {
            fields[k].setNextFocusableComponent(fields[k+1]);
        }

        if(fields.length > 0) {
            fields[fields.length - 1].setNextFocusableComponent(fields[0]);
        }

        // constrain the width to make sure the scrollpane does not let the
        // this panel grow to a huge width, and it will also
        // make it so that vertical scrolling occurs (instead of clipping).
        minWidth = gbLayout.minimumLayoutSize(this).width;
    }


    /**
     * Overriden to constrain the preferred width.
     */
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(minWidth, d.height);
    }

    public boolean getHasPK() {
        return hasPK;
    }

    public void focusFirstTextBox() {
        if(fields != null && fields.length > 0) {
            fields[0].requestFocus();
        }
    }


    public void setFieldsEditable(boolean b) {
        for(int j=0; j<fields.length; j++) {
            fields[j].setEditable(b);
            // this works around Java Bug #4838730 that occurs in version 1.4.1. 
            // See the Java bug database at java.sun.com for details.
			if(b) GUIHelper.setTabTraversal(fields[j]);
        }
    }

    public void clearFields() {
        for(int j=0; j<fields.length; j++) {
            fields[j].clear();
        }
    }

    void showRequiredFields(boolean b) {
        for(int j = 0; j < fields.length; j++) {
            fields[j].setRequiredFieldBorder(b);
        }
    }

    public boolean isAtBeginning() {
        if(resultSetBuffer != null) return resultSetBuffer.isAtBeginning();
        return false;
    }

    public int getCurrentRowNum() {
        if(resultSetBuffer == null) return -1;
        return resultSetBuffer.getCursor();
    }

    public int getCursorVal() {
        if(resultSetBuffer == null) return -1;
        return resultSetBuffer.getCursor();
    }



    public String getPkWhereClause() 
        throws UnknownPrimaryKeyException
    {
        return getPkWhereClause(true);
    }

    public String getPkWhereClause(boolean includeWhere) 
        throws UnknownPrimaryKeyException
    {
        if(resultSetBuffer == null) throw new RuntimeException("There is no current record.");

        // collect the indices of all of the PK fields.
        ArrayList pkFieldIndices = new ArrayList();
        for(int j=0; j<fields.length; j++) {
            if(fields[j].isPkComponent()) {
                pkFieldIndices.add(new Integer(j));
            }
            else if(hasPK == false) {
                // see if this is a candidate key.
                int dataType = fields[j].getDataType().intValue();
                if(fields[j].isTypeDisplayable()
                   && dataType != Types.BIT
                   && fields[j].isNullable() == false
                   && fields[j].getLength() > 0
                   && ((DBUtil.isCharType(dataType) && fields[j].getLength() < 51) || DBUtil.isNumericType(dataType))) {
                    pkFieldIndices.add(new Integer(j));
                }
            }
        }

        if(pkFieldIndices.size() == 0) {
            throw new UnknownPrimaryKeyException();
        }
        Vector row = resultSetBuffer.getCurrentRow();
        StringBuffer buffer = new StringBuffer(25 + (40* pkFieldIndices.size()));
        if(includeWhere) buffer.append("WHERE ");
        for(int j=0; j<pkFieldIndices.size(); j++) {

            int fieldIndex = ((Integer) pkFieldIndices.get(j)).intValue();

            TextBox field = fields[fieldIndex];

            if(j > 0) {
                buffer.append(" AND ");
            }
            buffer.append(field.getColumnNameForQuery());
            buffer.append(" = ");

            // if this field is null, something's wrong 
            // so let it fail.
            String val = row.get(fieldIndex).toString().trim();
            if(field.isTypeNumeric()) {
                buffer.append(val);
            }
            else {

                buffer.append("'");
                buffer.append(DBUtil.escape(val));
                buffer.append("'");
            }
        }
        return buffer.toString();
    }

    public String getSetClause() {
        if(resultSetBuffer == null) throw new RuntimeException("There is no current record.");
        Vector row = resultSetBuffer.getCurrentRow();
        StringBuffer buffer = new StringBuffer(fields.length * 30);
        boolean dirtyFieldFound = false;
        for(int j=0; j<fields.length; j++) {
            if(fields[j].isDirty()) {
                if(dirtyFieldFound) buffer.append(", ");
                dirtyFieldFound = true;
                buffer.append(fields[j].getSetClause());
            }
        }
        if(dirtyFieldFound == false) return null;
        return buffer.toString();
    }

    /**
     * Copies the values from the textboxes into the current row's Vector.
     */
    public int updateCurrentRow() {
        if(resultSetBuffer == null) return -1;
        Vector cachedRow = resultSetBuffer.getCurrentRow();

        if(cachedRow != null) {
            for(int j=0; j<fields.length; j++) {
                String fieldValue = fields[j].getText();
                cachedRow.set(j, fieldValue);
                fields[j].setDirty(false);
            }
            return resultSetBuffer.getCursor();
        }

        else {
//            System.out.println("cachedRow is null.");
            clearFields();
            return -1;
        }

    }





    public int current() {

        if(resultSetBuffer == null) return -1;

        Vector cachedRow = resultSetBuffer.getCurrentRow();

        if(cachedRow != null) {
            for(int j=0; j<fields.length; j++) {
                Object fieldValue = cachedRow.elementAt(j);
                fields[j].setValue(fieldValue);
            }
            return resultSetBuffer.getCursor();
        }

        else {
//            System.out.println("cachedRow is null.");
            clearFields();
            return -1;
        }

    }

    

    public int next() throws SQLException {

        Vector cachedRow = resultSetBuffer.getNextRow();

        // if we're at the end of the backbuffer...
        if(cachedRow == null) {

            endReached = true;
            return(0-resultSetBuffer.getCursor());
        } 
        else {
            // if we're not at the end of the backbuffer, read in the next row from it.
            for(int j = 0; j < fields.length; j++) {
                Object fieldValue = cachedRow.elementAt(j);
                fields[j].setValue(fieldValue);
            }
            return resultSetBuffer.getCursor();
        }
    }


    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    //
    // display the previous record
    //
    public int previous() {

        Vector cachedRow = resultSetBuffer.getPreviousRow();

        if(cachedRow != null) {
            for(int j = 0; j < fields.length; j++) {
                Object fieldValue = cachedRow.elementAt(j);
                fields[j].setValue(fieldValue);
            }
            return resultSetBuffer.getCursor();
        } else {
            return -1;
        }
    }


    public boolean first() {


        if(resultSetBuffer.isAtBeginning()) return false;

        Vector cachedRow = resultSetBuffer.getFirstRow();

        if(cachedRow != null) {
            for(int j=0; j<fields.length; j++) {
                Object fieldValue = cachedRow.get(j);
                fields[j].setValue(fieldValue);
            }
            return true;
        } else {
            return false;
        }
    }

    public int removeCurrentRow() throws SQLException {

        if(resultSetBuffer == null) return -1;

        resultSetBuffer.removeCurrentRow();
        int cur = resultSetBuffer.getCursor();
//        System.out.println("CURSOR IS NOW " + cur);
        return cur;

    }

    public void showRow(int row) {
        resultSetBuffer.setCursor(row);
        current();
    }

    public boolean maybeShowRow(int row) {
        boolean b = resultSetBuffer.maybeSetCursor(row);
        if(false == b) return false;
        current();
        return true;
    }

    public boolean isEndOfResultsReached() {
        return resultSetBuffer.isEndOfResultsReached();
    }

    public boolean hasCurrentResultSet() {
        return resultSetBuffer != null && resultSetBuffer.size() > 0;
    }

    public int getRowsRead() {
        if(resultSetBuffer == null) return 0;
        return resultSetBuffer.size();
    }

    public void replaceCurrentRow(ResultSet rs) 
        throws SQLException
    {

        // replace the current row in the buffer
        resultSetBuffer.replaceCurrentRow(rs);
        
        // now display the newly read row in the form
        current();
    }

    public Vector[] getCurrentRowset() {
        if(resultSetBuffer == null) return null;
        return resultSetBuffer.getCurrentRowset();
    }

    public Vector getCurrentRowClone() {
        if(resultSetBuffer == null) return null;
        return resultSetBuffer.getCurrentRowClone();
    }

    public void populateFields(Vector v) {
        for(int j=0; j<fields.length; j++) {
            if(fields[j].isTypeDisplayable()) {
                fields[j].setValue(v.get(j));
            }
        }
    }

    public Vector getContentsOfFields() {
        Vector v = new Vector();
        for(int j=0; j<fields.length; j++) {
            v.add(fields[j].getText());
        }
        return v;
    }
        

    public void addPopupMenuManager(PopupMenuManager popupManager) {
        this.addMouseListener(popupManager);
        Component[] comps = this.getComponents();
        for(int j=0; j<comps.length; j++) {
            if(comps[j] instanceof JScrollPane) {
                ((JScrollPane) comps[j]).getViewport().getView().addMouseListener(popupManager);
            }
            else {
                comps[j].addMouseListener(popupManager);
            }
        }
    }

}
