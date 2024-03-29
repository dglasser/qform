/* ====================================================================
 * Copyright (c) 1998 - 2021 David F. Glasser.  All rights
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

project.version = "1.4.1"

plugins {
    java
    application
    id("com.bmuschko.izpack") version "3.0"
}

repositories {
    mavenCentral {
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

tasks.compileJava {
    var compilerArgs = mutableListOf<String>()
    if("true".equals(System.getProperty("deprecation"))) {
        compilerArgs.add("-Xlint:deprecation")
    }
    if("true".equals(System.getProperty("unchecked"))) {
        compilerArgs.add("-Xlint:unchecked")
    }
    if(compilerArgs.size > 0) {
        println ("Compiler Args: " + compilerArgs)
        getOptions().setCompilerArgs(compilerArgs)
    }
}

dependencies {
    implementation("commons-collections:commons-collections:3.2.2")
    implementation("commons-pool:commons-pool:1.6")
    implementation("commons-dbcp:commons-dbcp:1.4")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.slf4j:slf4j-api:1.7.25")

    runtimeOnly("commons-collections:commons-collections:3.2.2")
    runtimeOnly("commons-pool:commons-pool:1.6")
    runtimeOnly("commons-dbcp:commons-dbcp:1.4")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
    runtimeOnly("org.slf4j:slf4j-api:1.7.25")

    // JDBC drivers
    runtimeOnly("mysql:mysql-connector-java:8.0.19")
    runtimeOnly("net.sourceforge.jtds:jtds:1.3.1")
    runtimeOnly("org.postgresql:postgresql:42.2.12")

    // 10.15.n.n and later versions of the derbyclient library do not
    // contain org.apache.derby.jdbc.ClientDriver. See
    // https://issues.apache.org/jira/browse/DERBY-6945
    runtimeOnly("org.apache.derby:derbyclient") {
        version {
            strictly("(,10.15)")
        }
    }

    implementation("com.bmuschko:gradle-izpack-plugin:3.0")

    izpack("org.codehaus.izpack:izpack-ant:5.1.3")
    
}

application {
    mainClassName = "org.glasser.qform.QForm"
}

tasks.jar {
    manifest {
        attributes("Main-Class" to application.mainClassName,
                   "Class-Path" to "commons-collections-3.2.2.jar commons-dbcp-1.4.jar commons-pool-1.6.jar " +
				                   "logback-core-1.2.3.jar logback-classic-1.2.3.jar slf4j-api-1.7.25.jar " +
                   "libs/commons-collections-3.2.2.jar libs/commons-dbcp-1.4.jar libs/commons-pool-1.6.jar " +
				   "libs/logback-core-1.2.3.jar libs/logback-classic-1.2.3.jar libs/slf4j-api-1.7.25.jar")
    }
    getArchiveVersion().set("")
}

izpack {
    setBaseDir (
        file("installer")
    )
    setInstallFile(
        file("installer/qform_izpack.xml")
    )
    outputFile = file("$buildDir/distributions/qform-${version}-installer.jar")
    compression = "deflate"
    compressionLevel = 9
    appProperties = mapOf("app.group" to "QueryForm", 
                          "app.name" to "qform", 
                          "app.title" to "QueryForm",
                          "app.version" to project.version, 
                          "app.subpath" to "QueryForm-$version")
}

tasks.register<Copy>("copyToLib") {
    into("${buildDir}/libs")
    from(configurations.runtimeClasspath)
}

tasks.izPackCreateInstaller {
    dependsOn(":assemble")
    dependsOn(":copyToLib")
}

tasks.register<Zip>("srcZip") {
    getArchiveClassifier().set("src")
    getArchiveVersion().set(project.version.toString())
    from(".")
    include("src/**")
    include("installer/**")
    include("docs/**")
    include("build.gradle.kts")
    include("gradle/**")
    include("gradlew")
    include("gradlew.bat")
    exclude("build/**")
}

tasks.register<Zip>("binZip") {
    getArchiveClassifier().set("bin")
    getArchiveVersion().set(project.version.toString())
    from("build/libs")
    include("qform.jar")
    from("docs")
    include("instructions.html")

    dependsOn(":assemble")
}



