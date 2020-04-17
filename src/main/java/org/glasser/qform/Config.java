/* ====================================================================
 * The QueryForm License, Version 1.1
 *
 * Copyright (c) 1998 - 2020 David F. Glasser.  All rights
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
 * $Source: /cvsroot/qform/qform/src/org/glasser/qform/Config.java,v $
 * $Revision: 1.5 $
 * $Author: dglasser $
 * $Date: 2003/07/24 01:53:12 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.qform;


import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.sql.*;
import javax.sql.*;
import org.glasser.sql.*;
import org.glasser.util.comparators.MethodComparator;
import javax.swing.*;



public class Config {


    private final static boolean debug = System.getProperty("Config.debug") != null;

    protected final static String DOCUMENT_TAG = "qform-config";

    protected final static String LOCAL_DATASOURCES_TAG = "local-datasources";

    protected final static String LOCAL_DATASOURCE_TAG = "local-datasource";

    protected final static String THIRD_PARTY_LAFS_TAG = "third-party-lafs";

    protected final static String THIRD_PARTY_LAF_TAG = "third-party-laf";

    protected final static String ENDL = "\n";

    protected final static String TAB = "    ";

    protected final static String TAB2 = TAB + TAB;

    protected final static String TAB3 = TAB + TAB + TAB;


    ArrayList localDataSourceConfigs = new ArrayList();

    Hashtable thirdPartyLafs = new Hashtable();

    public void setLocalDataSourceConfigs(ArrayList localDataSourceConfigs) {
        this.localDataSourceConfigs = localDataSourceConfigs;
    }

    public LocalDataSourceConfig[] getLocalDataSourceConfigs() {
        return (LocalDataSourceConfig[]) 
            localDataSourceConfigs.toArray(new LocalDataSourceConfig[localDataSourceConfigs.size()]);
    }

    public void deleteLocalDataSourceConfig(LocalDataSourceConfig config) {
        for(int j=0; j<localDataSourceConfigs.size(); j++) {
            if(localDataSourceConfigs.get(j) == config) {
                localDataSourceConfigs.remove(j);
            }
        }
    }

    public void addlocalDataSourceConfig(LocalDataSourceConfig config) {
        localDataSourceConfigs.add(config);
    }

    public void clearLocalDataSourceConfigs() {
        localDataSourceConfigs.clear();
    }


    /**
     * Sorts UIManager.LookAndFeelInfo objects by their name fields.
     */
    private MethodComparator lafComparator =
        new MethodComparator(UIManager.LookAndFeelInfo.class, "getName", false, false, null, false);

    /**
     * Returns all of the third-party LAF's listed in the config file.
     */
    public UIManager.LookAndFeelInfo[] getThirdPartyLafs() {

        UIManager.LookAndFeelInfo[] lafs = new UIManager.LookAndFeelInfo[thirdPartyLafs.size()];
        int j=0;
        for(Iterator i = thirdPartyLafs.values().iterator(); i.hasNext(); ) {
            lafs[j++] = (UIManager.LookAndFeelInfo) i.next();
        }

        Arrays.sort(lafs, lafComparator);

        return lafs;

    }


    public void deleteThirdPartyLaf(String lafClassName) {
        thirdPartyLafs.remove(lafClassName);
    }


    public void addOrUpdateThirdPartyLaf(String lafName, String lafClassName) {

        // get the existing one if it's there.
        UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) thirdPartyLafs.get(lafClassName);

        // only create the object if we need to.
        if(info == null || lafName.equals(info.getName()) == false) {
            thirdPartyLafs.put(lafClassName, new UIManager.LookAndFeelInfo(lafName, lafClassName));
        }
    }

    /**
     * If there is currently a UIManager.LookAndFeelInfo object stored for the given lafClassName,
     * this method will update it so its name field matches lafName, otherwise it does nothing.
     */
    public void maybeUpdateThirdPartyLaf(String lafName, String lafClassName) {

        // get the existing one if it's there.
        UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) thirdPartyLafs.get(lafClassName);

        // only create the object if we need to.
        if(info != null && lafName.equals(info.getName()) == false) {
            thirdPartyLafs.put(lafClassName, new UIManager.LookAndFeelInfo(lafName, lafClassName));
        }
    }

    public void clearThirdPartyLafs() {
        thirdPartyLafs.clear();
    }




    public Config() {}

    public Config(File configFile) 
        throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(configFile);
        init(doc);

    }


    public Config(InputStream is) 
        throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(is);
        init(doc);

    }


    private void init(Document doc) 
        throws SAXException, IOException, ParserConfigurationException
    {

        Element root = doc.getDocumentElement();
        NodeList conns = root.getElementsByTagName(LOCAL_DATASOURCES_TAG);
        if(conns.getLength() < 1) {
            throw new SAXException(LOCAL_DATASOURCES_TAG + " element not found in configuration file.");
        }
        else if(conns.getLength() > 1) {
            throw new SAXException("More than one " + LOCAL_DATASOURCES_TAG + " element was found in the configuration file.");
        }

        Element e = (Element) conns.item(0);

        conns = e.getElementsByTagName(LOCAL_DATASOURCE_TAG);


        for(int j=0; j<conns.getLength(); j++) {
            Element con = (Element) conns.item(j);
            LocalDataSourceConfig config = new LocalDataSourceConfig();
            config.setDisplayName(trimToNull(con.getAttribute("display-name")));
            config.setDriverClassName(trimToNull(con.getAttribute("driver-class")));
            config.setUrl(trimToNull(con.getAttribute("url")));
            String s = trimToNull(con.getAttribute("login-required"));
            if(s != null) {
                try {
                    config.setLoginRequired(new Boolean(s).booleanValue());
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            config.setUser(trimToNull(con.getAttribute("user")));
            config.setPassword(trimToNull(con.getAttribute("password")));

            s = trimToNull(con.getAttribute("max-connections"));
            if(s != null) {
                try {
                    config.setMaxConnections(new Integer(s));
                }                
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

            s = trimToNull(con.getAttribute("login-timeout"));
            if(s != null) {
                try {
                    config.setLoginTimeout(new Integer(s));
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

            localDataSourceConfigs.add(config);

            if(debug) System.out.println("Connection: " + config);
        }

        NodeList lafs = root.getElementsByTagName(THIRD_PARTY_LAFS_TAG);
        if(lafs != null && lafs.getLength() > 0) {
            e = (Element) lafs.item(0);
            lafs = e.getElementsByTagName(THIRD_PARTY_LAF_TAG);
            for(int j=0; j<lafs.getLength(); j++) {
                Element laf = (Element) lafs.item(j);
                String name = laf.getAttribute("laf-name");
                String lafClassName = laf.getAttribute("laf-class");
                UIManager.LookAndFeelInfo lafInfo = new UIManager.LookAndFeelInfo(name, lafClassName);
                this.thirdPartyLafs.put(lafInfo.getClassName(),lafInfo);
            }
        }


    } 


    public void writeConfig(String fileName) 
        throws Exception
    {
        writeConfig(new File(fileName));
    }


    public void writeConfig(File file) 
        throws Exception
    {
        FileOutputStream fos = new FileOutputStream(file);
        writeConfig(fos);
    }


    public void writeConfig(OutputStream os) 
        throws IOException, ParserConfigurationException, TransformerException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        if(debug) System.out.println("DocumentBuilderFactory is " + dbf.getClass().getName());

        DocumentBuilder db = dbf.newDocumentBuilder();

        if(debug) System.out.println("DocumentBuilder is " + db.getClass().getName());

        Document doc = db.newDocument();

        if(debug) System.out.println("Document is " + doc.getClass().getName());

        Element root = doc.createElement(DOCUMENT_TAG);

        doc.appendChild(root);

        root.appendChild(doc.createTextNode(ENDL + TAB));
        
        Element localConns = doc.createElement(LOCAL_DATASOURCES_TAG);

        root.appendChild(localConns);
    
        LocalDataSourceConfig[] conns = this.getLocalDataSourceConfigs();
        for(int j=0; j<conns.length; j++) {
            LocalDataSourceConfig config = conns[j];
            Element con = doc.createElement(LOCAL_DATASOURCE_TAG);
            con.setAttribute("display-name", trim(config.getDisplayName()) );
            if(debug) System.out.println("DISPLAY NAME = " + config.getDisplayName());
            con.setAttribute("driver-class", trim(config.getDriverClassName()) );
            con.setAttribute("url", trim(config.getUrl()) );
            con.setAttribute("login-required", String.valueOf(config.isLoginRequired()) );
            con.setAttribute("user", trim(config.getUser()) );
            con.setAttribute("password", trim(config.getPassword()) );
            con.setAttribute("max-connections", trim(config.getMaxConnections()) );
            con.setAttribute("login-timeout", trim(config.getLoginTimeout()) );

            localConns.appendChild(doc.createTextNode(ENDL + TAB2));
            localConns.appendChild(con);
            if(j == conns.length - 1) {
                localConns.appendChild(doc.createTextNode(ENDL + TAB));
            }
        }

        root.appendChild(doc.createTextNode(ENDL + TAB));

        Element lafs = doc.createElement(THIRD_PARTY_LAFS_TAG);
        root.appendChild(lafs);

        UIManager.LookAndFeelInfo[] lafInfos = this.getThirdPartyLafs();
        for(int j=0; j<lafInfos.length; j++) {
            UIManager.LookAndFeelInfo info = lafInfos[j];
            Element lafElement = doc.createElement(THIRD_PARTY_LAF_TAG);
            lafElement.setAttribute("laf-name", trim(info.getName()));
            lafElement.setAttribute("laf-class", trim(info.getClassName()));
            lafs.appendChild(doc.createTextNode(ENDL + TAB2));
            lafs.appendChild(lafElement);
            if(j == lafInfos.length - 1) {
                lafs.appendChild(doc.createTextNode(ENDL + TAB));
            }
        }

        root.appendChild(doc.createTextNode(ENDL));

        javax.xml.transform.Transformer transformer 
            = javax.xml.transform.TransformerFactory.newInstance().newTransformer();

        StreamResult sr = new StreamResult(os);

        transformer.transform(new DOMSource(doc), sr);


        OutputStream ostream = sr.getOutputStream(); 
        if(ostream != os) {
//            System.out.println("Closing StreamResult's output stream.");
            ostream.close();
        }

        os.close();


    }

    private static final String trimToNull(String s) {
        if(s == null || (s = s.trim()).length() == 0) return null;
        return s;
    }

    private static final String trim(Object o) {
        if(o == null) return "";
        return o.toString().trim();
    }   




    public static void main(String[] args) throws Exception {
        Config c = new Config(new File("C:/0/qform.xml"));
        LocalDataSourceConfig[] configs = c.getLocalDataSourceConfigs();
        for(int j=0; j<configs.length; j++) {
            System.out.println("----" + configs[j]);
            DataSource ds = DataSourceManager.getLocalDataSource(configs[j], null, null);
            for(int k=0; k<7; k++) {
                Connection conn = ds.getConnection();
                System.out.println("DONE.");
//                conn.close();
            }
        }



        c.writeConfig("C:/0/qf2.xml");
    } 
    

}
