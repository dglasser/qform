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


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.datatransfer.*;



/**
 * This is a simple dialog box that displays the system properties of a
 * running app, along with the memory usage and the program startup time
 * (if a Date instance is passed to the constructor.)
 * 
 * @author Dave Glasser
 */
public class SystemInfoDialog extends JDialog implements ActionListener {

    private JButton btnCopy = new JButton("Copy to Clipboard");

    private JButton btnClose = new JButton("Close");

    private Object[][] buttonConfig =
    {
        {btnCopy, "C", "COPY", "Copy the displayed information to the clipboard."}
        ,{btnClose, "S", "CLOSE", "Close this dialog."}
    };

    private SystemPanel systemPanel = null;


    public SystemInfoDialog() {
        this(null, null);
    }


    /**
     * If a Date is passed to this constructor, it will be displayed
     * on the dialog with the label "Up since:". Otherwise it won't be.
     * 
     * @param runningSince a java.util.Date that is used to indicate when the app
     * that owns this dialog box was started.
     * 
     */
    public SystemInfoDialog(Date runningSince) {
        this(null, runningSince);
    }

    /**
     * If a Date is passed to this constructor, it will be displayed
     * on the dialog with the label "Up since:". Otherwise it won't be.
     * 
     * @owner the Frame, if any, that "owns" this dialog.
     * 
     * @param runningSince a java.util.Date that is used to indicate when the app
     * that owns this dialog box was started.
     * 
     */
    public SystemInfoDialog(Frame owner, Date runningSince) {
        super(owner);
        setTitle("System Information");

        JPanel cp = new JPanel();
        cp.setLayout(new BorderLayout());
        
        systemPanel = new SystemPanel(runningSince);
        systemPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        cp.add(systemPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        GUIHelper.buildButtonPanel(buttonPanel, buttonConfig, this);
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        cp.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(cp);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if(cmd.equals("CLOSE")) {
            setVisible(false);
        }
        else if(cmd.equals("COPY")) {
            String info = systemPanel.getInfoAsString("\n");
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            cb.setContents(new StringSelection(info), null);
        }
    }

    /**
     * Launches a small program to test this class.
     */
    public static void main(String[] args) throws Exception {
        SystemInfoDialog d = new SystemInfoDialog(new Date());
        d.setModal(true);
        d.setVisible(true);
        System.exit(0);
    } 


    public void setVisible(boolean b) {
        if(b) {
            systemPanel.refreshSystemPropertiesTable();
        }
        super.setVisible(b);

    }


}
