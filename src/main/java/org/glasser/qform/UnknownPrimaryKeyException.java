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
 * $Source: /cvsroot/qform/qform/src/org/glasser/qform/UnknownPrimaryKeyException.java,v $
 * $Revision: 1.1 $
 * $Author: dglasser $
 * $Date: 2003/07/11 03:04:07 $
 * 
 * --------------------------------------------------------------------
 */
package org.glasser.qform;


/**
 * This exception is thrown inside QueryForm when a table's primary key is unknown 
 * (it was not reported in the metadata, which is the case with Access / ODBC ) and
 * could not be "guessed" heuristically, and an operation is attempted which required
 * knowledge of the primary key (update, delete, etc.)
 */
public class UnknownPrimaryKeyException extends Exception {
}
