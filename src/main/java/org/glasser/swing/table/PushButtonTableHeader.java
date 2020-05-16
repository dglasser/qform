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


import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import org.glasser.swing.BevelArrowIcon;

/**
 * This is a JTableHeader subclass that uses a JButton to paint the column
 * headers on a JTable. It also keeps track of a sorted column and a sort order
 * which it can use to paint up or down arrow icons on the header of a sorted column.
 * The main() method of this class invokes a small sample program that demonstrates
 * the use of this class.
 * 
 * @author David F. Glasser
 */
public class PushButtonTableHeader extends JTableHeader {
    

    protected int sortedColumn = -1;

    protected boolean descendingSort = false;

    protected int pressedColumn = -1;

    public final static double DEFAULT_ICON_SIZE = 10.0;

    public final static Icon DEFAULT_UP_ICON = new BevelArrowIcon(DEFAULT_ICON_SIZE, BevelArrowIcon.UP);

    public final static Icon DEFAULT_DOWN_ICON = new BevelArrowIcon(DEFAULT_ICON_SIZE, BevelArrowIcon.DOWN);

    /**
     * This is the JButton used to paint the column headers.
     */
    protected JButton rendererButton = new JButton();

    /**
     * This is the icon used to indicate that a column has been sorted in ascending order.
     * The default value is the DEFAULT_DOWN_ICON.
     */
    protected Icon upIcon = DEFAULT_UP_ICON;


    /**
     * This is the icon used to indicate that a column has been sorted in descending order.
     * The default value is the DEFAULT_DOWN_ICON.
     */
    protected Icon downIcon = DEFAULT_DOWN_ICON;

    public PushButtonTableHeader() {
        this(null);
    }


    public PushButtonTableHeader(TableColumnModel tcm) {
        super(tcm);

        rendererButton.setFont(this.getFont());
        rendererButton.setForeground(this.getForeground());
		rendererButton.setMargin(new Insets(0,0,0,0));
		rendererButton.setHorizontalTextPosition(JButton.LEFT);

        // this prevents the empty space that normally surrounds a JButton under
        // the Motif look and feel.
        rendererButton.setDefaultCapable(false);

        // the mouse listener added here will manage the "pushbutton" appearance
        // of the column headers.
        this.addMouseListener(new MouseAdapter() {
                /**
                 * Invoked when a mouse button has been pressed on a component.
                 */
                public void mousePressed(MouseEvent e) {



                    int modifiers = e.getModifiers();
                    if((modifiers & e.BUTTON1_MASK) == 0) return;

                    // if the user has clicked on the border between two columns
                    // for the purpose of resizing one of them, this call will return non-null.
                    TableColumn tc = getResizingColumn();

                    // don't sort if the user's only trying to resize a column
                    if(tc != null) return;

                    pressedColumn = columnAtPoint(e.getPoint());
					JTable table = getTable();
					if(table != null) {
						// always store the pressedColumn index in terms of the column model
						// instead of the view, because the view can change as the user
						// drags columns around.
						pressedColumn = table.convertColumnIndexToModel(pressedColumn);
					}

                    PushButtonTableHeader.this.repaint();

                }
                /**
                 * Invoked when a mouse button has been released on a component.
                 */
                public void mouseReleased(MouseEvent e) {
                    pressedColumn = -1;
                    PushButtonTableHeader.this.repaint();
                }
        });

    }


    public void setTable(JTable table) {
        super.setTable(table);
        this.setColumnModel(table.getColumnModel());
    }


    /**
     * This method tells a PushButtonTableHeader which column should have
     * the arrow icon painted on it to indicate it is the sorted column, and
     * whether the arrow should point up or down.
     * 
     * @param the VIEW index of the sortedColumn. It is important that the sortedColumn
     * parameter is the view-based index rather than the model-based index. The view-based index
     * will be the index returned by the JTableHeader.columnAtPoint() method. If an out-of-range
     * index is passed in (for instance, -1) then no arrow icons will be drawn. This
     * can be used to remove the arrows from an unsorted table.
     * 
     * @param descendingSort if true, the downIcon (arrow pointing down) will be painted on the
     * sorted column header; if false the upIcon (arrow pointing up) will be used.
     */
    public void setSortedColumn(int columnIndex, boolean descendingSort) {

		JTable table = getTable();
		if(table != null) {
			// always store the sortedColumn index in terms of the column model
			// instead of the view, because the view can change as the user
			// drags columns around.
			sortedColumn = table.convertColumnIndexToModel(columnIndex);
		}
		else {
			sortedColumn = columnIndex;
		}
        this.descendingSort = descendingSort;

        repaint();
    }

    /**
     * Sets the icon used to indicate that a column has been sorted in ascending order.
     * The default value is the DEFAULT_DOWN_ICON.
     */
    public void setUpIcon(Icon upIcon) {
        this.upIcon = upIcon;
    }

    /**
     * Sets the icon used to indicate that a column has been sorted in descending order.
     * The default value is the DEFAULT_DOWN_ICON.
     */
    public void setDownIcon(Icon downIcon) {
        this.downIcon = downIcon;
    }

    protected class PushButtonRenderer implements TableCellRenderer {


        /**
         *  Returns the component used for drawing the cell.  This method is
         *  used to configure the renderer appropriately before drawing.
         *
         * @param	table		the <code>JTable</code> that is asking the
         *				renderer to draw; can be <code>null</code>
         * @param	value		the value of the cell to be rendered.  It is
         *				up to the specific renderer to interpret
         *				and draw the value.  For example, if
         *				<code>value</code>
         *				is the string "true", it could be rendered as a
         *				string or it could be rendered as a check
         *				box that is checked.  <code>null</code> is a
         *				valid value
         * @param	isSelected	true if the cell is to be rendered with the
         *				selection highlighted; otherwise false
         * @param	hasFocus	if true, render cell appropriately.  For
         *				example, put a special border on the cell, if
         *				the cell can be edited, render in the color used
         *				to indicate editing
         * @param	row	        the row index of the cell being drawn.  When
         *				drawing the header, the value of
         *				<code>row</code> is -1
         * @param	column	        the column index of the cell being drawn
         */
        public Component getTableCellRendererComponent(JTable table, 
            Object value, 
            boolean isSelected, 
            boolean hasFocus, 
            int row, 
            int column) {

			int actualColumn = table.convertColumnIndexToModel(column);

            if(actualColumn == sortedColumn) {
                if(descendingSort) {
                    rendererButton.setIcon(downIcon);
                }
                else {
                    rendererButton.setIcon(upIcon);
                }
            }
            else {
                rendererButton.setIcon(null);
            }
			

            boolean isPressed = actualColumn == pressedColumn;
            rendererButton.getModel().setArmed(isPressed);
            rendererButton.getModel().setPressed(isPressed);

            rendererButton.setText(value == null ? "" : value.toString());

            return rendererButton;

        }
    }


    /**
     * This is the TableCellRenderer used to paint the column headers.
     */
    protected PushButtonRenderer renderer = new PushButtonRenderer();

    
    /**
     * Returns the default renderer used when no <code>headerRenderer</code>
     * is defined by a <code>TableColumn</code>.
     * @return the default renderer
     */
    public TableCellRenderer getDefaultRenderer() {
        return renderer;
    }


    /**
     * Notification from the <code>UIManager</code> that the look and feel
     * (L&F) has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        super.updateUI();
        if(rendererButton != null) rendererButton.updateUI();
    }


    /**
     * Invokes a small sample program that demonstrates how this
     * class is used.
     */
    public static void main(String[] args) throws Exception {

        System.out.println("Java version: "
            + System.getProperty("java.version"));

        JFrame frame = new JFrame("PushButtonTableHeader Demo");
        JPanel cp = new JPanel();
        cp.setLayout(new BorderLayout());
        JTable table = new JTable();
        table.setModel(new DefaultTableModel(50, 5));
        PushButtonTableHeader header = new PushButtonTableHeader();
        table.setTableHeader(header);

        //////////////////////////////////////////////////////////////////////////
        // This code is only necessary when the Java Runtime Environment is prior
        // to version 1.3. To be on the safe side, it should probably always be
        // included, because it doesn't hurt anything on versions 1.3 or 1.4.
        TableColumnModel tcm = table.getColumnModel();
        for(int j=0; j<tcm.getColumnCount(); j++) {
            TableColumn tc = tcm.getColumn(j);
            tc.setHeaderRenderer(header.getDefaultRenderer());
        }
        //////////////////////////////////////////////////////////////////////////

        header.addMouseListener(new MouseAdapter() {

                int sortedColumn = -1;

                boolean descendingSort = false;
                    /**
                     * Invoked when a mouse button has been pressed on a component.
                     */
                    public void mousePressed(MouseEvent e) {

                        int modifiers = e.getModifiers();
                        if((modifiers & e.BUTTON1_MASK) == 0) return;

                        PushButtonTableHeader header = (PushButtonTableHeader) e.getSource();

                        // if the user has clicked on the border between two columns
                        // for the purpose of resizing one of them, this call will return non-null.
                        TableColumn tc = header.getResizingColumn();

                        // don't sort if the user's only trying to resize a column
                        if(tc != null) return;

                        int viewColumn = header.columnAtPoint(e.getPoint());

                        // translate the view column index to the model column index
                        int modelColumn = header.getTable().convertColumnIndexToModel(viewColumn);

                        // if the clicked column is already sorted, reverse the
                        // current sort order
                        if(modelColumn == sortedColumn) {
                            descendingSort = !descendingSort;
                        }
                        else {
                            // the first time a column is clicked the
                            // sort order is ascending.
                            descendingSort = false;
                        }


                        // remember the currently sorted column in terms of
                        // the model, not the view
                        sortedColumn = modelColumn;

                        //////////////////////////////////////////////////////
                        // a method call to sort the table would go here
                        //////////////////////////////////////////////////////

                        // set the sortedColumn on the header so it knows how to draw the arrow.
                        // Notice that we pass in the column index that's based on the view, not 
                        // the model, because the header will tranlate it internally.
                        header.setSortedColumn(viewColumn,  descendingSort);
                    }
                });

        cp.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setContentPane(cp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    } 
}
