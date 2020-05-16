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
package org.glasser.swing;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.net.URL;
import java.util.*;
import java.lang.reflect.*;
import org.glasser.util.*;

import java.util.ResourceBundle;

public class GUIHelper {


    private GUIHelper() {}

    private static boolean debug = System.getProperty("GUIHelper.debug") != null;

    public static String defaultMessageTitle = null;

    /**
     * After a JButton has been passed to this method, it will be invoked by the ENTER
     * key whenever it has the focus. This is not the default behavior for the Metal
     * look-and-feel.
     */
    public static void enterPressesWhenFocused(JButton button) {
    
        button.registerKeyboardAction(
            button.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)), 
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), 
                JComponent.WHEN_FOCUSED);
    
        button.registerKeyboardAction(
            button.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)), 
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), 
                JComponent.WHEN_FOCUSED);
    
    }

    /**
     * Centers a window on the screen. Should be used for top-level windows only. Does nothing
     * if any needed information is missing, or if window is larger than the screen in
     * either direction. */
    public static void centerWindowOnScreen(java.awt.Window w) {
        Dimension wsize = w.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(wsize == null || screenSize == null) return;
        int hdiff = screenSize.width - wsize.width;
        int vdiff = screenSize.height - wsize.height;
        if(hdiff < 0 || vdiff < 0) return;
        w.setLocation(hdiff / 2, vdiff / 2);
    }


    /**
     * Beeps and displays message box with icon of given type. messageType parameter
     * may be JOptionPane.INFORMATION_MESSAGE, JOptionPane.WARNING_MESSAGE
     * or JOptionPane.ERROR_MESSAGE.
     */
    public static void showMessageDialog(Component parent, 
                                         String msg, 
                                         String title, 
                                         int messageType) {

        msg = Util.wrapLines(msg, 55);

        Toolkit.getDefaultToolkit().beep();

        JOptionPane.showMessageDialog(parent,msg,title,messageType);

    }

    public static int showConfirmDialog(Component parent, Object msg, String title, int optionType) {

        return JOptionPane.showConfirmDialog(parent, msg, title, optionType);
    }

    public static int showConfirmDialog(Component parent, Object msg, String title, int optionType, int messageType) {

        return JOptionPane.showConfirmDialog(parent, msg, title, optionType, messageType);
    }


    /**
     * Beeps and displays message box with info icon.
     */
    public static void infoMsg(Component parent, String msg, String title) {
        if(title == null) title = defaultMessageTitle;
        showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Beeps and displays message box with error icon.
     */
    public static void errMsg(Component parent, String msg, String title) {
        if(title == null) title = defaultMessageTitle;
        showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE);

    }

    /**
     * Beeps and displays message box with a warning icon.
     */
    public static void warningMsg(Component parent, String msg, String title) {
        if(title == null) title = defaultMessageTitle;
        showMessageDialog(parent, msg, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays info about an exception in a message box.
     */
    public static void exceptionMsg(Component parent, Exception ex) {
        if(ex == null) return;
        String msg = ex.getMessage();
        if(msg == null) msg = "A " + ex.getClass().getName()
                              + " has occurred. Please see the error logs for more information.";
        errMsg(parent, msg, ex.getClass().getName());
    }

    /**
     * Displays info about an exception in a message box.
     */
    public static void exceptionMsg(Exception ex) {
        exceptionMsg(null, ex);
    }

    /**
     * This method peforms the tedious chore of constructing a form
     * panel consisting of JTextField objects, each with a corresponding
     * JLabel to its left. It uses a GridBagLayout to layout the components.
     *
     * @param panel The JPanel to be laid out.
     * @param fields Each member array in this NxN array is of the form:
     * <pre>
     * {&lt;JTextField object&gt;, &lt;Label Text String&gt;, [&lt;Tooltip Text String&gt;]}
     * </pre>
     * The String containing the label text is used to construct a JLabel
     * for the JTextField. If the Tooltip Text String is present (it need not be)
     * then it will be used as the tooltip text for both the JLabel and 
     * the JTextField.<p>
     *
     * @param fieldWidth if negative, this parameter is ignored, otherwise it
     * is used as the ipadx value in the GridBagConstraints object for each
     * JTextField object. If the panel's width will not be determined by a layout
     * manager or some other means, this parameter should be used.
     *
     * @param vgap the vertical gap between label/textfield rows.
     *
     * @param labelFont the Font that will be used for the JLabels. If null,
     * the default font is used.
     * 
     * @param labelForeground the color that will be used for the foreground of
     * the JLabels. If null, the default color is used.
     *
     * @param stretchLastRow if true, the component in the last row will have it's
     * fill attribute set to "BOTH" so that it will stretch vertically as well as
     * horizontally.
     *
     * @param lastRowHeight this is the amount used for ipady on the last row's component.
     * if less than 1, then the component's preferred height will be used.
     */
    public static void buildFormPanel(JPanel panel, 
                                      Object[][] fields, 
                                      int fieldWidth, 
                                      int vgap, 
                                      Font labelFont, 
                                      Color labelForeground,
                                      boolean stretchLastRow, 
                                      int lastRowHeight) {
        GridBagLayout gb = new GridBagLayout();
        panel.setLayout(gb);
        GridBagConstraints gc = new GridBagConstraints();
        Insets labelInsets = new Insets(0,0,0,0);
        Insets fieldInsets = new Insets(0,5,vgap,0);
        gc.anchor = gc.NORTH;
        gc.fill = gc.HORIZONTAL;

        // configure one row at a time...
        for(int row=0; row<fields.length; row++) {

            // if we're on the last row...
            if(row == fields.length - 1) {
                // make this row "stretchable"
                gc.weighty = 1;

                // and don't add any padding to the bottom
//                fieldInsets.bottom = 0;
            } else {  // if we're not on the last row ...
                // constrain the height of this row
                gc.weighty = 0;

                // and pad the bottom to acheive a gap between 
                // the rows.
                fieldInsets.bottom = vgap;
            }

            // add the label
            gc.gridx = 0;
            gc.gridy = row;
            gc.weightx = 0;
            gc.ipadx = 0;
            gc.insets = labelInsets;

            JLabel label = new JLabel((String) fields[row][1]);
            if(labelFont != null) label.setFont(labelFont);
            if(labelForeground != null) label.setForeground(labelForeground);
            panel.add(label, gc);
            // if a tooltip was supplied, set it for the label
            if(fields[row].length > 2) label.setToolTipText((String) fields[row][2]);

            // now add the textfield
            gc.gridx = 1;
            gc.weightx = 1;
            if(fieldWidth > -1) gc.ipadx = fieldWidth;
            gc.insets = fieldInsets;
            JComponent field = (JComponent) fields[row][0];

            // if thie is the last row
            if(row == fields.length - 1) {
                if(stretchLastRow) gc.fill = gc.BOTH;
                if(lastRowHeight > 0) gc.ipady = lastRowHeight;
            }


            // enableInputMethods(false) is called so pressing Alt-<key>
            // combinations (for button mnemonics) will not cause the
            // <key> character to be inserted into the text field. This
            // works around a bug in JDK 1.2 which is fixed in JDK 1.3.
//            field.enableInputMethods(false);
            panel.add(field, gc);
            // if a tooltip was supplied, set it for the label
            if(fields[row].length > 2) field.setToolTipText((String) fields[row][2]);

        }
    }

    /**
     * Adds JMenuItems, separators and submenus to a JMenu or JPopup menu.
     *
     * @param menu the menu to be configured. If null, a JPopupMenu is intantiated and returned.
     *
     * @param menuConfig a array of arrays containing the information needed to configure
     *     the menu. Each element array is used to configure a JMenuItem that is added to the
     *     menu, or a submenu. An element array must have at least one element, and may have
     *     up to four. These elements represent:
     *
     * <ul>
     *     <li> element 0:</li> (REQUIRED) The String used as the text for a menu item or a submenu. If it is
     *     is for a submenu, it should begin with an underscore (_), which will be stripped off. Optionally,
     *     this element may be the String "SEPARATOR", in which case a menu separator will be inserted.
     *     <li> element 1:</li> (REQUIRED, unless element 0 is "SEPARATOR") If this row is for a JMenuItem,
     *         then this element is a string that will be set as the "actionCommand" for that JMenuItem, so
     *         ActionListeners can identify which menu item was selected. If this row is for a submenu, then
     *         this element is another Object[][] menuConfig array that will be used to configure the
     *         submenu.
     *     <li> element 2:</li> (OPTIONAL) a String, the first character of which will be set as the
     *         mnemonic for the menu item of submenu.
     *     <li> element 3:</li> (OPTIONAL) a String which contains a message that is sent to the StatusMessageDisplay object
     *         (which typically would be a statusbar) if one was provided, any time the menu item is 
     *         passed over by the mouse.
     * </ul>
     *
     * @param listener an ActionListener that will be added to each JMenuItem.
     *
     * @param statusDisplay a StatusMessageDisplay object that will receive status messages from JMenuItems
     *     they're passed over in an open menu. This would typically be a status bar. If status message
     *     elements are provided in any of the config array elements, this argument should be provided also.
     * 
     */
    public static JComponent buildMenu(JComponent menu, 
                                       Object[][] menuConfig, 
                                       ActionListener listener, 
                                       ChangeListener changeListener, 
                                       ItemListener itemListener) {
        return buildMenu(menu ,menuConfig, listener, changeListener, itemListener, null);
    }



    /**
     * Adds JMenuItems, separators and submenus to a JMenu or JPopup menu.
     *
     * @param menu the menu to be configured. If null, a JPopupMenu is intantiated and returned.
     *
     * @param menuConfig a array of arrays containing the information needed to configure
     *     the menu. Each element array is used to configure a JMenuItem that is added to the
     *     menu, or a submenu. An element array must have at least one element, and may have
     *     up to four. These elements represent:
     *
     * <ul>
     *     <li> element 0:</li> (REQUIRED) The String used as the text for a menu item or a submenu. If it is
     *     is for a submenu, it should begin with an underscore (_), which will be stripped off. Optionally,
     *     this element may be the String "SEPARATOR", in which case a menu separator will be inserted.
     *     <li> element 1:</li> (REQUIRED, unless element 0 is "SEPARATOR") If this row is for a JMenuItem,
     *         then this element is a string that will be set as the "actionCommand" for that JMenuItem, so
     *         ActionListeners can identify which menu item was selected. If this row is for a submenu, then
     *         this element is another Object[][] menuConfig array that will be used to configure the
     *         submenu.
     *     <li> element 2:</li> (OPTIONAL) a String, the first character of which will be set as the
     *         mnemonic for the menu item of submenu.
     *     <li> element 3:</li> (OPTIONAL) a String which contains a message that is sent to the StatusMessageDisplay object
     *         (which typically would be a statusbar) if one was provided, any time the menu item is 
     *         passed over by the mouse.
     * </ul>
     *
     * @param listener an ActionListener that will be added to each JMenuItem.
     *
     * @param statusDisplay a StatusMessageDisplay object that will receive status messages from JMenuItems
     *     they're passed over in an open menu. This would typically be a status bar. If status message
     *     elements are provided in any of the config array elements, this argument should be provided also.
     * 
     */
    public static JComponent buildMenu(JComponent menu, 
                                       Object[][] menuConfig, 
                                       ActionListener listener, 
                                       ChangeListener changeListener, 
                                       ItemListener itemListener,
                                       ResourceBundle bundle) {

        if(menu == null) menu = new JPopupMenu();

        for(int j=0; j<menuConfig.length; j++) {
            Object[] row = menuConfig[j];

            String label = (String) row[0];
            if(debug) System.out.println("label " + label);

            // if the label text is "SEPARATOR", then this is a menu separator. That means
            // that you can't have an actual menu item called "SEPARATOR unless you're using
            // a resource bundle.
            boolean isSeparator = "SEPARATOR".equals(label);

            // if the label text begins with "CHECKBOX_, it indicates that this menuitem is supposed
            // to be a checkbox. The "CHECKBOX_" prefix is then removed.
            boolean isCheckbox = label.indexOf("CHECKBOX_") == 0;
            if(isCheckbox) {
                label = label.substring(9);
            }


            boolean isSubMenu = (!isSeparator && (row[1] instanceof Object[][]));

            String actionCommand = null;
            Object[][] subMenuConfig = null;

            if(isSubMenu) {
                subMenuConfig = (Object[][]) row[1];
            }
            else if(!isSeparator) {
                actionCommand = (String) row[1];
            }


            // to be consistent with previous versions of this code, we'll remove the leading
            // underscore off of the menu item. If you want a leading underscore on your menu
            // item, you have to use a double-leading underscore, and only the first one will be
            // removed.
            if(label.indexOf("_") == 0) {
                // remove the leading underscore from the label text.
                label = label.substring(1);
            }

            String mnemonicCharacter = null;
            String tooltipText = null;
            if(row.length > 2) {
                mnemonicCharacter = (String) row[2];
            }
            if(row.length > 3) {
                tooltipText = (String) row[3];
            }

            // now that we've gotten all of the elments out of the array, we'll use them as strings
            // to fetch localized strings from a ResourceBundle, if we have one. Otherwise, we'll use
            // them as the actual displayed text.
            if(!isSeparator && bundle != null) {
                label = bundle.getString(label);
                if(mnemonicCharacter != null) {
                    mnemonicCharacter = bundle.getString(mnemonicCharacter);
                }
                if(tooltipText != null) {
                    tooltipText = bundle.getString(tooltipText);
                }
            }

            if(isSubMenu) {
                JMenu subMenu = new JMenu(label);
                if(mnemonicCharacter != null && mnemonicCharacter.length() > 0) {
                    subMenu.setMnemonic(mnemonicCharacter.charAt(0));
                }
                if(tooltipText != null) {
                    subMenu.setToolTipText(tooltipText);
                }
                if(changeListener != null) subMenu.addChangeListener(changeListener);
                if(itemListener != null) subMenu.addItemListener(itemListener);
                menu.add(buildMenu(subMenu, subMenuConfig, listener, changeListener, itemListener, bundle));
            }
            else if(isSeparator) {
                if(menu instanceof JPopupMenu) {
                    ((JPopupMenu) menu).addSeparator();
                }
                else if(menu instanceof JMenu) {
                    ((JMenu) menu).addSeparator();
                }
            }
            else {
                JMenuItem item = null;
                if(isCheckbox) {
                    item = new JCheckBoxMenuItem(label);
                }
                else {
                    item = new JMenuItem(label);
                }
                item.setActionCommand(actionCommand);
                menu.add(item);

                if(mnemonicCharacter != null && mnemonicCharacter.length() > 0) {
                    item.setMnemonic(mnemonicCharacter.charAt(0));
                }
                if(tooltipText != null) {
                    item.setToolTipText(tooltipText);
                }

                if(listener != null) item.addActionListener(listener);
                if(changeListener != null) item.addChangeListener(changeListener);
                if(itemListener != null) item.addItemListener(itemListener);

            }
        }

        return menu;
    }


    /**
     * This returns an array containing all of the JMenuItems from
     * the given JMenu. It does not recurse through submenus.
     */
    public static JMenuItem[] getMenuItems(JMenu menu) {
        ArrayList<JMenuItem> list = new ArrayList<>();
        for(int j=0; j<menu.getItemCount(); j++) {
            JMenuItem mi = menu.getItem(j);
            if(mi != null) list.add(mi);
        }
        return list.toArray(new JMenuItem[list.size()]);
    }


    /**
     * This returns an array containing all of the JMenuItems from
     * the given JPopupMenu. It does not recurse through submenus.
     */
    public static JMenuItem[] getMenuItems(JPopupMenu menu) {
        ArrayList<JMenuItem> list = new ArrayList<>();
        MenuElement[] elements = menu.getSubElements();
        for(int j=0; j<elements.length; j++) {
            if(elements[j] instanceof JMenuItem) {
                list.add((JMenuItem) elements[j]);
            }
        }
        return list.toArray(new JMenuItem[list.size()]);
    }


    /**
     * Searches through a tree of menus and returns the JMenuItem that has the string
     * "command" set as its ActionCommand.
     */
    public static JMenuItem findMenuItemByActionCommand(MenuElement menu, String command) {
        MenuElement[] subElements = menu.getSubElements();
        for(int j=0; j<subElements.length; j++) {
            if(subElements[j] instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) subElements[j];
                if(command.equals(item.getActionCommand())) return item;
            }
            JMenuItem descendantItem = findMenuItemByActionCommand(subElements[j], command);
            if(descendantItem != null) return descendantItem;
        }
        return null;
    }

    /**
     * Searches through a tree of menus and returns the JMenuItem that has the string
     * "labelText" set as its label text.
     */
    public static JMenuItem findMenuItemByLabelText(MenuElement menu, String labelText) {
        MenuElement[] subElements = menu.getSubElements();
        for(int j=0; j<subElements.length; j++) {
            if(subElements[j] instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) subElements[j];
                if(labelText.equals(item.getText())) return item;
            }
            JMenuItem descendantItem = findMenuItemByLabelText(subElements[j], labelText);
            if(descendantItem != null) return descendantItem;
        }
        return null;
    }

    /**
     * Configures and adds JButtons to a JPanel, which is assumed to have a FlowLayout
     * (although other layout managers may work.)
     * 
     * @param buttonPanel the JPanel to which the JButtons will be added.
     * @param buttonConfig a two-dimensional Object array, where each outer element
     * is an array with the following elements:
     * <ul>
     *     <li>A JButton to be added</li>
     *     <li>A single-character String, which will be used to set the mnemonic for the button.</li>
     *     <li>A String representing the ActionCommand that will be set for the button.</li>
     *     <li>A String that will be set as the ToolTipText for the button
     *  </ul>
     * @param An ActionListener that will be passed to each JButton's addActionListener() method.
     *     
     */
    public static void buildButtonPanel(JPanel buttonPanel, Object[][] buttonConfig, ActionListener listener) {
        for(int j=0; j<buttonConfig.length; j++) {
            JButton button = (JButton) buttonConfig[j][0];
            if(buttonConfig[j][1] != null) {
                button.setMnemonic(((String) buttonConfig[j][1]).charAt(0));
            }
            button.setActionCommand((String) buttonConfig[j][2]);
            button.setToolTipText((String) buttonConfig[j][3]);
            if(listener != null) button.addActionListener(listener);
            buttonPanel.add(button);
            enterPressesWhenFocused(button);
        }
    }

    public static void setAllSizes(JComponent c, Dimension d) {
        c.setPreferredSize(d == null ? d : (Dimension) d.clone());
        c.setMaximumSize(d == null ? d : (Dimension) d.clone());
        c.setMinimumSize(d == null ? d : (Dimension) d.clone());
    }

    public static void configureToolbar(JToolBar toolBar,
        Object[][] toolBarConfig,
        String imageDir,
        Dimension buttonSize,
        ActionListener actionListener) {

        if(imageDir != null) {
            imageDir = imageDir.replace('\\', '/');
            if((imageDir = imageDir.trim()).length() > 0
               && imageDir.endsWith("/") == false) {
                imageDir += "/";
            }
        }

        for(int j=0; j<toolBarConfig.length; j++) {
            

            JButton button = (JButton) toolBarConfig[j][0];
            String cmd = (String) toolBarConfig[j][1];
            String image = (String) toolBarConfig[j][2];
            String tip = (String) toolBarConfig[j][3];
            int numSeps = 0;
            if(toolBarConfig[j].length > 4) {
                try {
                    numSeps = Integer.parseInt((String) toolBarConfig[j][4]);
                }
                catch(Exception ex) {
                    ex.printStackTrace();   
                }
            }

            if(image != null) {
                if(imageDir != null) image = imageDir + image;
                ImageIcon icon = getImageIconFromClasspath(image);
                if(icon != null) button.setIcon(icon);
            }
            button.setToolTipText(tip);
            button.setActionCommand(cmd);
            button.addActionListener(actionListener);
            if(buttonSize != null) {
                button.setMinimumSize(buttonSize);
                button.setPreferredSize(buttonSize);
                button.setMaximumSize(buttonSize);
            }

            toolBar.add(button);

            for(int n=0; n<numSeps; n++) { 
                toolBar.addSeparator();
            }
        }
    }

    /**
     * Given the path, relative to the classpath, of an image file which is IN the classpath,
     * this will return an ImageIcon constructed from the given image.
     */
    public static ImageIcon getImageIconFromClasspath(String imageFilePath) {

        ClassLoader cl = GUIHelper.class.getClassLoader(); 
        URL url = cl.getResource(imageFilePath);

        if(url != null) {
            if(debug) System.out.println("GOT URL: " + url);
            return new ImageIcon(url);
        }
        else {
            System.err.println("Icon image not found: " + imageFilePath);
            return null;
        }
    }

    public static JViewport getViewport(Component c) {
        Container parent = c.getParent();
        while(parent != null) {
            if(parent instanceof JViewport) return (JViewport) parent;
            parent = parent.getParent();
        }
        return null;
    }

	private static HashSet<AWTKeyStroke> forwardTraversalKeys = new HashSet<>();;
	private static HashSet<AWTKeyStroke> backwardTraversalKeys = new HashSet<>();

	static {

        forwardTraversalKeys.add(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, 0));

        backwardTraversalKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
														java.awt.event.InputEvent.SHIFT_MASK));
	}

    /**
     * This method will modify the given JTextArea so that pressing 
     * the tab key will shift focus away from the JTextArea, rather than insert 
     * a tab character.
     */
	public static void setTabTraversal(JTextArea textArea) {
        textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardTraversalKeys);
        textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardTraversalKeys);
	}

}
