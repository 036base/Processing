@echo off

rem カレントディレクトリ移動
cd /D %~dp0

rem MainSketch3D実行
java -jar StartUp.jar com.awesomebase.processing.MainSketch3D

exit /b 0

