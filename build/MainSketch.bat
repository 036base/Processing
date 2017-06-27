@echo off

rem カレントディレクトリ移動
cd /D %~dp0

rem Processing Sketch実行
rem -----------------------------------------------
rem ※実行するクラスを指定する
rem 水族館：com.awesomebase.processing.SketchAquarium
rem 宇宙  ：com.awesomebase.processing.SketchSpace
rem 散歩  ：com.awesomebase.processing.SketchWalk
rem -----------------------------------------------
java -jar StartUp.jar com.awesomebase.processing.SketchAquarium

exit /b 0

