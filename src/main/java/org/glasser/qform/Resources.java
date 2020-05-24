/* ====================================================================
 * Copyright (c) 1998 - 2005 David F. Glasser.  All rights
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

/**
 * This is the base ResouceBundle for QueryForm.
 * 
 * @author Dave Glasser
 */
public class Resources extends ResourceBundle {


    private final static String[][] mappings =
    {
         {"menu.CONNECT.title",                                 "Connect"}
        ,{"menu.CONNECT.mnemonic",                              "C"}
        ,{"menu.CONNECT.DATA_SOURCE_DIALOG.label",			    "Data Source..."}
        ,{"menu.CONNECT.DATA_SOURCE_DIALOG.mnemonic",			"D"}
        ,{"menu.CONNECT.DATA_SOURCE_DIALOG.tooltip",			"Create, edit or connect to a data source."}
        ,{"menu.CONNECT.TABLE_METADATA.label",			        "Table Metadata..."}
        ,{"menu.CONNECT.TABLE_METADATA.mnemonic",			    "T"}
        ,{"menu.CONNECT.TABLE_METADATA.tooltip",			    "View metadata for a table."}
        ,{"menu.CONNECT.CLOSE_WINDOW.label",			        "Close Window"}
        ,{"menu.CONNECT.CLOSE_WINDOW.mnemonic",			        "C"}
        ,{"menu.CONNECT.CLOSE_WINDOW.tooltip",			        "Close the current window."}
        ,{"menu.CONNECT.EXIT.label",			                "Exit"}
        ,{"menu.CONNECT.EXIT.mnemonic",			                "X"}
        ,{"menu.CONNECT.EXIT.tooltip",			                "Exit the program."}

        ,{"menu.QUERY_FORM.title",                              "QueryForm"}
        ,{"menu.QUERY_FORM.mnemonic",                           "Q"}
        ,{"menu.QUERY_FORM.NEW_QUERY_FORM.label",			    "New Query Form..."}
        ,{"menu.QUERY_FORM.NEW_QUERY_FORM.mnemonic",			"Q"}
        ,{"menu.QUERY_FORM.NEW_QUERY_FORM.tooltip",			    "Open a new query form."}
        ,{"menu.QUERY_FORM.WINDOW_TITLE.label",			        "Window title..."}
        ,{"menu.QUERY_FORM.WINDOW_TITLE.mnemonic",			    "T"}
        ,{"menu.QUERY_FORM.WINDOW_TITLE.tooltip",			    "Set the window title for the current query form."}
        ,{"menu.QUERY_FORM.OPEN_FOR_QUERY.label",			    "Form Query"}
        ,{"menu.QUERY_FORM.OPEN_FOR_QUERY.mnemonic",			"F"}
        ,{"menu.QUERY_FORM.OPEN_FOR_QUERY.tooltip",			    "Enter query criteria in the current query form."}
        ,{"menu.QUERY_FORM.WHERE_CLAUSE_QUERY.label",			"Where Clause Query..."}
        ,{"menu.QUERY_FORM.WHERE_CLAUSE_QUERY.mnemonic",		"W"}
        ,{"menu.QUERY_FORM.WHERE_CLAUSE_QUERY.tooltip",			"Enter a custom where clause query."}
        ,{"menu.QUERY_FORM.ADD_RECORD.label",			        "Add Record"}
        ,{"menu.QUERY_FORM.ADD_RECORD.mnemonic",			    "A"}
        ,{"menu.QUERY_FORM.ADD_RECORD.tooltip",			        "Add a record through the current query form."}
        ,{"menu.QUERY_FORM.MODIFY_RECORD.label",			    "Modify Record"}
        ,{"menu.QUERY_FORM.MODIFY_RECORD.mnemonic",			    "M"}
        ,{"menu.QUERY_FORM.MODIFY_RECORD.tooltip",			    "Modify the current record."}
        ,{"menu.QUERY_FORM.DELETE_RECORD.label",			    "Delete Record"}
        ,{"menu.QUERY_FORM.DELETE_RECORD.mnemonic",			    "D"}
        ,{"menu.QUERY_FORM.DELETE_RECORD.tooltip",			    "Delete the current record."}
        ,{"menu.QUERY_FORM.CLONE_RECORD.label",			        "Clone Record"}
        ,{"menu.QUERY_FORM.CLONE_RECORD.mnemonic",			    "L"}
        ,{"menu.QUERY_FORM.CLONE_RECORD.tooltip",			    "Clone the current record."}
        ,{"menu.QUERY_FORM.GRID_VIEW.label",			        "Grid View "}
        ,{"menu.QUERY_FORM.GRID_VIEW.mnemonic",			    "G"}
        ,{"menu.QUERY_FORM.GRID_VIEW.tooltip",			        "Configure grid view tab."}
        ,{"menu.QUERY_FORM.EXPORT.label",			            "Export"}
        ,{"menu.QUERY_FORM.EXPORT.mnemonic",			        "X"}
        ,{"menu.QUERY_FORM.EXPORT.tooltip",			            "Export query results to a file."}
        ,{"menu.QUERY_FORM.EXECUTE_ACTION.label",			    "Excecute Operation"}
        ,{"menu.QUERY_FORM.EXECUTE_ACTION.mnemonic",			"E"}
        ,{"menu.QUERY_FORM.EXECUTE_ACTION.tooltip",			    "Execute the currently pending operation."}
        ,{"menu.QUERY_FORM.CANCEL_ACTION.label",			    "Cancel Operation"}
        ,{"menu.QUERY_FORM.CANCEL_ACTION.mnemonic",			    "C"}
        ,{"menu.QUERY_FORM.CANCEL_ACTION.tooltip",			    "Cancel the currently pending operation."}
        ,{"menu.WINDOW.MINIMIZE_ALL.label",			            "Minimize all"}

        ,{"menu.WINDOW.title",			                        "Window"}
        ,{"menu.WINDOW.mnemonic",			                    "W"}
        ,{"menu.WINDOW.MINIMIZE_ALL.mnemonic",			        "N"}
        ,{"menu.WINDOW.MINIMIZE_ALL.tooltip",			        "Minimize all windows"}
        ,{"menu.WINDOW.MAXIMIZE_ALL.label",			            "Maximize all"}
        ,{"menu.WINDOW.MAXIMIZE_ALL.mnemonic",			        "X"}
        ,{"menu.WINDOW.MAXIMIZE_ALL.tooltip",			        "Maximize all windows"}
        ,{"menu.WINDOW.RESTORE_ALL.label",			            "Restore all"}
        ,{"menu.WINDOW.RESTORE_ALL.mnemonic",			        "R"}
        ,{"menu.WINDOW.RESTORE_ALL.tooltip",			        "Restore all windows"}
        ,{"menu.WINDOW.CASCADE.label",			                "Cascade"}
        ,{"menu.WINDOW.CASCADE.mnemonic",			            "C"}
        ,{"menu.WINDOW.CASCADE.tooltip",			            "Cascade all windows"}
        ,{"menu.WINDOW.LOOK_AND_FEEL.label",			        "Look and Feel"}
        ,{"menu.WINDOW.LOOK_AND_FEEL.mnemonic",			        "L"}
        ,{"menu.WINDOW.LOOK_AND_FEEL.tooltip",			        "Change QueryForm's Look-and-Feel"}

        ,{"menu.HELP.title",			                        "Help"}
        ,{"menu.HELP.mnemonic",			                        "H"}
        ,{"menu.HELP.SYSINFO.label",			                "System Information..."}
        ,{"menu.HELP.SYSINFO.mnemonic",			                "I"}
        ,{"menu.HELP.SYSINFO.tooltip",			                "Display system information."}
        ,{"menu.HELP.ABOUT.label",			                    "About..."}
        ,{"menu.HELP.ABOUT.mnemonic",			                "A"}
        ,{"menu.HELP.ABOUT.tooltip",			                "Display program information."}

        ,{"menu.GRID_VIEW_SUB.COLUMN_MAP_DIALOG.label",			"Select visible columns..."}
        ,{"menu.GRID_VIEW_SUB.COLUMN_MAP_DIALOG.mnemonic",		"V"}
        ,{"menu.GRID_VIEW_SUB.COLUMN_MAP_DIALOG.tooltip",		"Select which columns will be displayed in this table."}
        ,{"menu.GRID_VIEW_SUB.TOGGLE_HORIZONTAL_SCROLLBAR.label",	"Horizontal scrollbar"}
        ,{"menu.GRID_VIEW_SUB.TOGGLE_HORIZONTAL_SCROLLBAR.mnemonic","H"}
        ,{"menu.GRID_VIEW_SUB.TOGGLE_HORIZONTAL_SCROLLBAR.tooltip",	"Show or hide the horizontal scrollbar."}

        ,{"menu.EXPORT_SUB.SELECTED_ROWS.label",			    "Selected Row(s)"}
        ,{"menu.EXPORT_SUB.SELECTED_ROWS.mnemonic",		        "S"}
        ,{"menu.EXPORT_SUB.SELECTED_ROWS.tooltip",		        ""}
        ,{"menu.EXPORT_SUB.ALL_ROWS.label",			            "All Rows"}
        ,{"menu.EXPORT_SUB.ALL_ROWS.mnemonic",			        "A"}
        ,{"menu.EXPORT_SUB.ALL_ROWS.tooltip",			        ""}
        ,{"menu.ALL_ROWS_SUB.EXPORT_ALL_INSERT.label",		"To INSERT Statements..."}
        ,{"menu.ALL_ROWS_SUB.EXPORT_ALL_INSERT.mnemonic",		"I"}
        ,{"menu.ALL_ROWS_SUB.EXPORT_ALL_INSERT.tooltip",		"Export all rows read as INSERT statements."}
        ,{"menu.ALL_ROWS_SUB.EXPORT_ALL_CSV.label",			"To CSV File..."}
        ,{"menu.ALL_ROWS_SUB.EXPORT_ALL_CSV.mnemonic",		"C"}
        ,{"menu.ALL_ROWS_SUB.EXPORT_ALL_CSV.tooltip",			"Export all rows read as comma-separated values."}
        ,{"menu.SELECTED_ROWS_SUB.EXPORT_SELECTED_INSERT.label",			    "To INSERT Statements..."}
        ,{"menu.SELECTED_ROWS_SUB.EXPORT_SELECTED_INSERT.mnemonic",			"I"}
        ,{"menu.SELECTED_ROWS_SUB.EXPORT_SELECTED_INSERT.tooltip",			"Export selected rows as INSERT statements."}
        ,{"menu.SELECTED_ROWS_SUB.EXPORT_SELECTED_CSV.label",			        "To CSV File..."}
        ,{"menu.SELECTED_ROWS_SUB.EXPORT_SELECTED_CSV.mnemonic",			    "C"}
        ,{"menu.SELECTED_ROWS_SUB.EXPORT_SELECTED_CSV.tooltip",			    "Export selected rows as comma-separated values."}
        ,{"menu.LOOK_AND_FEEL_SUB.INSTALL_LOOK_AND_FEEL.label",			        "Install new..."}
        ,{"menu.LOOK_AND_FEEL_SUB.INSTALL_LOOK_AND_FEEL.mnemonic",			    "I"}
        ,{"menu.LOOK_AND_FEEL_SUB.INSTALL_LOOK_AND_FEEL.tooltip",			    "Install a third-party Look-and-Feel"}
        ,{"menu.GRID_TAB_POPUP.COLUMN_MAP_DIALOG.label",			            "Select columns..."}
        ,{"menu.GRID_TAB_POPUP.COLUMN_MAP_DIALOG.mnemonic",			            ""}
        ,{"menu.GRID_TAB_POPUP.COLUMN_MAP_DIALOG.tooltip",			            "Select which columns will be displayed in this table."}
        ,{"menu.GRID_TAB_POPUP.WINDOW_TITLE.label",			                    "Window title..."}
        ,{"menu.GRID_TAB_POPUP.WINDOW_TITLE.mnemonic",			                ""}
        ,{"menu.GRID_TAB_POPUP.WINDOW_TITLE.tooltip",			                "Set the title for this window."}
        ,{"menu.GRID_TAB_POPUP.QUERYPANEL_TABLE_METADATA.label",			    "Table Metadata"}
        ,{"menu.GRID_TAB_POPUP.QUERYPANEL_TABLE_METADATA.mnemonic",			    ""}
        ,{"menu.GRID_TAB_POPUP.QUERYPANEL_TABLE_METADATA.tooltip",			    "View medadata for this table."}
        ,{"menu.GRID_TAB_POPUP.EXPORT_SELECTED.label",			                "Export Selected"}
        ,{"menu.GRID_TAB_POPUP.EXPORT_SELECTED.mnemonic",			            ""}
        ,{"menu.GRID_TAB_POPUP.EXPORT_SELECTED.tooltip",			            "Export selected rows to a file."}
        ,{"menu.GRID_TAB_POPUP.EXPORT_ALL.label",			                    "Export All"}
        ,{"menu.GRID_TAB_POPUP.EXPORT_ALL.mnemonic",			                ""}
        ,{"menu.GRID_TAB_POPUP.EXPORT_ALL.tooltip",			                    "Export all rows read to a file."}

    };

    private Hashtable<String, String> map = new Hashtable<>();

    public Resources() {
        for(int j=0; j<mappings.length; j++) {
            map.put(mappings[j][0], mappings[j][1]);
        }
    }

    protected Object handleGetObject(String key) {
        return map.get(key);
    }

    public Enumeration<String> getKeys() {
        return map.keys();
    }

    private static ResourceBundle defaultBundle = new Resources();

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle("org.glasser.qform.Resources");

        if(bundle == null) bundle = defaultBundle;
        return bundle;
    }



    public static void configureButton(JButton button, String command) {

        ResourceBundle bundle = getBundle();

        button.setActionCommand(command);

        String s = bundle.getString("button.label." + command);
        if(s != null && (s = s.trim()).length() > 0) {
            button.setText(s);
        }
        else {
            button.setText(null);
        }

        s = bundle.getString("button.mnemonic." + command);
        if(s != null && (s = s.trim()).length() > 0) {
            button.setMnemonic(s.charAt(0));
        }
        else {
            button.setMnemonic(0);
        }
         
        s = bundle.getString("button.tooltip." + command);
        if(s != null && (s = s.trim()).length() > 0) {
            button.setToolTipText(s);
        }
        else {
            button.setToolTipText(null);
        }

    }

    public static JLabel makeLabel(String key) {
        return new JLabel(getBundle().getString("label." + key));
    }


    /**
     * This will output all of the key-value mappings from
     * this ResourceBundle to System.out, in properties file format. This
     * is useful for starting a new bundle based on a properties file.
     */
    public static void main(String[] args) throws Exception {

        String ENDL = System.getProperty("line.separator");

        for(int j=0; j<mappings.length; j++) { 
            String val = mappings[j][1];
            if("_COMMENT_".equals(mappings[j][0])) {
               System.out.println(ENDL + "# " + val);
            }
            else {
               System.out.println(mappings[j][0] + "=" + val);
            }
        }
    } 
}

    


    






