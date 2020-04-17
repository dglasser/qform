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
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/ThinBevelBorder.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2003/07/05 03:00:46 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * This Border class is essentially the same as javax.swing.border.BevelBorder, except that
 * it is one pixel wide instead of two.
 */
public class ThinBevelBorder implements java.io.Serializable, javax.swing.border.Border {

    public final static int RAISED = BevelBorder.RAISED;
    public final static int LOWERED = BevelBorder.LOWERED;

    protected int bevelType = RAISED;

    protected Color shadowColor = null;
    
    protected Color highlightColor = null;


    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }

    /** 
     * Reinitialize the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 2;
        return insets;
    }

    public int getBevelType() {
        return bevelType;
    }

    public void setBevelType(int type) {
        bevelType = type;
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public ThinBevelBorder() {
    }

    public ThinBevelBorder(int bevelType) {
        this.bevelType = bevelType;
    }

    public ThinBevelBorder(int bevelType, Color shadowColor, Color highlightColor) {
        this.bevelType = bevelType;
        this.shadowColor = shadowColor;
        this.highlightColor = highlightColor;
    }


    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        Color highlight = highlightColor == null ? c.getBackground().brighter() : highlightColor;
        Color shadow = shadowColor == null ? c.getBackground().darker() : shadowColor;

        Color upperLeft, lowerRight;

        if(bevelType == RAISED) {
            upperLeft = highlight;
            lowerRight = shadow;
        }
        else {
            upperLeft = shadow;
            lowerRight = highlight;
        }

        int left = x;
        int right = width - 1;
        int top = y;
        int bottom = height - 1;

        g.setColor(upperLeft);
        g.drawLine(left, top, right, top);
        g.drawLine(left, top, left, bottom);

        g.setColor(lowerRight);
        g.drawLine(right, bottom, left, bottom);
        g.drawLine(right, bottom, right, top);

    }
}

