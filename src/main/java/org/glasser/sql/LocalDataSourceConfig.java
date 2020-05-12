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
 * $Source: /cvsroot/qform/qform/src/org/glasser/sql/LocalDataSourceConfig.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/01/25 23:35:03 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.sql;

import org.glasser.util.*;
import org.glasser.util.comparators.*;


public class LocalDataSourceConfig implements java.io.Serializable, Cloneable {



    public final static MethodComparator<LocalDataSourceConfig> DISPLAY_NAME_COMPARATOR =
        new MethodComparator<>(org.glasser.sql.LocalDataSourceConfig.class, "getDisplayName");


    protected String displayName = null;

    protected String driverClassName = null;

    protected boolean loginRequired = false;    

    protected String url = null;

    protected String user = null;

    protected String password = null;

    protected Integer maxConnections = null;

    protected Integer loginTimeout = null;


    // setters

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setLoginRequired(boolean loginRequired) {
        this.loginRequired = loginRequired;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setLoginTimeout(Integer loginTimeout) {
        this.loginTimeout = loginTimeout;
    }


    // getters

    public String getDisplayName() {
        return displayName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public boolean isLoginRequired() {
        return loginRequired;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public Integer getLoginTimeout() {
        return loginTimeout;
    }


    public String toString() {
        if(displayName == null) return "";
        return displayName;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch(CloneNotSupportedException ex) {
            // shouldn't happen because this class is cloneable.
            throw new java.lang.UnknownError(getClass().getName() + ".clone(): Clone failed, when it shouldn't have!");
        }
    }

    public String debugString() {
        StringBuffer buffer = new StringBuffer(200);
        buffer.append(getClass().getName());
        buffer.append("[");
        buffer.append("displayName=");
        buffer.append(displayName);
        buffer.append(",driverClassName=");
        buffer.append(driverClassName);
        buffer.append(",loginRequired=");
        buffer.append(loginRequired);
        buffer.append(",url=");
        buffer.append(url);
        buffer.append(",user=");
        buffer.append(user);
        buffer.append(",password=");
        buffer.append(password);
        buffer.append(",maxConnections=");
        buffer.append(maxConnections);
        buffer.append(",loginTimeout=");
        buffer.append(loginTimeout);
    
        buffer.append("]");
        return buffer.toString();
    }













}
