@echo off
echo #######################################################
echo   �z�z�p�p�b�P�[�W���쐬
echo #######################################################
echo;

cd /D %~dp0

set CURRENT_DIR=/D %~dp0


rem --------------------------------------------------------
rem �z�z�p�p�b�P�[�W�̐����R�}���h�ujavapackager�v���s
rem --------------------------------------------------------
rem javapackager -deploy -outdir �o�̓f�B���N�g�� -outfile �o�̓t�@�C���� -srcdir JAR�̂���f�B���N�g�� -srcfiles �ΏۂƂȂ�JAR�t�@�C���� -appclass �A�v���P�[�V�����N���X�� -name �A�v���P�[�V�������� -title �A�v���P�[�V�����^�C�g��

rem Windows �����p�b�P�[�W�����I�v�V������
javapackager -deploy -native exe -outdir %CURRENT_DIR% -outfile MyColoring -srcdir %CURRENT_DIR% -srcfiles MyColoring.jar -appclass com.awesomebase.mycoloring.Main -name "MyColoring" -title "MyColoring" -BappVersion=1.0.0 -BsystemWide=true -Bwin.menuGroup="MyColoring"


pause
