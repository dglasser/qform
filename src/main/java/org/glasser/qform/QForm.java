/* ====================================================================
 * Copyright (c) 1998 - 2020 David F. Glasser.  All rights
 * reserved.
 *
 * This file is part of the QueryForm Database Tool.
 *
 * The QueryForm Database Tool is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The QueryForm Database Tool is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the QueryForm Database Tool; if not, write to:
 *
 *      The Free Software Foundation, Inc.,
 *      59 Temple Place, Suite 330
 *      Boston, MA  02111-1307  USA
 *
 * or visit http://www.gnu.org.
 *
 * ====================================================================
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 *
 * ==================================================================== 
 */
package org.glasser.qform;



import javax.swing.*;
import org.glasser.util.*;
import org.glasser.swing.*;
import java.io.*;
import java.util.*;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import java.net.*;
import java.awt.*;

public class QForm {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(QForm.class);

    private final static String WSDP = "http://java.sun.com as part of the Java Web Services Developer Pack";

    private final static String COMMONS = "http://jakarta.apache.org as part of the Commons project";

    private final static String[][] checkClasses =
    {
         {"javax.sql.DataSource", "javax.sql", "jdbc2_0-stdext.jar", WSDP, "1.4.0"}
        ,{"javax.xml.parsers.DocumentBuilderFactory", "javax.xml", "jaxp-api.jar", WSDP, "1.4.0"}
        ,{"org.w3c.dom.Node", "org.w3c.dom", "dom.jar", WSDP, "1.4.0"}
        ,{"org.xml.sax.SAXException", "org.xml.sax", "sax.jar", WSDP, "1.4.0"}
        ,{"org.apache.commons.dbcp.DriverManagerConnectionFactory", "org.apache.commons.dbcp", "commons-dbcp.jar", COMMONS}
        ,{"org.apache.commons.pool.impl.GenericObjectPool", "org.apache.commons.pool", "commons-pool.jar", COMMONS}
        ,{"org.apache.commons.collections.Bag", "org.apache.commons.collections", "commons-collections.jar", COMMONS}
        
    };

    private static File getConfigFile(String path) {
        File f = new File(path);
        if(f.exists()) return f;
        else return null;
    }

    public static void main(String[] args) 
        throws Exception
    {

        LogHelper.initLogging();

        // this will be the default title for message boxes when one isn't supplied
        GUIHelper.defaultMessageTitle = "QueryForm";

        // make sure we're running at least JRE 1.8
        if(!Util.isCurrentJavaVersionAtLeast("1.8")) {
            String version = System.getProperty("java.version");
            GUIHelper.errMsg(null, "This application requires Java 1.8 or later. You are currently running version "
                + version + ".", null);
            System.exit(1);
        }

        // look in advance for some of the 3rd-party library classes we're going to need, so we
        // can handle it gracefully if they're not there.
        for(int j=0; j<checkClasses.length; j++) {
            try {
                Class.forName(checkClasses[j][0]);
            }
            catch(ClassNotFoundException ex) {
                String msg =
                    "Classes from the " + checkClasses[j][1] + " package were not found in your classpath. "
                    + "These classes are available in the jar file \"" + checkClasses[j][2] + "\", which is available "
                    + "from " + checkClasses[j][3] + ".";
                if(checkClasses[j].length > 4) {
                    msg = msg + "\n\nThis package is also a standard part of the Java Runtime Environment " + checkClasses[j][4]
                        + ". You are currently running version " + System.getProperty("java.version") + ".";
                }

                GUIHelper.errMsg(null, msg, "Required Classes Missing");
                System.exit(2);
            }
        }

        try {
            // if the program was loaded from a jar file,
            // add the drivers subdirectory (just below the directory
            // that contains the program jar file) to the classpath
            // of the ExtensionClassLoader singleton. That is the classloader
            // through which driver and look-and-feel classes are loaded.
            ClassLoader cl = QForm.class.getClassLoader();
            URL url = cl.getResource("org/glasser/qform/QForm.class");
            String path = url.getPath();
            logger.debug("main(): QForm.class URL is {}", path);
            int i = path.indexOf(".jar!/org/glasser/qform/QForm.class");
            if(i > 0) { 

                // truncate just after ".jar"
                // the "decode" is needed in 1.4 but not 1.3
                String jarpath = java.net.URLDecoder.decode( path.substring(0, i+4), "UTF-8");
                jarpath = new URL(jarpath).getPath();
                
                // if this is a Windows machine, the jarpath will look like
                // "/C:/dir1/dir2..." with a leading slash. It works on some
                // systems, but not on others, so remove it.
                if(jarpath.indexOf(':') == 2) jarpath = jarpath.substring(1);
                logger.debug("main(): Jar path is {}", jarpath);
                File jarFile = new File(jarpath);
                File jarDir =  jarFile.getParentFile();
                File driverDir = new File(jarDir, "drivers");
                if(driverDir.exists()) {
                    logger.debug("main(): {} exists.", driverDir.getAbsolutePath());
                    ExtensionClassLoader.getSingleton().addArchivesInDirectory(driverDir);
                }
                else {
                    // make sure we translated the jar directory correctly before showing the
                    // error message.
                    if(jarDir.exists()) {

                        logger.debug("main(): {} not found.", driverDir.getAbsolutePath());

                        GUIHelper.infoMsg(null,
                            "The JDBC drivers directory, " + driverDir 
                            + ", was not found. This is not necessary to run the program, "
                            + "however if you create it, you can place JAR or ZIP files "
                            + "containing JDBC drivers in that directory, and they will "
                            + "be available the next time the program is started.\n\n"
                            + "The drivers directory is a directory named \"drivers\" "
                            + "which is a subdirectory of the one where the "
                            + "application jar file, \"" + jarFile.getName() 
                            + "\" is located.",
                            "Drivers Directory Missing");
                    }
                    else {
                        logger.warn("main(): WARNING: Invalid jar path: {}", jarpath );
                    }
                }
            }
        }
        catch(Exception ex) {
            GUIHelper.errMsg(null,
                "An error occurred while reading your drivers directory:\n\n"
                + ex.getClass().getName()
                + "\n" + ex.getMessage()
                + "\n\n Some JDBC drivers may not be available.",
                "Application Error");
        }

        // if a Look and Feel was specified on the command line, attempt to set it.
        String lafClassName = System.getProperty("qform.laf");
        if(lafClassName != null && lafClassName.trim().length() > 0) {
            try {
                LookAndFeel laf = (LookAndFeel) ExtensionClassLoader.getSingleton().loadClass(lafClassName).newInstance();
                UIManager.setLookAndFeel(laf);
                UIManager.getDefaults().put("ClassLoader", laf.getClass().getClassLoader());
                UIManager.getLookAndFeelDefaults().put("ClassLoader", laf.getClass().getClassLoader());
                logger.info("main(): Look and Feel set to {}", laf.getName());
            }
            catch(Exception ex) {
                GUIHelper.errMsg(null, "There was an error setting the application's look-and-feel to "
                    + lafClassName + ".\n\n" + ex.getClass().getName() + "\n" + Util.trimToString(ex.getMessage()), "Application Error");
            }
        }
        
        // now read the configuration file.
        try {
            // the configuration file is determined by the following
            // priority:
            // 
            // 1. The command line argument
            // 2. The qform.config system property
            // 3. The user's home directory.

            File configFile = null;
            String configProp = System.getProperty("qform.config");
            String configFilePath = null;
            if(args.length > 0) {
                configFile = getConfigFile(args[0]);
                if(configFile == null) {
                    GUIHelper.errMsg(null, "The configuration file specified with a command line argument, \""
                        + args[0] + "\", was not found.", "Application Error");
                    System.exit(1);
                }
            }
            else if(configProp != null) {
                configFile = getConfigFile(configProp);
                if(configFile == null) {
                    GUIHelper.errMsg(null, "The configuration file specified with the \"qform.config\" system property, \""
                        + configProp + "\", was not found.", "Application Error");
                    System.exit(1);
                }
            }
            else {
                // the default config file is called qform.xml and is located in
                // the user's home directory. If it doesn't exist, it will be
                // created when the program exits.
                String userHome = System.getProperty("user.home");
                if(userHome == null) userHome = "";
                if(!userHome.endsWith("/") && !userHome.endsWith("\\")) {
                    userHome += "/";
                }
                configFile = new File( userHome + "qform.xml" );
            }
         
            JFrame frame = new JFrame();
            frame.setContentPane(new org.glasser.qform.MainPanel(frame, configFile));

            try {
                java.net.URL imageUrl = QForm.class.getClassLoader().getResource("org/glasser/qform/images/LogoIcon32.gif");
                frame.setIconImage(Toolkit.getDefaultToolkit().getImage(imageUrl));
            }
            catch(Throwable t) {
                logger.error("main(): Error setting frame icon: " + t, t );
            }
    
            frame.pack();
            frame.setTitle("QueryForm");
    
            GUIHelper.centerWindowOnScreen(frame);
            
            frame.setVisible(true);
        }
        catch(Throwable ex) {
            String msg = "An error occurred during program startup, which was probably due to missing libraries or class files:\n\n" + ex.getClass().getName()
                + "\n\n" + ex.getMessage()
                + "\n\nPlease see the console output for more information.";
            GUIHelper.errMsg(null, msg, "Unrecoverable Application Error");
            logger.error("main(): " + msg, ex );
            System.exit(13);
        }

    } 


}
