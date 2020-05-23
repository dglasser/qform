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

import javax.swing.*;
import org.glasser.util.*;
import org.glasser.sql.*;
import org.glasser.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.text.SimpleDateFormat;



public abstract class ExportPanel extends JPanel implements ActionListener {

    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ExportPanel.class);

    private String[] columnNames = null;

    private Formatter[] formatters = null;

    private JCheckBox[] checkboxes = null;

    private JTextField[] nameFields = null;

    private JComboBox[] formatterSelectors = null;

    private Column[] cols = null;

    protected abstract Object[] getFormatterChoices(int dataType);

    public ExportPanel(TableInfo ti) {

        cols = ti.getColumns();
        checkboxes = new JCheckBox[cols.length];
        nameFields = new JTextField[cols.length];
        formatterSelectors = new JComboBox[cols.length];

        setLayout(new GridBagLayout());
        GridBagConstraints gc0 = new GridBagConstraints();

        gc0.gridx = 0;
        gc0.fill = gc0.BOTH;
        gc0.anchor = gc0.CENTER;
        gc0.weightx = .5;

        GridBagConstraints gc1 = (GridBagConstraints) gc0.clone();
        GridBagConstraints gc2 = (GridBagConstraints) gc0.clone();

        gc1.gridx = 1;
        gc2.gridx = 2;
        gc0.weightx = 0;

        for(int j=0; j<cols.length; j++) {
            gc0.gridy = j;
            gc1.gridy = j;
            gc2.gridy = j;

            IndexedCheckBox cb = new IndexedCheckBox(j);
            checkboxes[j] = cb;
            cb.setSelected(true);
            
            add(cb, gc0);

            // the column's data type will determine
            // the choices that appear in the combo box.
            int type = cols[j].getDataType();

            if(DBUtil.isBinaryType(type)) {
                cb.setSelected(false);
            }
            
            // It's necessary that the ActionListener be added to the
            // checkbox after the possible call to setSelected() above.
            cb.addActionListener(this);


            JTextField field = new JTextField();
            field.setText(cols[j].getColumnName());
            field.setEnabled(cb.isSelected());
            field.setEditable(cb.isSelected());
            nameFields[j] = field;
            add(field, gc1);

            // populate the combo box with the appropriate choices for
            // this data type and export type.
            JComboBox<Object> cmb = new JComboBox<Object>(getFormatterChoices(type));

            cmb.setSelectedIndex(1);

            // this ActionListener will make the combo box editable whenever
            // the user selects the first (blank line) item, and non-editable
            // when one of the Formatter objects are selected from the list.
            cmb.addActionListener(new ActionListener() {

                    // this gets called when the user changes the selection on
                    // the combo box.
                    public void actionPerformed(ActionEvent e) {
                        
                        JComboBox combo = (JComboBox) e.getSource();
                        int selectedIndex = combo.getSelectedIndex();
                        Object o = combo.getSelectedItem();

                        logger.debug("actionPerformed(): {}/{}", selectedIndex, o);
                        
                        if(selectedIndex < 0) {
                            return;
                        }
                        if(selectedIndex == 0) {
                            combo.setEditable(true);
                            combo.requestFocus();
                        }
                        else {
                            combo.setEditable(false);

                            // this works around an apparent swing bug where the selected
                            // index seems to get changed by the call to setEditable(false).
                            if(o != null) combo.setSelectedItem(o);
                        }
                    }
                });
            cmb.setEnabled(cb.isSelected());
            formatterSelectors[j] = cmb;
            add(cmb, gc2);
        }
    }

    /**
     * This gets called whenever a JCheckBox is toggled.
     */
    public void actionPerformed(ActionEvent e) {

        // get the checkbox that fired this event, and read its index
        IndexedCheckBox chk = (IndexedCheckBox) e.getSource();
        int index = chk.index;

        // enable/disable the text field and the combo box to match
        // the selected state of the checkbox.
        nameFields[index].setEnabled(chk.isSelected());
        nameFields[index].setEditable(chk.isSelected());
        JComboBox cmb = formatterSelectors[index];
        cmb.setEnabled(chk.isSelected());
        // the editable state of the combo box is not affected by
        // this event.
    }

    public void update() {

        columnNames = new String[checkboxes.length];
        this.formatters = new Formatter[checkboxes.length];
        for(int j=0; j<checkboxes.length; j++) {
            if(checkboxes[j].isSelected()) {
                columnNames[j] = nameFields[j].getText();
                Object o = formatterSelectors[j].getSelectedItem();
                if(o == null) {
                    formatters[j] = new LiteralFormatter("");
                }
                else if(o instanceof Formatter) {
                    formatters[j] = (Formatter) o;
                }
                else {
                    formatters[j] = new LiteralFormatter(o.toString());
                }
            }
        }
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public Formatter[] getFormatters() {
        return formatters;
    }


    /**
     * This is a JCheckBox that knows what its index is in
     * the array of checkboxes.
     */
    public static class IndexedCheckBox extends JCheckBox {

        public int index = 0;

        public IndexedCheckBox(int index) {
            super();
            this.index = index;
        }
    }


    protected static class LiteralFormatter implements Formatter {

        String literal = null;
        public LiteralFormatter(String literal) {
            this.literal = literal;
        }

        public String getFormattedString(Object o) {
            return literal;
        }
    }


}
