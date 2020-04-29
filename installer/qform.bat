@echo off
set RUNPATH="$INSTALL_PATH\qform.jar"
if not "%CLASSPATH%" == "" goto fullclasspath
goto run
:fullclasspath
set RUNPATH="$INSTALL_PATH\qform.jar;%CLASSPATH%"
:run
"$JAVA_HOME\bin\java" -classpath %RUNPATH% org.glasser.qform.QForm
if errorlevel 1 goto err
exit
:err
pause

