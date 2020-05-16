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
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;


public class XInternalFrame extends JInternalFrame implements ActionListener {

    private final JMenuItem menuItem = new JMenuItem();

    private ImageIcon selectedIcon = null;

    private ImageIcon deselectedIcon = null;

    public static boolean debug = System.getProperty("XInternalFrame.debug") != null
        || System.getProperty("DEBUG") != null;


    private class Listener implements InternalFrameListener {
        /**
         * Invoked when an internal frame is activated.
         * @see javax.swing.JInternalFrame#setSelected
         */
        public void internalFrameActivated(InternalFrameEvent e) {

            menuItem.setIcon(selectedIcon);
        }
        /**
         * Invoked when a internal frame has been opened.
         * @see javax.swing.JInternalFrame#show
         */
        public void internalFrameOpened(InternalFrameEvent e) {
        }
        /**
         * Invoked when an internal frame is in the process of being closed.
         * The close operation can be overridden at this point.
         * @see javax.swing.JInternalFrame#setDefaultCloseOperation
         */
        public void internalFrameClosing(InternalFrameEvent e) {
            if(debug) System.out.println("TRC: " + getClass().getName() + ".internalFrameClosing()");
        }
        /**
         * Invoked when an internal frame is de-activated.
         * @see javax.swing.JInternalFrame#setSelected
         */
        public void internalFrameDeactivated(InternalFrameEvent e) {

            menuItem.setIcon(deselectedIcon);

        }
        /**
         * Invoked when an internal frame has been closed.
         * @see javax.swing.JInternalFrame#setClosed
         */
        public void internalFrameClosed(InternalFrameEvent e) {
            if(debug) System.out.println("TRC: " + getClass().getName() + ".internalFrameClosed()");
            Container c = menuItem.getParent();
            if(c != null) c.remove(menuItem);
            menuItem.removeActionListener(XInternalFrame.this);
        }
        /**
         * Invoked when an internal frame is iconified.
         * @see javax.swing.JInternalFrame#setIcon
         */
        public void internalFrameIconified(InternalFrameEvent e) {
        }
        /**
         * Invoked when an internal frame is de-iconified.
         * @see javax.swing.JInternalFrame#setIcon
         */
        public void internalFrameDeiconified(InternalFrameEvent e) {
        }

    }


    {
        menuItem.addActionListener(this);
        selectedIcon = GUIHelper.getImageIconFromClasspath("org/glasser/swing/images/Play16.gif");
        deselectedIcon = GUIHelper.getImageIconFromClasspath("org/glasser/swing/images/Clear16.gif");
        addInternalFrameListener(new Listener());

    }




    public XInternalFrame() {
        super();
    }

    public XInternalFrame(String title, boolean resizable) {
        super(title, resizable);
        menuItem.setText(title);
    }

    public XInternalFrame(String title, boolean resizable, boolean closable) {
        super(title, resizable, closable);
        menuItem.setText(title);
    }

    public XInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable) {
        super(title,resizable, closable, maximizable);
        menuItem.setText(title);
    }

    public XInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
        menuItem.setText(title);
    }

    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == menuItem) {

            try {
                if(this.isIcon()) {
                    this.setIcon(false);
                }
    
                JDesktopPane dp = this.getDesktopPane();

                if(dp != null) {
                    DesktopManager dm = dp.getDesktopManager();
                    if(dm != null) dm.activateFrame(this);
                    setSelected(true);
                    menuItem.setIcon(selectedIcon);
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }



    public JMenuItem getMenuItem() {
        return menuItem;
    }



    public JViewport getViewport() {
        Container parent = super.getParent();
        while(parent != null) {
            if(parent instanceof JViewport) return (JViewport) parent;
            parent = parent.getParent();
        }

        return null;

    }

    public void finalize() {
        if(debug) {
            System.out.println("XInternalFrame.finalize(): " + getTitle());
        }
    }

}
