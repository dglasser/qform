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



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;
import javax.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.xml.parsers.*;
import org.glasser.sql.*;
import org.glasser.swing.*;
import org.glasser.util.ExtensionClassLoader;
import org.glasser.util.Formatter;
import org.glasser.util.Util;
import org.glasser.util.comparators.*;
import org.w3c.dom.*;
import org.xml.sax.*;



public class MainPanel extends MDIPanel implements ActionListener, InternalFrameListener, QueryPanelListener {

    private static ResourceBundle bundle = ResourceBundle.getBundle("org.glasser.qform.Resources");

    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MainPanel.class);

    private boolean showSystemTables = System.getProperty("show.system.tables") != null;

    private Frame parent = null;

    private String configFileName = null;

    private Config config = null;

    private LocalDataSourceConfigDialog configDialog = null;

    private ColumnMapDialog columnMapDialog = null;

    private TableSelector tableSelector = null;

    private LoginDialog loginDialog = null;

    private WhereClauseDialog whereClauseDialog = null;

    private ExportDialog exportDialog = null;

    private AppendOverwriteDialog overwriteDialog = null;

    private JDialog aboutDialog = null;

    private JDialog sysInfoDialog = null;

    private ThirdPartyLafDialog tpLafDialog = null;

    private Component[] dialogs =  null;



    private static JButton tbNew = new JButton();
    private static JButton newFormButton = new JButton();
    private static JButton tbMetaData = new JButton();


    private static Object[][] toolBarConfig = 
    {
         {tbNew,            "DATA_SOURCE_DIALOG",   "DataConnection20.png",     "Create, modify or connect to a data source.", "1"}
        ,{newFormButton,    "NEW_QUERY_FORM",       "New20.gif",                "Select a table to query."}
        ,{tbMetaData,       "TABLE_METADATA",       "BCard20.png",              "Select a table to view its metadata."}

    };

    private boolean[][] toolBarStates =
    {                  
         {true,  false, false,}   // NO_CONNECTIONS = 0;                 
        ,{true,  true,  true, }   // HAS_CONNECTIONS = 1;                
        ,{true,  true,  true, }   // ALL_FRAMES_ICONIFIED = 2;           
        ,{true,  true,  true, }   // QUERY_PANEL_NO_RESULTSET = 3;       
        ,{true,  true,  true, }   // QUERY_PANEL_RESULTSET = 4;          
        ,{true,  true,  true, }   // QUERY_PANEL_OPEN_FOR_ADD = 5;       
        ,{true,  true,  true, }   // QUERY_PANEL_OPEN_FOR_UPDATE = 6;    
        ,{true,  true,  true, }   // QUERY_PANEL_OPEN_FOR_QUERY = 7;
        ,{true,  true,  true, }   // QUERY_PANEL_OPEN_FOR_CLONE = 5;
        ,{true,  true,  true, }   // TABLE_INFO_PANEL
    };


    private static String[][] connectMenuConfig =
    {
         {"Data Source...",     "DATA_SOURCE_DIALOG",      "D",    "Create, edit or connect to a data source." }
        ,{"Table Metadata...",  "TABLE_METADATA",          "T",    "View metadata for a table."}
        ,{"Close Window",       "CLOSE_WINDOW",            "C",    "Close the current window."}
        ,{"Exit",               "EXIT",                    "X",    "Exit the program."} 
    };

    private static String[][] connectMenuConfig_ =
    {
         {"DATA_SOURCE_DIALOG"}
        ,{"TABLE_METADATA"}
        ,{"CLOSE_WINDOW"}
        ,{"EXIT"} 
    };


    private boolean[][] connectMenuStates =
    {
         {true,  false,  false,  true}    // NO_CONNECTIONS = 0;                 
        ,{true,  true,   false,  true}    // HAS_CONNECTIONS = 1;                
        ,{true,  true,   false,  true}    // ALL_FRAMES_ICONIFIED = 2;           
        ,{true,  true,   true,   true}    // QUERY_PANEL_NO_RESULTSET = 3;       
        ,{true,  true,   true,   true}    // QUERY_PANEL_RESULTSET = 4;          
        ,{true,  true,   true,   true}    // QUERY_PANEL_OPEN_FOR_ADD = 5;       
        ,{true,  true,   true,   true}    // QUERY_PANEL_OPEN_FOR_UPDATE = 6;    
        ,{true,  true,   true,   true}    // QUERY_PANEL_OPEN_FOR_QUERY = 7;
        ,{true,  true,   true,   true}    // QUERY_PANEL_OPEN_FOR_CLONE = 8;
        ,{true,  true,   true,   true}    // TABLE_INFO_PANEL
    };


    private static Object[][] exportSelectedConfig =
    {
         {"To INSERT Statements...", "EXPORT_SELECTED_INSERT", "I", "Export selected rows as INSERT statements."}
        ,{"To CSV File...",  "EXPORT_SELECTED_CSV", "C", "Export selected rows as comma-separated values."}
    };

    private static Object[][] exportSelectedConfig_ =
    {
         {"EXPORT_SELECTED_INSERT"}
        ,{"EXPORT_SELECTED_CSV"}
    };


    private static Object[][] exportAllConfig =
    {
         {"To INSERT Statements...", "EXPORT_ALL_INSERT", "I", "Export all rows read as INSERT statements."}
        ,{"To CSV File...",  "EXPORT_ALL_CSV", "C", "Export all rows read as comma-separated values."}
    };

    private static Object[][] exportAllConfig_ =
    {
         {"EXPORT_ALL_INSERT"}
        ,{"EXPORT_ALL_CSV"}
    };




    private static Object[][] selectedOrAllConfig =
    {
         {"_Selected Row(s)", exportSelectedConfig, "S", null}
        ,{"_All Rows",        exportAllConfig,      "A", null}
    };

    private static Object[][] selectedOrAllConfig_ =
    {
         {"SELECTED_ROWS",      exportSelectedConfig_}
        ,{"ALL_ROWS",           exportAllConfig_}
    };


    private static Object[][] gridViewSubConfig =
    {
        {"Select visible columns...", "COLUMN_MAP_DIALOG", "V", "Select which columns will be displayed in this table."}
        ,{"CHECKBOX_Horizontal scrollbar", "TOGGLE_HORIZONTAL_SCROLLBAR", "H", "Show or hide the horizontal scrollbar."}
    };

    private static Object[][] gridViewSubConfig_ =
    {
         {"COLUMN_MAP_DIALOG"}
        ,{"TOGGLE_HORIZONTAL_SCROLLBAR", "CHECKBOX"}
    };



    private static Object[][] gridTabPopupConfig =
    {
        {"Select columns...", "COLUMN_MAP_DIALOG", null, "Select which columns will be displayed in this table."}
        ,{"Window title...", "WINDOW_TITLE", null, "Set the title for this window."}
        ,{"Table Metadata", "QUERYPANEL_TABLE_METADATA", null, "View medadata for this table."}
        ,{"_Export Selected", exportSelectedConfig, null, "Export selected rows to a file."}
        ,{"_Export All", exportAllConfig, null, "Export all rows read to a file."}
    };


    private static Object[][] queryformMenuConfig =
    {
         {"New Query Form...",  "NEW_QUERY_FORM",       "Q",    "Open a new query form."}
        ,{"Window title...",    "WINDOW_TITLE",         "T",   "Set the window title for the current query form."}
        ,{"Form Query",         "OPEN_FOR_QUERY",       "F",    "Enter query criteria in the current query form."}
        ,{"Where Clause Query...","WHERE_CLAUSE_QUERY", "W",    "Enter a custom where clause query."}
        ,{"Add Record",         "ADD_RECORD",           "A",    "Add a record through the current query form."}
        ,{"Modify Record",      "MODIFY_RECORD",        "M",    "Modify the current record."}
        ,{"Delete Record",      "DELETE_RECORD",        "D",    "Delete the current record."}
        ,{"Clone Record",       "CLONE_RECORD",         "L",    "Clone the current record."}
        ,{"_Grid View ",        gridViewSubConfig,      "G",   "Configure grid view tab."} 
        ,{"_Export",            selectedOrAllConfig,    "X",    "Export query results to a file."}
        ,{"Excecute Operation", "EXECUTE_ACTION",       "E",    "Execute the currently pending operation."}
        ,{"Cancel Operation",   "CANCEL_ACTION",        "C",    "Cancel the currently pending operation."}
    };


    private static Object[][] queryformMenuConfig_ =
    {
         {"NEW_QUERY_FORM",      }
        ,{"WINDOW_TITLE",        }
        ,{"OPEN_FOR_QUERY",      }
        ,{"WHERE_CLAUSE_QUERY",  }
        ,{"ADD_RECORD",          }
        ,{"MODIFY_RECORD",       }
        ,{"DELETE_RECORD",       }
        ,{"CLONE_RECORD",        }
        ,{"GRID_VIEW",      gridViewSubConfig_} 
        ,{"EXPORT",         selectedOrAllConfig_}
        ,{"EXECUTE_ACTION",      }
        ,{"CANCEL_ACTION",       }
    };

    public static void main(String[] args) throws Exception {

        convertMenu(connectMenuConfig, "CONNECT");
        convertMenu(queryformMenuConfig, "QUERY_FORM");
        convertMenu(windowMenuConfig, "WINDOW");
        convertMenu(helpMenuConfig, "HELP");
        convertMenu(gridViewSubConfig, "GRID_VIEW_SUB");
        convertMenu(selectedOrAllConfig, "EXPORT_SUB");
        convertMenu(exportAllConfig, "EXPORT_ALL_SUB");
        convertMenu(exportSelectedConfig, "EXPORT_SELECTED_SUB");
        convertMenu(lafSubConfig, "LOOKANDFEEL_SUB");
        convertMenu(gridTabPopupConfig, "GRID_TAB_POPUP");
        
    } 


    private static Object convert(Object[][] config, String menuName) {
        Object[][] results = new Object[config.length][];
        for(int j=0; j<config.length; j++) {
            boolean isCheckBox = false;
            Object subConfig = null;
            String action = (String) config[j][0];

            if("SEPARATOR".equals(action)) {
                results[j] = new String[] {action};
                continue;
            }
            
            if(config[j].length > 1) {
                Object obj = config[j][1];
                if("CHECKBOX".equals(obj)) {
                    isCheckBox = true;
                }
                else if(obj instanceof Object[][]) {
                    subConfig = (Object[][]) obj;
                }
            }

            String prefix = "menu." + menuName + "." + action;
            Object[] row = new Object[4];

            if(isCheckBox) {
                row[0] = "CHECKBOX_" + prefix + ".label";
            }
            else {
                row[0] = prefix + ".label";
            }

            if(subConfig != null) row[1] = convert((Object[][]) subConfig, action + "_SUB");
            else row[1] = action;
            row[2] = prefix + ".mnemonic";
            row[3] = prefix + ".tooltip";
            results[j] = row;
        }

        return results;
    }

    private static void convertMenu(Object[][] config, String menuName) {
        for(int j=0; j<config.length; j++) {
            Object[] row = config[j];
            String label = (String) row[0];
            if("SEPARATOR".equals(label)) continue;
            String command = "";
            String mnemonic = "";
            String tooltip = "";
            if(!(row[1] instanceof String)) {
                command = label.toUpperCase();
                command = command.replace(' ', '_');
                command = command.replace('.', ' ');
                command = command.trim();
            }
            else {
                command = (String) row[1];
            }

            if(row.length > 2 && row[2] != null) {
                mnemonic = (String) row[2];
            }

            if(row.length > 3 && row[3] != null) {
                tooltip = (String) row[3];
            }
            System.out.println("\t\t,{\"menu." + menuName + "."
                + command + ".label\",\t\t\t\"" + label + "\"}" );
            System.out.println("\t\t,{\"menu." + menuName + "."
                + command + ".mnemonic\",\t\t\t\"" + mnemonic + "\"}" );
            System.out.println("\t\t,{\"menu." + menuName + "."
                + command + ".tooltip\",\t\t\t\"" + tooltip + "\"}" );
        }
    }

    

    private boolean[][] queryformMenuStates =
    {
         // New     WinTitle  Query    WHERE   Add     Mod     Del     Clone   Grid    Export  Exec   Cancel
         {false,    false,    false,   false,  false,  false,  false,  false,  false,  false,  false, false}     // NO_CONNECTIONS = 0;             
        ,{true,     false,    false,   false,  false,  false,  false,  false,  false,  false,  false, false}     // HAS_CONNECTIONS = 1;            
        ,{true,     false,    false,   false,  false,  false,  false,  false,  false,  false,  false, false}     // ALL_FRAMES_ICONIFIED = 2;       
        ,{true,     true,     true,    true,   true,   false,  false,  false,  true,   false,  false, false}     // QUERY_PANEL_NO_RESULTSET = 3;   
        ,{true,     true,     true,    true,   true,   true,   true,   true,   true,   true,   false, false}     // QUERY_PANEL_RESULTSET = 4;      
        ,{true,     false,    false,   false,  false,  false,  false,  false,  false,  false,  true,  true }     // QUERY_PANEL_OPEN_FOR_ADD = 5;   
        ,{true,     false,    false,   false,  false,  false,  false,  false,  false,  false,  true,  true }     // QUERY_PANEL_OPEN_FOR_UPDATE = 6;
        ,{true,     false,    false,   false,  false,  false,  false,  false,  false,  false,  true,  true }     // QUERY_PANEL_OPEN_FOR_QUERY = 7;
        ,{true,     false,    false,   false,  false,  false,  false,  false,  false,  false,  true,  true }     // QUERY_PANEL_OPEN_FOR_CLONE = 8;   
        ,{true,     false,    false,   false,  false,  false,  false,  false,  false,  false,  false, false}     // TABLE_INFO_PANEL
    };

    final static String LOOK_AND_FEEL_SUBMENU_LABEL = "Look and Feel";

    static String[][] lafSubConfig =
    {
         {"Install new...", "INSTALL_LOOK_AND_FEEL","I",    "Install a third-party Look-and-Feel"}
         ,{"SEPARATOR"}
    };

    static String[][] lafSubConfig_ =
    {
         {"INSTALL_LOOK_AND_FEEL"}
         ,{"SEPARATOR"}
    };



    static Object[][] windowMenuConfig =
    {
         {"Minimize all",   "MINIMIZE_ALL",     "N",    "Minimize all windows"}
        ,{"Maximize all",   "MAXIMIZE_ALL",     "X",    "Maximize all windows"}
        ,{"Restore all",    "RESTORE_ALL",      "R",    "Restore all windows"}
        ,{"Cascade",        "CASCADE",          "C",    "Cascade all windows"}
        ,{"SEPARATOR"}
        ,{"_" + LOOK_AND_FEEL_SUBMENU_LABEL,  lafSubConfig,       "L",    "Change QueryForm's Look-and-Feel"}
        ,{"SEPARATOR"}
    };

    static Object[][] windowMenuConfig_ =
    {
         {"MINIMIZE_ALL"}
        ,{"MAXIMIZE_ALL"}
        ,{"RESTORE_ALL"}
        ,{"CASCADE"}
        ,{"SEPARATOR"}
        ,{"LOOK_AND_FEEL",  lafSubConfig_}
        ,{"SEPARATOR"}
    };







    private boolean[][] windowMenuStates =
    {
         {false,    false,  false,  false,   true}   // NO_CONNECTIONS = 0;             
        ,{false,    false,  false,  false,   true}   // HAS_CONNECTIONS = 1;            
        ,{false,     true,   true,   true,   true}   // ALL_FRAMES_ICONIFIED = 2;       
        ,{true,      true,   true,   true,   true}   // QUERY_PANEL_NO_RESULTSET = 3;   
        ,{true,      true,   true,   true,   true}   // QUERY_PANEL_RESULTSET = 4;      
        ,{true,      true,   true,   true,   true}   // QUERY_PANEL_OPEN_FOR_ADD = 5;   
        ,{true,      true,   true,   true,   true}   // QUERY_PANEL_OPEN_FOR_UPDATE = 6;
        ,{true,      true,   true,   true,   true}   // QUERY_PANEL_OPEN_FOR_QUERY = 7; 
        ,{true,      true,   true,   true,   true}   // QUERY_PANEL_OPEN_FOR_CLONE = 8;
        ,{true,      true,   true,   true,   true}   // TABLE_INFO_PANEL
    };

    ButtonGroup lafButtonGroup = new ButtonGroup();


    static String[][] helpMenuConfig =
    {
          {"System Information...",  "SYSINFO",  "I",    "Display system information."}
         ,{"About...",               "ABOUT",    "A",    "Display program information."}
    };

    static String[][] helpMenuConfig_ =
    {
          {"SYSINFO"}
         ,{"ABOUT"}
    };

    private boolean[][] helpMenuStates =
    {
         {true, true}   // NO_CONNECTIONS = 0;             
        ,{true, true}   // HAS_CONNECTIONS = 1;            
        ,{true, true}   // ALL_FRAMES_ICONIFIED = 2;       
        ,{true, true}   // QUERY_PANEL_NO_RESULTSET = 3;   
        ,{true, true}   // QUERY_PANEL_RESULTSET = 4;      
        ,{true, true}   // QUERY_PANEL_OPEN_FOR_ADD = 5;   
        ,{true, true}   // QUERY_PANEL_OPEN_FOR_UPDATE = 6;
        ,{true, true}   // QUERY_PANEL_OPEN_FOR_QUERY = 7;
        ,{true, true}   // QUERY_PANEL_OPEN_FOR_CLONE = 8;
        ,{true, true}   // TABLE_INFO_PANEL
    };




    JMenu connectMenu = new JMenu("Connect");
    JMenu queryformMenu = new JMenu("Query Form");
    JMenu windowMenu = new JMenu("Window");
    JMenu helpMenu = new JMenu("Help");

    JMenu lafMenu = null;

    Object[][] menus =
    {
         {connectMenu,      convert(connectMenuConfig_, "CONNECT"), "C", connectMenuStates}
        ,{queryformMenu,    convert(queryformMenuConfig_, "QUERY_FORM"),  "Q", queryformMenuStates}
        ,{windowMenu,       convert(windowMenuConfig_, "WINDOW"),  "W", windowMenuStates}
        ,{helpMenu,         convert(helpMenuConfig_, "HELP"),    "H", helpMenuStates}
    };

    /**
     * These are the messages that are displayed on the statusbar for each
     * screen state. The index of each message corresponds to a state number.
     */
    private final static String[] STATE_MESSAGES =
    {
        "Select \"Data Source...\" from the Connect menu to configure or open a connection. "
        ,null
        ,null
        ,null
        ,"Use PgUP/PgDN keys to browse results."
        ,"Enter data for new record. Fields with red borders are required. "
        ,"Modify the current record. "
        ,"Enter query criteria. \"%\" may be used as a wildcard in character fields, \"<\" and \">\" in numeric fields. "
        ,"Modify the primary key and other unique fields before saving cloned record. "
        ,null
    };


    JCheckBoxMenuItem horizontalScrollbarMenuItem = null;


    int offset = 0;
    int increment = 25;

    private File configFile = null;

    public MainPanel(Frame parent, File configFile) 
    {

        java.util.Date upSince = new java.util.Date();

        this.configFile = configFile;
        if(configFile.exists()) {
            try {
                config = new Config(configFile);
            }
            catch(IOException ex) {
                String msg = "An error occurred while reading your configuration file: " + configFile 
                    + ". The program can still run, but preexisting configuration information will not be available.";
                logger.error("MainPanel(): " + msg, ex );
                GUIHelper.errMsg(parent,msg,"Application Error");
                config = new Config();
            }
            catch(Exception ex) {
                String msg = "An error occurred while parsing your configuration file: " + configFile 
                    + ". The program can still run, but preexisting configuration information will not be available.";
                logger.error("MainPanel(): " + ex, ex );
                GUIHelper.errMsg(parent,msg,"Application Error");
                config = new Config();
            }
            catch(FactoryConfigurationError er) {
                String msg = "A Factory Configuration Error occurred while reading your configuration file: " + configFile 
                    + ". This probably occurred because no suitable XML parser could be located in your classpath. "
                    +"The program can still run, but preexisting configuration information will not be available.";
                GUIHelper.errMsg(parent, msg, "Application Error");
                logger.error("MainPanel(): " + msg, er );
                config = new Config();
            }

        }
        else {
            config = new Config();
        }


        this.parent = parent;

        if(this.parent != null) {
            if(this.parent instanceof JFrame) {
                ((JFrame) this.parent).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
            this.parent.addWindowListener(new WindowAdapter() {
                    /**
                     * Invoked when a window is in the process of being closed.
                     * The close operation can be overridden at this point.
                     */
                    public void windowClosing(WindowEvent e) {
                        maybeExitProgram();
                    }
                });
        }

        configDialog = new LocalDataSourceConfigDialog(parent);
        configDialog.setTitle("Available Data Sources");
        GUIHelper.centerWindowOnScreen(configDialog);

        tableSelector = new TableSelector(parent);
        tableSelector.setMainPanel(this);
        GUIHelper.centerWindowOnScreen(tableSelector);

        loginDialog = new LoginDialog(parent);
        GUIHelper.centerWindowOnScreen(loginDialog);

        columnMapDialog = new ColumnMapDialog(parent);
        GUIHelper.centerWindowOnScreen(columnMapDialog);

        whereClauseDialog = new WhereClauseDialog(parent);
        GUIHelper.centerWindowOnScreen(whereClauseDialog);

        exportDialog = new ExportDialog(parent);
        exportDialog.setTitle("Select Columns For Export");
        GUIHelper.centerWindowOnScreen(exportDialog);

        overwriteDialog = new AppendOverwriteDialog(parent);
        GUIHelper.centerWindowOnScreen(overwriteDialog);

        tpLafDialog = new ThirdPartyLafDialog(parent);
        GUIHelper.centerWindowOnScreen(tpLafDialog);


        sysInfoDialog = new SystemInfoDialog(parent, upSince);
        GUIHelper.centerWindowOnScreen(sysInfoDialog);

        // configure the about dialog.
        String msg = "<center><h1>QueryForm</h1><h3>Version " + VersionNumber.VERSION_NUMBER + "</h3><h3>Copyright © 1998 - 2020, David F. Glasser (dglasser@pobox.com)"
            + "</h3><h3><u><font color='blue'>https://github.com/dglasser/qform</font><u></h3><p>This product includes software developed by<br>the Apache Software Foundation "
            + "(http://www.apache.org/).<p>"
            + "Toolbar Icons Copyright © 1998 Dean S. Jones (dean@gallant.com).</center>";
        javax.swing.JEditorPane jep = new JEditorPane();
        jep.setContentType("text/html");
        jep.setEditable(false);
        jep.setOpaque(false);
        jep.setText(msg);
        
        aboutDialog = new JDialog(parent);
        ActionListener closer = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    aboutDialog.setVisible(false);
                }
            };

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel bPanel = new JPanel();
        JButton okButton = new JButton("OK");

        GUIHelper.buildButtonPanel(bPanel,
            new Object[][] {
                {okButton, "O", null, null}
            },
            closer);

        panel.add(jep, BorderLayout.CENTER);
        panel.add(bPanel, BorderLayout.SOUTH);

        aboutDialog.setContentPane(panel);
        aboutDialog.setTitle("QueryForm");
        aboutDialog.addKeyListener(new EnterEscapeKeyHandler(okButton, okButton));

        GUIHelper.setAllSizes(jep, new Dimension(450, 280));
        aboutDialog.setResizable(false);
        aboutDialog.pack();
        GUIHelper.centerWindowOnScreen(aboutDialog);

        // add the toolbar buttons
        toolBar.addSeparator();
        toolBar.setBorder(new EmptyBorder(7, 10, 7, 5));
        GUIHelper.configureToolbar(toolBar,toolBarConfig,"org/glasser/qform/images", new Dimension(36, 36), this);


        // now that all of the dialogs have been created, store them in an array for 
        // easy changing of the look and feel.
        Component[] temp =
        {
             configDialog
            ,columnMapDialog
            ,tableSelector
            ,loginDialog
            ,whereClauseDialog
            ,exportDialog
            ,overwriteDialog
            ,aboutDialog
            ,tpLafDialog
            ,sysInfoDialog
        };

        dialogs = temp;


        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        //
        // add the menus
        //
        for(int j=0; j<menus.length; j++) {
            JMenu menu = (JMenu) menus[j][0];
            menu.setMnemonic(((String)menus[j][2]).charAt(0));
            Object[][] menuItems = (Object[][]) menus[j][1];
            GUIHelper.buildMenu(menu, menuItems, this, menuItemListener, menuItemListener, bundle);
            menuBar.add(menu);
        }

        String horizontalScrollbarLabel =
            bundle.getString("menu.GRID_VIEW_SUB.TOGGLE_HORIZONTAL_SCROLLBAR.label");

        horizontalScrollbarMenuItem = (JCheckBoxMenuItem) GUIHelper.findMenuItemByLabelText(queryformMenu, horizontalScrollbarLabel);
        horizontalScrollbarMenuItem.setSelected(true);

        // build the Look And Feel submenu.
        this.lafMenu = (JMenu) GUIHelper.findMenuItemByActionCommand(this.windowMenu, LOOK_AND_FEEL_SUBMENU_LABEL);

        // add the JRE-included LAFs to the menu.
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        for(int j=0; j<lafs.length; j++) {
            UIManager.LookAndFeelInfo info = lafs[j];
            this.addOrUpdateLafMenuItem(info.getName(), info.getClassName());
        }

        // now add the third-party LAFs from the config file to the menu.
        lafs = config.getThirdPartyLafs();
        for(int j=0; j<lafs.length; j++) {
            UIManager.LookAndFeelInfo info = lafs[j];
            String lafName = info.getName();
            if(Util.isNothing(lafName)) lafName = "Unknown";
            String className = info.getClassName();
            if(Util.isNothing(className)){
                logger.error("MainPanel(): Error in configuration file: A third-party-laf element is missing its laf-class attribute.");
                continue;
            }
            this.addOrUpdateLafMenuItem(lafName, className);
        }

        this.setScreenState(NO_CONNECTIONS);

    }

    private HashMap<String, JRadioButtonMenuItem> lafMenuItems = new HashMap<>();


    /**
     * Adds a JRadioButtonMenuItem for a LookAndFeel to the LookAndFeel submenu. If an item already
     * exists for it, the item text is updated to match lafName. This is helpful if the
     * LAF was entered into qform.xml manually.
     */
    private void addOrUpdateLafMenuItem(String lafName, String lafClassName) {

        // see if the item already exists.
        JRadioButtonMenuItem item = (JRadioButtonMenuItem) lafMenuItems.get(lafClassName);

        // if it does, update it and return.
        if(item != null) {
            item.setText(lafName);
            return;
        }
            
        // otherwise add it.
        item = new JRadioButtonMenuItem(lafName);
        lafButtonGroup.add(item);
        LookAndFeel currentLAF = UIManager.getLookAndFeel();
        if(currentLAF != null && lafClassName.equals(currentLAF.getClass().getName())) {
            item.setSelected(true);
        }
        item.setActionCommand(lafClassName);
        lafMenu.add(item);
        item.addActionListener(lafManager);
        lafMenuItems.put(lafClassName, item);
    }


    private ActionListener lafManager = new ActionListener() {
            /**
             * Invoked when a LookAndFeel menu item is selected.
             */
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) e.getSource();
                String lafClassName = e.getActionCommand();
                logger.debug("actionPerformed(): Changing look-and-feel: {}", lafClassName);
                try {
                    LookAndFeel laf = null;
                    try {
                        Class lafClass = ExtensionClassLoader.getSingleton().loadClass(lafClassName);
                        laf = (LookAndFeel) lafClass.newInstance();
						if(laf.isSupportedLookAndFeel()) {
							addOrUpdateLafMenuItem(laf.getName(), laf.getClass().getName());

                            // if this is a third-party LAF, this will update it in the config file.
                            // This may be necessary if it was added to the config file manually.
							config.maybeUpdateThirdPartyLaf(laf.getName(), laf.getClass().getName());
							setLookAndFeel(laf);
						}
						else {
							// Unsupported LAF, so abort the look-and-feel change.

							// re-select the current LAF's menu item.
							selectCurrentLafMenuItem();

							int reply = JOptionPane.showConfirmDialog(parent,
								"The look-and-feel you selected ("
								+ menuItem.getText()
								+ ") is not supported on this platform.\n\nDo you want to remove this look-and-feel from QueryForm's configuration file?",
								"Application Error",
								JOptionPane.YES_NO_OPTION);
							if(reply == JOptionPane.YES_OPTION) {

								// delete the LAF from the config file.
								config.deleteThirdPartyLaf(lafClassName);
								// delete the menu item from the menu.
								lafMenu.remove(menuItem);
							}
							else { // disable the menu item, but leave it in the menu.
								menuItem.setEnabled(false);
							}
						}
                    }
                    catch(ClassNotFoundException cnf) {
                        
                        // re-select the current LAF's menu item.
                        selectCurrentLafMenuItem();

                        int reply = JOptionPane.showConfirmDialog(parent,
                            "The look-and-feel you selected ("
                            + menuItem.getText()
                            + ") was not found in QueryForm's classpath.\n\nDo you want to remove this look-and-feel from QueryForm's configuration file?",
                            "Application Error",
                            JOptionPane.YES_NO_OPTION);
                        if(reply == JOptionPane.YES_OPTION) {

                            // delete the LAF from the config file.
                            config.deleteThirdPartyLaf(lafClassName);
                            // delete the menu item from the menu.
                            lafMenu.remove(menuItem);
                        }
                        else { // disable the menu item, but leave it in the menu.
                            menuItem.setEnabled(false);
                        }
                        return;
                    }
                }
                catch(Throwable err) {
                    logger.error("actionPerformed(): " + err, err );
                    handleSevereLafError(err);
                }
            }

        };

    /**
     * Sets the application's LookAndFeel to the default (Metal) LookAndFeel. If the operation
     * fails, the program exits. This method should only be called when/if a call to UIManager.setLookAndFeel()
     * fails for some other LookAndFeel.
     */
    private void restoreDefaultLaf() {
        try {
            LookAndFeel defaultLAF = (LookAndFeel) Class.forName(UIManager.getCrossPlatformLookAndFeelClassName()).newInstance();
            setLookAndFeel(defaultLAF);
        }
        catch(Throwable ex) {
            logger.error("restoreDefaultLaf(): " + ex, ex );
            GUIHelper.errMsg(parent, "The attempt to restore the default Look-and-Feel was unsuccessful. QueryForm will now terminate.",
                "Unrecoverable Error");
            System.exit(9);
        }
    }

    /**
     * Selects the menu item for the app's current LookAndFeel. Does nothing if
     * the menu item is not found in the menu, or if the current LookAndFeel cannot
     * be determined.
     */
    private void selectCurrentLafMenuItem() {
        LookAndFeel currentLAF = UIManager.getLookAndFeel();
        if(currentLAF == null) {
            logger.warn("selectCurrentLafMenuItem(): Could not select menu item for current look-and-feel. UIManager.getLookAndFeel() returned null.");
            return;
        }

        JRadioButtonMenuItem menuItem =
            (JRadioButtonMenuItem) lafMenuItems.get(currentLAF.getClass().getName());
        if(menuItem != null) {
            menuItem.setSelected(true);
        }
        else {
            logger.warn("selectCurrentLafMenuItem(): Menu item not found for Look-and-Feel: " + currentLAF.getName());
        }
    }

    /**
     * This method is called when UIManager.setLookAndFeel() throws a Throwable. It displays an error
     * message to the user and attempts to set the app's look and feel to the default (Metal). If the default
     * LookAndFeel can't be restored, the program exits, on the assumption that is is irretrievably hosed.
     */
    private void handleSevereLafError(Throwable err) {

        String s = "An unexpected error has occurred: ";
        if(err instanceof Error) {
            s = "A severe error has occurred: ";
        }

        String msg = s + err
            + "\n\nQueryForm will now attempt to set the default Look-and-Feel so processing can continue.";

        GUIHelper.errMsg(parent, msg, "Application Error");

        // if this call fails, the program exits.
        restoreDefaultLaf();
        selectCurrentLafMenuItem();
    }



    /**
     * Sets the LookAndFeel for the app to the given LookAndFeel.
     * 
     * @throws UnsupportedLookAndFeelException if the given LookAndFeel is not suuported.
     */
    private void setLookAndFeel(LookAndFeel laf) 
        throws UnsupportedLookAndFeelException
    {
        UIManager.setLookAndFeel(laf);
        UIManager.getDefaults().put("ClassLoader", laf.getClass().getClassLoader());
        UIManager.getLookAndFeelDefaults().put("ClassLoader", laf.getClass().getClassLoader());
        SwingUtilities.updateComponentTreeUI(parent);
        for(int j=0; j<dialogs.length; j++) {
            SwingUtilities.updateComponentTreeUI(dialogs[j]);
        }
        setStatusMessage("Look-and-Feel changed to " + laf.getName() + ".");
        System.setProperty("qform.laf", laf.getClass().getName());
    }


    /**
     * Displays a message indicating that a LookAndFeel is not supported.
     */
    public void showUnsupportedLafErrorMessage(String lafName, String lafClassName) {
        if(lafName == null) lafName = "The selected look-and-feel";

        GUIHelper.errMsg(parent, 
                         lafName
                        + " (" 
                        + lafClassName 
                        + ") is not supported on this platform.",
                        "Unsupported Look-and-Feel");
    }



    public void setMenuState(int state) {
        for(int j=0; j<menus.length; j++) { 
            JMenu menu = (JMenu) menus[j][0];
            boolean[][] states = (boolean[][]) menus[j][3];

            JMenuItem[] items = GUIHelper.getMenuItems(menu);

            for(int k=0; k<states[state].length; k++) {
                items[k].setEnabled(states[state][k]);
            }

            QueryPanel qp = getCurrentQueryPanel();
            if(qp != null) {
                horizontalScrollbarMenuItem.setSelected(qp.isHorizontalScrollBarVisible());
            }
        }
    }

    public void setToolbarState(int state) {
        boolean [] states = toolBarStates[state];
        for(int j=0; j<toolBarConfig.length; j++) {
            JButton button = (JButton) toolBarConfig[j][0];
            button.setEnabled(states[j]);
        }
    }


    public void setScreenState(int state) {
        setMenuState(state);
        setToolbarState(state);
        setStatusMessage(STATE_MESSAGES[state]);
        screenState = state;
    }

    public void setScreenState() {

        JInternalFrame jif = desktop.getSelectedFrame();
        if(jif != null) {
            JComponent contentPane = (JComponent) jif.getContentPane();
            if(contentPane instanceof QueryPanel) {
                QueryPanel qp = (QueryPanel) contentPane;
                int qpState = qp.getState();
                setScreenState(qpState);
            }
            else if(contentPane instanceof TableInfoPanel) {
                setScreenState(MainPanel.TABLE_INFO_PANEL);
            }
        }
        else { // no active internal frames
            JInternalFrame[] allFrames = desktop.getAllFrames();
            if(allFrames != null && allFrames.length > 0) {
                setScreenState(this.ALL_FRAMES_ICONIFIED);
            }
            else {
                if(dsMap.size() > 0) {
                    this.setScreenState(this.HAS_CONNECTIONS);
                }
                else {
                    this.setScreenState(NO_CONNECTIONS);
                }
            }
        }
    }





    public static final int NO_CONNECTIONS = 0;
    public static final int HAS_CONNECTIONS = 1;
    public static final int ALL_FRAMES_ICONIFIED = 2;
    public static final int QUERY_PANEL_NO_RESULTSET = 3;
    public static final int QUERY_PANEL_HAS_RESULTSET = 4;
    public static final int QUERY_PANEL_OPEN_FOR_ADD = 5;
    public static final int QUERY_PANEL_OPEN_FOR_UPDATE = 6;
    public static final int QUERY_PANEL_OPEN_FOR_QUERY = 7;
    public static final int QUERY_PANEL_OPEN_FOR_CLONE = 8;

    public static final int TABLE_INFO_PANEL = 9;


    private int screenState = NO_CONNECTIONS;



    private int nextDataSourceId = 1;

    private HashMap<Integer, DataSource> dsMap = new HashMap<>();

    private HashMap<Integer, LocalDataSourceConfig> localConfigMap = new HashMap<>();

    private HashSet<Integer> pkeysNotSupported = new HashSet<>();

    private HashSet<Integer> fkeysNotSupported = new HashSet<>();

    private HashSet<Integer> exkeysNotSupported = new HashSet<>();

    private HashSet<LocalDataSourceConfig> establishedConnections = new HashSet<>();

    private Properties editableTypes = new Properties();

    private HashMap<String, Set<Integer>> driverClassToEditableTypeMap = new HashMap<>();


    {
        try {
            File file = null;

            // first check for a System property called "editable.types".
            String filepath = System.getProperty("editable.types");
            if(filepath != null) {
                file = new File(filepath);
            }
            else {
                // otherwise check for a file called qform_editable_types.properties,
                // first in the user's home directory, then in the current working directory.
                String filename = "qform_editable_types.properties";
                File dir = new File(System.getProperty("user.home", "."));
                file = new File(dir, filename);
                if(file.exists() == false) {
                    dir = new File(System.getProperty("user.dir", "."));
                    file = new File(dir, filename);
                }
            }
            if(file.exists() && file.canRead()) {
                FileInputStream fis = new FileInputStream(file);
                editableTypes.load(fis);
                fis.close();
            }
            else {
                // if the System property was used, warn the user that the file wasn't found.
                if(filepath != null) {
                    logger.warn("MainPanel(): Editable types file not found: " + filepath );
                }
            }

        }
        catch(Throwable t) {
            logger.error("MainPanel(): " + t, t );
        }
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


    public void actionPerformed(ActionEvent e) {
        try {
            _actionPerformed(e);
        }
        catch(NoSuchElementException ex) {
            setScreenState();
            noConnectionAvailableMessage(null);
            return;
        }
        catch(Throwable ex) {
            logger.error("actionPerformed(): " + ex, ex);
            GUIHelper.errMsg(parent, "An unexpected error occurred:\n\n" + ex, null);
        }
        finally {
            showNormalCursor();
        }
    }
        

    public void _actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // read the tooltip for this component because for some
        // actions, we'll want to display the tooltip on the
        // statusbar.
        String tooltip = null;
        try {
            // this should always be a JComponent so we won't
            // incur the cost of using instanceof
            tooltip = ((JComponent) src).getToolTipText();
        }
        catch(Exception ex) {
            logger.error("_actionPerformed(): " + ex, ex );
        }

        // clear away any existing status message.
        setStatusMessage(null);

        String command = e.getActionCommand();
        if(command == null) command = "";
        if(command.equals("DATA_SOURCE_DIALOG")) {
            setStatusMessage(tooltip);
            List<LocalDataSourceConfig> configs = new ArrayList<>(Arrays.asList(config.getLocalDataSourceConfigs()));
            this.configDialog.setList(configs);
            configDialog.setVisible(true);
            LocalDataSourceConfig ld = configDialog.getSelectedItem();
            configs = configDialog.getList();
            ArrayList<LocalDataSourceConfig> list = new ArrayList<>(configs.size());
            list.addAll(configs);
            config.setLocalDataSourceConfigs(list);
            if(ld != null) {
                if(establishedConnections.contains(ld)) {
                    int reply = JOptionPane.showConfirmDialog(this, "A connection to this data source has already been \nestablished. Do you want to create another one?",
                        "QueryForm", JOptionPane.YES_NO_OPTION);
                    if(reply == JOptionPane.NO_OPTION) {
                        setStatusMessage("Operation cancelled.");
                        return;
                    }
                }
                showBusyCursor();
                setStatusMessage("Connecting to " + ld.getDisplayName() + "...");
                try { 

                    DataSource ds = null;

                    if(ld.isLoginRequired()) {
                        String userName = ld.getUser();
                        String password = ld.getPassword();
                        if(userName != null && password != null) {
                            // if we already have a username and password, attempt to login.
                            ds = DataSourceManager.getLocalDataSource(ld,userName,password);
                        }
                        else {   // show the login dialog
                            loginDialog.setLoginHandler(new LocalDataSourceLoginHandler(ld));
                            loginDialog.setUserId(userName);
                            loginDialog.setPassword(password);
                            loginDialog.setVisible(true);

                            // if this returned null, the user cancelled.
                            ds = (DataSource) loginDialog.getLoginResult();
                            if(ds == null) {
                                setStatusMessage("Login cancelled.");
                                return;
                            }
                        }
                    }
                    else {  // no login required
                        ds = DataSourceManager.getLocalDataSource(ld, null, null);
                    }

                    this.addNewLocalDataSource(ld, ds);
    
                }
                catch(ClassNotFoundException ex) {

                    GUIHelper.errMsg(parent, "The JDBC driver class ("
                            + ld.getDriverClassName() + ") was not found in the classpath.",
                        "Login Failed");
                    setStatusMessage("Login failed.");
                    return;
                }
                catch(Exception ex) {
                    logger.error("_actionPerformed(): " + ex, ex );
                    GUIHelper.errMsg(parent, "Login failed: " + ex.getMessage(), "Login Failed");
                    setStatusMessage("Login failed.");
                    return;
                }
            }
            else {
                // Data source dialog was closed without selecting a database
                // to connect to.
                setStatusMessage(null);
            }
        }
        else if(command.equals("NEW_QUERY_FORM")) {
            setStatusMessage("Select table.");
            tableSelector.setVisible(true);
            Integer sourceId = tableSelector.getSelectedSourceId();
            TableInfo ti = tableSelector.getSelectedTableInfo();
            if(ti == null) {
                setStatusMessage(null);
                return;
            }
            else {
                showBusyCursor();
                saveSchemaSelection(sourceId.intValue(), ti.getTableSchem());
                try {
                    if(ti.getColumns() == null) {
                        this.setColumnsAndKeys(sourceId, ti);
                    }

                    if(ti.getColumns().length == 0) {
                        GUIHelper.errMsg(parent, "This QueryForm cannot be created because the database is indicating "
                            + "that the table or view has no columns or keys.", "Operation Failed");
                        setStatusMessage("Operation Failed.");
                        return;
                    }

                    showBusyCursor();
                    this.newQueryForm(sourceId, ti);
                    
                    
                }
                catch(NoSuchElementException ex) {
                    noConnectionAvailableMessage(sourceId);
                }
                catch(Exception ex) {
                    logger.error("_actionPerformed(): " + ex, ex );
                    GUIHelper.exceptionMsg(this,ex);
                    setStatusMessage("");
                }
            }
        }
        else if(command.equals("OPEN_FOR_QUERY")) {
            QueryPanel qp = getCurrentQueryPanel();
            qp.setState(qp.OPEN_FOR_QUERY);
            setScreenState(QUERY_PANEL_OPEN_FOR_QUERY);
        }
        else if(command.equals("ADD_RECORD")) {

            QueryPanel qp = getCurrentQueryPanel();
            qp.setState(qp.OPEN_FOR_ADD);
            setScreenState(QUERY_PANEL_OPEN_FOR_ADD);
        }
        else if(command.equals("CLONE_RECORD")) {

            QueryPanel qp = getCurrentQueryPanel();
            qp.setState(qp.OPEN_FOR_CLONE);
            setScreenState(QUERY_PANEL_OPEN_FOR_CLONE);
        }
        else if(command.equals("MODIFY_RECORD")) {

            QueryPanel qp = getCurrentQueryPanel();
            qp.setState(qp.OPEN_FOR_UPDATE);
            setScreenState(QUERY_PANEL_OPEN_FOR_UPDATE);
        }
        else if(command.equals("DELETE_RECORD")) {
            QueryPanel qp = getCurrentQueryPanel();
            qp.deleteCurrentRow();
//            setStatusMessage(null);
            setScreenState();

        }
        else if(command.equals("EXECUTE_ACTION")) {

            QueryPanel qp = getCurrentQueryPanel();
            try {
                showBusyCursor();
                switch(qp.getState()) {
                    case QueryPanel.OPEN_FOR_ADD :
                    case QueryPanel.OPEN_FOR_CLONE :
                        qp.executeAdd();
                        break;
                    case QueryPanel.OPEN_FOR_QUERY :
                        qp.runQuery();
                        break;
                    case QueryPanel.OPEN_FOR_UPDATE :
                        qp.executeUpdate();
                        break;
    
                }
                setScreenState();
            }
            catch(NoSuchElementException ex) {
                noConnectionAvailableMessage(qp.getDataSourceId());
                return;
            }
        }
        else if(command.equals("REFRESH")) {
            QueryPanel qp = getCurrentQueryPanel();
            qp.rerunLastQuery();
            setScreenState();
        }
        else if(command.equals("GOTO_FIRST")) {
            QueryPanel qp = getCurrentQueryPanel();
            qp.first();
        }
        else if(command.equals("GOTO_NEXT")) {
            QueryPanel qp = getCurrentQueryPanel();
            qp.next();
        }
        else if(command.equals("GOTO_PREVIOUS")) {
            QueryPanel qp = getCurrentQueryPanel();
            qp.previous();
        }
        else if(command.equals("CANCEL_ACTION")) {
            QueryPanel qp = getCurrentQueryPanel();
            qp.cancelPendingAction();
            setScreenState(qp.getState());
            setStatusMessage("Operation cancelled.");
        }
        else if(command.equals("TOGGLE_HORIZONTAL_SCROLLBAR")) {
            QueryPanel qp = getCurrentQueryPanel();
            if(qp == null) return;
            qp.setSelectedTab(1);
            boolean isVisible = qp.isHorizontalScrollBarVisible();
            qp.setHorizontalScrollBarVisible(!isVisible);
        }
		else if(command.equals("TOGGLE_LONGFORM_DATES")) {
			QueryPanel qp = getCurrentQueryPanel();
			if(qp == null) return;

			JCheckBoxMenuItem item = (JCheckBoxMenuItem) src;
			boolean useLongForm = item.isSelected();
			qp.setSelectedTab(1);
			qp.displayLongTimestampsInGrid(useLongForm);
		}


        else if(command.equals("MINIMIZE_ALL")) {
            JInternalFrame[] frames = desktop.getAllFrames();
            DesktopManager dm = desktop.getDesktopManager();
            for(int j=0; j<frames.length; j++) {

                try {

                frames[j].setIcon(true);
                }
                catch(Exception ex) {
                    logger.error("_actionPerformed(): " + ex, ex );
                }
            }
        }
        else if(command.equals("MAXIMIZE_ALL")) {
            JInternalFrame[] frames = desktop.getAllFrames();
            DesktopManager dm = desktop.getDesktopManager();
            for(int j=0; j<frames.length; j++) {
                try {
                    JInternalFrame jif = frames[j];
                    if(jif.isIcon()) {
                        jif.setIcon(false);
                    }
                    frames[j].setMaximum(true);
                }
                catch(Exception ex) {
                    logger.error("_actionPerformed(): " + ex, ex );
                }
            }
        }
        else if(command.equals("RESTORE_ALL")) {
            JInternalFrame[] frames = desktop.getAllFrames();
            DesktopManager dm = desktop.getDesktopManager();
            for(int j=0; j<frames.length; j++) {

                try {
                    JInternalFrame jif = frames[j];
                    if(jif.isIcon()) {
                        jif.setIcon(false);
                    }
                    else if(jif.isMaximum()) {
                        jif.setMaximum(false);
                        Rectangle r = jif.getNormalBounds();
                        if(r != null) jif.setBounds(r);
                    }
                }
                catch(Exception ex) {
                    logger.error("_actionPerformed(): " + ex, ex );
                }
            }
        }
        else if(command.equals("CASCADE")) {

            int tempOffset = 0;
            JInternalFrame[] frames = desktop.getAllFrames();
            for(int j=0; j<frames.length; j++) {
                try {

                    JInternalFrame jif = frames[j];
                    if(jif.isIcon()) {
                        jif.setIcon(false);
                    }
                    else if(jif.isMaximum()) {
                        jif.setMaximum(false);
                    }
                    jif.setBounds(tempOffset, tempOffset, 500, 300);
                    jif.setSelected(true);
                    tempOffset += increment;
                }
                catch(Exception ex) {}

            }
        }
        else if(command.equals("INSTALL_LOOK_AND_FEEL")) {
            tpLafDialog.openDialog();
            String className = tpLafDialog.getInput();
            if(className == null) return;
            try {
                LookAndFeel laf = (LookAndFeel) ExtensionClassLoader.getSingleton().loadClass(className).newInstance();
                if(laf.isSupportedLookAndFeel()) {
                    setLookAndFeel(laf);
                    this.addOrUpdateLafMenuItem(laf.getName(), laf.getClass().getName());
                    config.addOrUpdateThirdPartyLaf(laf.getName(), laf.getClass().getName());
                }
                else {
                    this.showUnsupportedLafErrorMessage(laf.getName(), laf.getClass().getName());
                }
            }
            catch(ClassNotFoundException ex) {
                GUIHelper.errMsg(this, "This look-and-feel class was not found in QueryForm's classpath:\n\n"
                    + className, null);

            }
            catch(Throwable t) {
                logger.error("_actionPerformed(): " + t, t );
                handleSevereLafError(t);
            }
        }
        else if(command.equals("WHERE_CLAUSE_QUERY")) {
            
            QueryPanel qp = this.getCurrentQueryPanel();
            if(qp == null) return; // shouldn't happen

            setStatusMessage("Use arrow buttons or Alt-P/Alt-N to see history.");
            whereClauseDialog.openDialog(qp.getTableName(), qp.getHistoryList());

            String whereClause = whereClauseDialog.getWhereClause();
            if(whereClause == null) {
                setStatusMessage("Operation cancelled.");
                return;
            }
            else {
                setStatusMessage("");
            }
        

            showBusyCursor();
            // if a where-clause query returns no data, we want to make
            // sure any existing resultsets are cleared from the form.
            qp.runQuery(whereClause, qp.NO_RESULTSET);

            // update the state of the main screen based on the
            // results of the where-clause query.
            setScreenState();

        }
        else if(command.equals("TABLE_METADATA")) {
            Integer sourceId = null;
            try {
                tableSelector.setVisible(true);
                sourceId = tableSelector.getSelectedSourceId();
                TableInfo ti = tableSelector.getSelectedTableInfo();
                
                if(ti  == null) return;

                showBusyCursor();
                saveSchemaSelection(sourceId.intValue(), ti.getTableSchem());

                if(ti.getColumns() == null) {
                    this.setColumnsAndKeys(sourceId, ti);
                }

                newTableInfoPanel(ti, sourceId);
    
            }
            catch(NoSuchElementException ex) {
                noConnectionAvailableMessage(sourceId);
                return;
            }
            catch(Exception ex) {
                logger.error("_actionPerformed(): " + ex, ex );
                GUIHelper.exceptionMsg(this,ex);
                return;
            }

        }
        else if(command.equals("CLOSE_WINDOW")) {
            JInternalFrame frame = desktop.getSelectedFrame();
            if(frame == null) return;
            frame.setVisible(false);
            frame.dispose();
        }

        if(command.equals("QUERYPANEL_TABLE_METADATA")) {
            QueryPanel qp = this.getCurrentQueryPanel();
            if(qp == null) return; // shouldn't happen

            try {

                newTableInfoPanel(qp.getTableInfo(), qp.getDataSourceId());
    
            }
            catch(Exception ex) {
                logger.error("_actionPerformed(): " + ex, ex );
                GUIHelper.exceptionMsg(this,ex);
                return;
            }


        }
        if(command.equals("COLUMN_MAP_DIALOG")) {
            setStatusMessage(tooltip);
            QueryPanel qp = this.getCurrentQueryPanel();
            if(qp == null) return; // shouldn't happen

            qp.setSelectedTab(1);

            int[] visibleGridColumns = qp.getVisibleGridColumns();

            String[] columnNames = qp.getColumnNames();

            columnMapDialog.openDialog(columnNames, visibleGridColumns);
            int selections[] = columnMapDialog.getSelections();
            if(selections == null) return;

            setStatusMessage("Ctrl-click to select/deselect columns for display.");
            qp.setVisibleGridColumns(selections);
            setStatusMessage(null);
            setScreenState();

        }
        else if(command.equals("WINDOW_TITLE")) {
            QueryPanel qp = this.getCurrentQueryPanel();
            if(qp == null) return; // shouldn't happen

            String title = qp.getWindowTitle();
            String dataSourceName = qp.getDataSourceDisplayName();

            Integer sourceId = qp.getDataSourceId();


            String newTitle = (String) JOptionPane.showInputDialog(this, 
                "Enter a new title for this window:", 
                "QueryForm", JOptionPane.INFORMATION_MESSAGE, 
                null, 
                null, 
                title);
            if(newTitle == null) return;

            String baseName = "(" + sourceId + ") " + newTitle + " [QUERY";
            XInternalFrame frame = (XInternalFrame) desktop.getSelectedFrame();
            frame.setTitle(baseName + " / " + dataSourceName + "]");
            frame.getMenuItem().setText(baseName + "]");
            qp.setWindowTitle(newTitle);
        }
        else if(command.equals("EXPORT_SELECTED_INSERT")) {
            exportResultSet(ExportDialog.EXPORT_INSERT_STATEMENTS, true);
        }
        else if(command.equals("EXPORT_ALL_INSERT")) {
            exportResultSet(ExportDialog.EXPORT_INSERT_STATEMENTS, false);
        }
        else if(command.equals("EXPORT_SELECTED_CSV")) {
            exportResultSet(ExportDialog.EXPORT_CSV, true);
        }
        else if(command.equals("EXPORT_ALL_CSV")) {
            exportResultSet(ExportDialog.EXPORT_CSV, false);
        }
        else if(command.equals("EXIT")) {
            maybeExitProgram();
        }
        else if(command.equals("ABOUT")) {
            aboutDialog.setVisible(true);
        }
        else if(command.equals("SYSINFO")) {
//            sysInfoDialog.setModal(true);
            sysInfoDialog.setVisible(true);
        }

    }


    public void noConnectionAvailableMessage(Integer dataSourceId) {
        if(dataSourceId != null) {
            LocalDataSourceConfig config = (LocalDataSourceConfig) localConfigMap.get(dataSourceId);
            GUIHelper.errMsg(parent, "There are currently no connections available in the connection pool for this data source:\n\n\""
                + config.getDisplayName()
                + "\"\n\nYou may create another pool by reconnecting through the data source dialog, or free a connection by reading in the remainder of any open resultsets for this data source, or closing a window with an open resultset.", "Connection Not Available");
        }
        else {
            GUIHelper.errMsg(parent, "There are currently no connections available in the connection pool for this data source. "
                + "\n\nYou may create another pool by reconnecting through the data source dialog, or free a connection by reading in the remainder of any open resultsets for this data source, or closing a window with an open resultset.", "Connection Not Available");
        }
    }
        




    private void exportResultSet(int exportType, boolean selectedOnly) {
        QueryPanel qp = this.getCurrentQueryPanel();
        if(qp == null) return; // shouldn't happen

        File f = null;
        boolean append = false;
        while(true) {
            f = this.getOutputFile(null);
            if(f == null) {
                setStatusMessage("Export cancelled.");
                return;
            }
            if(f.exists()) {
                if(f.canWrite() == false) {
                    GUIHelper.warningMsg(this, "The selected file or destination directory cannot be written. Please choose another output file.",
                        "Invalid Selection");
                    continue;
                }

                overwriteDialog.openDialog(f.getAbsolutePath(), true);
                String option = overwriteDialog.getSelection();
                if(option == null || option.equals(overwriteDialog.CANCEL_OPTION)) {
                    setStatusMessage("Export cancelled.");
                    return;
                }
                else if(option.equals(overwriteDialog.APPEND_OPTION)) {
                   append = true;
                }
                break;
            }
            else {
                break;
            }

        }

        TableInfo tableInfo = qp.getTableInfo();

        exportDialog.openDialog(tableInfo, exportType);

        String[] columnNames = exportDialog.getColumnNames();
        if(columnNames == null) {
            setStatusMessage("Operation cancelled.");
            return;
        }

        Formatter[] formatters = exportDialog.getFormatters();
        String tableName = exportDialog.getTableName();

        String terminal = exportDialog.getTerminal();
        if(terminal != null && terminal.trim().length() == 0) terminal = null;

        int[] selectedIndexes = null;
        if(selectedOnly) selectedIndexes = qp.getSelectedIndexes();

        List<Object>[] currentRowset = qp.getCurrentRowset();

        try {

            PrintWriter writer = new PrintWriter(new FileWriter(f.getAbsolutePath(), append));
            if(exportType == ExportDialog.EXPORT_INSERT_STATEMENTS) {
                ExportManager.exportInsertStatements(writer, Arrays.asList(currentRowset), selectedIndexes, tableName,columnNames,formatters,terminal);
            }
            else {
                ExportManager.exportDelimited(writer, Arrays.asList(currentRowset), selectedIndexes, ",", columnNames,formatters,terminal);
            }
            writer.close();
            GUIHelper.infoMsg(this, "Results saved successfully.", "QueryForm");
        }
        catch(Exception ex) {
            logger.error("exportResultSet(): " + ex, ex );
            GUIHelper.exceptionMsg(this,ex);
        }
    }




    private QueryPanel getCurrentQueryPanel() {
        JInternalFrame frame = desktop.getSelectedFrame();
        if(frame == null) return null;
        Container c = frame.getContentPane();
        if(c instanceof QueryPanel) return (QueryPanel) c;
        return null;
    }


    public void newTableInfoPanel(TableInfo ti, Integer dataSourceId) 
        throws NoSuchMethodException
    {
        LocalDataSourceConfig config = (LocalDataSourceConfig) localConfigMap.get(dataSourceId);
        TableInfoPanel tip = new TableInfoPanel(ti, config.getDisplayName());

        String baseName = "(" + dataSourceId + ") " + ti.getTableName() + " [METADATA";

        XInternalFrame jif = new XInternalFrame(baseName + " / " 
                                                + config.getDisplayName() 
                                                + "]", true, true, true, true);

        jif.getMenuItem().setText(baseName + "]");
        jif.setContentPane(tip);
        this.desktop.add(jif);
        jif.addInternalFrameListener(this);
        windowMenu.add(jif.getMenuItem());
        jif.pack();
        offset += increment;
        if(offset > (10 * increment)) offset = 0;
        jif.setBounds(offset, offset, 500, 300);
        jif.setVisible(true);

//                desktop.getDesktopManager().maximizeFrame(jif);
        setScreenState(MainPanel.TABLE_INFO_PANEL);
    }


    public final static String DEFAULT_SCHEMA = "<DEFAULT SCHEMA>";

    MethodComparator<TableInfo> tableTypeComparator =
        new MethodComparator<>(org.glasser.sql.TableInfo.class, "getTableType", false, false, TableInfo.NAME_COMPARATOR, false);


    public void addNewLocalDataSource(LocalDataSourceConfig ld, DataSource ds) 
        throws SQLException
    {

        Connection conn = null;
        try {

            Integer id = new Integer(nextDataSourceId++);
            this.dsMap.put(id, ds);

            // we'll clone this config because it may be modified and connected
            // to again, and we want to keep the original information associated
            // with this connection intact.
            LocalDataSourceConfig config = (LocalDataSourceConfig) ld.clone();
            config.clonedFrom = ld;
            TableInfo[] tis = readTableInfos(id);

            HashMap<String, List<TableInfo>> map = DBUtil.getTableInfoLists(tis, DEFAULT_SCHEMA);
            this.tableSelector.addDataSource(id, config.getDisplayName(), map, config.getSelectedSchema());
            
            this.localConfigMap.put(id, (LocalDataSourceConfig) config.clone());
            setStatusMessage("Connected to " + config.getDisplayName() + ".");

            // put the original (uncloned) config in a set so we'll know we're already
            // connected to it.
            establishedConnections.add(ld);

            if(screenState == NO_CONNECTIONS) {
                setScreenState(HAS_CONNECTIONS);
            }
        }
        finally {
            DBUtil.closeConnection(conn);
        }

    }

    public TableInfo[] readTableInfos(Integer dsId) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        try {
             setStatusMessage("Fetching table list...");
             showBusyCursor();
             
             DataSource ds = this.dsMap.get(dsId);
             conn = ds.getConnection();
             
             DatabaseMetaData dbmd = conn.getMetaData();

             if(showSystemTables) {
                 rs = dbmd.getTables(null, null, "%", null);
             }
             else {
                 rs = dbmd.getTables(null, null, "%", new String[] {"VIEW", "TABLE"});
             }

             TableInfo[] tis = DBUtil.getTableInfos(rs);
             Arrays.<TableInfo>sort(tis, tableTypeComparator);
             return tis;
        }
        finally {
            setStatusMessage(null);
            DBUtil.closeResultSet(rs);
            DBUtil.closeConnection(conn);
            showNormalCursor();
        }

    }



    public void newQueryForm(Integer dataSourceId, TableInfo ti) 
        throws SQLException, NoSuchElementException
    {

        // make sure the TableInfo has its columns set.
        if(ti.getColumns() == null) {
            setColumnsAndKeys(dataSourceId, ti);
        }

        if(ti.getColumns().length == 0) {
            throw new SQLException("This QueryForm cannot be created because the database is indicating that the table or view has no columns.");
        }

        DataSource ds = (DataSource) dsMap.get(dataSourceId);
        LocalDataSourceConfig config =  (LocalDataSourceConfig) localConfigMap.get(dataSourceId);

        String windowTitle = ti.getTableName();
        String dataSourceName = config.getDisplayName();

        Set editableTypes = getEditableTypes(config.getDriverClassName());
        
        QueryPanel qp = new QueryPanel(ti, ds, dataSourceId, this, this, editableTypes);
        qp.addQueryPanelListener(this);
        qp.setWindowTitle(windowTitle);
        qp.setDataSourceDisplayName(dataSourceName);

        String baseName = "(" + dataSourceId + ") " + windowTitle + " [QUERY";

        XInternalFrame jif = new XInternalFrame(baseName + " / " + dataSourceName + "]", true, true, true, true);
        jif.setContentPane(qp);
        this.desktop.add(jif);
        jif.addInternalFrameListener(this);
        jif.getMenuItem().setText(baseName + "]");
        windowMenu.add(jif.getMenuItem());
        jif.pack();
        offset += increment;
        if(offset > (10 * increment)) offset = 0;
        jif.setBounds(offset, offset, 500, 300);
        jif.setVisible(true);

//      desktop.getDesktopManager().maximizeFrame(jif);
        setScreenState(QUERY_PANEL_NO_RESULTSET);

    }








    public void maybeExitProgram() {

        try {
//            int reply = JOptionPane.showConfirmDialog(this,
//                "Exit the program?",
//                "Confirm Exit",
//                JOptionPane.YES_NO_OPTION);
//
//            if(reply == JOptionPane.NO_OPTION) return;

            File f = configFile;

            if(f.exists() && f.canWrite() == false) {
                GUIHelper.warningMsg(this, "The configuration file, " + f.getCanonicalPath()
                    + ", is read-only. Configuration changes will not be saved.", "QueryForm");
                System.exit(0);
            }
            setStatusMessage("Saving configuration to " + f.getAbsolutePath() + ".");

            File tempfile = File.createTempFile("qformxml", null);
            config.writeConfig(tempfile);
//            f.delete();
            
            FileOutputStream fos = new FileOutputStream(f);
            FileInputStream fis = new FileInputStream(tempfile);

            int inchar = -1;
            while((inchar = fis.read()) != -1) fos.write(inchar);

            fis.close();
            fos.close();
            tempfile.deleteOnExit();

            System.exit(0);
        }
        catch(Throwable ex) {
            logger.error("maybeExitProgram(): " + ex, ex );
            GUIHelper.errMsg(this, "An error occurred while saving the configuration. Changes to the configuration file have not been saved.", "Application Error");
            System.exit(-1);
        }
        
    }




    public void setStatusMessage(String msg) {
        super.setStatusMessage(msg);
    }



    /**
     * Invoked when a internal frame has been opened.
     * This is an empty implementation which can be overriden by subclasses.
     * @see javax.swing.JInternalFrame#show
     */
    public void internalFrameOpened(InternalFrameEvent e) {
        setScreenState();
    }


    /**
     * Invoked when an internal frame is activated.
     * This is an empty implementation which can be overriden by subclasses.
     * @see javax.swing.JInternalFrame#setSelected
     */
    public void internalFrameActivated(InternalFrameEvent e) {
        setScreenState();
        
    }


    /**
     * Invoked when an internal frame is in the process of being closed.
     * The close operation can be overridden at this point.
     * This is an empty implementation which can be overriden by subclasses.
     * @see javax.swing.JInternalFrame#setDefaultCloseOperation
     */
    public void internalFrameClosing(InternalFrameEvent e) {
        setScreenState();

    }

    /**
     * Invoked when an internal frame is de-activated.
     * This is an empty implementation which can be overriden by subclasses.
     * @see javax.swing.JInternalFrame#setSelected
     */
    public void internalFrameDeactivated(InternalFrameEvent e) {
        setScreenState();
    }

    public void internalFrameClosed(InternalFrameEvent e) {
        JInternalFrame f = (JInternalFrame) e.getSource();
        Container c = f.getContentPane();
        if(c instanceof QueryPanel) {
            QueryPanel qp = (QueryPanel) c;

            // make sure the current connection is 
            // closed on the query panel
            qp.dispose();
        }

        // this hopefully will mitigate a memory leak in some
        // JREs whereby a JInternalFrame never gets garbage collected.
        // We'll remove it's reference to the contentpane in the hopes
        // that it (the contentpane) will be garbage collected.
        f.setContentPane(new JPanel());

    }

    /**
     * Invoked when an internal frame is iconified.
     * This is an empty implementation which can be overriden by subclasses.
     * @see javax.swing.JInternalFrame#setIcon
     */
    public void internalFrameIconified(InternalFrameEvent e) {
        setScreenState();
    }

    /**
     * Invoked when an internal frame is de-iconified.
     * This is an empty implementation which can be overriden by subclasses.
     * @see javax.swing.JInternalFrame#setIcon
     */
    public void internalFrameDeiconified(InternalFrameEvent e) {
        setScreenState();
    }



    protected void setColumnsAndKeys(Integer sourceId, TableInfo ti) 
        throws SQLException, NoSuchElementException 
    {
        setColumns(sourceId, ti);

        if(System.getProperty("no.foreign.keys") != null) {
            fkeysNotSupported.add(sourceId);
        }

        if(this.pkeysNotSupported.contains(sourceId) == false) {
            try {
                setPrimaryKeys(sourceId, ti);
            }
            catch(SQLException ex) {
                logger.error("setColumnsAndKeys(): WARNING: Primary keys could not be read for data source ID = " + sourceId, ex);
                pkeysNotSupported.add(sourceId);
            }
        }

        if(this.fkeysNotSupported.contains(sourceId) == false) {
            try {
                setForeignKeys(sourceId, ti);
            }
            catch(SQLException ex) {
                logger.error("setColumnsAndKeys(): WARNING: Foreign keys could not be read for data source ID = " + sourceId, ex);
                fkeysNotSupported.add(sourceId);
            }
        }

        if(this.exkeysNotSupported.contains(sourceId) == false) {
            try {
                setExportedKeys(sourceId, ti);
            }
            catch(SQLException ex) {
                logger.error("setColumnsAndKeys(): WARNING: Exported keys could not be read for data source ID = " + sourceId, ex);
                exkeysNotSupported.add(sourceId);
            }
        }
    }




    public void setColumns(Integer sourceId, TableInfo ti) 
        throws SQLException, NoSuchElementException
    {
        DataSource ds = (DataSource) dsMap.get(sourceId);
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = ds.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getColumns(ti.getTableCat(), ti.getTableSchem(), ti.getTableName(), "%");
            Column[] cols = DBUtil.getColumns(rs);
            ti.setColumns(cols);
        }
        finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeConnection(conn);
        }
    }


    public void setPrimaryKeys(Integer sourceId, TableInfo ti) 
        throws SQLException
    {

        DataSource ds = (DataSource) dsMap.get(sourceId);
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = ds.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getPrimaryKeys(ti.getTableCat(), ti.getTableSchem(), ti.getTableName());
            while(rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                Column col = ti.getColumn(colName);
                if(col == null) {
                    logger.warn("setPrimaryKeys(): NO COLUMN FOUND MATCHING PK COLUMN " + colName);
                }
                else {
                    col.setPkComponent(true);
                }
            }
        }
        finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeConnection(conn);
        }
    }


    public void setForeignKeys(Integer sourceId, TableInfo ti) 
        throws SQLException
    {

        DataSource ds = (DataSource) dsMap.get(sourceId);
        Connection conn = null;
        ResultSet rs = null;


        try {
            conn = ds.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();

            rs = dbmd.getImportedKeys(ti.getTableCat(),
                ti.getTableSchem(), ti.getTableName());

            ForeignKey[] fkeys = DBUtil.getForeignKeys(rs);

            for(int j=0; j<fkeys.length; j++) {
                List<ForeignKeyColumn> fkeyColumns = fkeys[j].getForeignKeyColumns();
                for(int k=0; fkeyColumns != null && k<fkeyColumns.size(); k++) {
                    ForeignKeyColumn fkc = fkeyColumns.get(k);
                    fkc.setLocalColumn(ti.getColumn(fkc.getLocalColumnName()));
                }
            }

            ti.setForeignKeys(fkeys);
        }
        finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeConnection(conn);
        }
    }


    public void setExportedKeys(Integer sourceId, TableInfo ti) 
        throws SQLException
    {

        DataSource ds = (DataSource) dsMap.get(sourceId);
        Connection conn = null;
        ResultSet rs = null;


        try {
            conn = ds.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();

            rs = dbmd.getExportedKeys(ti.getTableCat(),
                ti.getTableSchem(), ti.getTableName());

            ForeignKey[] fkeys = DBUtil.getForeignKeys(rs);

            for(int j=0; j<fkeys.length; j++) {
                List<ForeignKeyColumn> fkeyColumns = fkeys[j].getForeignKeyColumns();
                for(int k=0; fkeyColumns != null && k<fkeyColumns.size(); k++) {
                    ForeignKeyColumn fkc = (ForeignKeyColumn) fkeyColumns.get(k);
                    fkc.setLocalColumn(ti.getColumn(fkc.getForeignColumnName()));
                }
            }

            ti.setExportedKeys(fkeys);
        }
        finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeConnection(conn);
        }
    }


    JFileChooser fileChooser = new JFileChooser();

    private File getOutputFile(String title) 
    {
        
        if(title != null) fileChooser.setDialogTitle(title);
        int option = fileChooser.showSaveDialog(this);
        if(option == fileChooser.CANCEL_OPTION) return null;
        File f = fileChooser.getSelectedFile();

        // remember the selected directory.
        File newDir = f.getParentFile().getAbsoluteFile();
        fileChooser.setCurrentDirectory(newDir);
        return f;
         
    }


    public void queryPanelStateChanged(QueryPanelEvent e) {
        setScreenState();
    }

    public void queryPanelHorizontalScrollbarStateChanged(QueryPanelEvent event) {
        QueryPanel qp = getCurrentQueryPanel();
        this.horizontalScrollbarMenuItem.setSelected(qp.isHorizontalScrollBarVisible());
    }


    private Set getEditableTypes(String driverClassName) {

        Set<Integer> set = driverClassToEditableTypeMap.get(driverClassName);
        if(set != null) return set;


        // see if the DriverClassList class knows anything about this driver type.
        set = DriverClassList.getEditableTypes(driverClassName);
        if(set == null) set = new HashSet<Integer>();
        driverClassToEditableTypeMap.put(driverClassName, set);

        // see if any editable types were configured in the file for this driver type
        String types = editableTypes.getProperty(driverClassName);
        if(types != null) {
            try {
                StringTokenizer st = new StringTokenizer(types);
                while(st.hasMoreTokens()) {
                    Integer i = new Integer(st.nextToken());
                    set.add(i);
                    logger.debug("getEditableTypes(): Adding editable type {} for driver {}", i, driverClassName);
                }
            }
            catch(Exception ex) {
                logger.error("getEditableTypes(): Error parsing editable types for driver class: " + driverClassName, ex);
            }
        }

        return set;
    }


    private void saveSchemaSelection(int sourceId,  String schema) {
         logger.debug("saveSchemaSelection(): sourceId={}, schema={}", sourceId, schema);
         LocalDataSourceConfig localConfig = this.localConfigMap.get(sourceId);
         if(localConfig == null) {
              logger.warn("saveSchemaSelection(): LocalDataSourceConfig not found for id {}", sourceId );
              return;
         }
         localConfig.clonedFrom.setSelectedSchema(schema);

    }

    public void dataSourceSelected(int sourceId) {
         logger.debug("dataSourceSelected(): {}", sourceId);
         LocalDataSourceConfig localConfig = localConfigMap.get(sourceId);
         if(localConfig != null) {
              String schema = localConfig.clonedFrom.getSelectedSchema();
              if(schema != null) {
                   tableSelector.setSelectedSchema(schema);
              }
         }

    }

    public void schemaSelected(int sourceId, String schema) {
         logger.debug("schemaSelected(): {}, {}", sourceId, schema);

         LocalDataSourceConfig localConfig = localConfigMap.get(sourceId);
         if(localConfig != null) {
              localConfig.clonedFrom.setSelectedSchema(schema);
         }
    }




    


}
