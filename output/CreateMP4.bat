@echo off
echo #######################################################
echo   .tgaファイルを元にmp4ファイルを作成
echo #######################################################
echo;

rem カレントディレクトリに移動
set CURRENT_DIRECTORY=%~dp0
cd /D %CURRENT_DIRECTORY%

rem ----------------------------------
rem 開始番号入力
rem ----------------------------------
set START_NUMBER=000000
set /P START_NUMBER="連番ファイル開始番号を入力してください: "
echo;

echo -------------------------------------------------------
echo %START_NUMBER%.tga 以降の連番で作成します。
echo よろしいですか？
echo -------------------------------------------------------
pause
echo;

rem ----------------------------------
rem ファイル名生成 YYYYMMDDHHMMSS
rem ----------------------------------
set YYYYMMDD=%date:~0,4%%date:~5,2%%date:~8,2%
set TIME2=%time: =0%
set HHMMSS=%time2:~0,2%%time2:~3,2%%time2:~6,2%
set FILE_NAME=%CURRENT_DIRECTORY%mp4/%YYYYMMDD%%HHMMSS%.mp4

rem ----------------------------------
rem 入力 .tgp ファイル
rem ----------------------------------
set TGA_FILE=%CURRENT_DIRECTORY%tga/%%06d.tga

rem ----------------------------------
rem ffmpegコマンド実行
rem ----------------------------------
ffmpeg.exe -start_number %START_NUMBER% -framerate 60 -i %TGA_FILE% -r 60 -an -vcodec libx264 -pix_fmt yuv420p %FILE_NAME%
echo;

echo -------------------------------------------------------
echo %FILE_NAME%を作成しました
echo -------------------------------------------------------
echo;

pause
