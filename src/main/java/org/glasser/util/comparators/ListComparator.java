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
package org.glasser.util.comparators;


import java.util.*;

/**
 * This is a comparator class that's used to compare one List with another List.
 * The comparison is actually performed by comparing the Nth element of
 * the first List with the Nth element of the second list. This comparator can
 * be used to sort a table, when each row of the table is contained in a List, and
 * the table is to be sorted on a particular (Nth) column.
 * 
 * @author David Glasser
 */
public class ListComparator<E> extends BaseComparator<List<? extends E>> {



    protected int elementIndex = 0;

    /**
     * This is the comparator that compares the elements read from the two lists being
     * compared. If this field is null, then the elements are compared as Comparables 
     * (if they both implement Comparables) or Strings (from their toString() methods.)
     */
    protected Comparator<? super E> valueComparator = null;


    /**
     * Constructs a ListComparator instance with the default field values. The resulting
     * instance is immutable and thread-safe, unless its fields have been exposed
     * by subclasses.
     */
    public ListComparator() { 
    }

    /**
     * Constructs a ListComparator instance with the given elementIndex and default
     * values for the other fields. The resulting
     * instance is immutable and thread-safe, unless its fields have been exposed
     * by subclasses.
     */
    public ListComparator(int elementIndex) {
        this.elementIndex = elementIndex;
    }

    /**
     * Constructs a ListComparator instance with the given field values. The resulting
     * instance is immutable and thread-safe, unless its fields have been exposed
     * by subclasses.
     * 
     * @param elementIndex
     */
    protected ListComparator(boolean nullIsGreater, 
        boolean sortDescending, 
        Comparator<? super List<? extends E>> nestedComparator,
        int elementIndex,
        Comparator<? super E> valueComparator) 
    {
        super(nullIsGreater, sortDescending, nestedComparator);
        this.elementIndex = elementIndex;
        this.valueComparator = valueComparator;
    }




    /**
     * This method is implemented by subclasses, which should know about the types
     * of the two objects being passed in and how to order them. This is a template
     * method which is called from within compare(), and both arguments are guaranteed
     * to be non-null. Implementations of this method should not be concerned with flipping
     * the sign of the return value for descending sorts; that task will be handled within this
     * (the base) class.
     */
    @SuppressWarnings("unchecked")
    protected int doCompare(List<? extends E> o1, List<? extends E> o2) {

        E val1 = o1.get(elementIndex);
        E val2 = o2.get(elementIndex);

        if(val1 == null && val2 == null) return 0;

        int retVal = compareForNulls(val1, val2);
        
        if(retVal != 0) return retVal;
        
        // both values are non-null
        if(valueComparator != null) return valueComparator.compare(val1, val2);
        
        if(val1 instanceof Comparable && val2 instanceof Comparable) {
            return ((Comparable) val1).compareTo((Comparable) val2);
        }
        
        return val1.toString().compareTo(val2.toString());
        
    }


}
