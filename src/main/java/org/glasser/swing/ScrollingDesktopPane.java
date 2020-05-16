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
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/ScrollingDesktopPane.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/01/26 01:02:15 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing;



import javax.swing.*;
import java.awt.*;



public class ScrollingDesktopPane extends JDesktopPane {



    public ScrollingDesktopPane() {
        setDesktopManager(new ScrollingDesktopManager());
    }
        
    /**
     * This method will examine the size and location of
     * all of the JInternalFrames in this JDesktopPane and 
     * return a preferred size that will encompass all of them.
     */
    public Dimension getPreferredSize() {

        

        JInternalFrame[] frames = this.getAllFrames();
        if(frames == null || frames.length == 0) {
            // nothing to do.
            return super.getPreferredSize();
        }

        int minX=0, minY=0, maxX=0, maxY=0;
        
        for(int j=0; j<frames.length; j++) {

            JInternalFrame jif = frames[j];
            Rectangle r = null;

            // maximized frames will be fitted to the
            // available space. Their bounds will be
            // whatever is available.
            if(jif.isMaximum()) {
                continue;
            }

            // if this internal frame is iconified, we need
            // to use the bounds for its desktop icon.
            if(jif.isIcon()) {
                r = jif.getDesktopIcon().getBounds();
            }
            else {
                r = jif.getBounds();
            }
            
            jif.getBounds();
            minX = (int) Math.min(minX, r.x);
            minY = (int) Math.min(minY, r.y);
            maxX = (int) Math.max(maxX, (r.x + r.width));
            maxY = (int) Math.max(maxY, (r.y + r.height));

        }

        Dimension d = new Dimension(Math.abs(minX) + maxX, Math.abs(minY) + maxY);
        //System.out.println("TRC: " + getClass().getName() + ".getPreferredSize() returning " + d);
        return d;



    }


}
