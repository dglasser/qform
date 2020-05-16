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
package org.glasser.sql;


import java.sql.*;
import javax.sql.*;
import java.util.*;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.*;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import org.glasser.sql.LocalDataSourceConfig;
import org.glasser.util.ExtensionClassLoader;


public class DataSourceManager {


    public static DataSource getLocalDataSource(LocalDataSourceConfig config, String userName, String password)
        throws ClassNotFoundException, Exception
    {

        Driver driver = null;
        Class driverClass = ExtensionClassLoader.getSingleton().loadClass(config.getDriverClassName());
        driver = (Driver) driverClass.newInstance();

        if(userName == null) userName = config.getUser();
        if(password == null) password = config.getPassword();

        int maxConnections = 1000;
        Integer n = config.getMaxConnections();
        if(n != null) maxConnections = n.intValue();

        Properties props = new Properties();
        if(userName != null) {
            props.setProperty("user", userName);
        }
        if(password != null) {
            props.setProperty("password", password);
        }

        if(driver.acceptsURL(config.getUrl()) == false) {
            throw new RuntimeException("The URL \"" + config.getUrl()
                + "\" is invalid for the JDBC driver class " + driverClass.getName() + ".");
        }

        DriverConnectionFactory connFactory = new DriverConnectionFactory(driver, config.getUrl(), props); 
        
        GenericObjectPool<Connection> genericPool = new GenericObjectPool<>(null, maxConnections);
        genericPool.setWhenExhaustedAction(genericPool.WHEN_EXHAUSTED_FAIL);
//        genericPool.setMaxWait(1);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connFactory, genericPool,null, null, false, true);
        PoolingDataSource dataSource = new PoolingDataSource(genericPool);
        return dataSource;
    }


}

