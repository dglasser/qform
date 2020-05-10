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
 * $Source: /cvsroot/qform/qform/src/org/glasser/util/comparators/BaseComparator.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2003/01/25 18:11:14 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.util.comparators;



import java.util.*;



/**
 * This is a base class for Comparators which provides commonly-needed functionality.
 * <p>
 * Its three fields are:
 * 
 * <ul>
 * <li> nullIsGreater (boolean)</li> which determines whether a null object
 * passed to compare() is considered greater than or less than a non-null object.
 * <li> sortDescending (boolean) </li> which determines whether the normal ordering
 * of two objects will be reversed, resulting in a descending sort.
 * <li> nestedComparator (Comparator)</li> when no order can be established for two objects
 * (in other words the compare() method would normally return 0) then the two objects
 * will be passed to the nestedComparator, in an attempt to determine an order. An example might
 * be a subclass that compared the value of the lastName fields of two Person objects. It could
 * have a similar comparator that compared the value of the firstName fields as its nestedComparator,
 * so Person objects with the same lastName would be sorted according to their firstName fields.
 * </ul>
 * 
 * The value of these three fields can only be set in the constructor, or by subclasses through
 * protected setter methods, which allows subclasses to be immutable.
 * <p>
 * A subclass only needs to implement the template method doCompare(Object, Object), which is
 * called from within compare(). The compare() method will first handle the cases where
 * one or both objects are null, and if both objects are non-null, it will pass them to the doCompare()
 * method. The doCompare() method does not need to concern itself with the possibility of 
 * null arguments or reverse-sorts. If the doCompare() method returns 0 and a nestedComparator
 * is available, the arguments will be passed to it. If the doCompare() method returns non-zero and
 * the sortDescending flag is true, the sign of the return value will be flipped before it's
 * returned to the caller.
 * 
 */
public abstract class BaseComparator<T> implements Comparator<T>, java.io.Serializable {


    protected boolean nullIsGreater = false;

    protected boolean sortDescending = false;

    protected Comparator<? super T> nestedComparator = null;

    /**
     * Constructs a BaseComparator instance with default field values. The resulting
     * instance is immutable and thread-safe, unless its fields have been exposed
     * by subclasses.
     */
    protected BaseComparator() {}

    /**
     * Constructs a BaseComparator instance with the given field values. The resulting
     * instance is immutable and thread-safe, unless its fields have been exposed
     * by subclasses.
     */
    protected BaseComparator(boolean nullIsGreater, 
        boolean sortDescending, 
        Comparator<? super T> nestedComparator) {
        this.nullIsGreater = nullIsGreater;
        this.sortDescending = sortDescending;
        this.nestedComparator = nestedComparator;
    }

    /**
     * Sets the value of the nullIsGreater field, which determines whether
     * null objects are considered "greater than" or "less than" non-null objects
     * for sorting purposes. The default is false.
     */
    protected void setNullIsGreater(boolean nullIsGreater) {
        this.nullIsGreater = nullIsGreater;
    }

    /**
     * Sets the value of the sortDescending field. If true, the sign of the
     * value of the compare() method is "flipped" before it's returned to
     * the caller, which will cause collections of objects to be sorted
     * in reverse order.
     */
    protected void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

    /**
     * Sets a comparator that will be used as a "backup" comparator if this one
     * is unable to establish an order for two objects (i.e., the compare() method
     * would return 0.)
     */
    protected void setNestedComparator(Comparator<? super T> nestedComparator) {
        this.nestedComparator = nestedComparator;
    }


    /**
     * Returns the value of the nullIsGreater flag, which by default
     * is false.
     */
    public boolean getNullIsGreater() {
        return nullIsGreater;
    }

    /**
     * Returns the value of the sortDescending flag, which by default
     * is false.
     */
    public boolean isSortDescending() {
        return sortDescending;
    }

    /**
     * Returns the Comparator being used as the nested, or backup comparator,
     * or null if one hasn't been set. Subclasses
     * may choose to expose this method publicly, but if the nestedComparator is
     * mutable, they may be compromising their immutability (and hence their thread safety.)
     */
    protected Comparator<? super T> getNestedComparator() {
        return nestedComparator;
    }


    public final int compare(T o1, T o2) {

        // handle the trivial case where both objects are null
        if(o1 == null && o2 == null) return 0;

        // if one object is null, this will return non-zero
        int retVal = compareForNulls(o1, o2);

        // if we got an ordering for these two objects, (which means
        // one was null and the other non-null) we're done.
        if(retVal != 0) return adjust(retVal);

        // if we still don't have an ordering, let the subclass try
        retVal = doCompare(o1, o2);
        if(retVal != 0) return adjust(retVal);

        // if the subclass couldn't establish an order, see if there's
        // a nested comparator. Note that the result from the 
        // nested comparator is not affected by the value of
        // the sortDescending field.
        if(nestedComparator != null) return nestedComparator.compare(o1, o2);

        // no ordering could be established for these two objects, so
        // return 0.
        return retVal;
        


    }

    /**
     * This method will establish the ordering for two objects when
     * one is null and one is non-null, based on the value of this Comparator's
     * nullIsGreater flag.
     * 
     * @return 0 if both objects are null or both are non-null; if only one object
     * is null, it is considered "greater than" the non-null object if the
     * nullIsGreater flag is true, and "less than" the non-null object if the
     * nullIsGreater flag is false (the default.)
     */
    protected int compareForNulls(Object o1, Object o2) {
        if(o1 == null && o2 == null) return 0;
        if(o1 == null) {
            if(nullIsGreater) return 1;
            else return -1;
        }
        else if(o2 == null) {
            if(nullIsGreater) return -1;
            else return 1;
        }
        else {
            return 0; // both are non-null;
        }
    }

    /**
     * This method is used to "flip" the sign of the compare() method's return value
     * whenever the sortDescending flag is true.
     */
    private int adjust(int val) {
        if(sortDescending == false) return val;
        return 0 - val;
    }


    /**
     * This method is implemented by subclasses, which should know about the types
     * of the two objects being passed in and how to order them. This is a template
     * method which is called from within compare(), and both arguments are guaranteed
     * to be non-null. Implementations of this method should not be concerned with flipping
     * the sign of the return value for descending sorts; that task will be handled within this
     * (the base) class.
     */
    protected abstract int doCompare(T o1, T o2);



}

