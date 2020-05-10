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
 * $Source: /cvsroot/qform/qform/src/org/glasser/util/comparators/MethodComparator.java,v $
 * $Revision: 1.4 $
 * $Author: dglasser $
 * $Date: 2005/06/06 14:56:53 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.util.comparators;


import java.lang.reflect.*;
import java.util.*;
import java.io.*;

/**
 * This Comparator is used to compare two objects of a common type, based
 * on the value returned from a no-argument method common to both of them.
 * (Typically, this will be a getter method.)
 */
public class MethodComparator<T> extends BaseComparator<T> {

    private Class<T> objectClass = null;

    private String getterName = null;

    private transient Method getter = null;

    private boolean returnTypeIsString = false;

    private boolean returnTypeIsComparable = false;

    private boolean caseSensitive = false;


    public MethodComparator(Class<T> objectClass, String getterName) {
        this(objectClass, getterName, false, false, null, false);
    }


    public MethodComparator(Class<T> objectClass, 
                            String getterName, 
                            boolean nullIsGreater, 
                            boolean sortDescending, 
                            Comparator<? super T> nestedComparator,
                            boolean caseSensitive) 
    {
        super(nullIsGreater, sortDescending, nestedComparator);

        this.caseSensitive = caseSensitive;
        this.objectClass = objectClass;
        this.getterName = getterName;

        try {
            getter = objectClass.getMethod(getterName, new Class[0]);
        }
        catch(NoSuchMethodException ex) {
            throw new IllegalArgumentException("Invalid getterName: " + getterName
                                               + " method not found in " + objectClass);
        }
        Class returnTypeClass = getter.getReturnType();
        if(returnTypeClass.equals(java.lang.String.class)) {
            returnTypeIsString = true;
            returnTypeIsComparable = true;
        }
        else if(java.lang.Comparable.class.isAssignableFrom(returnTypeClass)) {
            returnTypeIsComparable = true;
        }
        else if(returnTypeClass.isPrimitive()) {
            returnTypeIsComparable = true;
        }
    }

    @SuppressWarnings("unchecked")
    protected int doCompare(T o1, T o2) {

        try {
            Object val1 = getter.invoke(o1, (Object[]) null);
            Object val2 = getter.invoke(o2, (Object[]) null);
    
            if(val1 == null && val2 == null) return 0;
            int retVal = compareForNulls(val1, val2);
            if(retVal != 0) return retVal;
    
            // we know now that both val1 and val2 are non-null
            if(returnTypeIsString) {
                if(caseSensitive) {
                    return ((String) val1).compareTo((String) val2);
                }
                else {
                    return String.CASE_INSENSITIVE_ORDER.compare((String) val1, (String) val2);
                }
            }
            else if(returnTypeIsComparable) {
                return ((Comparable) val1).compareTo((Comparable) val2);
            }
            else {
                String s1 = val1.toString();
                String s2 = val2.toString();
                if(caseSensitive) {
                    return (s1).compareTo(s2);
                }
                else {
                    return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
                }
            }
        }
        catch(Exception ex) {
            throw new RuntimeException("A "
                                       + ex.getClass().getName()
                                       + " occurred in MethodComparator.doCompare(). objectClass="
                                       + objectClass.getName() + ", getterName=" + getterName);
        }
    }


    public boolean isCaseSensitive() {
        return caseSensitive;
    }


    /**
     * This must be implemented to make the MethodComparator class serializable,
     * because the getter field (which is a Method) is not.
     */
    private void writeObject(java.io.ObjectOutputStream out)
        throws IOException
    {
        out.defaultWriteObject();
    }

    /**
     * This must be implemented to make the MethodComparator class serializable,
     * because the getter field (which is a Method) is not.
     */
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
    
        in.defaultReadObject();
    
        try {
            getter = objectClass.getMethod(getterName, (Class[]) null);
        }
        catch(Exception ex) {
            throw new IOException("A "
                                   + ex.getClass().getName()
                                   + " occurred in MethodComparator(): objectClass="
                                   + objectClass.getName() + ", getterName=" + getterName);
        }
    
    }


}
