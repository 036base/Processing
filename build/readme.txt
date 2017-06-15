
■■■ 実行ファイル作成手順 ■■■

１．Eclipseで「build.xml」を右クリック >「実行」>「Antビルド」を実行する。

２．buildフォルダに「StartUp.jar」が作成される。

３．実行フォルダに以下のファイルをコピー。
      ・StartUp.jar
      ・StartUp.vbs（システム起動用）
      ・MainSketch3D.bat（Processing起動用）
      ・MyColoring.bat（管理画面起動用）
      ・confフォルダ
      ・imgフォルダ
      ・outputフォルダ
      ・libフォルダ

４．「StartUp.vbs」をダブルクリックするとシステムが起動する。

--------------------------------------------------------------------
■ TODO ■
  JREを同梱させたものを作りたい。
  javapackagerコマンドにより自己完結型パッケージの生成できる。
    ・インストール先の OS に合わせたネイティブインストーラの生成
    ・JRE も含めたパッケージを生成し、OS 側にランタイムの事前インストールを要求しない

  サンプルとして「CreateEXE.bat」を作成。ただし、conf等のフォルダを同梱できるかわからない。


　java起動オプション
　http://d.hatena.ne.jp/torutk/20151127/p1


http://takabrk.seesaa.net/article/445208235.html?seesaa_related=category
