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
 *
 * $Source: /cvsroot/qform/qform/src/org/glasser/qform/QueryPanel.java,v $
 * $Revision: 1.21 $
 * $Author: dglasser $
 * $Date: 2005/06/06 03:06:34 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.qform;


import javax.swing.*;
import java.sql.*;
import java.util.*;
import javax.sql.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;

import org.glasser.sql.*;
import org.glasser.swing.table.*;
import org.glasser.swing.*;
import org.glasser.util.*;

public class QueryPanel extends JPanel implements ResultSetBufferListener, ActionListener {

    private static boolean isJava1point3 = !Util.isCurrentJavaVersionAtLeast("1.4");

    public static final int NO_RESULTSET = MainPanel.QUERY_PANEL_NO_RESULTSET;
    public static final int HAS_RESULTSET = MainPanel.QUERY_PANEL_HAS_RESULTSET;
    public static final int OPEN_FOR_ADD = MainPanel.QUERY_PANEL_OPEN_FOR_ADD;
    public static final int OPEN_FOR_UPDATE = MainPanel.QUERY_PANEL_OPEN_FOR_UPDATE;
    public static final int OPEN_FOR_QUERY = MainPanel.QUERY_PANEL_OPEN_FOR_QUERY;
    public static final int OPEN_FOR_CLONE = MainPanel.QUERY_PANEL_OPEN_FOR_CLONE;


    JToolBar toolBar = new JToolBar();


    JButton newQueryButton = new JButton();
    JButton whereClauseButton = new JButton();
    JButton addRowButton = new JButton();
    JButton updateRowButton = new JButton();
    JButton deleteRowButton = new JButton();
    JButton leftEndButton = new JButton();
    JButton leftButton = new JButton();
    JButton rightButton = new JButton();
    JButton executeButton = new JButton();
    JButton refreshButton = new JButton();
    JButton cancelButton = new JButton();




    /**
     * Maps keystrokes to toolbar buttons that are clicked
     * in response to the keystrokes.
     */
    public Object[][] keyMappings =
    {
         {"control Q",      "OPEN_FOR_QUERY",       newQueryButton}
        ,{"control W",      "WHERE_CLAUSE_QUERY",   whereClauseButton}
        ,{"control A",      "ADD_RECORD",           addRowButton}
        ,{"control M",      "MODIFY_RECORD",        updateRowButton}
        ,{"DELETE",         "DELETE_RECORD",        deleteRowButton}
        ,{"control ENTER",  "EXECUTE_ACTION",       executeButton}
        ,{"control R",      "REFRESH",              refreshButton}
        ,{"ESCAPE",         "CANCEL_ACTION",        cancelButton}
    };


    /**
     * Maps keystrokes to buttons that are clicked if the form tab is selected,
     * or are forward to the grid table (as KeyEvents) if the grid tab is selected.
     */
    public Object[][] keyMappings2 =
    {
         {"control HOME",   leftEndButton}
        ,{"HOME",           leftEndButton}
        ,{"END",            null}
        ,{"control END",    null}
        ,{"PAGE_DOWN",      rightButton}
        ,{"PAGE_UP",        leftButton}
        ,{"DOWN",           null}
        ,{"UP",             null}
    };


    /**
     * An instance of this class handles a single keystroke that invokes different
     * behavior depending on what tab is selected. If the grid tab is selected,
     * it simply sends a KeyEvent corresponding to its KeyStroke to the grid table.
     * If the form tab is selected, it will call doClick() on a particular button (provided
     * it has been passed a button in its constructor.)
     */
    private class ActionKeyManager extends AbstractAction {

        KeyStroke keyStroke = null;

        JButton toClick = null;

        public ActionKeyManager(KeyStroke keyStroke, JButton toClick) {
            this.keyStroke = keyStroke;
            this.toClick = toClick;
        }

        public void actionPerformed(ActionEvent e) {
            Component selectedTab = tabbedPane.getSelectedComponent();
            if(selectedTab == formTab) {
                if(toClick != null && toClick.isEnabled()) toClick.doClick();
            }
            else if(selectedTab == gridTab) {
                gridTable.processActionKey(keyStroke);
            }
        }

    }
            


    JButton popupButton = new JButton(". . .");

    JLabel statusLabel = new JLabel() {
            public void setText(String s) {

                // always make sure there's some text there, so the
                // label maintains its preferredSize.
                if(s == null || s.length() == 0) {
                    s = "                   ";
                }
                super.setText(s);
            }
        };
    

    Object[][] toolBarConfig = 
    {
         {newQueryButton,   "OPEN_FOR_QUERY",       "DataQuery2_20.png",        "New query. (Ctrl-Q)"}
        ,{whereClauseButton, "WHERE_CLAUSE_QUERY",  "SQLWizard32.gif",          "Query with custom WHERE clause. (Ctrl-W)"}
        ,{addRowButton,     "ADD_RECORD",           "NewRow20.gif",             "Add a row to the current table. (Ctrl-A)"}
        ,{updateRowButton,  "MODIFY_RECORD",        "UpdateRow20.png",          "Modify current row. (Ctrl-M)"}
        ,{deleteRowButton,  "DELETE_RECORD",        "DeleteRow20.png",          "Delete the current row. (Del)", "1"}
        ,{leftEndButton,    "GOTO_FIRST",           "VCRBegin20.png",           "Move to first record. (Home)"}
        ,{leftButton,       "GOTO_PREVIOUS",        "VCRBack20.png",            "Move to previous record."}
        ,{rightButton,      "GOTO_NEXT",            "VCRForward20.png",         "Move to next record.", "1"}
        ,{executeButton,    "EXECUTE_ACTION",       "ExecuteProject20.gif",     "Execute operation (Ctrl-Enter)"}
        ,{refreshButton,    "REFRESH",              "Redo20.png",               "Re-execute the current query. (Ctrl-R)"}
        ,{cancelButton,     "CANCEL_ACTION",        "Stop20.png",               "Cancel operation (ESC)", "1"}
        ,{popupButton,      "SHOW_POPUP",           null,                       "More options...", "1"}

    };

    private boolean[][] toolBarStates =
    {                  
        // Q       W        A      U       D       1st    prev   next    exec    refrsh  canc
         {true,   true,   true,   false, false,  false,  false,  false,  false,  false,  false, true}     // QUERY_PANEL_NO_RESULTSET = 3;       
        ,{true,   true,   true,   true,  true,   true,   true,   true,   false,  true,   false, true}     // QUERY_PANEL_RESULTSET = 4;          
        ,{false,  false,  false,  false, false,  false,  false,  false,  true,   false,  true,  false}    // QUERY_PANEL_OPEN_FOR_ADD = 5;       
        ,{false,  false,  false,  false, false,  false,  false,  false,  true,   false,  true,  false}    // QUERY_PANEL_OPEN_FOR_UPDATE = 6;    
        ,{false,  false,  false,  false, false,  false,  false,  false,  true,   false,  true,  false}    // QUERY_PANEL_OPEN_FOR_QUERY = 7;     
        ,{false,  false,  false,  false, false,  false,  false,  false,  true,   false,  true,  false}    // QUERY_PANEL_OPEN_FOR_CLONE = 8;
    };



    String[] columnNames = null;

    /**
     * To see debugging output from this class, start the program with the switch 
     * -DQueryPanel.debug 
     */
    private static boolean debug = System.getProperty("QueryPanel.debug") != null;


    private static Object[][] exportSelectedConfig =
    {
         {"To INSERT Statements...", "EXPORT_SELECTED_INSERT", null, "Export selected rows as INSERT statements."}
        ,{"To CSV File...",  "EXPORT_SELECTED_CSV", null, "Export selected rows as comma-separated values."}
    };

    private static Object[][] exportAllConfig =
    {
         {"To INSERT Statements...", "EXPORT_ALL_INSERT", null, "Export all rows read as INSERT statements."}
        ,{"To CSV File...",  "EXPORT_ALL_CSV", null, "Export all rows read as comma-separated values."}
    };

    private static Object[][] selectedOrAllConfig =
    {
         {"_Selected Row(s)", exportSelectedConfig, "S", null}
        ,{"_All Rows",        exportAllConfig,      "A", null}
    };

    private static Object[][] gridTabPopupConfig =
    {
         {"Window title...", "WINDOW_TITLE", "W", "Set the title for this window."}
        ,{"Table Metadata", "QUERYPANEL_TABLE_METADATA", "T", "View medadata for this table."}
        ,{"Select visible columns...", "COLUMN_MAP_DIALOG", "V", "Select which columns will be displayed in this table."}
		,{"CHECKBOX_Long-form dates", "TOGGLE_LONGFORM_DATES", "L", "Dispay dates in long format."}
        ,{"CHECKBOX_Horizontal scrollbar", "TOGGLE_HORIZONTAL_SCROLLBAR", "H", "Show or hide the horizontal scrollbar."}
    };

    private static Object[][] gridTabSubmenuConfig =
    {
         {"Select visible columns...", "COLUMN_MAP_DIALOG", "V", "Select which columns will be displayed in this table."}
        ,{"CHECKBOX_Long-form dates", "TOGGLE_LONGFORM_DATES", "L", "Dispay dates in long format."}
        ,{"CHECKBOX_Horizontal scrollbar", "TOGGLE_HORIZONTAL_SCROLLBAR", "H", "Show or hide the horizontal scrollbar."}
    };

    private static Object[][] gridTabPopupStateMappings =
    {

        {
            new Integer(NO_RESULTSET),
            new boolean[] {true, true, true, true, true}
        }
        ,{
            new Integer(HAS_RESULTSET),
            new boolean[] {true, true, true, true, true}
        }
    };

    private static HashMap gridPopupStateMap = Util.buildMap(gridTabPopupStateMappings);


    private static Object[][] extraOperationsPopupConfig =
    {
         {"Window title...", "WINDOW_TITLE", null, "Set the title for this window."}
        ,{"Table Metadata", "QUERYPANEL_TABLE_METADATA", null, "View medadata for this table."}
        ,{"Clone Record",   "CLONE_RECORD",              null, "Clone the current record."}
        ,{"_Grid View ",         gridTabSubmenuConfig,  "G",    "Configure grid view tab."} 
        ,{"_Export",            selectedOrAllConfig,    "X",    "Export query results to a file."}
    };


    private static Object[][] formTabPopupConfig = 
    {
         {"Window title...", "WINDOW_TITLE", "W", "Set the title for this window."}
        ,{"Table Metadata", "QUERYPANEL_TABLE_METADATA", "T", "View medadata for this table."}
        ,{"Add Record",         "ADD_RECORD",           "A",    "Add a record through the current query form."}
        ,{"Modify Record",      "MODIFY_RECORD",        "M",    "Modify the current record."}
        ,{"Delete Record",      "DELETE_RECORD",        "D",    "Delete the current record."}
        ,{"Clone Record",       "CLONE_RECORD",         "L",    "Clone the current record."}
    };


    private static Object[][] formPopupStateMappings =
    {
        {
            new Integer(NO_RESULTSET),
            new boolean[] {true, true, true, false, false, false}
        }
        ,{
            new Integer(HAS_RESULTSET),
            new boolean[] {true, true, true, true, true, true}
        }
    };

    HashMap formPopupStateMap = Util.buildMap(formPopupStateMappings);


    boolean[][] extraOpStates =
    {
         {true,     true,   false,  true,   false}
        ,{true,     true,   true,   true,   true}
        ,{false,    false,  false,  false,  false}
        ,{false,    false,  false,  false,  false}
        ,{false,    false,  false,  false,  false}
        ,{false,    false,  false,  false,  false}
    };


    private String[] stateNames =
    {
         null
        ,null
        ,null
        ,"NO_RESULTSET"
        ,"HAS_RESULTSET"
        ,"OPEN_FOR_ADD"
        ,"OPEN_FOR_UPDATE"
        ,"OPEN_FOR_QUERY" 
        ,"OPEN_FOR_CLONE"
    };


    Vector v;
    String tableName;
    String tableOwner;
    BaseForm baseForm = null;

    JPopupMenu extraItemsPopup = new JPopupMenu();

    javax.swing.JPopupMenu gridTabPopup = new JPopupMenu();
    javax.swing.JPopupMenu formTabPopup = new JPopupMenu();

    PopupMenuManager gridPopupManager = new PopupMenuManager(gridTabPopup);

    PopupMenuManager formPopupManager = new PopupMenuManager(formTabPopup);

    private JMenuItem[] gridTabPopupItems = null;

    private JMenuItem[] formTabPopupItems = null;

    private JCheckBoxMenuItem[] horizontalScrollbarMenuItems = null;

	private JCheckBoxMenuItem[] longformTimestampMenuItems = null;

    JTabbedPane tabbedPane = new JTabbedPane();

    JPanel formTab = new JPanel();

    JPanel gridTab = new JPanel();

    class GridTable extends JTable {

        /**
         * Sends a KeyEvent for the given KeyStroke to the gridTable. The 
         * KeyStroke should be an "action" key like HOME, PGUP, etc., not
         * a letter key. This allows the JTable to be scrolled programmatically
         * in response to user-pressed keys, even when it doesn't have the focus.
         */
        public void processActionKey(KeyStroke keyStroke) {

            processKeyEvent(new KeyEvent(this, 
                KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 
                keyStroke.getModifiers(), 
                keyStroke.getKeyCode(), 
                KeyEvent.CHAR_UNDEFINED));
        }

    };


    GridTable gridTable = new GridTable(); 

    PushButtonTableHeader gridTableHeader = new PushButtonTableHeader();

    JScrollPane gridScrollPane = new JScrollPane(gridTable);

    int state = NO_RESULTSET;

    final static int SELECT = 0;
    final static int DELETE = 1;
    final static int COUNT = 2;
    final static int INSERT = 3;
    final static int UPDATE = 4;
    final static int PK_SELECT = 5;


    final static int margin = 2;

    public String getTableName() {
        return tableName;
    }

    DataSource dataSource = null;

    StatusMessageDisplay status = null;

    TableInfo tableInfo = null;

    Integer dataSourceId = null;

    private String windowTitle = null;

    private String dataSourceDisplayName = null;

    SmartEventListenerList listeners = new SmartEventListenerList();

	FormattedCellRenderer longDateRenderer = new FormattedCellRenderer(null);

	FormattedCellRenderer defaultDateRenderer = new FormattedCellRenderer(java.text.DateFormat.getDateInstance());


	public void displayLongTimestampsInGrid(boolean b) {

		if(b) {
			gridTable.setDefaultRenderer(java.util.Date.class, longDateRenderer);
		}
		else {
			gridTable.setDefaultRenderer(java.util.Date.class, defaultDateRenderer);
		}

		for(int j=0; j<longformTimestampMenuItems.length; j++) { 
			longformTimestampMenuItems[j].setSelected(b);
		}

		AbstractTableModel model = (AbstractTableModel) gridTable.getModel();
		if(model != null) {
			model.fireTableDataChanged();
		}
	}


    QueryPanel(TableInfo ti, 
               DataSource dataSource, 
               Integer dataSourceId, 
               StatusMessageDisplay status, 
               ActionListener actionListener,
               Set editableTypes) {

        super();

        this.tableInfo = ti;
        this.dataSource = dataSource;
        this.dataSourceId = dataSourceId;
        this.status = status;

        tableName = ti.getTableName();

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        // add the toolbar buttons
        toolBar.addSeparator();
        toolBar.setBorder(new EmptyBorder(7, 10, 7, 5));
        GUIHelper.configureToolbar(toolBar,toolBarConfig,"org/glasser/qform/images", new Dimension(36, 36), actionListener);
        popupButton.setFont(new Font("Serif", Font.BOLD, 13));
        toolBar.setFloatable(false);

        popupButton.removeActionListener(actionListener);
        popupButton.addActionListener(this);

        add(toolBar, BorderLayout.NORTH);


//          this.getViewport().setLayout(new javax.swing.ViewportLayout());
//            this.getViewport().setLayout(null);
        try {
            tableOwner = ti.getTableSchem();
            if((tableOwner != null && tableOwner.length() == 0) || tableOwner == "<DEFAULT SCHEMA>") {
                tableOwner = null;
            }

//            ResultSet tableInfo = dbInfo.getColumns(null, tableOwner, tableName, "%");

            Column[] columns = ti.getColumns();

            v = new Vector(columns.length);

            columnNames = new String[columns.length];

            for(int j=0; j<columns.length; j++) {

                Column col = columns[j];
                columnNames[j] = col.getColumnName();


                tableOwner = col.getTableSchema();
                String columnName = col.getColumnName();
                JLabel label = new JLabel(columnName);

                // add a TextBox to the Vector for this field
                TextBox textBox = new TextBox(columnName,
                                              col.getDataType(),
                                              col.getTypeName(),
                                              col.getColumnSize(),
                                              col.getNullable(),
                                              col.getOrdinal(),
                                              label);

                textBox.setPkComponent(col.getPkComponent());

                textBox.setEditableTypes(editableTypes);

                textBox.setEditable(false);
                v.addElement(textBox);
            }


            baseForm = new BaseForm(v);

            JScrollPane sp = new JScrollPane(baseForm);
            formTab.setLayout(new BorderLayout());
            formTab.add(sp, BorderLayout.CENTER);

            sp.getVerticalScrollBar().setUnitIncrement(30);
            sp.getHorizontalScrollBar().setUnitIncrement(30);

            tabbedPane.addTab("Form View", formTab);

            gridTab.setLayout(new BorderLayout());

            gridTable.setTableHeader(gridTableHeader);
			
            gridTable.setModel(new ResultSetTableModel(columnNames));
            gridTab.add(gridScrollPane, BorderLayout.CENTER);

            tabbedPane.addTab("Grid View", gridTab);

            MenuItemListener menuItemListener = new MenuItemListener(status);

            GUIHelper.buildMenu(extraItemsPopup, extraOperationsPopupConfig, actionListener, menuItemListener, menuItemListener);
            GUIHelper.buildMenu(gridTabPopup, gridTabPopupConfig, actionListener, menuItemListener, menuItemListener);
            GUIHelper.buildMenu(formTabPopup, formTabPopupConfig, actionListener, menuItemListener, menuItemListener);

            horizontalScrollbarMenuItems = new JCheckBoxMenuItem[2];
            horizontalScrollbarMenuItems[0] = (JCheckBoxMenuItem) GUIHelper.findMenuItemByActionCommand(extraItemsPopup,
                                                                                    "TOGGLE_HORIZONTAL_SCROLLBAR");
            horizontalScrollbarMenuItems[1] = (JCheckBoxMenuItem) GUIHelper.findMenuItemByActionCommand(gridTabPopup,
                                                                                    "TOGGLE_HORIZONTAL_SCROLLBAR");


			longformTimestampMenuItems = new JCheckBoxMenuItem[2];
			longformTimestampMenuItems[0] = (JCheckBoxMenuItem) GUIHelper.findMenuItemByActionCommand(extraItemsPopup,
																					"TOGGLE_LONGFORM_DATES");
			longformTimestampMenuItems[1] = (JCheckBoxMenuItem) GUIHelper.findMenuItemByActionCommand(gridTabPopup,
																					"TOGGLE_LONGFORM_DATES");
 

            gridTabPopupItems = GUIHelper.getMenuItems(gridTabPopup);
            formTabPopupItems = GUIHelper.getMenuItems(formTabPopup);

            baseForm.addPopupMenuManager(formPopupManager);
    
            gridScrollPane.addMouseListener(gridPopupManager);
            gridTable.addMouseListener(gridPopupManager);
            gridTableHeader.addMouseListener(gridPopupManager);


            gridScrollPane.setToolTipText("Right-click for options menu.");
            gridTable.setToolTipText("Right-click for options menu.");

            setHorizontalScrollBarVisible(true);

            gridTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                    int lastSelection = -1;

                    /**
                     * Called whenever the value of the selection changes.
                     * @param e the event that characterizes the change.
                     */
                    public void valueChanged(ListSelectionEvent e) {

                        // convert this to a one-based index.
                        int selection = gridTable.getSelectedRow();
       
                        if(selection > -1 
                           && selection != baseForm.getCursorVal() 
                           && state == HAS_RESULTSET) {

//                          if(selection == lastSelection) {
////                                new Throwable().printStackTrace();
//                              return;
//                          }
                            if(debug) System.out.println("SELECTED ROW " + selection);
                            lastSelection = selection;
                            baseForm.showRow(selection);
                            displayRowNum(selection+1);

                        }
                    }
                });


            // add a ListTableModel sorter that will sort the ResultSetTableModel whenever
            // a column header is clicked.
            gridTableHeader.addMouseListener(new ListTableModelSorter());


            statusLabel.setText(null);
            statusLabel.setBorder(new CompoundBorder(new ThinBevelBorder(ThinBevelBorder.RAISED),
                new EmptyBorder(5,5,5,5)));

            statusLabel.setFont(new Font("Dialog", Font.PLAIN, 10));
            statusLabel.setForeground(Color.black);

            this.add(statusLabel, BorderLayout.SOUTH);


            // set all the key bindings that allow the form to be manipulated
            // with keys.
            for(int j=0; j<keyMappings.length; j++) {
                Object[] row = keyMappings[j];
                Object key = row[0];
                String actionName = (String) row[1];
                JButton button = (JButton) row[2];
                KeyStroke keyStroke = null;
                if(key instanceof KeyStroke) keyStroke = (KeyStroke) key;
                else keyStroke = KeyStroke.getKeyStroke((String) key);

                getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, actionName);
                getActionMap().put(actionName, new ButtonClicker(button));
            }

            for(int j=0; j<keyMappings2.length; j++) {
                KeyStroke key = KeyStroke.getKeyStroke((String) keyMappings2[j][0]);
                JButton button = (JButton) keyMappings2[j][1];
                Action action = new ActionKeyManager(key, button);
                String cmd = "_KEY_" + j;
                getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, cmd);
                getActionMap().put(cmd, action);

                // set these bindings for the tabbedPane as well, to replace the existing ones.
                tabbedPane.getInputMap(JComponent.WHEN_FOCUSED).put(key, cmd);
                tabbedPane.getActionMap().put(cmd, action);
            }


            // this code fixes inconsistent focus behavior between JDK 1.3 and 1.4. 
            tabbedPane.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        Component c = tabbedPane.getSelectedComponent();
                        if(c == formTab) {
                            if(state == HAS_RESULTSET || state == NO_RESULTSET) {
                                tabbedPane.requestFocus();
                            }
                        }
                        else if(c == gridTab) {
                            if(state == HAS_RESULTSET) {
                                tabbedPane.requestFocus();
                            }
                        }
                    }
                });


            setState(NO_RESULTSET);


        } catch(Exception ex) {
            if(debug) System.out.println("Exception in QueryPanel constructor: " + ex.toString());
            ex.printStackTrace();
            GUIHelper.exceptionMsg(ex);

        }
    }


    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public boolean getHasPK() {
        return baseForm.getHasPK();
    }

    public int getState() {
        return state;
    }

    public Vector[] getCurrentRowset() {
        return baseForm.getCurrentRowset();
    }

    public int getCursorVal() {
        return baseForm.getCursorVal();
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setDataSourceDisplayName(String dataSourceDisplayName) {
        this.dataSourceDisplayName = dataSourceDisplayName;
    }

    public String getDataSourceDisplayName() {
        return dataSourceDisplayName;
    }


    boolean formTabFrozen = false;

    public void freezeFormTab(boolean b) {
        if(debug) System.out.println("TRC: " + getClass().getName() + ".freezeFormTab("
            + b + ")");

        if(formTabFrozen == b) return;

        if(b) {
            tabbedPane.setSelectedComponent(formTab);
            tabbedPane.remove(gridTab);
            formTabFrozen = true;
         }
        else {
            tabbedPane.addTab("Grid View", gridTab);
            formTabFrozen = false;
        }
    }

    public void setState(int state)  {
        this.state = state;

        if(debug) System.out.println("QueryPanel.setState(" + stateNames[state] + ")");

        boolean focusFirstField = false;

        switch(state) {
            case HAS_RESULTSET :
                setFieldsEditable(false);
                showRequiredFields(false);
                showRowNum(baseForm.getCursorVal());
                freezeFormTab(false);
                break;
            case OPEN_FOR_QUERY :
                clearFields();
                clearRowNumDisplay();
                setFieldsEditable(true);
                showRequiredFields(false);
                freezeFormTab(true);
                focusFirstField = true;
                break;
            case NO_RESULTSET :
                status.setStatusMessage(null);
                clearRowNumDisplay();
                clearFields();
                setFieldsEditable(false);
                showRequiredFields(false);
                freezeFormTab(false);
                break;
            case OPEN_FOR_ADD :
                clearFields();
                clearRowNumDisplay();
                setFieldsEditable(true);
                showRequiredFields(true);
                freezeFormTab(true);
                focusFirstField = true;
                break;
            case OPEN_FOR_UPDATE :
                setFieldsEditable(true);
                clearRowNumDisplay();
                showRequiredFields(true);
                freezeFormTab(true);
                focusFirstField = true;
                break;
            case OPEN_FOR_CLONE :
                clearFields();
                clearRowNumDisplay();
                setFieldsEditable(true);
                showRequiredFields(true);
                freezeFormTab(true);
                baseForm.populateFields(baseForm.getCurrentRowClone());
                focusFirstField = true;
                break;


        }


        boolean[] menuStates = this.extraOpStates[state - 3];
        JMenuItem[] menuItems = GUIHelper.getMenuItems(extraItemsPopup);
        for(int j=0; j<menuItems.length; j++) {
            menuItems[j].setEnabled(menuStates[j]);
        }

        Integer n = new Integer(state);
        menuStates = (boolean[]) gridPopupStateMap.get(n);
        if(menuStates == null) {
            gridPopupManager.setPopupEnabled(false);
        }
        else {
            gridPopupManager.setPopupEnabled(true);
            for(int j=0; j<gridTabPopupItems.length; j++) {
                gridTabPopupItems[j].setEnabled(menuStates[j]);
            }
        }


        menuStates = (boolean[]) formPopupStateMap.get(n);
        if(menuStates == null) {
            formPopupManager.setPopupEnabled(false);
        }
        else {
            formPopupManager.setPopupEnabled(true);
            for(int j=0; j<formTabPopupItems.length; j++) {
                formTabPopupItems[j].setEnabled(menuStates[j]);
            }
        }

        boolean[] buttonStates = this.toolBarStates[state - 3];
        for(int j=0; j<buttonStates.length; j++) {
            JButton button = (JButton) toolBarConfig[j][0];
            button.setEnabled(buttonStates[j]);
        }


        fireEvent(QueryPanelEvent.STATE_CHANGED);

        // the focus is set after the event is fired, because it could
        // change it
        if(focusFirstField) {
            focusFirstTextBox();
        }
        else {
            tabbedPane.requestFocus();
        }

    }

    public int getTabState() {
        return state;
    }


    public boolean hasCurrentResultSet() {
        return baseForm.hasCurrentResultSet();
    }

//    protected void finalize() throws Throwable {
//        if(debug) System.out.println("QueryPanel.finalize() called.");
//        try {
////              baseForm.setResultSetBuffer(null);
//        } catch(Exception ex) {
//            ex.printStackTrace();
//            GUIHelper.exceptionMsg(ex);
//        }
//    }

    public void showState() {
//          if(debug) System.out.println("Scrollpane: " + toString());
//          if(debug) System.out.println("JPanel: " + baseForm.toString());
//          if(debug) System.out.println("backBuffer: size = " + backBuffer.size() + ", cursor = " + backBuffer.cursor);
    }

    /**
     * Toggle the editable state of all textboxes on the form.        
     */
    public void setFieldsEditable(boolean b) {
        baseForm.setFieldsEditable(b);
    }


    void showRequiredFields(boolean b) {
        baseForm.showRequiredFields(b);
    }


    /**
     * Clears the text fields.
     */
    public void clearFields() {
        baseForm.clearFields();
    }


    /**
     * 
     * Construct an SQL statement from the values entered in the form
     */
    public String getSQL(int type) 
        throws UnknownPrimaryKeyException
    {

        String header = "SELECT * ";

        StringBuffer buffer = null;
        switch(type) {
            case INSERT :
                // get only the fields that have something
                // in them to build the insert statement with.
                ArrayList list = new ArrayList(v.size());
                for(int j=0; j<v.size(); j++) {
                    TextBox textBox = (TextBox) v.get(j);
                    if(!Util.isNothing(textBox.getText())) {
                        list.add(textBox);
                    }
                }
                buffer = new StringBuffer(200);
                buffer.append("INSERT INTO ");
                buffer.append(getQualifiedTableName());
                buffer.append(" (");
                for(int j=0; j<list.size(); j++) {
                    TextBox tb = (TextBox) list.get(j);
                    if(j > 0) buffer.append(", ");
                    buffer.append(tb.getColumnName());
                }

                buffer.append(") VALUES (");
                for(int j=0; j<list.size(); j++) {
                    TextBox tb = (TextBox) list.get(j);
                    if(j > 0) buffer.append(", ");
                    buffer.append(tb.getValueClause(false));
                }

                buffer.append(")");

                return buffer.toString();
             
            case DELETE :
                header = "DELETE FROM " + this.getQualifiedTableName()
                         + " " + baseForm.getPkWhereClause();
                return header;
            case COUNT :
                header = "SELECT COUNT(*) ";
                break;
            case UPDATE :
                String setClause = baseForm.getSetClause();
                if(setClause == null) return null; // no fields were edited.
                return "UPDATE " + getQualifiedTableName() 
                + " SET " + setClause + " " +
                baseForm.getPkWhereClause();
            case PK_SELECT :
                return "SELECT * " + getFromClause() + " WHERE " + getPkWhereClause();
            case SELECT :
            default :   
                header = "SELECT * ";
                break;
        }

        String SQL = header + getFromClause() + " " + getWhereClause();

        if(debug) System.out.println("SQL: " + SQL);

        return SQL;
    }

    private String getFromClause() {

        return "FROM " + getQualifiedTableName();

    }

    public String getQualifiedTableName() {

        if(tableInfo == null) return null;
        return tableInfo.getQualifiedTableName();

    }

    public String getWhereClause() {

        String whereClause = null;

        for(int j = 0; j < v.size(); j++) {
            String condition = ((TextBox) v.elementAt(j)).getCondition();
            if(condition != null) {
                if(whereClause == null) whereClause = condition;
                else whereClause += (" AND " + condition);
            }
        }
        if(whereClause == null) whereClause = "";

        return whereClause;
    }

    /**
     * This builds a WHERE clause from the PK fields of the currently displayed row,
     * based on the values displayed in the form.
     */
    public String getPkWhereClause() 
        throws UnknownPrimaryKeyException
    {

        Vector fields = v;
        ArrayList pkFields = new ArrayList();
        for(int j=0; j<fields.size(); j++) {
            TextBox field = (TextBox) fields.get(j);
            if(field.isPkComponent()) {
                pkFields.add(field);
            }
            else if(baseForm.hasPK == false) {
                int dataType = field.getDataType().intValue();
                if(field.isTypeDisplayable()
                   && field.isNullable() == false
                   && field.getLength() > 0
                   && ((DBUtil.isCharType(dataType) && field.getLength() < 51) || DBUtil.isNumericType(dataType))) {
                    pkFields.add(field);
                }
            }
        }

        if(pkFields.size() == 0) {
            throw new UnknownPrimaryKeyException();
        }

        StringBuffer buffer = new StringBuffer();
        boolean needAnd = false;
        for(int j = 0; j < pkFields.size(); j++) {
            String condition = ((TextBox) pkFields.get(j)).getCondition();
            if(condition != null) {
                if(needAnd) {
                    buffer.append(" AND ");
                }
                else {
                    needAnd = true;
                }
                buffer.append(condition);
            }
        }

        return buffer.toString();

    }

    
    /**
     * Opens the form for a query.
     */
    public void newQuery() {
        setState(OPEN_FOR_QUERY);
    }

    public void addRow() {
        setState(OPEN_FOR_ADD);
    }

    public void cloneRecord() {
        setState(OPEN_FOR_CLONE);
    }

    public void focusFirstTextBox() {
        baseForm.focusFirstTextBox();
    }


    /**
     * Cancels a pending add, update or query.
     */
    public void cancelPendingAction() {
        if(this.hasCurrentResultSet() ) {
            current();

            setState(HAS_RESULTSET);

        } else {
            setState(NO_RESULTSET);

        }
    }


    public void deleteCurrentRow() {

        int reply = JOptionPane.showConfirmDialog(this, 
                                                  "Do you want to delete the current row?", 
                                                  "QueryForm" , 
                                                  JOptionPane.YES_NO_OPTION, 
                                                  JOptionPane.QUESTION_MESSAGE);

        if(reply == JOptionPane.NO_OPTION) return;


        // get the SQL statement to do the delete.
        String SQL = null;
        
        try {
            SQL = getSQL(DELETE);
        }
        catch(Exception ex) {
            String msg = "This record cannot be deleted because the primary key for this table is unknown, and could not be adequately surmised by the application.";
                
            GUIHelper.errMsg(this, msg, "Application Error");
            return;
        }


        if(debug) System.out.println(SQL);
        int rowsDeleted = -1;
        Connection conn = null;
        Statement s = null;
        boolean saveAutoCommit = false;
        boolean success = false;
        try {
            conn = dataSource.getConnection();
            s = conn.createStatement();
            saveAutoCommit = conn.getAutoCommit();            
            if(saveAutoCommit) conn.setAutoCommit(false);
            rowsDeleted = s.executeUpdate(SQL);
            if(debug) System.out.println("Num rows deleted: " + rowsDeleted);
            if(rowsDeleted < 1) {
                GUIHelper.errMsg(this, "The current row was not deleted because it has "
                                 + "been deleted or modified by another user.", 
                                 "Operation Failed");
                return;
            }
            else if(rowsDeleted > 1) {
                GUIHelper.errMsg(this, "The delete operation will be rolled back because more than one row was deleted.", 
                                 "Operation Failed");
                return;
            }

            conn.commit();
            success = true;

            int rowNum = baseForm.removeCurrentRow();
            if(debug) System.out.println("rowNum is " + rowNum);

            ResultSetTableModel model = (ResultSetTableModel) gridTable.getModel();
            model.fireTableDataChanged();
            
            if(rowNum < 0) {
                setState(NO_RESULTSET);
                updateStatusMessage();

            } 
            else {
                baseForm.current();
                updateStatusMessage();
            }

        } 
        catch(Exception ex) {
            String msg = "The following error occurred while attempting to delete the current record:\n\n" +
                         ex.getMessage();
            GUIHelper.errMsg(this, msg, "Database Error");
        }
        finally {
            if(success == false && conn != null) {
                try {
                    conn.rollback();
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            DBUtil.closeStatement(s);
            try {
                if(saveAutoCommit) conn.setAutoCommit(true);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            DBUtil.closeConnection(conn);
        }
    }


    public void executeAdd() {

        boolean hasInput = false;
        for(int j=0; j<v.size(); j++) {
            TextBox textBox = (TextBox) v.get(j);
            if(!Util.isNothing(textBox.getText())) {
                hasInput = true;
                break;
            }
        }

        if(! hasInput ) {
            GUIHelper.errMsg(this, "You must provide a value for at least one field to insert a row.", null);
            return;
        }

        Connection conn = null;
        try {

            String sql = getSQL(INSERT);
            if(debug) System.out.println(sql);

            // this call was added to work around an apparent bug in Access, where
            // if a current resultset was still open with unread records,
            // then the refresh query (the runQuery call below) would not find
            // the row that was just inserted.
            this.maybeCloseCurrentConnection();
            conn = dataSource.getConnection();
            Statement s = conn.createStatement();
            s.executeUpdate(sql);
            DBUtil.closeStatement(s);
            s = null;
            DBUtil.closeConnection(conn);
            conn = null;
            try {
                String pkWhereClause = getPkWhereClause();
                runQuery(pkWhereClause, NO_RESULTSET);                
            }
            catch(UnknownPrimaryKeyException upkex) {

                setState(NO_RESULTSET);

                GUIHelper.warningMsg(this,
                    "The row you entered has been successfully inserted, however it could not be re-read from the "
                    + "database and displayed because the primary key for this table is unknown."
                    + "\n\nYou may re-display the inserted row by searching for it with a form query. (Ctrl-Q)",
                    null);

            }
        } 
        catch(Exception ex) {
           ex.printStackTrace();
           this.clearCurrentResultSet();
           GUIHelper.errMsg(this, "The following database error occurred:\n"
                   + ex.getClass().getName() + "\n"
                   + ex.getMessage(), "Database Error");
        }
        finally {
            
            DBUtil.closeConnection(conn);
        }
    }








    public void executeUpdate() {

        setState(HAS_RESULTSET);

        Connection conn = null;
        ResultSet rs = null;
        Statement s = null;
        boolean saveAutoCommit = false;
        boolean autoCommitChanged = false;

        String sql = null;

        try {
            sql = getSQL(UPDATE);
        }
        catch(UnknownPrimaryKeyException ex) {

            // refresh the fields with the existing data
            current();

            GUIHelper.errMsg(this, "This record cannot be updated because the primary key for this table is unknown, and could not be adequately surmised by the application.",
                "Application Error");
            
            return;
        }

        try {
            
            if(debug) System.out.println(sql);
            if(sql == null) return;

            conn = dataSource.getConnection();
            saveAutoCommit = conn.getAutoCommit();            
            if(saveAutoCommit) {
                conn.setAutoCommit(false);
                autoCommitChanged = true;
            }

            s = conn.createStatement();
            int rowsUpdated = s.executeUpdate(sql);
            if(rowsUpdated > 1) {
                System.err.println("Too many rows updated: " + rowsUpdated 
                    + ". SQL: " + sql);
                conn.rollback();
                GUIHelper.errMsg(this,
                    "The update has been rolled back because " 
                    + rowsUpdated + " rows would have been modified.", "Database Error");
                if(saveAutoCommit) conn.setAutoCommit(true);
                return;
            }
            s.close();
            conn.commit();
            s = conn.createStatement();

            // this should not fail because of an UnknownPrimaryKeyException, since
            // the call to getSQL(UPDATE) succeeded.
            String sql2 = getSQL(PK_SELECT);
            rs = s.executeQuery(sql2);
            if(rs.next()) {
                baseForm.replaceCurrentRow(rs);
            }
            else {
                throw new RuntimeException("Refresh query returned no rows!");
            }

        } 
        catch(Exception ex) { 
            ex.printStackTrace();
            try {
                System.err.print("DATABASE ERROR, ROLLING BACK.");
                if(conn != null) conn.rollback();
            }
            catch(Exception ex2) {
                ex2.printStackTrace();
            }

            try {
                if(autoCommitChanged) conn.setAutoCommit(saveAutoCommit);
            }
            catch(Exception ex2) {
                ex2.printStackTrace();
            }
            GUIHelper.errMsg(this, "The following database error occurred:\n"
                   + ex.getClass().getName() + "\n"
                   + ex.getMessage(), "Database Error");
            showRowNum(baseForm.current());
        }
        finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(s);
            DBUtil.closeConnection(conn);
        }
    }




    /**
     * Executes a query based on criteria entered into the form.
     */
    public void runQuery() {
        runQuery(getWhereClause(), -1);
    }


    private Stack historyList = new Stack();

    private boolean lastQueryBlank = false;

    public java.util.List getHistoryList() {
        return historyList;
    }

    Connection currentConn = null;
    ResultSet currentResultSet = null;


    private void maybeCloseCurrentConnection() {

        if(currentResultSet != null) {
            if(debug) System.out.println("QUERYPANEL: CLOSING RESULTSET");
            DBUtil.closeResultSet(currentResultSet);
            currentResultSet = null;
        }
        else {
            if(debug) System.out.println("QUERYPANEL: CURRENT RESULTSET IS NULL");
        }
    
        if(currentConn != null) {
            if(debug) System.out.println("QUERYPANEL: CLOSING CONNECTION");
            DBUtil.closeConnection(currentConn);
            currentConn = null;
        }
        else {
            if(debug) System.out.println("QUERYPANEL: CURRENT RESULTSET IS NULL");
        }

    }

    private void clearCurrentResultSet() {
        maybeCloseCurrentConnection();
        baseForm.removeResultSetBuffer();
        ((ResultSetTableModel) gridTable.getModel()).setDataList(null);
    }

    public void dispose() {
        if(debug) System.out.println("QueryPanel.dispose(), closing connection.");
        this.clearCurrentResultSet();
    }



    public void runQuery(String whereClause, int noResultsTabState) {
        if(debug) System.out.println(whereClause);

        // close any currently open resultsets and connections.
        maybeCloseCurrentConnection();

		// remove any "sort arrows" from the grid table header.
		gridTableHeader.setSortedColumn(-1, false);

        Connection queryConn = null;
        ResultSet rs = null;

        String SQL = "SELECT * " + getFromClause();
        if(whereClause != null  && (whereClause = whereClause.trim()).length() > 0) {
            SQL = SQL + " WHERE " + whereClause;
        }

        if(debug) System.out.println("SQL: " + SQL);

        try {
            queryConn = dataSource.getConnection();
            endReached = false;

            Statement s = queryConn.createStatement();
            rs = s.executeQuery(SQL);

            ResultSetBuffer buffer = new ResultSetBuffer(rs, 250);
            buffer.addResultSetBufferListener(this);

            if(baseForm.setResultSetBuffer(buffer)) {

                if(baseForm.isEndOfResultsReached() == false) {
                    currentConn = queryConn;
                    currentResultSet = rs;
                }
                else {
                    DBUtil.closeResultSet(rs);
                    DBUtil.closeConnection(queryConn);
                }

                showRowNum(0);


                setState(HAS_RESULTSET);

                // push the SQL for this query onto the stack of 
                // previous queries. Don't do it if it already matches
                // the one on the top of the stack, however.
                if(whereClause != null && whereClause.length() > 0) {
                    lastQueryBlank = false;
                    if(historyList.size() > 0) {
                        String lastQuery = (String) historyList.peek();
                        if(lastQuery.equals(whereClause) == false) {
                            historyList.push(whereClause);
                        }
                    }
                    else {
                        historyList.push(whereClause);
                    }
                }
                else {
                    lastQueryBlank = true;
                }

//                gridTable.setModel(new ResultSetTableModel(buffer));
//                ResultSetTableModel model = (ResultSetTableModel) gridTable.getModel();
                ((ResultSetTableModel) gridTable.getModel()).setDataList(buffer);
//                model.setColumnMappings(visibleGridColumns);
//                model.fireTableDataChanged();



            } 
            else { // no results were found.

                DBUtil.closeResultSet(rs);
                DBUtil.closeConnection(queryConn);

                ((ResultSetTableModel) gridTable.getModel()).setDataList(null);

                // if a valid state was passed in, set the window to that state.
                if(noResultsTabState != -1) setState(noResultsTabState);

                // alert the user.
                GUIHelper.infoMsg(this, "This query did not return any rows.",
                        null);
            }
        } 
        catch(SQLException ex) {
            ex.printStackTrace();
            DBUtil.closeResultSet(rs);
            DBUtil.closeConnection(queryConn);
            GUIHelper.errMsg(this, "The following database error occurred:\n"
                   + ex.getClass().getName() + "\n"
                   + ex.getMessage(), "Database Error");
            if(debug) System.out.println("Query exception: " + ex.toString());

            if(noResultsTabState != -1) setState(noResultsTabState);
        }
    }

    public void rerunLastQuery() {
        int currentSelection = baseForm.getCursorVal();
        if(lastQueryBlank) runQuery(null, this.NO_RESULTSET);
        else runQuery((String) historyList.peek(), this.NO_RESULTSET);
        boolean b = baseForm.maybeShowRow(currentSelection);
        if(b) showRowNum(currentSelection);
    }

    /**
     * Displays the current record in the resultset.
     */
    public void current() {
        showRowNum(baseForm.current());
    }

    boolean endReached = false;

    boolean atBeginningFlag = false;

    /**
     * Displays the next record in the resultset.
     */
    public void next() {



        try {

            if(state != HAS_RESULTSET) {
                GUIHelper.infoMsg(this, "There are no query results to display.", null);
                return;
            }

            int nextRecNum = baseForm.next();
            if(nextRecNum > 0) {
                showRowNum(nextRecNum);
            } 
            else {

                status.setStatusMessage("End of resultset reached.");

                // if there are no more rows in the resultset, display a message.
                boolean isEnabled = rightButton.isEnabled();

                // the enabled state of the button is checked first. It's turned off while
                // the message box is popped up to prevent multiple popups when the user
                // presses and holds the PGDN key.
                if(isEnabled) {
                    rightButton.setEnabled(false);
                    GUIHelper.infoMsg(this, "There are no more records to display.", null);
                    rightButton.setEnabled(true);
                    displayRowNum();
                    current();
                }
            }
        } catch(Exception ex) {
            GUIHelper.errMsg(this, "An unexpected error occurred:\n"
                    + ex.getClass().getName() + "\n"
                    + ex.getMessage(), "Application Error");
            if(debug) System.out.println("next() exception: " + ex.getMessage());
            ex.printStackTrace();

        }

        
    }


    /**
     * Displays the previous record.
     */
    public void previous() {

        if(state != HAS_RESULTSET) {
            GUIHelper.infoMsg(this, "There are no query results to display.", null);
            return;
        }

        int prevRowNum = baseForm.previous();
        if(prevRowNum > -1) {
            showRowNum(prevRowNum);
        } 
        else {
            boolean isEnabled = leftButton.isEnabled();

            // the enabled state of the button is checked first. It's turned off while
            // the message box is popped up to prevent multiple popups when the user
            // presses and holds the PGUP key.
            if(isEnabled) {
                status.setStatusMessage("Beginning of resultset reached.");
                leftButton.setEnabled(false);
                GUIHelper.infoMsg(this, "There are no previous records to display.", null);
                leftButton.setEnabled(true);
                displayRowNum();
            }
        }
    }



    /**
     * Displays the first record.
     */
    public void first() {

        if(state != HAS_RESULTSET) {
            GUIHelper.infoMsg(this, "There are no query results to display.", null);
            return;
        }

        if(baseForm.isAtBeginning()) {
            status.setStatusMessage("Beginning of resultset reached.");
            boolean isEnabled = leftEndButton.isEnabled();

            // the enabled state of the button is checked first. It's turned off while
            // the message box is popped up to prevent multiple popups when the user
            // presses and holds the HOME key.
            if(isEnabled) {
                leftEndButton.setEnabled(false);
                GUIHelper.infoMsg(this, "The first record is already displayed.", null);
                leftEndButton.setEnabled(true);
                displayRowNum();
            }
            return;
        }

        baseForm.first();
        gridTable.getSelectionModel().setSelectionInterval(0, 0);
        displayRowNum();
    }

    public void updateStatusMessage() {

        Component visibleTab = tabbedPane.getSelectedComponent();

        switch(state) {
            case NO_RESULTSET :
                statusLabel.setText(null);
                if(visibleTab == this.gridTab) {
                    status.setStatusMessage("Right-click for options menu.");
                }
                else {
                    status.setStatusMessage(null);
                }
                break;
            case HAS_RESULTSET :
                displayRowNum();
                break;
            default :
                status.setStatusMessage(null);
        }
    }

    public void showRowNum(int rowNum) {
        if(debug) System.out.println("TRACE:" + getClass().getName() + ".showRowNum()");
        displayRowNum(rowNum+1); 

        // select the row for this record in the table.
        gridTable.getSelectionModel().setSelectionInterval(rowNum, rowNum);

        // now make sure the selected row is visible.
        Rectangle r = gridTable.getCellRect(rowNum, 0, true);
        gridTable.scrollRectToVisible(r);
    }


    public void displayRowNum() {
        int num = baseForm.getCursorVal();
        if(num > -1) displayRowNum(num+1);
    }

    public void clearRowNumDisplay() {
        statusLabel.setText(null);
    }

    public void displayRowNum(int rowNum) {
        boolean endReached = baseForm.isEndOfResultsReached();
        StringBuffer buffer = new StringBuffer(50);
        buffer.append(" Row ");
        buffer.append(rowNum);
        buffer.append(" of ");
        buffer.append(baseForm.getRowsRead());
        if(endReached == false) buffer.append(" read.");
//        status.setStatusMessage(buffer.toString());


        statusLabel.setText(buffer.toString());
    }

    class GetCount extends Thread {

        int rowCount = -1;

        public int getRowCount() {
            return rowCount;
        }

        public void run() {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                String SQL = getSQL(COUNT);
                Statement s = conn.createStatement();
                ResultSet rst = s.executeQuery(SQL);
                if(rst.next()) rowCount = rst.getInt(1);
            } 
            catch(Exception ex) {
                rowCount = -100;
                ex.printStackTrace();
                GUIHelper.exceptionMsg(ex);
            }
            finally {
                DBUtil.closeConnection(conn);
            }
        }
    }


//    static ColumnMapDialog columnMapDialog = new ColumnMapDialog(null);
//
//    static {
//        GUIHelper.centerWindowOnScreen(columnMapDialog);
//    }

    int[] visibleGridColumns = null;


    public int[] getVisibleGridColumns() {
        return visibleGridColumns;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setVisibleGridColumns(int[] visibleGridColumns) {
        this.visibleGridColumns = visibleGridColumns;
        if(visibleGridColumns != null) {
            ResultSetTableModel model = (ResultSetTableModel) gridTable.getModel();
            model.setColumnMappings(visibleGridColumns);
            model.fireTableStructureChanged();
        }

		gridTableHeader.setSortedColumn(-1, false);
    }


    /**
     * This method is called to notify a listener that a ResultSetBuffer has
     * read more rows from its ResultSet.
     */
    public void moreRowsRead(ResultSetBufferEvent e) {

        // we'll remove any arrows from the table header that might indicate
        // a sorted buffer, to let the user no that the buffer contents may no
        // longer be in sorted order.
        gridTableHeader.setSortedColumn(-1, true);
        displayRowNum();
        
    }
    /**
     * This method is called to notify a listener that a ResultSetBuffer has
     * read the last row of its ResultSet.
     */
    public void endOfResultsReached(ResultSetBufferEvent e) {
        this.maybeCloseCurrentConnection();
        displayRowNum();
    }

    public void bufferSorted(ResultSetBufferEvent e) {
        int rowNum = baseForm.getCurrentRowNum();
        baseForm.showRow(rowNum);
        this.showRowNum(rowNum);
        displayRowNum();

        // this works around an apparent bug in JDK 1.3
        // that causes garbage to appear in the area of the JScrollPane
        // not covered by the JTable, when the JTable is narrower
        // than the JScrollPane.
        if(isJava1point3 && isHorizontalScrollBarVisible()) {
            gridScrollPane.repaint();
        }
    
    }



    /**
     * This returns an array containing the indexes of all of the
     * rows selected in the grid table. If there is no current ResultSet,
     * null is returned. If no table rows are selected, a single-element
     * array containing the index of the row displayed in the form is
     * returned.
     */
    public int[] getSelectedIndexes() {

        int[] selectedIndexes = gridTable.getSelectedRows();
        if(selectedIndexes == null || selectedIndexes.length == 0) {
            int cursor = baseForm.getCursorVal();
            if(cursor < 0) return null;
            return new int[] {cursor};
        }
        return selectedIndexes;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals("SHOW_POPUP")) {
            extraItemsPopup.show(popupButton, 0, 36);
        }
    }

    public void addQueryPanelListener(QueryPanelListener listener) {

        listeners.add(QueryPanelListener.class, listener);

    }

    public void fireEvent(int type) {
        QueryPanelEvent event = new QueryPanelEvent(this, type);
        listeners.fireSmartEvent(event);
    }

    public void setHorizontalScrollBarVisible(boolean b) {

        boolean oldState = this.isHorizontalScrollBarVisible();

        if(b) {
            gridTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            gridScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }
        else {
            gridTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            gridScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        for(int j=0; j<horizontalScrollbarMenuItems.length; j++) {
            horizontalScrollbarMenuItems[j].setSelected(b);
        }

        // notify listeners if the state has changed.
        if(b != oldState) this.fireEvent(QueryPanelEvent.HORIZONTAL_SCROLLBAR_STATE_CHANGED);
    }

    public boolean isHorizontalScrollBarVisible() {
        return gridTable.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF;
    }

    public void setSelectedTab(int tabIndex) {
        tabbedPane.setSelectedIndex(tabIndex);
    }


}

