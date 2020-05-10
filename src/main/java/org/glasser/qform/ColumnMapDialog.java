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
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.glasser.swing.*;
import org.glasser.util.*;


public class ColumnMapDialog extends JDialog implements ActionListener {


    private JList list = new JList();

    private JButton btnSelectAll = new JButton("Select All");
    private JButton btnOK = new JButton("OK");
    private JButton btnCancel = new JButton("Cancel");


    private int[] selections = null;

    Object[][] buttonConfig = 
    {
         {btnSelectAll, "A", "SELECT_ALL", "Select all listed columns."}
        ,{btnOK,   "O", "OK", "Accept selections."}
        ,{btnCancel, "C", "CANCEL", "Discard selections."}
    };

    public ColumnMapDialog(Frame parent) {

        super(parent);
        setTitle("Select Visible Columns");

        JPanel cp = new JPanel();
        cp.setLayout(new BorderLayout());

        list.setToolTipText("Ctrl-click to select/deselect columns.");


        cp.setBorder(new EmptyBorder(10,10,10,10));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        JLabel prompt = new JLabel("Ctrl-click to select/deselect columns.");
        prompt.setBorder(new EmptyBorder(10, 0, 0, 0));
        centerPanel.add(prompt, BorderLayout.SOUTH);


        centerPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        cp.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.setLayout(new FlowLayout());
        for(int j=0; j<buttonConfig.length; j++) {
            JButton button = (JButton) buttonConfig[j][0];
            button.setMnemonic(((String) buttonConfig[j][1]).charAt(0));
            button.setActionCommand((String) buttonConfig[j][2]);
            button.setToolTipText((String) buttonConfig[j][3]);
            button.addActionListener(this);
            buttonPanel.add(button);
        }
        cp.add(buttonPanel, BorderLayout.SOUTH);

        addKeyListener(new EnterEscapeKeyHandler(btnOK, btnCancel));

        setContentPane(cp);

        list.setVisibleRowCount(20);

        setModal(true);
        pack();
    }

    public void openDialog(String[] columnNames, int[] selections) {

        list.setListData(columnNames);

        if(selections != null) list.setSelectedIndices(selections);

        setModal(true);
        selections = null;
        super.setVisible(true);

    }


    public int[] getSelections() {
        return selections;
    }


    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals("OK")) {
            selections = list.getSelectedIndices();
            if(selections.length == 0) selections = null;
            setVisible(false);
        }
        else if(command.equals("CANCEL")) {
            setVisible(false);
        }
        else if(command.equals("SELECT_ALL")) {
            int size = list.getModel().getSize();
            if(size > 0) {
                list.setSelectionInterval(0, size-1);
            }
        }
    }

}
