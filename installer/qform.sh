#!/bin/sh

PATH=$JAVA_HOME/bin:$PATH
export PATH

runpath=$INSTALL_PATH/qform.jar

if [ "$CLASSPATH" = "" ]
then
   runpath=$runpath:$CLASSPATH
fi

java -classpath "$runpath" org.glasser.qform.QForm
