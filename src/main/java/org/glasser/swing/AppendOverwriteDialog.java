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


import javax.swing.*;
import org.glasser.util.*;
import org.glasser.sql.*;
import org.glasser.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;



public class AppendOverwriteDialog extends JDialog implements ActionListener {


    private JButton btnAppend = new JButton("Append");

    private JButton btnOverwrite = new JButton("Overwrite");

    private JButton btnCancel = new JButton("Cancel");


    public final static String CANCEL_OPTION = "CANCEL";

    public final static String APPEND_OPTION = "APPEND";

    public final static String OVERWRITE_OPTION = "OVERWRITE";

    private JTextArea textArea = new JTextArea();


    private Object[][] buttonConfig =
    {
        {btnAppend, "A", APPEND_OPTION, "Append output to the selected file."}
        ,{btnOverwrite, "O", OVERWRITE_OPTION, "Overwrite existing file."}
        ,{btnCancel, "C", CANCEL_OPTION, "Cancel operation."}
    };


    public AppendOverwriteDialog(Frame parent) {
        super(parent);

        JPanel cp = new JPanel();
        setTitle("Confirm File Overwrite");
        setContentPane(cp);
        cp.setLayout(new BorderLayout());
        textArea.setOpaque(false);
        textArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setEnabled(false);
        textArea.setDisabledTextColor(Color.black);

        Font f = (Font) UIManager.getDefaults().get("Button.font");
        if(f != null) {
            textArea.setFont(f);
        }

        cp.add(textArea, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(new EmptyBorder(15,0, 15, 0));
        GUIHelper.buildButtonPanel(buttonPanel,buttonConfig,this);
        cp.add(buttonPanel, BorderLayout.SOUTH);
        setModal(true);
    }


    public void openDialog(String fileName, boolean showAppendButton) {
        
        setModal(true);
        btnAppend.setVisible(showAppendButton);
        if(fileName != null) {
            textArea.setText("The file you have selected:\n\n"
                + fileName
                + "\n\nalready exists. How do you want to proceed?");
        }
        else {
            textArea.setText("The selected file already exists.\n\nHow do you want to proceed?");
        }

        pack();
        selection = CANCEL_OPTION;
        
        super.setVisible(true);
    }

    public void setVisible(boolean b) {
        if(b) throw new RuntimeException("Use openDialog() to make dialog visible.");
    }

    private String selection = null;

    public void actionPerformed(ActionEvent e) {
        selection = e.getActionCommand();
        super.setVisible(false);
    }

    public String getSelection() {
        return selection;
    }


    public static void main(String[] args) throws Exception {


        AppendOverwriteDialog d = new AppendOverwriteDialog(null);
        d.openDialog("C:\\autoexec.bat", true);
        System.out.println(d.getSelection());
        System.exit(0);
        
    } 
    
       

}
