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
 *
 * $Source: /cvsroot/qform/qform/src/org/glasser/qform/QueryPanelListener.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/05/29 00:40:24 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.qform;



import org.glasser.util.*;



/**
 * Listener interface for the QueryPanelEvent.
 */
public interface QueryPanelListener extends java.util.EventListener {

    public void queryPanelStateChanged(QueryPanelEvent event);

    public void queryPanelHorizontalScrollbarStateChanged(QueryPanelEvent event);

}
