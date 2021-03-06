/* ====================================================================
 * The QueryForm License, Version 1.1
 *
 * Copyright (c) 1998 - 2003 David F. Glasser.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by 
 *        David F. Glasser."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "QueryForm" and "David F. Glasser" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact dglasser@pobox.com.
 *
 * 5. Products derived from this software may not be called "QueryForm",
 *    nor may "QueryForm" appear in their name, without prior written
 *    permission of David F. Glasser.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL DAVID F. GLASSER, THE APACHE SOFTWARE 
 * FOUNDATION OR ITS CONTRIBUTORS, OR ANY AUTHORS OR DISTRIBUTORS
 * OF THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 *
 * ==================================================================== 
 */
package org.glasser.swing;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.glasser.sql.*;
import org.glasser.util.*;


public class LocalDataSourceConfigDialog extends JDialog implements ActionListener, ListSelectionListener {


    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LocalDataSourceConfigDialog.class);

    private LocalDataSourceConfigPanel configPanel = new LocalDataSourceConfigPanel();

    private JList<LocalDataSourceConfig> listbox = new JList<>();

    private JButton btnClone = new JButton("Clone");

    private JButton btnNew = new JButton("New");

    private JButton btnEdit = new JButton("Edit");

    private JButton btnDelete = new JButton("Delete");

    private JButton btnSave = new JButton("Save");

    private JButton btnCancel = new JButton("Cancel");

    private JButton btnClose = new JButton("Close");

    private JButton btnConnect = new JButton("Connect");

    private List<LocalDataSourceConfig> configList = new ArrayList<>();


    private Object[][] buttonConfig = 
    {
         {btnNew,   "N", "OPEN_NEW", "Configure a new data source."}
        ,{btnClone, "O", "CLONE", "Clone the selected data source."}
        ,{btnEdit, "E", "EDIT_EXISTING", "Edit the selected data source."}
        ,{btnDelete, "D", "DELETE_EXISTING", "Delete the selected data source configuration."}
        ,{btnSave, "S", "SAVE", "Save changes."}
        ,{btnCancel, "C", "CANCEL", "Discard changes."}
        ,{btnClose, "L", "CLOSE", "Close dialog."}
        ,{btnConnect, "T", "CONNECT", "Connect to selected data source."}
    };


    public final static int NOTHING_SELECTED = 0;

    public final static int ITEM_SELECTED = 1;

    public final static int EDIT_NEW = 2;

    public final static int EDIT_EXISTING = 3;
    public final static int CLONE = 4;


    private int screenState = NOTHING_SELECTED;

    
    private final static boolean[][] buttonStates = 
    {
        {true,  false,  false,  false,  false,  false,  true,   false}   // 0 NOTHING_SELECTED
       ,{true,  true,   true,   true,   false,  false,  true,   true}    // 1 ITEM_SELECTED
       ,{false, false,  false,  false,  true,   true,   false,  false}   // 2 EDIT_NEW
       ,{false, false,  false,  false,  true,   true,   false,  false}   // 3 EDIT_EXISTING
       ,{false, false,  false,  false,  true,   true,   false,  false}   // 4 CLONE

    };

    private boolean listStates[] = {true, true, false, false, false};

    private boolean configPanelStates[] = {false, false, true, true, true};

    private boolean disableSelectionEvents = false;


    public void setScreenState(int newState) {

        int index = listbox.getSelectedIndex();

        for(int j=0; j<buttonConfig.length; j++) {
            JButton button = (JButton) buttonConfig[j][0];
            button.setEnabled(buttonStates[newState][j]);
        }
        
        switch(newState) {
            case EDIT_NEW :

				LocalDataSourceConfig config = new LocalDataSourceConfig();
				configPanel.displayObject(config);
				selectedConfig = null;

                if(index > -1) {
                    // this flag is set to keep this method from being called
                    // from the valueChanged(ListSelectionEvent) method that will
                    // get fired as a result of removing the selection.
                    disableSelectionEvents = true;
                    listbox.removeSelectionInterval(index, index);
                }
                break;
            case CLONE :
                config = configList.get(index);
                if(config != null) {
                    config = (LocalDataSourceConfig) config.clone();
                    config.setDisplayName(null);
                    selectedConfig = null;
                    if(index > -1) {
                        disableSelectionEvents = true;
                        listbox.removeSelectionInterval(index, index);
                    }
                    configPanel.displayObject(config);
                    configPanel.focusDisplayNameField();
                }
                break;
            case EDIT_EXISTING :
                selectedConfig = configList.get(index);
                break;
            case NOTHING_SELECTED :
                configPanel.clearFields();
                break;
            case ITEM_SELECTED :
                LocalDataSourceConfig ld = configList.get(index);
                this.configPanel.displayObject(ld);
                break;

        }

        this.listbox.setEnabled(listStates[newState]);
        this.configPanel.setEditable(configPanelStates[newState]);

        screenState = newState;

    }

    private LocalDataSourceConfig selectedConfig = null;

    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        if(command == null) command = "";
                
        if(command.equals("OPEN_NEW")) {
            setScreenState(EDIT_NEW);
        }
        else if(command.equals("EDIT_EXISTING")) {
            setScreenState(EDIT_EXISTING);
        }
        else if(command.equals("CLONE")) {
            setScreenState(CLONE);
        }
        else if(command.equals("DELETE_EXISTING")) {

            int reply = JOptionPane.showConfirmDialog(this,
                "Do you want to delete the selected data source configuration?"
                + "\n(This operation cannot be undone.)", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if(reply == JOptionPane.NO_OPTION) return;

            int index = listbox.getSelectedIndex();
            if(index > -1) configList.remove(index);
            listbox.setListData(configList.toArray(new LocalDataSourceConfig[configList.size()]));
            configPanel.clearFields();
            setScreenState(NOTHING_SELECTED);

        }
        else if(command.equals("SAVE")) {

            // make sure a display name was entered.
            String displayName = configPanel.txtDisplayName.getText();
            if(Util.isNothing(displayName)) {
                GUIHelper.errMsg(this,
                    "Please enter a display name for this data source.",
                    null);
                configPanel.txtDisplayName.requestFocus();
                return;
            }

            if(selectedConfig != null) {
                configPanel.updateObject(selectedConfig);
            }
            else {
                selectedConfig = new LocalDataSourceConfig();
                configPanel.updateObject(selectedConfig);
                configList.add(selectedConfig);
            }

            Collections.<LocalDataSourceConfig>sort(configList, LocalDataSourceConfig.DISPLAY_NAME_COMPARATOR);
            listbox.setListData(configList.toArray(new LocalDataSourceConfig[configList.size()]));
            listbox.setSelectedValue(selectedConfig, true);
            
            selectedConfig = null;

            setScreenState(ITEM_SELECTED);
        
        }
        else if(command.equals("CANCEL")) {
            disableAndRefreshFields();
        }
        else if(command.equals("CLOSE")) {
            selectedItem = null;
            super.setVisible(false);
        }
        else if(command.equals("CONNECT")) {
            selectedItem = (LocalDataSourceConfig) listbox.getSelectedValue();
            super.setVisible(false);
        }
    }

    private LocalDataSourceConfig selectedItem = null;


    public LocalDataSourceConfig getSelectedItem() {
        return selectedItem;
    }


    public void setVisible(boolean b) {

        // clear any previous selections when the dialog is opened.
        if(b) {
            selectedItem = null;
            disableAndRefreshFields();        
        }
        super.setVisible(b);
    }



    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {
        if(disableSelectionEvents) {
            // when the selection is changed programatically, this flag can be
            // set to false to keep the event listener (this method) from
            // doing anything. The flag is reset automatically everytime it's
            // read to be true.
            disableSelectionEvents = false;
            return;
        }
        disableAndRefreshFields();
    }

    /**
     * If nothing is selected in the listbox, this will make sure that the fields
     * are cleared, and set the screen state to NOTHING_SELECTED. If something is
     * selected, this will populate the fields from the selected object and set
     * the screen state to ITEM_SELECTED.
     */
    private void disableAndRefreshFields() {
        int index = listbox.getSelectedIndex();
        if(index > -1) {
            setScreenState(ITEM_SELECTED);
        }
        else {
            setScreenState(NOTHING_SELECTED);
        }
    }


    public void setList(List<LocalDataSourceConfig> configs) {

        Collections.sort(configs, LocalDataSourceConfig.DISPLAY_NAME_COMPARATOR);
        configList = configs;
        listbox.setListData((LocalDataSourceConfig[]) configs.toArray(new LocalDataSourceConfig[configList.size()]));

    }

    public List<LocalDataSourceConfig> getList() {
        return configList;
    }



    public LocalDataSourceConfigDialog(Frame parent) {
        super(parent);

        JPanel cp = new JPanel();
        cp.setBorder(new EmptyBorder(10,10,10,10));
        cp.setLayout(new BorderLayout());


        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        JLabel label = new JLabel("Data Sources");
        label.setForeground(Color.black);
        p.add(label, BorderLayout.NORTH);
        listbox.setVisibleRowCount(10);
//        listbox.setPreferredSize(new Dimension(200, 120));
//        listbox.setBorder(new ThinBevelBorder(ThinBevelBorder.LOWERED));
        listbox.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listbox.getSelectionModel().addListSelectionListener(this);
        p.setBorder(new EmptyBorder(0,0,10, 0));
        p.add(new JScrollPane(listbox), BorderLayout.CENTER);

        cp.add(p, BorderLayout.CENTER);
       


        p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(configPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        for(int j=0; j<buttonConfig.length; j++) {
            JButton button = (JButton) buttonConfig[j][0];
            button.setMnemonic(((String) buttonConfig[j][1]).charAt(0));
            button.setActionCommand((String) buttonConfig[j][2]);
            button.setToolTipText((String) buttonConfig[j][3]);
            button.addActionListener(this);
            buttonPanel.add(button);
        }
        p.add(buttonPanel, BorderLayout.SOUTH);

        // create an Action that clicks the cancel button
        ButtonClicker cancelClicker = new ButtonClicker(btnCancel);

        // give the cancelClicker a nested action that clicks the close button,
        // so if the cancel button is disabled, the close button will be clicked.
        cancelClicker.setNestedAction(new ButtonClicker(btnClose));

        // now bind the action to the ESCAPE key.
        KeyStroke esc = KeyStroke.getKeyStroke("ESCAPE");
        cp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(esc, "_ESCAPE_");
        cp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "_ESCAPE_");
        cp.getActionMap().put("_ESCAPE_", cancelClicker);


        cp.add(p, BorderLayout.SOUTH);

        setContentPane(cp);

        setScreenState(NOTHING_SELECTED);

        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        listbox.addMouseListener(new MouseAdapter() {
                                               public void mouseClicked(MouseEvent e) {
                                                   if(e.getClickCount() == 2) {
                                                       Point p = e.getPoint();
                                                       logger.debug("mouseClicked(): clickpoint={}", p);
                                                       int clickedIndex = listbox.locationToIndex(p);

                                                       logger.debug("mouseClicked(): clickedIndex={}", clickedIndex);
                                                       if(clickedIndex > -1 && btnConnect.isEnabled()) {
                                                           btnConnect.doClick(0);
                                                       }
                                                   }
                                               }
                                           });


        setModal(true);
        pack();

    }

    public LocalDataSourceConfigDialog() {
        this(null);
    }

}
