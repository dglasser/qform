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


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.glasser.sql.*;
import org.glasser.swing.*;

public class TableSelector extends JDialog implements ActionListener {

    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TableSelector.class);


    private class DataSourceListItem  implements Comparable<DataSourceListItem> {

        private String displayName = null;

        private Integer sourceId = null;

        public DataSourceListItem(Integer sourceId, String displayName) {
            this.sourceId = sourceId;
            this.displayName = "(" + sourceId + ") " + displayName;
        }

        public String toString() {
            return displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Integer getSourceId() {
            return sourceId;
        }

        public boolean equals(Object other) {
            try {
                return((DataSourceListItem) other).getSourceId().equals(sourceId);
            } catch(Exception ex) {
                return this == other;
            }
        }

        public int hashCode() {
            return sourceId.intValue();
        }

        public int compareTo(DataSourceListItem other) {
            try {
                return sourceId.compareTo(other.getSourceId());
            } catch(Exception ex) {
                return 0;
            }
        }
    }



    protected JComboBox<DataSourceListItem> sourceList = new JComboBox<>();

    protected Vector<DataSourceListItem> sourceVector = new Vector<>();

    protected DefaultComboBoxModel<DataSourceListItem> sourceModel = new DefaultComboBoxModel<>(sourceVector);

    private JComboBox<String> schemaList = new JComboBox<>();

    private ComboBoxModel<String> emptySchemaListModel = schemaList.getModel();

    private Vector<TableInfo> emptyVector = new Vector<>();

    private JList<TableInfo> tableList = new JList<>();

    private JButton btnOK = new JButton("OK");

    private JButton btnCancel = new JButton("Cancel");

    private JButton btnRefresh = new JButton();

    private HashMap<Integer, DefaultComboBoxModel<String>> schemaModelMap = new HashMap<>();

    private HashMap<Integer, HashMap<String, List<TableInfo>>> tableListMap = new HashMap<>();

    private Object[] selections = null;

    private TableInfo selectedTableInfo = null;

    private Integer selectedSourceId = null;

    private MainPanel mainPanel = null;

    private boolean blockNotifications = false;

    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    public void addDataSource(Integer sourceId, 
                              String sourceName, 
                              HashMap<String, List<TableInfo>> tables, 
                              String schema) 
    {
        blockNotifications = true;

        try {
            DataSourceListItem item = new DataSourceListItem(sourceId, sourceName);
            sourceVector.add(item);
            Collections.<DataSourceListItem>sort(sourceVector);
            sourceList.setModel(sourceModel);

            Vector<String> schemas = new Vector<String>();
            for(Iterator<String> i = tables.keySet().iterator(); i.hasNext(); ) {
                schemas.add(i.next());
            }

            Collections.<String>sort(schemas);

            schemaModelMap.put(sourceId, new DefaultComboBoxModel<String>(schemas));


            tableListMap.put(sourceId, tables);
            sourceList.setSelectedItem(item);

            if(schema != null) {
                schemaList.setSelectedItem(schema);
            }

        }
        finally {
            blockNotifications = false;
        }
    }

    public void removeDataSource(Integer sourceId) {

        for(int j=sourceVector.size()-1; j>=0; j--) {
            DataSourceListItem li = (DataSourceListItem) sourceVector.get(j);
            if(sourceId.equals(li.getSourceId())) {
                sourceVector.remove(j);
            }
        }

        logger.debug("removeDataSource(): New sourceModel size is {}", sourceModel.getSize());
        Object o = schemaModelMap.remove(sourceId);
        o = tableListMap.remove(sourceId);

        sourceList.setModel(sourceModel);
        try {
            sourceList.setSelectedIndex(-1);
        }
        catch(Exception ex) {
            logger.error("removeDataSource(): " + ex, ex );
        }
        schemaList.setModel(this.emptySchemaListModel);
        tableList.setListData(emptyVector);
        if(sourceList.getSelectedIndex() < 0 
           && sourceModel.getSize() > 0) {
            sourceList.setSelectedIndex(0);
        }
    }


    public TableSelector() {
        this(null);
    }


    public TableSelector(Frame parent) {

        super(parent);


        setTitle("Select a table");

        sourceList.setModel(sourceModel);



        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(new JLabel("Data Source"), BorderLayout.NORTH);
        p1.add(sourceList, BorderLayout.CENTER);


        JPanel panel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(10,10,10, 10));
        centerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        panel.setLayout(new BorderLayout());
        topPanel.setLayout(new BorderLayout());
        centerPanel.setLayout(new BorderLayout(0, 10));

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(new JLabel("Table Owner"), BorderLayout.NORTH);
        p2.add(schemaList, BorderLayout.CENTER);
        p2.setBorder(new EmptyBorder(10, 0, 0, 0));

        topPanel.add(p1, BorderLayout.NORTH);
        topPanel.add(p2, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(tableList), BorderLayout.CENTER);
        JPanel hdrPanel = new JPanel();
        hdrPanel.setLayout(new BorderLayout());
        centerPanel.add(hdrPanel, BorderLayout.NORTH);
        hdrPanel.add(new JLabel("Tables/Views (Views in ITALICS)"), BorderLayout.WEST);
        
        btnRefresh.setToolTipText("Refresh this list.");
        btnRefresh.addActionListener(this);
        btnRefresh.setPreferredSize(new Dimension(20, 20));
        hdrPanel.add(btnRefresh, BorderLayout.EAST);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        GUIHelper.enterPressesWhenFocused(btnOK);
        GUIHelper.enterPressesWhenFocused(btnCancel);

        buttonPanel.add(btnOK);
        buttonPanel.add(btnCancel);
        btnOK.setPreferredSize(btnCancel.getPreferredSize());
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnOK.addActionListener(this);
        btnCancel.addActionListener(this);
        this.addKeyListener(new EnterEscapeKeyHandler(btnOK, btnCancel));

        sourceList.addActionListener(this);
        schemaList.addActionListener(this);
        tableList.addMouseListener(new MouseAdapter() {
                                       public void mouseClicked(MouseEvent e) {
                                           if(e.getClickCount() == 2) {
                                               setSelections();
                                               if(selections != null) {
                                                   setVisible(false);
                                               }
                                           }
                                       }
                                   });
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableList.setCellRenderer(new TableInfoListCellRenderer());

        setModal(true);
        this.addComponentListener(new ComponentAdapter() {
                                      public void componentShown(ComponentEvent e) {
                                          tableList.requestFocus();
                                          if(tableList.getSelectedIndex() < 0 && tableList.getModel().getSize() > 0) {
                                              try {
                                                  tableList.setSelectedIndex(0);
                                              } catch(Exception ex) {
                                              }
                                          }
                                      }

                                      public void componentResized(ComponentEvent e) {
                                          int index = tableList.getSelectedIndex();
                                          if(index > -1) {
                                              tableList.ensureIndexIsVisible(index);
                                          }
                                      }
                                  });


        // make the OK button close the default so ENTER invokes it
        this.getRootPane().setDefaultButton(this.btnOK);

        // make the Escape key click the cancel button.
        KeyStroke esc = KeyStroke.getKeyStroke("ESCAPE");
        Action closer = new ButtonClicker(btnCancel);
        panel.getInputMap(panel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(esc, "_ESCAPE_");
        panel.getInputMap(panel.WHEN_IN_FOCUSED_WINDOW).put(esc, "_ESCAPE_");
        panel.getActionMap().put("_ESCAPE_", closer);
        
        

        setContentPane(panel);
        pack();


    }
    private boolean showingBusyCursor = false;
    private Cursor busyCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private Cursor normalCursor =  Cursor.getDefaultCursor();

    public void showBusyCursor() {
        if(showingBusyCursor) {
            return;
        }
        showingBusyCursor = true;
        this.setCursor(busyCursor);
    }

    public void showNormalCursor() {
        if(!showingBusyCursor) {
            return;
        }
        showingBusyCursor = false;
        this.setCursor(normalCursor);
    }


//    protected void handleSchemaChange

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if(source == sourceList) {

            DataSourceListItem sourceItem = (DataSourceListItem) sourceList.getSelectedItem();
            if(sourceItem == null) return;
            Integer sourceId = sourceItem.getSourceId();
            DefaultComboBoxModel<String> schemaModel = schemaModelMap.get(sourceId);
            schemaList.setModel(schemaModel);
            boolean save = blockNotifications;
            blockNotifications = true;
            try {
                schemaList.setSelectedIndex(0);
            }
            catch(Exception ex) {
                logger.error("actionPerformed(): " + ex, ex );
            }
            finally {
                blockNotifications = save;
            }

            if(mainPanel != null && !blockNotifications) {
                mainPanel.dataSourceSelected(sourceId.intValue());
            }
            return;
        }
            


        if(source == schemaList) {
            DataSourceListItem sourceItem = (DataSourceListItem) sourceList.getSelectedItem();
            if(sourceItem == null) return; // all lists are empty.
            Integer sourceId = sourceItem.getSourceId();
            String owner = (String) schemaList.getSelectedItem(); 
            HashMap<String, List<TableInfo>> schemaTables = tableListMap.get(sourceId);

            if(owner != null) {
                List<TableInfo> v = schemaTables.get(owner);
                if(v == null) {
                    v = emptyVector;
                }
                tableList.setListData(v.toArray(new TableInfo[v.size()]));
                tableList.requestFocus();
                try {
                    if(v.size() > 0) {
                        tableList.setSelectedIndex(0);
                    }
                } catch(Exception ex) {
                    logger.error("actionPerformed(): " + ex, ex );
                }
                if(mainPanel != null && !blockNotifications) {
                    mainPanel.schemaSelected(sourceId, owner);
                }
            }
        } 
        else if(source == btnCancel) {
            selections = null;
            setVisible(false);
        } 
        else if(source == btnOK) {
            setSelections();
            setVisible(false);
        }
        else if(source == btnRefresh) {
            setSelections();
            Integer sourceId = getSelectedSourceId();
            if(sourceId == null) { // shouldn't happen.
                return;
            }

            try {
                showBusyCursor();
                TableInfo[] tis = mainPanel.readTableInfos(sourceId);
                HashMap<String, List<TableInfo>> map = DBUtil.getTableInfoLists(tis, mainPanel.DEFAULT_SCHEMA);

                tableListMap.put(sourceId, map);
                String schema = (String) schemaList.getSelectedItem();
                if(schema != null) {
                    schemaList.setSelectedItem(schema);
                }
            }
            catch(SQLException ex) {
                logger.error("actionPerformed(): " + ex, ex );
            }
            finally {
                showNormalCursor();
            }
        }

    }

    /**
     * If a selection was made, returns an Object[3] array. The first element
     * is an Integer representing the DataSource id, the second element is
     * a String representing the schema, or table owner, and the third
     * element is the name of the table.
     */
    protected void setSelections() {

        selections = null;
        DataSourceListItem source = (DataSourceListItem) sourceList.getSelectedItem();
        if(source == null) return;

        selectedTableInfo = (TableInfo) tableList.getSelectedValue();
        selectedSourceId = (Integer) source.getSourceId();
        if(selectedTableInfo == null) {
            return;
        }
            

        selections = new Object[3];
        selections[0] = source.getSourceId();
        selections[1] = schemaList.getSelectedItem();
        selections[2] = selectedTableInfo;

    }


    public void setVisible(boolean b) {
        if(b) {
            selections = null;
            selectedTableInfo = null;
            selectedSourceId = null;
        }
        super.setVisible(b);
    }

    public Object[] getSelection() {
        return selections;
    }

    public TableInfo getSelectedTableInfo() {
        return selectedTableInfo;
    }

    public Integer getSelectedSourceId() {
        return selectedSourceId;
    }

    public void setSelectedSchema(String schema) {
        boolean save = blockNotifications;
        try {
            blockNotifications = true;
            schemaList.setSelectedItem(schema);
        }
        catch(Exception ex) {
            logger.error("setSelectedSchema(): schema=" + schema, ex );
        }
        finally {
            blockNotifications = save;
        }
    }


    public static void main(String[] args) throws Exception {
        TableSelector ts = new TableSelector();
//        ts.setDefaultCloseOperation(ts.EXIT_ON_CLOSE);
        Config config = new Config(new java.io.File("C:/0/qform.xml"));
        LocalDataSourceConfig[] lds = config.getLocalDataSourceConfigs();
        for(int j=0; j<lds.length; j++) {
            try {
                DataSource ds = DataSourceManager.getLocalDataSource(lds[j], null, null);
                Connection c = ds.getConnection();
                DatabaseMetaData dbmd = c.getMetaData();
                ResultSet rs = dbmd.getTables(null, null, "%", new String[] {"VIEW", "TABLE"});
                TableInfo[] tis = DBUtil.getTableInfos(rs);
                rs.close();
                c.close();
                HashMap<String, List<TableInfo>> map = DBUtil.getTableInfoLists(tis, "<DEFAULT SCHEMA>");
                ts.addDataSource(new Integer(j+1), lds[j].getDisplayName(), map, null);
            }
            catch(Exception ex) {
                logger.error("main(): " + ex, ex );
            }
        }

        ts.setModal(true);
        ts.setVisible(true);

        Object[] sels = ts.getSelection();

        ts.removeDataSource(new Integer(1));
        ts.setVisible(true);

        sels = ts.getSelection();


        ts.removeDataSource(new Integer(2));
        ts.setVisible(true);

        sels = ts.getSelection();

    } 
    
}
