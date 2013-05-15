@echo off

rem
rem YesCart. Start db and tomcat
rem @author Igor Azarny (iazarny@yahoo.com)

echo �����㥬 �ࢥ� ���� ������ derby ...
echo ��������, �� ����뢠�� ��� ����
ping 1.1.1.1 -n 1 -w 2000 > NUL
set DERBY_HOME=%~dp0..
cd %~dp0
set LOCALCLASSPATH=%DERBY_HOME%/lib/derby.jar;%DERBY_HOME%/lib/derbynet.jar;%DERBY_HOME%/lib/derbyclient.jar;%DERBY_HOME%/lib/derbytools.jar

start %~dp0\startNetworkServer.bat
ping 1.1.1.1 -n 1 -w 5000 > NUL

echo ��ࢥ� ���� ������ ����饭
echo ������� ����� ���� ������...

call java -classpath "%LOCALCLASSPATH%" -Dderby.system.home=%DERBY_HOME% -Dij.outfile=%DERBY_HOME%\derbyinit.log -Dderby.ui.codeset=UTF8 org.apache.derby.tools.ij dbinit_rus.sql

echo ��

exit 0
