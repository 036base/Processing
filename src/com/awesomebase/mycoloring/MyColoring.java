package com.awesomebase.mycoloring;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class MyColoring extends Application {
	//---------------------------------------------------------------------------------------------
	// 【実装メモ】
	//
	//  画面のレイアウトは「JavaFX Scene Builder」を使用してデザインする。
	//  MyColoring.fxmlが編集される。
	//
	//  画面の処理は MyColoringController に実装する。
	//  fxmlのコントロール名、イベント名と一致させる。
	//  MyColoringControllerで先に宣言するとデザイナ側でコントロール名、イベント名を選択できる。
	//
	//---------------------------------------------------------------------------------------------

	private final Logger _logger = LogManager.getLogger();

	private static String[] _args;

	public static void main(String[] args) {
		_args = args;
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			_logger.info("Start...");

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyColoring.fxml"));

			BorderPane root = (BorderPane) fxmlLoader.load();
			Scene scene = new Scene(root, 900, 600);
			scene.getStylesheets().add(getClass().getResource("MyColoring.css").toExternalForm());
			primaryStage.setScene(scene);

			// 引数により起動モード判定
			final MyColoringController controller = (MyColoringController) fxmlLoader.getController();
			if (_args != null && _args.length > 0) {
				_logger.info("Arguments:" + Arrays.toString(_args));
				if(Arrays.asList(_args).contains("--startup")){
					controller.setStartUpMode(true);
				}
			} else {
				_logger.info("No Arguments");
			}
			controller.localInit();

			// 画面設定
			primaryStage.setTitle("ぼくのぬりえ");
			primaryStage.setResizable(false);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
