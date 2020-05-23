/* ====================================================================
 * Copyright (c) 1998 - 2004 David F. Glasser.  All rights
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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.*;
import org.glasser.util.*;
import org.glasser.util.comparators.MethodComparator;
import org.glasser.sql.*;
import org.glasser.swing.*;
import org.glasser.swing.table.*;
import org.glasser.sql.*;
import javax.swing.event.*;




public class TableInfoPanel extends JPanel {

    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TableInfoPanel.class);

    private TableInfo tableInfo = null;

    private JTabbedPane tabbedPane = new JTabbedPane();

    protected final String[] columnNames =
    {
        "Ordinal"
        ,"PK"
        ,"Column Name"
        ,"Data Type"
        ,"SQL Type"
        ,"Nullable"
        ,"Size"
        ,"Decimal Digits"
        ,"Default"
    };
    
    protected final String[] getters = 
    {
         "getOrdinal"
        ,"getPkComponent"
        ,"getColumnName"
        ,"getTypeName"
        ,"getDataType"
        ,"getNullable"
        ,"getColumnSize"
        ,"getDecimalDigits"
        ,"getColumnDefaultValue"
    };

    private ListTableModel<Column> columnTableModel = null;

    private JTable columnTable = null;

    private ListTableModel<ForeignKey> fkeyModel = null;
    private JPanel fkeyPanel = new JPanel();
    private JTable fkeysTable = null;

    private ListTableModel<ForeignKeyColumn> fcolModel = new ListTableModel<>(new ForeignKeyColumnManager(), null);
    private JTable fcolTable = new JTable(fcolModel);



    private ListTableModel<ForeignKey> exkeyModel = null;
    private JPanel exkeyPanel = new JPanel();
    private JTable exkeysTable = null;

    private ListTableModel<ForeignKeyColumn> excolModel = new ListTableModel<>(new ExportedKeyColumnManager(), null);
    
    private JTable excolTable = new JTable(excolModel);



    private JTextField txtDsName = new JTextField();
    private JTextField txtTableName = new JTextField();
    private JTextField txtTableSchem = new JTextField();
    private JTextField txtTableType = new JTextField();


    private Object[][] fields1 =
    {
        {txtDsName, "Data Source"}
        ,{txtTableType, "Table Type"}

    };

    private Object[][] fields2 =
    {
         {txtTableSchem, "Table Schema"}
        ,{txtTableName, "Table Name"}
        
    };

    private Object[][] fields =
    {
         fields1[0]
        ,fields1[1]
        ,fields2[0]
        ,fields2[1]
    };


    /**
     * This comparator is used to sort the ForeignKey objects displayed on
     * the Foreign Keys tab.
     */
	private final static MethodComparator<ForeignKey> FOREIGN_KEY_COMPARATOR =
		new MethodComparator<ForeignKey> (ForeignKey.class, 
							 "getForeignTableName", 
							 false, 
							 false, 
							 new MethodComparator<ForeignKey>(ForeignKey.class, "getForeignKeyName"), 
							 false);


    /**
     * This comparator is used to sort the ForeignKey objects displayed
     * on the Exported Keys tab.
     */
	private final static MethodComparator<ForeignKey> EXPORTED_KEY_COMPARATOR =
		new MethodComparator<ForeignKey>(ForeignKey.class, 
							 "getLocalTableName", 
							 false, 
							 false, 
							 new MethodComparator<ForeignKey>(ForeignKey.class, "getForeignKeyName"), 
							 false);



    public TableInfoPanel(TableInfo tableInfo, String dataSourceName) 
        throws NoSuchMethodException
    {


        this.tableInfo = tableInfo;

        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        JPanel leftHeader = new JPanel();
        JPanel rightHeader = new JPanel();
        Font font = new Font("SansSerif", Font.PLAIN, 9);
        GUIHelper.buildFormPanel(leftHeader,fields1,  -1, 5, font, Color.black, false, -2);
        GUIHelper.buildFormPanel(rightHeader,fields2,  -1, 5, font, Color.black, false, -2);


        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = .5;
        gc.weighty = 0;
        gc.fill = gc.BOTH;
        gc.anchor = gc.CENTER;
        gc.insets = new Insets(0, 0, 0, 5);
        headerPanel.add(leftHeader, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 5, 0, 0);
        headerPanel.add(rightHeader, gc);



        for(int j=0; j<fields.length; j++) {
            JTextField field = (JTextField) fields[j][0];
            field.setFont(font);
            field.setEditable(false);
            field.setEnabled(false);
            field.setDisabledTextColor(Color.black);
        }

        txtDsName.setText(dataSourceName);
        txtTableSchem.setText(tableInfo.getTableSchem());
        txtTableName.setText(tableInfo.getTableName());
        txtTableType.setText(tableInfo.getTableType());

        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(headerPanel, BorderLayout.NORTH);

        add(tabbedPane, BorderLayout.CENTER);

        ColumnManager<Column> cm = new ReflectionColumnManager<Column>(columnNames, Column.class, getters, null);

        Column[] columns = (Column[]) tableInfo.getColumns();
        if(columns != null) columns = (Column[]) columns.clone();

        columnTableModel = new ListTableModel<Column>(cm, Arrays.asList(columns));

        columnTable = new JTable(columnTableModel);

        // make the table sortable
        PushButtonTableHeader columnTableHeader = new PushButtonTableHeader();
        columnTable.setTableHeader(columnTableHeader);
        columnTableHeader.addMouseListener(new ListTableModelSorter());


        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JScrollPane(columnTable), BorderLayout.CENTER);

        tabbedPane.addTab("Columns", p);

        // assemble the Foreign Keys tab.

        ForeignKey[] fkeys = tableInfo.getForeignKeys();
        if(fkeys != null) {

            fkeys = (ForeignKey[]) fkeys.clone();
    		Arrays.sort(fkeys, FOREIGN_KEY_COMPARATOR);

            tabbedPane.addTab("Foreign Keys", fkeyPanel);
            fkeyModel = new ListTableModel<>(
                new ReflectionColumnManager<ForeignKey>(new String[] {"Foreign Table", "Foreign Key Name"},
                                            ForeignKey.class,
                                            new String[] {"getForeignTableName", "getForeignKeyName"},
                                            null), Arrays.asList(fkeys));
    
            fkeyPanel.setLayout(new BoxLayout(fkeyPanel, BoxLayout.Y_AXIS));
    
            fkeysTable = new JTable(fkeyModel);
    
            
            // add a listener to the foreignTableList table that will cause each
            // foreign table's key columns to be displayed in the fcolTable
            // when the table selection changes.
            fkeysTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        int row = fkeysTable.getSelectedRow();
                        if(row > -1) {
                            ForeignKey f = (ForeignKey) fkeyModel.getObjectAtRow(row);
                            if(f != null) {
                                fcolModel.setDataList(f.getForeignKeyColumns());
                            }
                            else {
                                logger.debug("valueChanged(): FOREIGN KEY OBJECT IS NULL AT ROW {}", row);
                            }
                        }
                    }
                });
    
            if(fkeyModel.getRowCount() > 0) fkeysTable.getSelectionModel().setSelectionInterval(0, 0);
    
            fkeysTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    
            // now assemble the visual components of the Foreign Key Fields
            // tab.
            JLabel lbl1 = new JLabel("Foreign Keys");
            lbl1.setHorizontalAlignment(JLabel.LEFT);
            lbl1.setBorder(new EmptyBorder(4,5,4,0));
    
            JLabel lbl2 = new JLabel("Foreign Key Columns");
            lbl2.setHorizontalAlignment(JLabel.LEFT);
            lbl2.setBorder(new EmptyBorder(4,5,4,0));
    
            fkeyPanel.setLayout(new GridBagLayout());
            gc = new GridBagConstraints();
    
            gc.weightx = 1.0;
            gc.gridx = 0;
            gc.gridy = gc.RELATIVE;
            gc.fill = gc.BOTH;
    
            gc.weighty = 0;
            fkeyPanel.add(lbl1, gc);
    
            gc.weighty = .6;
            fkeyPanel.add(new JScrollPane(fkeysTable), gc);
    
            gc.weighty = 0;
            fkeyPanel.add(lbl2, gc);
    
            gc.weighty = .4;
            fkeyPanel.add(new JScrollPane(fcolTable), gc);
        }


        // assemble the Exported Keys tab.

        ForeignKey[] exkeys = tableInfo.getExportedKeys();
        if(exkeys != null) {

            exkeys = (ForeignKey[]) exkeys.clone();
			Arrays.sort(exkeys, EXPORTED_KEY_COMPARATOR);
    
            tabbedPane.addTab("Exported Keys", exkeyPanel);
            exkeyModel = new ListTableModel<ForeignKey>(
                new ReflectionColumnManager<ForeignKey>(new String[] {"Foreign Table", "Exported Key Name"},
                                            ForeignKey.class,
                                            new String[] {"getLocalTableName", "getForeignKeyName"},
                                            null), Arrays.asList(exkeys));
    
            exkeyPanel.setLayout(new BoxLayout(exkeyPanel, BoxLayout.Y_AXIS));
    
            exkeysTable = new JTable(exkeyModel);
    
            
            // add a listener to the foreignTableList table that will cause each
            // foreign table's key columns to be displayed in the excolTable
            // when the table selection changes.
            exkeysTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        int row = exkeysTable.getSelectedRow();
                        if(row > -1) {
                            ForeignKey f = exkeyModel.getObjectAtRow(row);
                            if(f != null) {
                                excolModel.setDataList(f.getForeignKeyColumns());
                            }
                            else {
                                logger.debug("valueChanged(): FOREIGN KEY OBJECT IS NULL AT ROW {}", row);
                            }
                        }
                    }
                });
    
            if(exkeyModel.getRowCount() > 0) exkeysTable.getSelectionModel().setSelectionInterval(0, 0);
    
            exkeysTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    
            // now assemble the visual components of the Foreign Key Fields
            // tab.
            JLabel lbl1 = new JLabel("Exported Keys");
            lbl1.setHorizontalAlignment(JLabel.LEFT);
            lbl1.setBorder(new EmptyBorder(4,5,4,0));
    
            JLabel lbl2 = new JLabel("Exported Key Columns");
            lbl2.setHorizontalAlignment(JLabel.LEFT);
            lbl2.setBorder(new EmptyBorder(4,5,4,0));
    
            exkeyPanel.setLayout(new GridBagLayout());
            gc = new GridBagConstraints();
    
            gc.weightx = 1.0;
            gc.gridx = 0;
            gc.gridy = gc.RELATIVE;
            gc.fill = gc.BOTH;
    
            gc.weighty = 0;
            exkeyPanel.add(lbl1, gc);
    
            gc.weighty = .6;
            exkeyPanel.add(new JScrollPane(exkeysTable), gc);
    
            gc.weighty = 0;
            exkeyPanel.add(lbl2, gc);
    
            gc.weighty = .4;
            exkeyPanel.add(new JScrollPane(excolTable), gc);
        }

		tabbedPane.addTab("DDL", new DDLPanel(tableInfo));


    }

}
