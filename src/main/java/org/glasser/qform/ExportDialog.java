/* ====================================================================
 * Copyright (c) 1998 - 2003 David F. Glasser.  All rights
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
import org.glasser.util.Formatter;
import org.glasser.sql.*;
import org.glasser.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.HashMap;


public class ExportDialog extends JDialog implements ActionListener {



    static class IndexedCheckBox extends JCheckBox {

        public int index = 0;

        public IndexedCheckBox(int index) {
            super();
            this.index = index;
        }
    }

    JPanel topPanel = new JPanel();
    JPanel scrollHolder = new JPanel();
    JPanel buttonPanel = new JPanel();

    JTextField txtTableName = new JTextField();
    JTextField txtTerminalChar = new JTextField();

    JButton btnOk = new JButton("OK");
    JButton btnCancel = new JButton("Cancel");

    Object[][] formFields =
    {
        {txtTableName, "Table Name", "Name of the table that will be used in each insert statement."}
        ,{txtTerminalChar, "Terminal Character(s)", "Optional character(s) that will be appended to the end of each insert statement. (Usually \";\")"}
    };

    Object[][] buttonConfig = 
    {
         {btnOk,   "O", "OK", "Export resultset."}
        ,{btnCancel, "C", "CANCEL", "Cancel the export operation."}
    };


    public ExportDialog(Frame parent) {
        super(parent);

        JPanel cp = new JPanel();
        cp.setLayout(new BorderLayout());

        GUIHelper.buildFormPanel(topPanel, formFields, -1, 5, null, Color.black, false, -1);

        cp.add(topPanel, BorderLayout.NORTH);

        cp.add(scrollHolder, BorderLayout.CENTER);

        buttonPanel.setLayout(new FlowLayout());
        Border defaultBorder = txtTerminalChar.getBorder();
        txtTerminalChar.setBorder(new CompoundBorder(defaultBorder, new EmptyBorder(0, 15, 0, 0)));
        txtTerminalChar.setText(";");
        txtTerminalChar.setFont(new Font("Dialog", Font.BOLD, 13));
        topPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
        scrollHolder.setLayout(new BorderLayout());
        scrollHolder.setBorder(new EmptyBorder(10, 10, 10, 10));

        for(int j=0; j<buttonConfig.length; j++) {
            JButton button = (JButton) buttonConfig[j][0];
            button.setMnemonic(((String) buttonConfig[j][1]).charAt(0));
            button.setActionCommand((String) buttonConfig[j][2]);
            button.setToolTipText((String) buttonConfig[j][3]);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

//        buttonPanel.setBackground(Color.pink);
//        topPanel.setBackground(Color.pink);
//        cp.setBackground(Color.blue);
        cp.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(cp);



    }

    private String[] columnNames = null;

    private Formatter[] formatters = null;

    public String[] getColumnNames() {
        return columnNames;
    }

    public Formatter[] getFormatters() {
        return formatters;
    }

    public String getTableName() {
        if(columnNames == null) return null;
        return txtTableName.getText();
    }

    public String getTerminal() {
        if(columnNames == null) return null;
        return txtTerminalChar.getText();
    }


    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals("OK")) {
            exportPanel.update();
            columnNames = exportPanel.getColumnNames();
            formatters = exportPanel.getFormatters();
            defaultTerminals[currentExportType] = this.txtTerminalChar.getText();
            setVisible(false);
        }
        else if(command.equals("CANCEL")) {

            columnNames = null;
            formatters = null;
            setVisible(false);
        }
    }

    ExportPanel exportPanel = null;

    HashMap cachedPanels = new HashMap();

    int currentExportType = EXPORT_INSERT_STATEMENTS;

    public void openDialog(TableInfo ti, int exportType) {

        if(exportType == EXPORT_CSV) {
            topPanel.setVisible(false);
        }
        else {
            topPanel.setVisible(true);
        }

        columnNames = null;
        formatters = null;
        currentExportType = exportType;

        txtTableName.setText(ti.getQualifiedTableName());
        exportPanel = getExportPanel(ti, exportType);
        
        scrollHolder.removeAll();

        JScrollPane sp = new JScrollPane(exportPanel);
        scrollHolder.add(sp, BorderLayout.CENTER);

        this.txtTerminalChar.setText(defaultTerminals[exportType]);

        pack();

        // don't let the dialog be more than 80% of the screen height
        Dimension size = this.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        size = (Dimension) size.clone();
        size.height = Math.min((int) (screenSize.height * .8), size.height);
        setSize(size);
        setModal(true);
        setVisible(true);
    }


    public final static int EXPORT_INSERT_STATEMENTS = 0;

    public final static int EXPORT_CSV = 1;

    HashMap[] panelCaches = {new HashMap(), new HashMap()};

    String [] defaultTerminals = {";", ""};

    private ExportPanel getExportPanel(TableInfo ti, int exportType) {


        ExportPanel panel = (ExportPanel) panelCaches[exportType].get(ti);
        if(panel != null) return panel;
        switch(exportType) {
            case EXPORT_INSERT_STATEMENTS :
                panel = new InsertExportPanel(ti);
                break;
            case  EXPORT_CSV :
                panel = new CsvExportPanel(ti);
                break;
            default : // should never be reached because a NullPointer would already have been thrown
                throw new IllegalArgumentException("Invalid export type: " + exportType);
        }

        // explicitly set the sizes
        Dimension d = panel.getPreferredSize();
        panel.setMinimumSize((Dimension) d.clone());
        panel.setPreferredSize((Dimension) d.clone());

        panelCaches[exportType].put(ti, panel);

        return panel;

    }
                


}









