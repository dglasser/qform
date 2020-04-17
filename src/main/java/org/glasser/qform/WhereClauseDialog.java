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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.border.EmptyBorder;

import org.glasser.swing.*;


public class WhereClauseDialog extends JDialog implements ActionListener {


    private String sql = null;

    private String tableName = null;
    
    private JTextArea txtInput = new JTextArea() {
            public boolean isManagingFocus() {
                return false;
            }
        };

    private JLabel lblHeader = new JLabel();

    private JButton btnPrev = new JButton();

    private JButton btnNext = new JButton();

    private JButton btnSubmit = new JButton("Submit");

    private JButton btnCancel = new JButton("Cancel");

    private Object[][] buttonConfig =
    {
        {btnPrev, "P", "PREVIOUS", "Edit the previous WHERE clause in the history list."}
        ,{btnNext, "N", "NEXT",     "Edit the next WHERE clause in the history list."}
        ,{btnSubmit, "S", "SUBMIT", "Submit a query with the given WHERE clause."}
        ,{btnCancel, "C", "CANCEL", "Cancel the query"}
        

    };

    /**
     * The index, within the history list, of the WHERE clause that's currently
     * being edited. It will be -1 if the history list is empty.
     */
    private int pointer = -1;

    /**
     * When a user closes the dialog with the Submit button, this will hold the
     * results of the editing, and can be retrieved by the getWhereClause() method.
     * It will always be non-null if the user clicks Submit; it will be an
     * empty String ("") if the user clicks Submit without entering anything. It will be
     * null if the user closes the dialog or clicks Cancel.
     */
    private String whereClause = null;

    private java.util.List historyList = null;

        
    


    public WhereClauseDialog(Frame parent) {
        super(parent);

        setTitle("Edit WHERE clause");

        JPanel panel = new JPanel();

        panel.setBorder(new EmptyBorder(10,10,10,10));

        panel.setLayout(new BorderLayout());

        panel.add(lblHeader, BorderLayout.NORTH);

        panel.add(txtInput, BorderLayout.CENTER);

        btnPrev.setIcon(GUIHelper.getImageIconFromClasspath("org/glasser/swing/images/Back16.gif"));
        btnNext.setIcon(GUIHelper.getImageIconFromClasspath("org/glasser/swing/images/Forward16.gif"));

        Dimension d = btnSubmit.getPreferredSize();
        d = (Dimension) d.clone();

        d.width = d.height;

        GUIHelper.setAllSizes(btnPrev,d);
        GUIHelper.setAllSizes(btnNext,d);

       

        

        JPanel buttonPanel = new JPanel();

        GUIHelper.buildButtonPanel(buttonPanel,buttonConfig,this);


        txtInput.setBorder(new ThinBevelBorder(ThinBevelBorder.LOWERED));
        txtInput.setPreferredSize(new Dimension(400, 150));
        txtInput.enableInputMethods(false);
        txtInput.setWrapStyleWord(true);
        txtInput.setLineWrap(true);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // make it so the ESCAPE key closes the dialog.
        KeyStroke esc = KeyStroke.getKeyStroke("ESCAPE");
        ButtonClicker closer = new ButtonClicker(btnCancel);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "_ESCAPE_");
        panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(esc, "_ESCAPE_");
        panel.getActionMap().put("_ESCAPE_", closer);


        setContentPane(panel);
        pack();

    }

    public void setVisible(boolean b) {
        if(b) throw new RuntimeException("WhereClauseDialog must be opened with the openDialog() method.");
    }


    public String getSQL(String tableName) {
        lblHeader.setText("SELECT * FROM " + tableName + " WHERE ");
        this.tableName = tableName;
        sql = null;
        txtInput.setText(null);
        setModal(true);
        txtInput.requestFocus();
        super.setVisible(true);


        return sql;
    }


    /**
     * This opens the dialog in a modal state, with the last item
     * in the historyList displayed for editing. When this method returns,
     * the results of the user editing can be retrieved with getWhereClause().
     */
    public void openDialog(String tableName, java.util.List historyList) {
        lblHeader.setText("SELECT * FROM " + tableName + " WHERE ");
        this.tableName = tableName;
        this.historyList = historyList;
        pointer = historyList.size() - 1;
        if(pointer > -1) txtInput.setText((String) historyList.get(pointer));
        else txtInput.setText(null);
        setModal(true);
        txtInput.requestFocus();
        super.setVisible(true);
    }


    /**
     * When a user closes the dialog, this returns the results of the editing.
     * The returned String will always be non-null if the user clicks Submit; it will be an
     * empty String ("") if the user clicks Submit without entering anything. It will be
     * null if the user closes the dialog or clicks Cancel.
     */
    public String getWhereClause() {
        return whereClause;
    }


    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        if(command.equals("SUBMIT")) {
            String input = txtInput.getText();
            if(input != null && (input = input.trim()).length() > 0) {
                // if the user included "WHERE" remove it.
                if(input.toUpperCase().indexOf("WHERE ") == 0) {
                    input = input.substring(6);
                }
                whereClause = input;
                sql = "SELECT * FROM " + tableName + " WHERE " + input; 
            }
            else {
                whereClause = "";
                sql = "SELECT * FROM " + tableName;
            }

            super.setVisible(false);

        }
        else if(command.equals("CANCEL")) {
            sql = null;
            whereClause = null;
            super.setVisible(false);
        }
        else if(command.equals("PREVIOUS")) {
            if(historyList.size() == 0
               || pointer == 0) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            pointer--;
            txtInput.setText((String) historyList.get(pointer));
            txtInput.requestFocus();
        }
        else if(command.equals("NEXT")) {
            if(historyList.size() == 0 || pointer > historyList.size() - 2) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            pointer++;
            txtInput.setText((String) historyList.get(pointer));
            txtInput.requestFocus();
        }
    }

}
