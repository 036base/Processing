@echo off
echo #######################################################
echo   .tga�t�@�C��������mp4�t�@�C�����쐬
echo #######################################################
echo;

rem �J�����g�f�B���N�g���Ɉړ�
set CURRENT_DIRECTORY=%~dp0
cd /D %CURRENT_DIRECTORY%

rem ----------------------------------
rem �J�n�ԍ�����
rem ----------------------------------
set START_NUMBER=000000
set /P START_NUMBER="�A�ԃt�@�C���J�n�ԍ�����͂��Ă�������: "
echo;

echo -------------------------------------------------------
echo %START_NUMBER%.tga �ȍ~�̘A�Ԃō쐬���܂��B
echo ��낵���ł����H
echo -------------------------------------------------------
pause
echo;

rem ----------------------------------
rem �t�@�C�������� YYYYMMDDHHMMSS
rem ----------------------------------
set YYYYMMDD=%date:~0,4%%date:~5,2%%date:~8,2%
set TIME2=%time: =0%
set HHMMSS=%time2:~0,2%%time2:~3,2%%time2:~6,2%
set FILE_NAME=%CURRENT_DIRECTORY%mp4/%YYYYMMDD%%HHMMSS%.mp4

rem ----------------------------------
rem ���� .tgp �t�@�C��
rem ----------------------------------
set TGA_FILE=%CURRENT_DIRECTORY%tga/%%06d.tga

rem ----------------------------------
rem ffmpeg�R�}���h���s
rem ----------------------------------
ffmpeg.exe -start_number %START_NUMBER% -framerate 60 -i %TGA_FILE% -r 60 -an -vcodec libx264 -pix_fmt yuv420p %FILE_NAME%
echo;

echo -------------------------------------------------------
echo %FILE_NAME%���쐬���܂���
echo -------------------------------------------------------
echo;

pause
