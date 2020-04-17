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
 *
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/MDIPanel.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2003/07/13 04:36:58 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing;



import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;


public class MDIPanel extends JPanel implements StatusMessageDisplay {


    protected JMenuBar menuBar = new JMenuBar();

    protected JPanel toolbarHolder = new JPanel();

    protected JToolBar toolBar = new JToolBar();

    protected JDesktopPane desktop = new ScrollingDesktopPane();

    protected JPanel statusBar = new JPanel();

    protected JLabel statusLabel = new JLabel();    

    public MenuItemListener menuItemListener = new MenuItemListener(this);


    public MDIPanel() {


        setPreferredSize(new Dimension(760, 560));


        this.setLayout(new BorderLayout());


        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        //
        // add the menubar
        //
        add(menuBar, BorderLayout.NORTH);

        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        //
        // add the toolbar
        //
        add(toolbarHolder, BorderLayout.CENTER);
        toolbarHolder.setLayout(new BorderLayout());
        toolbarHolder.add(toolBar, BorderLayout.NORTH);


        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        //
        // configure the desktop
        //
//        desktop.setBackground(new java.awt.Color(127, 127, 127));
        desktop.putClientProperty("JDesktopPane.dragMode",
                          "outline");
//        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        desktop.setBorder(new ThinBevelBorder(ThinBevelBorder.LOWERED));
//        desktop.setDesktopManager(new ScrollingDesktopManager());
        toolbarHolder.add(new JScrollPane(desktop), BorderLayout.CENTER);


        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        //
        // add the statusbar
        //
        add(statusBar, BorderLayout.SOUTH);
        statusBar.setLayout(new BorderLayout());
        statusLabel.setOpaque(true);
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 10));
        statusLabel.setForeground(java.awt.Color.black);
        statusLabel.setText("  ");
        statusLabel.setBorder(new CompoundBorder(
            new ThinBevelBorder(ThinBevelBorder.LOWERED),
            new EmptyBorder(2, 6, 2, 0)));

        statusLabel.setMinimumSize(statusLabel.getPreferredSize());
        statusLabel.setPreferredSize(statusLabel.getPreferredSize());
        
        statusBar.add(statusLabel);
        statusBar.setBorder(new EmptyBorder(5,2,2,2));

    }



    /**
     * Displays the String "message" on the status bar.
     */
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    public String getStatusMessage() {
        return statusLabel.getText();
    }


}
