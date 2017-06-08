@echo off
echo #######################################################
echo   配布用パッケージを作成
echo #######################################################
echo;

cd /D %~dp0

set CURRENT_DIR=/D %~dp0


rem --------------------------------------------------------
rem 配布用パッケージの生成コマンド「javapackager」実行
rem --------------------------------------------------------
rem javapackager -deploy -outdir 出力ディレクトリ -outfile 出力ファイル名 -srcdir JARのあるディレクトリ -srcfiles 対象となるJARファイル名 -appclass アプリケーションクラス名 -name アプリケーション名称 -title アプリケーションタイトル

rem Windows 向けパッケージ生成オプションで
javapackager -deploy -native exe -outdir %CURRENT_DIR% -outfile MyColoring -srcdir %CURRENT_DIR% -srcfiles MyColoring.jar -appclass com.awesomebase.mycoloring.Main -name "MyColoring" -title "MyColoring" -BappVersion=1.0.0 -BsystemWide=true -Bwin.menuGroup="MyColoring"


pause
