package com.awesomebase.mycoloring;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * ぼくのぬりえメインクラス
 *
 * @author
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {

		/*---------------------------------------------------------------------------------------------
		 * 【実装メモ】
		 *
		 *  画面のレイアウトは「JavaFX Scene Builder」を使用してデザインする。
		 *  MyColoring.fxmlが編集される。
		 *
		 *  画面の処理は MyColoringController に実装する。
		 *  fxmlのコントロール名、イベント名と一致させる。
		 *  MyColoringControllerで先に宣言するとデザイナ側でコントロール名、イベント名を選択できる。
		 *
		 * インストーラも作成できる。（未検証）
		 * build.fxbuild に必要は情報を設定（※作成には他のツールのインストールも必要）
		 * http://wannabe-jellyfish.hatenablog.com/entry/2016/03/05/230100
		 *
		 *---------------------------------------------------------------------------------------------*/

		try {
			BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("MyColoring.fxml"));
			Scene scene = new Scene(root, 900, 600);
			scene.getStylesheets().add(getClass().getResource("MyColoring.css").toExternalForm());
			primaryStage.setScene(scene);

			// 画面設定
			primaryStage.setTitle("ぼくのぬりえ");
			primaryStage.setResizable(false);
			primaryStage.show();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
