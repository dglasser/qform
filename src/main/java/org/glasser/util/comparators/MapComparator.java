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
 * $Source: /cvsroot/qform/qform/src/org/glasser/util/comparators/MapComparator.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/01/25 18:08:20 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.util.comparators;



import java.util.*;


/**
 * This Comparator is used for sorting a collection of Maps. An instance
 * is given a "key" object when it's constructed. Each time it is given
 * two Maps to compare, it will use the key to get a value from each map,
 * and compare those two values to determine the ordering of the two 
 * maps.
 * <p>
 * If a separate "value comparator" is provided, it will be used to compare
 * the values fetched from the Maps. Otherwise, if the fetched values are
 * java.util.Comparables, they'll be compared with the compareTo() method
 * of the first value. Finally, if no value comparator was provided and the fetched
 * values are not Comparables, their toString() values will be compared.
 */
public class MapComparator<K, V> extends BaseComparator<Map<? super K, ? extends V> > {



    private K key = null;

    private Comparator<? super V> valueComparator = null;


    public MapComparator(K key) {
        this.key = key;
    }

    public MapComparator(K key, Comparator<? super V> valueComparator) {
        this.key = key;
        this.valueComparator = valueComparator;
    }


    public MapComparator(K key, 
                         Comparator<? super V> valueComparator, 
                         boolean nullIsGreater, 
                         boolean sortDescending, 
                         Comparator<? super Map<? super K, ? extends V>> nestedComparator) 
    {
        super(nullIsGreater, sortDescending, nestedComparator);
        this.key = key;
        this.valueComparator = valueComparator;
    }


    public int doCompare(Map<? super K, ? extends V> o1, Map<? super K, ? extends V> o2) {

        V val1 = o1.get(key);
        V val2 = o2.get(key);

        if(val1 == null && val2 == null) return 0;

        int retVal = super.compareForNulls(val1, val2);

        if(retVal != 0) return retVal;

        // both values are non-null
        if(valueComparator != null) return valueComparator.compare(val1, val2);

        if(val1 instanceof Comparable) return ((Comparable) val1).compareTo((Comparable) val2);

        return val1.toString().compareTo(val2.toString());
        
        
    }


}
