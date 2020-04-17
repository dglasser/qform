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
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/table/PushButtonTableHeaderListener.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/05/18 23:36:35 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing.table;


import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;

/**
 * This is a version of TableHeaderListener that's meant to be used with a
 * PushButtonTableHeader. It will handle updating the up/down arrows on the
 * PushButtonTableHeader when the table is sorted on various columns.<p>
 * 
 * Subclasses should override the sortTable() method that takes a PushButtonTableHeader
 * argument instead of a JTableHeader argument.
 * 
 * @author Dave Glasser
 */
public abstract class PushButtonTableHeaderListener extends TableHeaderListener {


    /**
     * See the Javadoc for TableHeaderListener for an explanation of this method. This method
     * has been made final, so superclasses should override the sortTable() method that takes
     * a PushButtonTableHeader argument.
     */
    protected final boolean sortTable(JTableHeader header, 
                              int modelColumn, 
                              int viewColumn, 
                              boolean sortDescending) {

        PushButtonTableHeader pushButtonTableHeader = (PushButtonTableHeader) header;

        boolean wasSorted = sortTable(pushButtonTableHeader,
                                      modelColumn,
                                      viewColumn,
                                      sortDescending);

        // if a sort occurred, update the up/down arrows on the PushButtonTableHeader.
        if(wasSorted) {
            // notice here that the table header is given the _view-based_ column index
            // that was sorted. Table models ususally care about the model-based
            // index when the sort the model.
            pushButtonTableHeader.setSortedColumn(viewColumn,  sortDescending);
        }

        return wasSorted;

    }


    /**
     * This is the method that knows how to sort the table in response to a column
     * header being clicked, and the implementation must be provided by a
     * subclass.
     * 
     * @param header the PushButtonTableHeader that was clicked upon to trigger a table sort.
     * @param the column index, based on the TableModel, of the column that was clicked. This 
     *        is likely the index that implementations of this method will use to sort
     *        a table.
     * @param viewColumn the column index of the clicked column, from the user's, or the
     *        "view" perspective. This may differ from the modelColumn if the user has 
     *        rearranged the column order by dragging them with the mouse.
     * @param sortDescending if true, the table should be sorted in descending order, if
     *        false, the table should be sorted in ascending order.
     * 
     * @return a boolean indicating whether or not the table was sorted. This allows the sort to
     *        be "vetoed."
     */
    protected abstract boolean sortTable(PushButtonTableHeader header, 
                              int modelColumn, 
                              int viewColumn, 
                              boolean sortDescending);





}
