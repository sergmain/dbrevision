set CLASSPATH=..\riverock-lib\lib\ant-1.6.2.jar;..\riverock-lib\lib\ant-launcher-1.6.2.jar;%JAVA_HOME%\lib\tools.jar;

%JAVA_HOME%\bin\java -Xms64m -Xmx128m org.apache.tools.ant.Main %1 %2 %3
