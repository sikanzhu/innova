set TheCD=%CD%

CD ..\..\thirdparty\Redis-x64-3.2.100
START start.bat

CD %TheCD%

java -jar innova_demo_server.jar
pause