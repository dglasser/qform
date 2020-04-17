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
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/text/NumericDocument.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/01/26 00:17:34 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing.text;

import javax.swing.text.*;

public class NumericDocument extends ValidatingDocument 
{  

    protected double minValue = Double.MIN_VALUE;

    protected double maxValue = Double.MAX_VALUE;

    protected boolean floatingPointAllowed = false;

    public void setMinValue(double minValue) {
        if(minValue > 0) throw new IllegalArgumentException("setMinValue(): minValue argument must be 0 or less.");
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        if(maxValue < 0) throw new IllegalArgumentException("setMaxValue(): maxValue argument must be 0 or greater.");
        this.maxValue = maxValue;
    }

    public void setFloatingPointAllowed(boolean floatingPointAllowed) {
        this.floatingPointAllowed = floatingPointAllowed;
    }



    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public boolean isFloatingPointAllowed() {
        return floatingPointAllowed;
    }


    protected boolean isValidValue(String s) {
        s = s.trim();

        // check for the possibility that this is the the
        // first character of a negative value (the minus sign.)
        if(s.equals("-") && minValue < 0) return true;

        try {
            if(floatingPointAllowed) {
                double d = Double.parseDouble(s);
                if(d >= minValue && d <= maxValue) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                long l = Long.parseLong(s);
                if(l >= minValue && l <=maxValue) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        catch(NumberFormatException ex) {
            return false;
        }
    }
                

}

