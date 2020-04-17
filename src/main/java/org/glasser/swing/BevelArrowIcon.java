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
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/BevelArrowIcon.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2003/07/05 02:54:33 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing;



import javax.swing.*;
import java.awt.*;



/**
 * This is a very limited, basic Icon implementation that can be used as a beveled
 * arrow pointing up or down on a JButton.
 */
public class BevelArrowIcon implements Icon, java.io.Serializable {

    /**
     * Given the base of an equilateral triangle, this multiplier will give you its height.
     */
    protected final static double heightMultiplier = 0.86602540378443864676372317075294;

    protected double size = 16.0;


    public final static int UP = 0;

    public final static int DOWN = 1;

    protected int direction = UP;



    protected int margin;

    protected int height;

    protected int width;

    protected int middle;

    /**
     * Constructs a BevelArrowIcon with a base of 10 pixels, and the given direction.
     * 
     * @param direction indicates what direction the arrow should point, either BevelArrowIcon.UP
     * or BevelArrowIcon.DOWN.
     */
    public BevelArrowIcon(int direction) {
        this(10.0, direction);
    }

    /**
     * Constructs a BevelArrowIcon with a base of "size" pixels, and the given direction.
     * 
     * @param direction indicates what direction the arrow should point, either BevelArrowIcon.UP
     * or BevelArrowIcon.DOWN.
     */
    public BevelArrowIcon(double size, int direction) {
        if(size < 8 || size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("BevelArrowIcon(int size): size must be between 8 and "
                + Integer.MAX_VALUE + ".");
        }

        this.size = size;

        width = (int) size;

        height = (int) (heightMultiplier * size);

        margin = (int) ((size - (heightMultiplier * size)) / 2.0);

        middle = (int) (size / 2.0);

        if(direction == UP) {
            this.direction = UP;
        }
        else {
            this.direction = DOWN;
        }
    }


    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {

        Color background = c.getBackground();
        Color highlight = background.brighter();
        Color shadowColor = background.darker();
        if(c instanceof AbstractButton) {
            ButtonModel model = ((AbstractButton) c).getModel();

            // if the parent component is a button that is "armed" (pressed
            // but not yet released) then paint the background in the
            // dark shadow color.
            if(model.isArmed()) {
                shadowColor = shadowColor.darker();
            }
        }


        int topY = y + margin;
        int bottomY = topY + height;
        int rightX = x + width;
        int axisX = x + middle;


        if(direction == UP) {

            g.setColor(highlight);
            g.drawLine(x, bottomY, rightX, bottomY);
            g.drawLine(rightX, bottomY, axisX, topY);

            g.setColor(shadowColor);
            g.drawLine(axisX, topY, x, bottomY);

        }
        else {
            g.setColor(shadowColor);
            g.drawLine(x, topY, rightX, topY);
            g.drawLine(x, topY, axisX, bottomY);

            g.setColor(highlight);
            g.drawLine(axisX, bottomY, rightX, topY);

        }
    }


    /**
     * Returns the icon's width.
     *
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
        return width;
    }
    /**
     * Returns the icon's height.
     *
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
        return width;
    }


    /**
     * Launches a small demonstration program.
     */
    public static void main(String[] args) throws Exception {
         
        JFrame frame = new JFrame();
        JButton button = new JButton("Click");
        frame.setSize(200, 200);
        frame.setContentPane(new JPanel());
        frame.getContentPane().add(button);

        button.addActionListener(new java.awt.event.ActionListener() {
            int counter = 0;
            BevelArrowIcon upIcon = new BevelArrowIcon(UP);
            BevelArrowIcon downIcon = new BevelArrowIcon(DOWN);
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    counter++;
                    if(counter % 2 == 0) {
                        button.setIcon(upIcon);
                    }
                    else {
                        button.setIcon(downIcon);
                    }

                }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);



    } 
    




}
