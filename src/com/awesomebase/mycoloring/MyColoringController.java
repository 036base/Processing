package com.awesomebase.mycoloring;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Controller
 *
 * @author
 *
 */
public class MyColoringController implements Initializable {

	private final Logger _logger = LogManager.getLogger();

	private final Properties _properties = new Properties();

	@FXML
	private BorderPane paneRoot;
	@FXML
	private RadioButton rdoFullScreenModeOn;
	@FXML
	private RadioButton rdoFullScreenModeOff;
	@FXML
	private TextField txtDisplayNo;
	@FXML
	private TextField txtScreenSizeWidth;
	@FXML
	private TextField txtScreenSizeHeight;
	@FXML
	private RadioButton rdoBackgroundModeImage;
	@FXML
	private TextField txtBackgroundImageFile;
	@FXML
	private Button btnBackgroundImageChooser;
	@FXML
	private RadioButton rdoBackgroundModeMovie;
	@FXML
	private TextField txtBackgroundMovieFile;
	@FXML
	private Button btnBackgroundMovieChooser;
	@FXML
	private TextField txtCharacterImageFolder;
	@FXML
	private Button btnCharacterImageFolderChooser;
	@FXML
	private TextField txtMonitoringInterval;
	@FXML
	private TextField txtDefaultImageWidth;
	@FXML
	private TextField txtDefaultAnimationSpeed;
	@FXML
	private ImageView imvLogo;
	@FXML
	private Button btnSaveAndStart;
	@FXML
	private Button btnSaveAndClose;
	@FXML
	private Button btnClose;


	//----- プロパティ -----//
	// 起動用シェルから実行されたか
	private boolean isStartUpMode = false;
	public boolean isStartUpMode() {
		return isStartUpMode;
	}
	public void setStartUpMode(boolean isStartUpMode) {
		this.isStartUpMode = isStartUpMode;
	}



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			_logger.info("initialize...");

			// 設定ファイル読み込み
			_properties.load((new InputStreamReader(new FileInputStream("conf/processing.properties"), "UTF-8")));

			//--------------------------------
			// 入力制御の設定
			//--------------------------------
			// ディスプレイ番号
			addNumericTextFilter(txtDisplayNo, 1, false);
			// デフォルト画面サイズ幅
			addNumericTextFilter(txtScreenSizeWidth, 5, false);
			// デフォルト画面サイズ高さ
			addNumericTextFilter(txtScreenSizeHeight, 5, false);
			// 監視間隔
			addNumericTextFilter(txtMonitoringInterval, 6, false);
			// デフォルト画像幅
			addNumericTextFilter(txtDefaultImageWidth, 4, false);
			// デフォルトアニメーション速度
			addNumericTextFilter(txtDefaultAnimationSpeed, 5, true);

			// 画面に設定値を表示
			dispProperties();

			// TODO:画像表示
			Image image = new Image((new File("img/my-coloring-logo.jpg")).toURI().toString());
			imvLogo.setFitWidth(image.getRequestedWidth());
			imvLogo.setFitHeight(image.getRequestedHeight());
			imvLogo.setImage(image);

			txtDisplayNo.requestFocus();

		} catch (Exception e) {
			_logger.error("*** System Error!! ***", e);
		}
	}

	/**
	 * 初期処理
	 *   initialize後に設定することを実装
	 */
	public void localInit() {
		_logger.info("LocalInit...");

		if (isStartUpMode) {
			// 起動用VBScriptから実行された場合は起動ボタンを表示
			btnSaveAndStart.setVisible(true);
		} else {
			// 単独で実行された場合は起動ボタン非表示
			btnSaveAndStart.setVisible(false);
		}
	}

	/**
	 * ファイル参照ボタン押下処理
	 * @param evt
	 */
	@FXML
	protected void onFileChooser(ActionEvent evt) {
		try {
			FileChooser fc = new FileChooser();
			fc.setTitle("ファイル選択");

			// 拡張子フィルタを設定
			if (evt.getSource() == btnBackgroundImageChooser) {
				// 背景イメージファイル
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("イメージファイル", "*.png", "*.jpg", "*.jpeg", "*.jpe", "*.gif"));
			} else if (evt.getSource() == btnBackgroundMovieChooser) {
				// 背景ムービーファイル
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4ファイル", "*.mp4"));
			}

			// 初期ディレクトリをホームに設定
			//fc.setInitialDirectory(new File(System.getProperty("user.home")));
			fc.setInitialDirectory(null);

			// ファイル選択
			File file = fc.showOpenDialog(paneRoot.getScene().getWindow());
			if (file != null) {
				if (evt.getSource() == btnBackgroundImageChooser) {
					// 背景イメージファイル
					txtBackgroundImageFile.setText(file.getAbsolutePath());
				} else if (evt.getSource() == btnBackgroundMovieChooser) {
					// 背景ムービーファイル
					txtBackgroundMovieFile.setText(file.getAbsolutePath());
				}
			} else {

			}

		} catch (Exception e) {
			_logger.error("*** System Error!! ***", e);
		}
	}

	/**
	 * フォルダ参照ボタン押下処理
	 * @param evt
	 */
	@FXML
	protected void onFolderChooser(ActionEvent evt) {
		try {
			DirectoryChooser dc = new DirectoryChooser();

			dc.setTitle("ディレクトリ選択");
			// 初期ディレクトリをホームに設定
			//dc.setInitialDirectory(new File(System.getProperty("user.home")));
			dc.setInitialDirectory(null);

			// ディレクトリ選択
			File file = dc.showDialog(paneRoot.getScene().getWindow());
			if (file != null) {
				// キャラクター画像フォルダ
				txtCharacterImageFolder.setText(file.getAbsolutePath());
			}
		} catch (Exception e) {
			_logger.error("*** System Error!! ***", e);
		}
	}

	/**
	 * 処理ボタン押下処理
	 * @param evt
	 */
	@FXML
	protected void onCommandButton(ActionEvent evt) {
		try {
			int ret = 0;

			if (evt.getSource() == btnSaveAndStart || evt.getSource() == btnSaveAndClose) {
				// 入力チェック
				if (!inputCheck()) {
					return;
				}
				// 設定値をプロパティファイルに保存
				saveProperties();
			}

			if (evt.getSource() == btnSaveAndStart) {
				ret = 1;
			}

			// 終了
			Platform.exit();
			System.exit(ret);

		} catch (Exception e) {
			_logger.error("*** System Error!! ***", e);
		}
	}

	/**
	 * 入力チェック
	 * @return
	 */
	private boolean inputCheck() {

		//--------------------------------
		// 必須チェック
		//--------------------------------
		if (!chkRequired(txtDisplayNo, "スクリーン表示ディスプレイ番号")) {
			return false;
		}

		if (!chkRequired(txtScreenSizeWidth, "デフォルト画面サイズ（幅）")) {
			return false;
		}

		if (!chkRequired(txtScreenSizeHeight, "デフォルト画面サイズ（高さ）")) {
			return false;
		}

		if (!chkRequired(txtBackgroundImageFile, "背景イメージファイル")) {
			return false;
		}

		if (!chkRequired(txtBackgroundMovieFile, "背景ムービーファイル")) {
			return false;
		}

		if (!chkRequired(txtCharacterImageFolder, "キャラクター画像フォルダ")) {
			return false;
		}

		if (!chkRequired(txtMonitoringInterval, "キャラクター画像取り込み監視間隔")) {
			return false;
		}

		if (!chkRequired(txtDefaultImageWidth, "キャラクター画像デフォルト幅")) {
			return false;
		}

		if (!chkRequired(txtDefaultAnimationSpeed, "デフォルトアニメーション速度")) {
			return false;
		}

		//--------------------------------
		// ファイルパスの疎通チェック
		//--------------------------------
		if (!chkFileExists(txtBackgroundImageFile, "背景イメージファイル", false)) {
			return false;
		}

		if (!chkFileExists(txtBackgroundMovieFile, "背景ムービーファイル", false)) {
			return false;
		}

		if (!chkFileExists(txtCharacterImageFolder, "キャラクター画像フォルダ", true)) {
			return false;
		}

		return true;
	}

	/**
	 * 設定値を画面に表示
	 */
	private void dispProperties() {
		// フルスクリーンモード
		if ("1".equals(_properties.getProperty("full_screen"))) {
			rdoFullScreenModeOn.setSelected(true);
			rdoFullScreenModeOff.setSelected(false);
		} else {
			rdoFullScreenModeOn.setSelected(false);
			rdoFullScreenModeOff.setSelected(true);
		}

		// ディスプレイ番号
		txtDisplayNo.setText(_properties.getProperty("display_no"));

		// デフォルト画面サイズ
		String[] screenSize = _properties.getProperty("screen_size").split(",");
		txtScreenSizeWidth.setText(screenSize[0]);
		txtScreenSizeHeight.setText(screenSize[1]);

		// 背景モード
		if ("1".equals(_properties.getProperty("background_mode"))) {
			rdoBackgroundModeMovie.setSelected(true);
			rdoBackgroundModeImage.setSelected(false);
		} else {
			rdoBackgroundModeMovie.setSelected(false);
			rdoBackgroundModeImage.setSelected(true);
		}

		// 背景イメージファイル
		txtBackgroundImageFile.setText(_properties.getProperty("file_background_image"));

		// 背景ムービーファイル
		txtBackgroundMovieFile.setText(_properties.getProperty("file_background_movie"));

		// キャラクター画像フォルダ
		txtCharacterImageFolder.setText(_properties.getProperty("dir_animated_image"));

		// キャラクター画像取り込み監視間隔
		Long interval = Long.parseLong(_properties.getProperty("dir_monitoring_interval")) / 1000;
		txtMonitoringInterval.setText(String.valueOf(interval));

		// キャラクター画像デフォルト幅
		txtDefaultImageWidth.setText(_properties.getProperty("default_image_width"));

		// デフォルトアニメーション速度
		txtDefaultAnimationSpeed.setText(_properties.getProperty("default_animation_speed"));

	}

	/**
	 * 設定値をプロパティファイルに保存
	 */
	private boolean saveProperties() {

		boolean ret = true;
		try {
			// フルスクリーンモード
			if (rdoFullScreenModeOn.isSelected()) {
				_properties.setProperty("full_screen", "1");
			} else {
				_properties.setProperty("full_screen", "0");
			}

			// ディスプレイ番号
			_properties.setProperty("display_no", txtDisplayNo.getText());

			// デフォルト画面サイズ
			_properties.setProperty("screen_size", txtScreenSizeWidth.getText() + "," + txtScreenSizeHeight.getText());

			// 背景モード
			if (rdoBackgroundModeMovie.isSelected()) {
				_properties.setProperty("background_mode", "1");
			} else {
				_properties.setProperty("background_mode", "0");
			}

			// 背景イメージファイル
			_properties.setProperty("file_background_image", txtBackgroundImageFile.getText());

			// 背景ムービーファイル
			_properties.setProperty("file_background_movie", txtBackgroundMovieFile.getText());

			// キャラクター画像フォルダ
			_properties.setProperty("dir_animated_image", txtCharacterImageFolder.getText());

			// キャラクター画像取り込み監視間隔
			Long interval = Long.parseLong(txtMonitoringInterval.getText()) * 1000;
			_properties.setProperty("dir_monitoring_interval", String.valueOf(interval));

			// キャラクター画像デフォルト幅
			_properties.setProperty("default_image_width", txtDefaultImageWidth.getText());

			// デフォルトアニメーション速度
			_properties.setProperty("default_animation_speed", txtDefaultAnimationSpeed.getText());

			// 設定値を保存
			_properties.store((new OutputStreamWriter(new FileOutputStream("conf/processing.properties"), "UTF-8")), "MyColoring Configuration");

		} catch (FileNotFoundException e) {
			ret = false;
			_logger.error("*** System Error!! ***", e);
		} catch (IOException e) {
			ret = false;
			_logger.error("*** System Error!! ***", e);
		}

		return ret;

	}

	/**
	 * 数値入力制御
	 * @param tf
	 * @param maxLength
	 * @param decimalPoint
	 */
	private void addNumericTextFilter(final TextField tf, final int maxLength, boolean decimalPoint) {
		final UnaryOperator<TextFormatter.Change> filter = (TextFormatter.Change change) -> {
			if (change.getControlNewText().length() > maxLength) {
				return null;
			}

			if (change.isReplaced()) {
				if (change.getText().matches("[^0-9]")) {
					change.setText(change.getControlText().substring(change.getRangeStart(), change.getRangeEnd()));
				}
			}

			if (change.isAdded()) {
				if (decimalPoint) {
					if (change.getControlText().contains(".")) {
						if (change.getText().matches("[^0-9]")) {
							change.setText("");
						}
					} else if (change.getText().matches("[^0-9.]")) {
						change.setText("");
					}
				} else {
					if (change.getText().matches("[^0-9]")) {
						change.setText("");
					}
				}
			}
			return change;
		};

		tf.setTextFormatter(new TextFormatter<Number>(filter));

	}

	/**
	 * 必須チェック
	 * @param tf
	 * @param fieldName
	 * @return
	 */
	private boolean chkRequired(final TextField tf, String fieldName) {
		if (StringUtils.isEmpty(tf.getText())) {
			showErrorMessageDialog(String.format("%s を入力してください", fieldName));
			tf.requestFocus();
			return false;
		}

		return true;
	}

	/**
	 * ファイルパスの疎通チェック
	 * @param tf
	 * @param fieldName
	 * @param isDirectory
	 * @return
	 */
	private boolean chkFileExists(final TextField tf, String fieldName, boolean isDirectory) {
		File file = new File(tf.getText());
		if (!file.exists()) {
			showErrorMessageDialog(String.format("%s が存在しません", fieldName));
			tf.requestFocus();
			return false;
		}
		if (isDirectory) {
			if (!file.isDirectory()) {
				showErrorMessageDialog(String.format("%s がフォルダではありません", fieldName));
				tf.requestFocus();
				return false;
			}
		}

		if (StringUtils.isEmpty(tf.getText())) {
		}

		return true;
	}

	/**
	 * メッセージ表示
	 * @param msg
	 */
	private void showMessageDialog(String msg, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setHeaderText(msg);
		alert.showAndWait();
	}

	/**
	 * エラーメッセージ表示
	 * @param msg
	 */
	private void showErrorMessageDialog(String msg) {
		showMessageDialog(msg, AlertType.ERROR);
	}

}
