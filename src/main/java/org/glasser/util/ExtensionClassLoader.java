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
 * $Source: /cvsroot/qform/qform/src/org/glasser/util/ExtensionClassLoader.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2003/02/02 14:35:11 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.util;


import java.net.*;
import java.io.*;



/**
 * This is a URLClassLoader with some minor functionality added. It has
 * a Singleton instance which is globally accessible, and it also
 * has a method (addArchivesInDirectory(File directory) that adds all
 * of the jar and zip files in a given directory to the classpath for
 * an instance of this classloader.
 */
public class ExtensionClassLoader extends java.net.URLClassLoader {


    public static boolean debug = System.getProperty("ExtensionClassLoader.debug") != null;

    /**
     * This is not a Singleton class in the strict sense that only one instance
     * can be created, (the constructors are public) however there is one instance
     * that is globally accessible.
     */
    private final static ExtensionClassLoader singleton = new ExtensionClassLoader(new URL[0]);


    static class ArchiveFilter implements java.io.FileFilter {
    
        public boolean accept(File pathName) {
            String upcase = pathName.getName().toUpperCase();
            if(upcase.endsWith(".ZIP") || upcase.endsWith(".JAR")) return true;
            return false;
        }
    }

    private ArchiveFilter archiveFilter = new ArchiveFilter();


    public ExtensionClassLoader(URL[] urls) {
        super(urls);
    }


    public ExtensionClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }


    public ExtensionClassLoader(URL[] urls, 
        ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    /**
     * This is not a Singleton instance in the strict sense that only one instance
     * can be created, (the constructors are public) however there is one instance
     * that is globally accessible.
     */
    public static ExtensionClassLoader getSingleton() {
        return singleton;
    }


    public void addArchivesInDirectory(File directory) 
        throws MalformedURLException
    {
        if(directory.isDirectory() == false) {
            throw new IllegalArgumentException("directory argument is not a directory.");
        }
        if(debug) System.out.println("Adding archives in " + directory + " to ExtensionClassLoader classpath." );

        File[] archives = directory.listFiles(archiveFilter);
        for(int j=0; archives != null && j<archives.length; j++) {
            if(debug) System.out.println("Adding " + archives[j] + " to ExtensionClassLoader classpath.");
            this.addURL(archives[j].toURL());
        }
    }

}
