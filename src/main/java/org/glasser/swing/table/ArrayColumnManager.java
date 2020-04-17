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
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/table/ArrayColumnManager.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/01/26 00:19:08 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing.table;



import javax.swing.*;
import java.util.*;
import java.awt.*;

/**
 * This is a ColumnManager implementation that assumes each row of a table
 * is contained in an Object array, with each element of the array mapping to the
 * corresponding column of the table.
 * 
 * @author Dave Glasser
 */
public class ArrayColumnManager extends AbstractColumnManager {


    public ArrayColumnManager(String[] columnNames, Class[] columnClasses) {
        super(columnNames, columnClasses);
    }


    /**
     * The rowObject passed into this method is assumed to be an object array that contains
     * the data for a single row of the table.
     */
    public void setValueAt(Object newCellValue, int rowIndex, int columnIndex, Object rowObject) {
        ((Object[]) rowObject)[columnIndex] = newCellValue;
    }

    /**
     * The rowObject passed into this method is assumed to be an object array that contains
     * the data for a single row of the table.
     */
    public Object getValueAt(int rowIndex, int columnIndex, Object rowObject) {
        Object[] row = (Object[]) rowObject;
        if(row == null || row.length <= columnIndex) return null;
        return row[columnIndex];
    }


    /**
     * Launches a small program demonstrating the use of this class.
     */
    public static void main(String[] args) throws Exception {

        int numRows = 20;
        int numCols = 5;

        String[] colNames = new String[numCols];
        for(int j=0; j<numCols; j++) {
            colNames[j] = "** " + j + " **";
        }

        // each element in this list represents a row in the table,
        // and will be an Object array.
        ArrayList list = new ArrayList();

        for(int j=0; j<numRows; j++) {
            // create the array that will be the row.
            Object[] rowArray = new Object[numCols];
            for(int k=0; k<numCols; k++) {
                rowArray[k] = "Row " + j + ", Col " + k;
            }
            list.add(rowArray);
        }

        // instantiate the ColumnManager
        ArrayColumnManager colMgr = new ArrayColumnManager(colNames, null);

        // instantiate the ListTableModel
        ListTableModel model = new ListTableModel(colMgr, list);

        JFrame frame = new JFrame();
        JPanel cp = new JPanel();
        cp.setLayout(new BorderLayout());
        JTable table = new JTable(model);
        cp.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setContentPane(cp);
        frame.pack();
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);


    } 
    

}
