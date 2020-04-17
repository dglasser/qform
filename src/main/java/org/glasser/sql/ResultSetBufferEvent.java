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
 * $Source: /cvsroot/qform/qform/src/org/glasser/sql/ResultSetBufferEvent.java,v $
 * $Revision: 1.2 $
 * $Author: dglasser $
 * $Date: 2003/05/01 00:47:41 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.sql;


import java.util.*;
import org.glasser.util.*;


/**
 * @author David F. Glasser
 */
public final class ResultSetBufferEvent implements SmartEvent, java.io.Serializable {


    public final static int MORE_ROWS_READ = 0;

    public final static int END_OF_RESULTS_REACHED = 1;

    public final static int BUFFER_SORTED = 2;

    private int type = MORE_ROWS_READ;

    private ResultSetBuffer source = null;

    /**
     * Constructs a ResultSetBufferEvent objects.
     * 
     * @param source the ResultSetBuffer which originated this event.
     * @param type an it indicating what type of ResultSetBufferEvent this
     * is, either MORE_ROWS_READ or END_OF_RESULTS_REACHED.
     */
    public ResultSetBufferEvent(ResultSetBuffer source, int type) {
        this.source = source;
        this.type = type;
    }

    /**
     * Returns the Class for the interface that receives this type of event; in this
     * particular case is is ResultSetBufferListener.class.
     */
    public Class getListenerClass() {
        return ResultSetBufferListener.class;
    }

    /**
     * Returns an int indicating the type of ResultSetBufferEvent this is, either
     * MORE_ROWS_READ or END_OF_RESULTS_REACHED.
     */
    public int getType() {
        return type;
    }

    /**
     * This method, specified by the org.glasser.util.SmartEventListener interface,
     * is passed a reference to one of the listeners registered to receive this
     * event. It then calls the appropriate method on the listener interface,
     * notifying the object of the event.
     */
    public void notifyListener(EventListener listener) {

        ResultSetBufferListener bufferListener = (ResultSetBufferListener) listener;

        switch(type) {
            case MORE_ROWS_READ :
                bufferListener.moreRowsRead(this);
                break;
            case END_OF_RESULTS_REACHED :
                bufferListener.endOfResultsReached(this);
                break;
            case BUFFER_SORTED :
                bufferListener.bufferSorted(this);
                break;
            default :
                throw new RuntimeException("ResultSetBufferEvent: Invalid Type: " + type);
        }

    }

}
