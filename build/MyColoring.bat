@echo off

rem カレントディレクトリ移動
cd /D %~dp0

rem MyColoring実行
java -jar StartUp.jar com.awesomebase.mycoloring.MyColoring %1

exit /b %ERRORLEVEL%

