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


import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.glasser.sql.*;
import org.glasser.swing.text.*;
import org.glasser.util.*;



public class LocalDataSourceConfigPanel extends JPanel {




    public final JTextField txtDisplayName = new JTextField();

    public final DriverClassSelector cmbDriverClass = new DriverClassSelector();

    public final JTextField txtUrl = new JTextField();

    public final JCheckBox chkLoginRequired = new JCheckBox();

    public final JTextField txtUserName = new JTextField();

    public final JPasswordField txtPassword = new JPasswordField();

    public final JTextField txtMaxConnections = new JTextField();

    public final JTextField txtLoginTimeout = new JTextField();

    public Object[][] config = 
    {
         {txtDisplayName, "Display Name", "Name for this connection."}
        ,{cmbDriverClass, "Driver Class", "The fully-qualified class name for the JDBC driver to be used. (Enter it manually if it does not appear in the dropdown.)"}
        ,{txtUrl, "Database URL", "The URL that will be used to connect to this data source."}
        ,{chkLoginRequired, "Requires Login", "Indicates if a user/password is needed to connect to this data source."}
        ,{txtUserName, "User Name", "If required, the user name used to connect to this data source. (Leave blank to prompt for user name.)"}
        ,{txtPassword, "Password", "If required, the password used to connect to this data source. (Leave blank to prompt for password.)"}
        ,{txtMaxConnections, "Max Connections", "The maximum number of connections that will be pooled. (Enter 0 or leave blank for no maximum.)"}
        ,{txtLoginTimeout, "Login Timeout", "The maximum time, in seconds, to wait for a successful login to this data source. (Enter 0 or leave blank for no maximum.)"}
    };


    public LocalDataSourceConfigPanel() {

        GUIHelper.buildFormPanel(this, config, 250, 6, null, Color.black, false, -1);

        // make txtMaxConnections and txtLoginTimeout numeric-input only.
        NumericDocument nd = new NumericDocument();
        nd.setMinValue(0);
        nd.setMaxValue(Integer.MAX_VALUE);
        txtMaxConnections.setDocument(nd);

        nd = new NumericDocument();
        nd.setMinValue(0);
        nd.setMaxValue(Integer.MAX_VALUE);
        txtLoginTimeout.setDocument(nd);

    }


    public void setEditable(boolean b) {

        txtDisplayName.setEditable(b);
        cmbDriverClass.setEnabled(b);
        txtUrl.setEditable(b);
        chkLoginRequired.setEnabled(b);
        txtUserName.setEditable(b);
        txtPassword.setEditable(b);
        txtMaxConnections.setEditable(b);
        txtLoginTimeout.setEditable(b);

    }

    public void displayObject(LocalDataSourceConfig config) {

        txtDisplayName.setText(config.getDisplayName());

        String driverClass = config.getDriverClassName();
        if(driverClass == null || (driverClass = driverClass.trim()).length() == 0) {
            driverClass = DriverClassSelector.EMPTY_ITEM;
        }
        boolean cmbState = cmbDriverClass.isEditable();
        cmbDriverClass.setEditable(true);
        cmbDriverClass.setSelectedItem(driverClass);
        cmbDriverClass.setEditable(cmbState);

        txtUrl.setText(config.getUrl());
        chkLoginRequired.setSelected(config.isLoginRequired());
        txtUserName.setText(config.getUser());
        txtPassword.setText(config.getPassword());

        Integer maxConnections = config.getMaxConnections();
        String s = maxConnections == null ? null : maxConnections.toString();
        txtMaxConnections.setText(s);

        Integer loginTimeout = config.getLoginTimeout();
        s = loginTimeout == null ? null : loginTimeout.toString();
        txtLoginTimeout.setText(s);

    }


    public void clearFields() {
        txtDisplayName.setText(null);
        cmbDriverClass.setSelectedItem(cmbDriverClass.EMPTY_ITEM);
        txtUrl.setText(null);
        chkLoginRequired.setSelected(false);
        txtUserName.setText(null);
        txtPassword.setText(null);
        txtMaxConnections.setText(null);
        txtLoginTimeout.setText(null);
    }

    @SuppressWarnings("deprecation")
    public void updateObject(LocalDataSourceConfig config) {

        config.setDisplayName(Util.trimToNull(txtDisplayName.getText()));
        config.setDriverClassName(Util.trimToNull((String) cmbDriverClass.getSelectedItem()));
        config.setUrl(Util.trimToNull(txtUrl.getText()));
        config.setLoginRequired(chkLoginRequired.isSelected());
        
        config.setUser(Util.trimToNull(txtUserName.getText()));
        config.setPassword(Util.trimToNull(txtPassword.getText()));
        
        String s = Util.trimToNull(txtMaxConnections.getText());
        if(s != null) config.setMaxConnections(new Integer(s));
        else config.setMaxConnections(null);
        
        s = Util.trimToNull(txtLoginTimeout.getText());
        if(s != null) config.setLoginTimeout(new Integer(s));
        else config.setLoginTimeout(null);
    }

    public void focusDisplayNameField() {
        txtDisplayName.requestFocus();
    }



}
