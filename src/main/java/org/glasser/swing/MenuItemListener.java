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


import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

/**
 * This listener can be used to transmit status messages to a status bar 
 * whenever a JMenuItem is passed over in an open menu. It should be
 * added as both a ChangeListener and ItemListener to a JMenuItem.
 */
public class MenuItemListener implements ChangeListener, ItemListener {

    private StatusMessageDisplay display = null;

    /**
     * Constructor.
     *
     * @param statusMsg the message that is sent to the StatusMessageDisplay whenever
     * a JMenuItem is passed over.  May be null.
     *
     * @param  display an object implementing the StatusMessageDisplay interface, which will
     * receive the status messages related to the menu item.
     */
    public MenuItemListener(StatusMessageDisplay display) {
        if(display == null) throw new IllegalArgumentException("Required argument missing.");
        this.display = display;
    }

    private JMenuItem lastItem = null;
    private boolean lastArmed = false;

    public void stateChanged(ChangeEvent e) {

        Object src = e.getSource();
        if(src instanceof JMenu) {
            JMenuItem menuItem = (JMenuItem) src;
            String statusMsg = menuItem.getToolTipText();
            boolean isArmed = menuItem.isSelected();
//            System.out.println("JMenu STATE_CHANGED: (" + menuItem.isArmed() + "/" + menuItem.isSelected() + ") " + menuItem.getText());
            if(isArmed && (menuItem != lastItem || isArmed != lastArmed)) {
                display.setStatusMessage(statusMsg);
            }
            else {
                String currentStatusMessage = display.getStatusMessage();
//                System.out.println(statusMsg + "|" + currentStatusMessage);
                if(statusMsg != null && statusMsg.equals(currentStatusMessage)) {
                    display.setStatusMessage(null);
                }
            }
            lastItem = menuItem;
            lastArmed = isArmed;

        }
        else if(src instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) src;
            String statusMsg = menuItem.getToolTipText();
            boolean isArmed = menuItem.isArmed();
//            System.out.println("STATE_CHANGED: (" + menuItem.isArmed() + "/" + menuItem.isSelected() + ") " + menuItem.getText());
            if(isArmed && (menuItem != lastItem || isArmed != lastArmed)) {
                display.setStatusMessage(statusMsg);
            }
            else {
                String currentStatusMessage = display.getStatusMessage();
//                System.out.println(statusMsg + "|" + currentStatusMessage);
                if(statusMsg != null && statusMsg.equals(currentStatusMessage)) {
                    display.setStatusMessage(null);
                }
            }
            lastItem = menuItem;
            lastArmed = isArmed;

        }
    }

    public void itemStateChanged(ItemEvent e) {
        
        Object src = e.getSource();
        if(src instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) src;
            String statusMsg = menuItem.getToolTipText();
//            System.out.println("ITEM_STATE_CHANGED: (" + menuItem.isArmed() + ") " + menuItem.getText());
            if(menuItem.isArmed() && ! menuItem.isSelected()) {
                display.setStatusMessage(statusMsg);
            }
            else {
                String currentStatusMessage = display.getStatusMessage();
//                System.out.println(statusMsg + "|" + currentStatusMessage);
                if(statusMsg != null && statusMsg.equals(currentStatusMessage)) {
                    display.setStatusMessage(null);
                }
            }
        }
    }
}

