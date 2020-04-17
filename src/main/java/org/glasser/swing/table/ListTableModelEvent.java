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
 * $Source: /cvsroot/qform/qform/src/org/glasser/swing/table/ListTableModelEvent.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/01/26 00:19:08 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.swing.table;


import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import org.glasser.util.*;

public class ListTableModelEvent extends EventObject implements SmartEvent {

    public final static int UPDATE = TableModelEvent.UPDATE;

    public final static int DELETE = TableModelEvent.DELETE;

    public final static int INSERT = TableModelEvent.INSERT;

    protected int type = UPDATE;

    protected Object rowObject = null;

    public ListTableModelEvent(ListTableModel source, Object rowObject)  {
        super(source);
        this.rowObject = rowObject;
    }


    public ListTableModelEvent(ListTableModel source, Object rowObject, int type)  {
        super(source);
        this.rowObject = rowObject;
        this.type = type;
    }


    public Object getRowObject() {
        return rowObject;
    }

    public int getType() {
        return type;
    }

    public void notifyListener(EventListener listener) 
    {

        ListTableModelListener ltml = (ListTableModelListener) listener;
        ltml.dataListChanged(this);
    }

    public Class getListenerClass() {

        return org.glasser.swing.table.ListTableModelListener.class;

    }

}
