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
 */


plugins {
    java
    application
}

repositories {
    jcenter {
        content {
            excludeGroup("com.oracle.jdbc")
        }
    }
    maven {
        // for Oracle JDBC driver
        url = uri("https://repo.spring.io/libs-release/")
        content {
            includeGroup("com.oracle.jdbc")
        }
    }
}

dependencies {
    implementation("commons-collections:commons-collections:3.2.2")
    implementation("commons-pool:commons-pool:1.6")
    implementation("commons-dbcp:commons-dbcp:1.4")


    // JDBC drivers
    runtime("mysql:mysql-connector-java:8.0.19")
    runtime("net.sourceforge.jtds:jtds:1.3.1")
    runtime("org.postgresql:postgresql:42.2.12")

    // 10.15.n.n and later versions of the derbyclient library do not
    // contain org.apache.derby.jdbc.ClientDriver. See
    // https://issues.apache.org/jira/browse/DERBY-6945
    runtime("org.apache.derby:derbyclient:10.14.2.0") {
        isForce = true
    }

    runtime("com.oracle.jdbc:com.springsource.oracle.jdbc:10.2.0.2")
    
}

application {
    mainClassName = "org.glasser.qform.QForm"
}
