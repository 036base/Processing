@echo off

rem �J�����g�f�B���N�g���ړ�
cd /D %~dp0

rem MyColoring���s
java -jar StartUp.jar com.awesomebase.mycoloring.MyColoring %1

exit /b %ERRORLEVEL%

