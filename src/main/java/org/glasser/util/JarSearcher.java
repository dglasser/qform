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
package org.glasser.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;

/**
 * This program will search all jar and zip files in the current directory for
 * a given class file.
 * 
 * @author Dave Glasser
 */
public class JarSearcher {

    /**
     * Returns true if the jar or zip file at jarFilePath contains (with the internal
     * path) the file named by classFilePath. 
     */
    public static boolean searchJarFile(String jarFilePath, String classFilePath) {
        return searchJarFile( new File(jarFilePath), classFilePath);
    }

    public static boolean searchJarFile(File file, String classFilePath) {

        try {
            if(!file.exists()) return false;
    
            ZipFile jarFile = new ZipFile(file);
            if(jarFile.getEntry(classFilePath) != null) {
                jarFile.close();
                return true;
            }
            else {
                jarFile.close();
                return false;
            }
        }
        catch(IOException ex) {
            System.out.println(ex.toString());
            return false;
        }
    }




  static class ArchiveFilter implements FileFilter {

    public boolean accept(File pathName) {
      String upcase = pathName.getName().toUpperCase();
      if(upcase.endsWith(".ZIP") || upcase.endsWith(".JAR")) return true;
      return false;
    }
  }


    public static void main (String[] args)  {
        if(args.length == 0) {
            System.out.println("usage: java ClassFinder <class name>\n\n"
                               + "example: java ClassFinder java.lang.String\n");
            System.exit(0);
        }

        File cwd = new File(".");
        File[] archives = cwd.listFiles(new ArchiveFilter());
    
        String classFileName = args[0].replace('.', '/');
        if(classFileName.endsWith(".class") == false) {
            classFileName += ".class";
        }
    
        System.out.println("Searching for " + classFileName + " ...");
        for(int j=0; j<archives.length; j++) {
    //      System.out.println("Searching " + archives[j].getName());
          if(searchJarFile(archives[j], classFileName)) {
            System.out.println("FOUND IN " + archives[j].getName());
          }
        }
    }
}
