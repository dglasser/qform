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
 * $Source: /cvsroot/qform/qform/src/org/glasser/qform/ThirdPartyLafDialog.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2003/07/14 10:13:58 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.qform;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.border.EmptyBorder;

import org.glasser.swing.*;


/**
 * This dialog is used to get the name of a Look And Feel class from the user.
 */
public class ThirdPartyLafDialog extends JDialog implements ActionListener {

    public static boolean debug = System.getProperty("ThirdPartyLafDialog.debug") != null;
    
    private JTextField txtInput = new JTextField();

    private JLabel lblHeader = new JLabel();

    private JButton btnPrev = new JButton();

    private JButton btnNext = new JButton();

    private JButton btnOK = new JButton("OK");

    private JButton btnCancel = new JButton("Cancel");

    private Object[][] buttonConfig =
    {
         {btnOK,     "O", "SUBMIT",     "Enter the class name for a third-party look-and-feel."}
        ,{btnCancel, "C", "CANCEL", "Cancel the operation."}
        

    };

    String prompt =
        "Enter the fully-qualified class name of the Look and Feel class you "
        + "are going to install. This will be something similar to \"com.sun.java.swing.plaf.gtk.GTKLookAndFeel,\""
        + " and the class it represents should currently be in QueryForm's classpath."
        + "\n\nIf it is not in QueryForm's classpath, "
        + "you can add it by placing its jar file in QueryForm's \"drivers\" directory and restarting QueryForm."
        + "\n\nIf you do not know the class name for the Look and Feel, consult the Look and Feel's documentation.";
    

    JTextArea promptDisplay = null;

    public ThirdPartyLafDialog(Frame parent) {
        super(parent);

        setTitle("Install Look And Feel");
        
        JPanel panel = new JPanel();

        panel.setBorder(new EmptyBorder(10,10,10,10));

        panel.setLayout(new BorderLayout());

        promptDisplay = new JTextArea() {
                public void updateUI() {
                    super.updateUI();
                    Color background = UIManager.getColor("Panel.background");
                    if(background != null) {
                        if(debug) System.out.println("promptDisplay: using Panel.background.");
                        setBackground(background);
                    }
                    else {
                        Container parent = getParent();
                        if(parent != null) {
                            System.out.println("Parent class is " + parent.getClass());
                            background = parent.getBackground();
                            if(background != null) {
                                if(debug) System.out.println("promptDisplay: using parent.getBackground(): " + background);
                                setBackground(background);
                            }
                        }
                    }

                    Color foreground = UIManager.getColor("Label.foreground");
                    if(foreground == null) foreground = Color.black;
                    setDisabledTextColor(foreground);
                    setForeground(foreground);

                    setFont(UIManager.getFont("Label.font"));
                }
            };


        promptDisplay.setEditable(false);
        promptDisplay.setEnabled(false);
        promptDisplay.setText(prompt);
        promptDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        promptDisplay.setLineWrap(true);
        promptDisplay.setWrapStyleWord(true);
        promptDisplay.setColumns(35);
        promptDisplay.setRows(12);
        promptDisplay.setOpaque(false);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(promptDisplay, BorderLayout.CENTER);
        topPanel.add(txtInput, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.CENTER);


        

        JPanel buttonPanel = new JPanel();

        GUIHelper.buildButtonPanel(buttonPanel,buttonConfig,this);


        txtInput.setBorder(new ThinBevelBorder(ThinBevelBorder.LOWERED));
        txtInput.enableInputMethods(false);
        txtInput.setColumns(35);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // make it so the ESCAPE key closes the dialog.
        KeyStroke esc = KeyStroke.getKeyStroke("ESCAPE");
        ButtonClicker closer = new ButtonClicker(btnCancel);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "_ESCAPE_");
        panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(esc, "_ESCAPE_");
        panel.getActionMap().put("_ESCAPE_", closer);
        setContentPane(panel);
        promptDisplay.updateUI();
        pack();

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

    }

    public void setVisible(boolean b) {
        if(b) throw new RuntimeException("ThirdPartyLafDialog must be opened with the openDialog() method.");
    }



    /**
     * This opens the dialog in a modal state, with the last item
     * in the historyList displayed for editing. When this method returns,
     * the results of the user editing can be retrieved with getWhereClause().
     */
    public void openDialog() {
        txtInput.setText(null);
        setModal(true);
        txtInput.requestFocus();
        super.setVisible(true);
        txtInput.requestFocus();
    }

    String input = null;


    /**
     * When a user closes the dialog, this returns the results of the editing.
     * The returned String will always be non-null if the user clicks Submit; it will be an
     * empty String ("") if the user clicks Submit without entering anything. It will be
     * null if the user closes the dialog or clicks Cancel.
     */
    public String getInput() {
        return input;
    }


    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        if(command.equals("SUBMIT")) {
            input = txtInput.getText();
            if(input == null || (input = input.trim()).length() == 0) {
                input = null;
                GUIHelper.errMsg(this, "Please enter a Look And Feel class name, or click Cancel.", null);
                return;
            }
            super.setVisible(false);
        }
        else if(command.equals("CANCEL")) {
            input = null;
            super.setVisible(false);
        }
    }

    
}
