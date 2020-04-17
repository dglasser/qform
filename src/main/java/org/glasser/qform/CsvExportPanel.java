/* ====================================================================
 * Copyright (c) 1998 - 2004 David F. Glasser.  All rights
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
import org.glasser.sql.*;
import org.glasser.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.text.SimpleDateFormat;



public class CsvExportPanel extends ExportPanel {



    private static final Formatter numFormatter =
        new Formatter() {

            public String getFormattedString(Object obj) {
                if(obj == null) return "";
                else return obj.toString();
            }

            public String toString() {
                return "<Copy Value>";
            }
        };

    private static final Formatter nullFormatter =
        new Formatter() {

            public String getFormattedString(Object obj) {
                return "";
            }

            public String toString() {
                return "<Empty String>";
            }
        };


    private static class TimestampFormatter implements Formatter {

        public final static int TIME_ESCAPE = 0;

        public final static int DATE_ESCAPE = 1;

        public final static int TIMESTAMP_ESCAPE = 2;

        public final static int TIME_STRING = 3;

        public final static int DATE_STRING = 4;

        public final static int TIMESTAMP_STRING = 5;

        private int formatStyle = TIMESTAMP_STRING;

        private final static SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("HH:mm:ss");

        private final static SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd");

        private final static SimpleDateFormat TIMESTAMP_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public TimestampFormatter() {}

        public TimestampFormatter(int formatStyle) {
            this.formatStyle = formatStyle;
        }



        public String getFormattedString(Object obj) {
            if(obj == null) return "NULL";

            java.util.Date date = (java.util.Date) obj;

            switch(formatStyle) {
                case TIME_ESCAPE :
                    return "{t '" + TIME_FORMATTER.format(date) + "'}";
                case DATE_ESCAPE :
                    return "{d '" + DATE_FORMATTER.format(date) + "'}";
                case TIMESTAMP_ESCAPE :
                    return "{ts '" + TIMESTAMP_FORMATTER.format(date) + "'}";
                case TIME_STRING :
                    return "'" + TIME_FORMATTER.format(date) + "'";
                case DATE_STRING :
                    return "'" + DATE_FORMATTER.format(date) + "'";
                default :
                    return "'" + TIMESTAMP_FORMATTER.format(date) + "'";

            }
        }

        public String toString() {

            switch(formatStyle) {
                case TIME_ESCAPE :
                    return "{t 'hh:mm:ss'}";
                case DATE_ESCAPE :
                    return "{d 'YYYY-MM-DD'}";
                case TIMESTAMP_ESCAPE :
                    return "{ts 'YYYY-MM-DD hh:mm:ss'}";
                case TIME_STRING :
                    return "'hh:mm:ss'";
                case DATE_STRING :
                    return "'YYYY-MM-DD'";
                default : // TIMESTAMP_STRING
                    return "'YYYY-MM-DD hh:mm:ss'";

            }
        }

    }

    /**
     * This escapes a field for an Excel-style CSV file.
     */
    private static class CsvFormatter implements Formatter {

        public String getFormattedString(Object obj) {

            if(obj == null) return "";
            String val = (String) obj;
            val = val.trim();

            // First, scan the string for characters that require escaping.
            // This way we can avoid creating the StringBuffer object if we don't need to.
            char[] chars = val.toCharArray();
            boolean needsEscaping = false;
            for(int j=0; j<chars.length; j++) { 
                if(chars[j] == ',' || chars[j] == '\n' || chars[j] == '"') {
                    needsEscaping = true;
                    break;
                }
            }

            // if no bad characters were found, return the orginal value
            if(needsEscaping == false) return val;

            // otherwise, the string needs escaping
            StringBuffer buffer = new StringBuffer(val.length() + 20);
            buffer.append('"');
            for(int j=0; j<chars.length; j++) { 
                if(chars[j] == '"') {
                    buffer.append('"');
                }
                else if(chars[j] == '\r' && chars.length > j+1 && chars[j+1] == '\n') {
                    // "\r\n" is normalized to '\n', so skip past the '\r'.
                    j++;
                }

                buffer.append(chars[j]);
            }

            buffer.append('"');

            return buffer.toString();
        }

        public String toString() {
            return "<Copy Value>";
        }

    }

    private static CsvFormatter charFormatter = new CsvFormatter();

    private static final Object[] charChoices = {"", charFormatter, nullFormatter};

    private static final Object[] numChoices = {"", numFormatter, nullFormatter};

    private static final Object[] binChoices = {"", nullFormatter};

    private static final Object[] dateTimeChoices =
    {
        "" 
        ,nullFormatter
        ,new TimestampFormatter(TimestampFormatter.TIMESTAMP_STRING)
        ,new TimestampFormatter(TimestampFormatter.DATE_STRING)
        ,new TimestampFormatter(TimestampFormatter.TIME_STRING)
        ,new TimestampFormatter(TimestampFormatter.TIMESTAMP_ESCAPE)
        ,new TimestampFormatter(TimestampFormatter.DATE_ESCAPE)
        ,new TimestampFormatter(TimestampFormatter.TIME_ESCAPE)
    };


    public CsvExportPanel(TableInfo ti) {
        super(ti);
    }

    protected Object[] getFormatterChoices(int type) {
        if(DBUtil.isCharType(type)) {
            return charChoices;
        }
        else if(DBUtil.isNumericType(type)) {
            return numChoices;
        }
        else if(DBUtil.isDateTimeType(type)) {
            return dateTimeChoices;
        }
        else {
            return binChoices;
        }

        
    }



}
