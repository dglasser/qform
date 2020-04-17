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
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/BlankIcon.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/04/30 02:41:58 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing;



import javax.swing.*;
import java.awt.*;



/**
 * This is a blank icon. It does no actual painting, so whatever component it is contained
 * by should paint the area beneath it if necessary. It's primary use is as a spacer.
 */
public class BlankIcon implements Icon, java.io.Serializable {


    protected double size = 16.0;

    int width = 16;

    int height = 16;


    /**
     * Constructs a BlankIcon with a height and width of 16 pixels.
     */
    public BlankIcon() {
    }

    /**
     * Constructs a BlankIcon with a height and width equal to "size".
     */
    public BlankIcon(double size) {
        if(size < 1 || size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("BlankIcon(int size): size must be between 1 and "
                + Integer.MAX_VALUE + ".");
        }

        this.size = size;

        width = (int) size;

        height = (int) size;

    }





    /**
     * This is an empty implementation, that does no actual painting.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {

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
            BevelArrowIcon upIcon = new BevelArrowIcon(10.0, BevelArrowIcon.UP);
            BevelArrowIcon downIcon = new BevelArrowIcon(10.0, BevelArrowIcon.DOWN);
            BlankIcon blankIcon = new BlankIcon(10.0);

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    int  i = counter++ % 3;
                    if(i == 0) {
                        button.setIcon(upIcon);
                    }
                    else if(i == 1) {
                        button.setIcon(downIcon);
                    }
                    else {
                        button.setIcon(blankIcon);
                    }

                }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);



    } 
    




}
