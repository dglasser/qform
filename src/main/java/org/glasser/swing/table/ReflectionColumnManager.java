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
package org.glasser.swing.table;


import javax.swing.table.*;
import java.lang.reflect.*;
import org.glasser.util.*;


public class ReflectionColumnManager<R> extends AbstractColumnManager<R> {


    private Class objectClass = null;

    private Method[] getters = null;

    private Method[] setters = null;

    public ReflectionColumnManager(String[] columnNames, Class<R> objectClass, String[] getterNames, String[] setterNames) 
        throws NoSuchMethodException
    {
        super.setColumnNames(columnNames);
        getters = new Method[getterNames.length];
        Class[] columnClasses = new Class[getterNames.length];

        for(int j=0; j<getters.length; j++) {
            getters[j] = objectClass.getMethod(getterNames[j], (Class[]) null);
            columnClasses[j] = getters[j].getReturnType();
        }

        Class[] paramTypes = new Class[1];

        for(int j=0; setterNames != null && j<setterNames.length; j++) {
            String setterName = setterNames[j];
            if(setterName == null) continue;
            paramTypes[0] = columnClasses[j];
            setters[j] = objectClass.getMethod(setterName, paramTypes);
        }

        // now, if any of the columnClasses are for primitive types 
        // (int, long, etc.) then we'll convert them to their corresponding
        // wrapper class. This must be done after the setters have been
        // created.
        for(int j=0; j<columnClasses.length; j++) {
            Class wrapperClass = Util.getWrapperClass(columnClasses[j]);
            if(wrapperClass != null) columnClasses[j] = wrapperClass;
        }

        // now send the columnClasses array to the AbstractColumnManager superclass.
        super.setColumnClasses(columnClasses);

    }


    public Object getValueAt(int rowIndex, int columnIndex, R rowObject) {

        Method getter = getters[columnIndex];
        try {
            if(rowObject == null) return null;
            return getter.invoke(rowObject, (Object[]) null);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Caught " + ex.getClass().getName() + ": Object class is " 
                + objectClass.getName()
                + ", method is " + getter.getName());
        }
    }

    public void setValueAt(Object newCellValue, int rowIndex, int columnIndex, R rowObject) {

        Method setter = setters[columnIndex];
        try {
            setter.invoke(rowObject, new Object[] { newCellValue });
        }
        catch(Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Caught " + ex.getClass().getName() + ": Object class is " 
                + objectClass.getName()
                + ", setter method is " + setter.getName());
        }
    }


    public boolean isCellEditable(int rowIndex, int columnIndex, R rowObject) {
        return rowObject != null && setters != null && setters[columnIndex] != null;
    }

}


