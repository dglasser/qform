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
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

public class ScrollingDesktopManager extends DefaultDesktopManager {

    /**
     * Searches up the ancestor hierarchy for the given JInternalFrame until
     * it finds a JDesktopPane, and then returns it.
     */
    private JDesktopPane getJDesktopPaneAncestor(JComponent f) {

        Component parent = f;
        while((parent = parent.getParent()) != null) {
            if(parent instanceof JDesktopPane) {
                return (JDesktopPane) parent;
            }
        }
        return null;
    }

    /**
     * Searches up the ancestor hierarchy for the given JInternalFrame until
     * it finds a JScrollPane, and then calls validate() on that scrollpane.
     */
    private void validateScrollPane(JComponent f) {
        JDesktopPane jdp = getJDesktopPaneAncestor(f);
        if(jdp != null) {
            jdp.validate();
        }
    }

    /** 
     * This is set to true while a call to the minimizeFrame method 
     * is in process. 
     */  
    private boolean minimizeFrameCalled = false;


    /** Generally, this indicates that the frame should be restored to it's
      * size and position prior to a maximizeFrame() call.
      */
    public void minimizeFrame(JInternalFrame f) {

        boolean recursive = minimizeFrameCalled;
        try {
            minimizeFrameCalled = true; 
//            System.out.println("minimizeFrame(): Called. recursive=" + recursive + ", isIcon=" + f.isIcon() 
//                               + " " + f.getTitle());
            if(!recursive) {
                JDesktopPane owner = getJDesktopPaneAncestor(f);
                if(owner != null) {
                    JInternalFrame[] frames = owner.getAllFrames();
                    for(int j=0; j<frames.length; j++) {
                        JInternalFrame jif = frames[j];
                        if(jif == f) {
                            continue;
                        }
                        try {
                            if(jif.isMaximum()) {
                                // Before this returns, a recursive call to this method will
                                // be made for this frame. 
                                jif.setMaximum(false);
                                if(wasIcon(jif)) {
                                    jif.setIcon(true);
                                }
                            }
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            _minimizeFrame(f);
        }
        finally {
            minimizeFrameCalled = recursive;
        }
    }

    private void _minimizeFrame(JInternalFrame f) {
        super.minimizeFrame(f);
    }


    /** If possible, display this frame in an appropriate location.
      * Normally, this is not called, as the creator of the JInternalFrame
      * will add the frame to the appropriate parent.
      */
    public void openFrame(JInternalFrame f) {
        super.openFrame(f);
        validateScrollPane(f);
    }


    /** Generally, this call should remove the frame from it's parent. */
    public void closeFrame(JInternalFrame f) {
        super.closeFrame(f);
        validateScrollPane(f);
    }


    /** Generally, remove any iconic representation that is present and restore the
      * frame to it's original size and location.
      */
    public void deiconifyFrame(JInternalFrame f) {
        super.deiconifyFrame(f);
        validateScrollPane(f);
    }


    private boolean maximizeFrameCalled = false;

    /** Generally, the frame should be resized to match it's parents bounds. */
    /**
     * Resizes the frame to fill its parents bounds.
     * @param the frame to be resized
     */

    public void maximizeFrame(JInternalFrame f) {

        boolean recursive = maximizeFrameCalled;
        
        try {
            
            maximizeFrameCalled = true; 

            System.out.println("maximizeFrame(): Called. recursive=" + recursive + ", isIcon=" + f.isIcon() + " " + f.getTitle());

            if(!recursive) {
                JDesktopPane owner = getJDesktopPaneAncestor(f);
                if(owner != null) {
                    JInternalFrame[] frames = owner.getAllFrames();
                    
                    for(int j=0; j<frames.length; j++) {
                        JInternalFrame jif = frames[j];
                        if(jif == f) {
                            continue;
                        }
                        try {
                            if(!jif.isMaximum()) {
                                frames[j].setMaximum(true);
                            }
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            _maximizeFrame(f, !recursive);
        }
        finally {
            maximizeFrameCalled = recursive;
        }
    }

    private void _maximizeFrame(JInternalFrame f, boolean setSelected) {
        System.out.println("_maximizeFrame(): Called. " + setSelected);
    
        Rectangle p;
        if(!f.isIcon()) {
            Container c = GUIHelper.getViewport(f);
            if(c == null) return;
            p = c.getBounds();
        } 
        else {
            Container c = f.getDesktopIcon().getParent();
            if(c == null)
                return;
            p = c.getBounds();
            try {
                f.setIcon(false);
            } 
            catch(PropertyVetoException e2) {
            }
        }
        f.setNormalBounds(f.getBounds());
        setBoundsForFrame(f, 0, 0, p.width, p.height);
        try {
            if(setSelected) {
                f.setSelected(true);
            }
        } 
        catch(PropertyVetoException e2) {
        }
    
        removeIconFor(f);
    }


//    /**
//     * Generally, indicate that this frame has lost focus. This is usually called
//     * after the JInternalFrame's IS_SELECTED_PROPERTY has been set to false.
//     */
//    public void deactivateFrame(JInternalFrame f) {
//        super.deactivateFrame(f);
//        validateScrollPane(f);
//    }
//
//
    /** Generally, remove this frame from it's parent and add an iconic representation. */
    public void iconifyFrame(JInternalFrame f) {
        super.iconifyFrame(f);
        validateScrollPane(f);
    }
//
//
////    /** The user has moved the frame. Calls to this method will be preceded by calls
//      * to beginDraggingFrame().
//      *  Normally <b>f</b> will be a JInternalFrame.
//      */
//    public void dragFrame(JComponent f, int newX, int newY) {
//        super.dragFrame(f, newX, newY);
//    }
//
//
//    /**
//     * Generally, indicate that this frame has focus. This is usually called after
//     * the JInternalFrame's IS_SELECTED_PROPERTY has been set to true.
//     */
//    public void activateFrame(JInternalFrame f) {
//        super.activateFrame(f);
//        validateScrollPane(f);
//    }


    /** This methods is normally called when the user has indicated that
      * they will begin resizing the frame. This method should be called
      * prior to any resizeFrame() calls to allow the DesktopManager to prepare any
      * necessary state.  Normally <b>f</b> will be a JInternalFrame.
      */
    public void beginResizingFrame(JComponent f, int direction) {
        super.beginResizingFrame(f, direction);
    }

    /** This method is normally called when the user has indicated that
      * they will begin dragging a component around. This method should be called
      * prior to any dragFrame() calls to allow the DesktopManager to prepare any
      * necessary state. Normally <b>f</b> will be a JInternalFrame.
      */
    public void beginDraggingFrame(JComponent f) {
        super.beginDraggingFrame(f);
    }
    /** This method signals the end of the resize session. Any state maintained by
      * the DesktopManager can be removed here.  Normally <b>f</b> will be a JInternalFrame.
      */
    public void endResizingFrame(JComponent f) {
        super.endResizingFrame(f);
//        System.out.println("END RESIZING " + f.getBounds());
        if(f instanceof JInternalFrame) {
            validateScrollPane((JInternalFrame) f);
        }
    }
    /** This method signals the end of the dragging session. Any state maintained by
      * the DesktopManager can be removed here.  Normally <b>f</b> will be a JInternalFrame.
      */
    public void endDraggingFrame(JComponent f) {
        super.endDraggingFrame(f);
//        System.out.println("END DRAGGING " + f.getBounds());
//        if(f instanceof JInternalFrame) {
            validateScrollPane(f);
//        }    
    }
    /** The user has resized the component. Calls to this method will be preceded by calls
      * to beginResizingFrame().
      *  Normally <b>f</b> will be a JInternalFrame.
      */
    public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
//        System.out.println("TRC: " + getClass().getName() + ".resizeFrame()");
        super.resizeFrame(f, newX, newY, newWidth, newHeight);
    }
    /** This is a primitive reshape method.*/
    public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
//        System.out.println("TRC: " + getClass().getName() + ".setBoundsForFrame()");
        super.setBoundsForFrame(f, newX, newY, newWidth, newHeight);
    }



}
