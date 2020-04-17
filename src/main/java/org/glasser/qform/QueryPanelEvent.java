/* ====================================================================
 * Copyright (c) 1998 - 2003 David F. Glasser.  All rights
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
 *
 * $Source: /cvsroot/qform/qform/src/org/glasser/qform/QueryPanelEvent.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/05/29 00:40:24 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.qform;


import java.util.*;
import org.glasser.util.*;


/**
 * This is a SmartEvent that is fired by a QueryPanel to notify listeners of
 * various types of events.
 * 
 * @author David F. Glasser
 */
public final class QueryPanelEvent implements SmartEvent, java.io.Serializable {


    public final static int STATE_CHANGED = 0;

    public final static int HORIZONTAL_SCROLLBAR_STATE_CHANGED = 1;

    private int type = STATE_CHANGED;

    

    private QueryPanel source = null;

    /**
     * Constructs a QueryPanelEvent object.
     * 
     * @param source the QueryPanel which originated this event.
     * @param type an it indicating what type of event occurred. Currently the only
     * type of event is STATE_CHANGED.
     */
    public QueryPanelEvent(QueryPanel source, int type) {
        this.source = source;
        this.type = type;
    }

    /**
     * Returns the Class for the interface that receives this type of event; in this
     * particular case is is QueryPanelListener.class.
     */
    public Class getListenerClass() {
        return QueryPanelListener.class;
    }

    /**
     * Returns an int indicating the type of event occurred. Currently the only
     * type of event is STATE_CHANGED.
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

        QueryPanelListener panelListener = (QueryPanelListener) listener;

        switch(type) {  // a switch is used in anticipation of adding more event types.
            case STATE_CHANGED :
                panelListener.queryPanelStateChanged(this);
                break;
            case HORIZONTAL_SCROLLBAR_STATE_CHANGED :
                panelListener.queryPanelHorizontalScrollbarStateChanged(this);
                break;
            default :
                throw new RuntimeException("QueryPanelEvent: Invalid Type: " + type);
        }
    }
}
