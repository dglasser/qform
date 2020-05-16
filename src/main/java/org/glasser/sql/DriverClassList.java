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
import java.util.*;
import org.glasser.util.ExtensionClassLoader;


public class DriverClassList {


    /**
     * This is a list of driver class names that were collected through a google 
     * search for "Class.forName" and "driver".
     */
    private final static String[] driverList =
    {
         "acs.jdbc.Driver"
        ,"com.experlog.universe.Driver"
        ,"com.ibm.db2.jcc.DB2Driver"
        ,"COM.ibm.db2.jdbc.app.DB2Driver"
        ,"com.informix.jdbc.IfxDriver"
        ,"com.intellidimension.jdbc.Driver"
        ,"com.microsoft.jdbc.sqlserver.SQLServerDriver"
        ,"com.mysql.jdbc.Driver"
        ,"com.newatlanta.jturbo.driver.Driver"
        ,"com.novell.sql.LDAPDriver"
        ,"com.sap.dbtech.jdbc.DriverSapDB"
        ,"com.sybase.jdbc.SybDriver"
        ,"gwe.sql.gweMysqlDriver"
        ,"ids.sql.IDSDriver"
        ,"in.co.daffodil.db.jdbc.DaffodilDBDriver"
        ,"in.co.daffodil.db.rmi.RmiDaffodilDBDriver"
        ,"interbase.interclient.Driver"
        ,"jdbc.sqlmx.SQLmxDriver"
        ,"oracle.jdbc.driver.OracleDriver"
        ,"org.enhydra.instantdb.jdbc.idbDriver"
        ,"org.gjt.mm.mysql.Driver"
        ,"org.postgresql.Driver"
        ,"postgres95.pgDriver"
        ,"postgresql.Driver"
        ,"sun.jdbc.odbc.JdbcOdbcDriver"
        ,"weblogic.jdbc.jts.Driver"
        ,"org.hsqldb.jdbcDriver"
        ,"org.firebirdsql.jdbc.FBDriver"
    };


    private static String[] availableDrivers = null;


    public static String[] getDriverClassNames() {
        return (String[]) driverList.clone();
    }


    public static String[] getAvailableDriverClassNames() {

        ClassLoader extensionLoader = ExtensionClassLoader.getSingleton();
        
        if(availableDrivers == null) {
            ArrayList<String> foundDriverClasses = new ArrayList<>();
            for(int j=0; j<driverList.length; j++) {
                try {
                    extensionLoader.loadClass(driverList[j]);
                    foundDriverClasses.add(driverList[j]);
                }
                catch(ClassNotFoundException ex) {
                    continue;
                }
            }

            availableDrivers = (String[]) foundDriverClasses.toArray(new String[foundDriverClasses.size()]);
        }

        return (String[]) availableDrivers.clone();

    }


    public static void main(String[] args) throws Exception {
        String[] availableDrivers = getAvailableDriverClassNames();
        for(int j=0; j<availableDrivers.length; j++) {
            System.out.println("--" + availableDrivers[j]);
        }
    } 

    private final static String[][] driverClassToEditableTypeMappings = 
    {
        {"oracle.jdbc.driver.OracleDriver", "1111"}
    };

    private final static HashMap<String, Set<Integer>> driverClassToEditableTypeMap = new HashMap<>();

    static {

        for(int j=0; j<driverClassToEditableTypeMappings.length; j++) {
            String[] row = driverClassToEditableTypeMappings[j];
            String types = row[1];
            StringTokenizer st = new StringTokenizer(types);
            HashSet<Integer> setForDriverClass = new HashSet<>();
            while(st.hasMoreTokens()) {
                setForDriverClass.add(new Integer(st.nextToken()));
            }
            driverClassToEditableTypeMap.put(row[0], setForDriverClass);
        }
    }

    public static Set<Integer> getEditableTypes(String driverClassName) {
        return driverClassToEditableTypeMap.get(driverClassName);
    }

}
