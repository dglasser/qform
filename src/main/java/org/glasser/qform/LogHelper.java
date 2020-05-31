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

import java.io.*;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.*;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;

public class LogHelper {

    private LogHelper(){}

    private static boolean loggingInitialized = false;

    public static synchronized boolean initLogging() {

      if(loggingInitialized) {
          return true;
      }

      try {

          LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
          PatternLayoutEncoder ple = new PatternLayoutEncoder();

          ple.setPattern("[%-5p][%d][%logger{0}] %m%n");
          ple.setContext(loggerContext);
          ple.start();
          FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
          File logFile = getLogFile();
          System.out.println("log file is " + logFile.getAbsolutePath());
          fileAppender.setFile(logFile.getAbsolutePath());
          fileAppender.setAppend(false);
          fileAppender.setEncoder(ple);
          fileAppender.setContext(loggerContext);
          fileAppender.start();

          Logger logger = (Logger) LoggerFactory.getLogger("org.glasser");
          logger.addAppender(fileAppender);
          logger.setLevel(Level.DEBUG);
          logger.setAdditive(false);
          loggingInitialized = true;
          return true;
      }
      catch(Exception ex) {
          System.err.println("Error intializing the logging framework: " + ex);
          ex.printStackTrace();
          return false;
      }

    }

    public static File getLogFile() {
        return new File("qform.log");
    }

    public static String getLogFilePath() {
        return getLogFile().getAbsolutePath();
    }
}
