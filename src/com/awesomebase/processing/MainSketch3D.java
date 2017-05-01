package com.awesomebase.processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Movie;

/**
 * Processingメインクラス
 *
 * @author
 *
 */
public class MainSketch3D extends PApplet {

	private Logger _logger = LogManager.getLogger();

	private Properties _properties;	// 設定ファイル

	private String _backgroundMode;	// 背景設定
	private PImage _backgroundImg;		// 背景イメージ
	private Movie _backgroundMov;		// 背景ムービー
	private int _maxImageCount;		// 最大表示数
	private int _defaultImageWidth;	// アニメーション画像の初期幅
	private int _animationSpeed;		// デフォルトアニメーション速度
	private  List<Animation> _animatedImgList;

	private boolean _recording = false;


	public static void main(String[] args) {
		Logger logger = LogManager.getLogger();
		try {
			String className = Thread.currentThread().getStackTrace()[1].getClassName();

			logger.info("{} start...", className);

			// Processing起動
			PApplet.main(className);

		} catch (Exception e) {
			logger.error("*** System Error!! ***", e);
		}
	}

	/**
	 * Processing設定
	 */
	@Override
	public void settings() {
		try {
			_logger.info("Settings...");

			// 設定ファイル読み込み
			_properties = new Properties();
			InputStream inputStream = new FileInputStream("processing.properties");
			_properties.load(inputStream);
			inputStream.close();

			// 初期処理
			initProc();


			if ("1".equals(_properties.getProperty("full_screen"))) {
				// フルスクリーンモードで表示
				int display = Integer.parseInt(_properties.getProperty("display_no"));
				fullScreen(P3D, display);
			} else {
				// デフォルト画面サイズで表示
				String[] scrennSize = _properties.getProperty("screen_size").split(",");
				size(Integer.parseInt(scrennSize[0]), Integer.parseInt(scrennSize[1]), P3D);
			}

			// アンチエイリアス無効
			noSmooth();

			// ファイル監視
			fileMonitor();

		} catch (Exception e) {
			_logger.error("*** System Error!! ***", e);
			exit();
		}
	}

	/**
	 * Processing初期設定
	 */
	@Override
	public void setup() {
		try {
			_logger.info("Setup...");
			hint(ENABLE_DEPTH_SORT);

			// 画面のタイトル、リサイズ許可設定
			surface.setTitle("");
			surface.setResizable(true);

			if ("0".equals(_backgroundMode)) {
				// 背景画像の読み込み
				_backgroundImg = loadImage(_properties.getProperty("file_background_image"));
				if (_backgroundImg == null) {
					_logger.error("Could not load image file " + _properties.getProperty("file_background_image"));
					exit();
				}
			} else if ("1".equals(_backgroundMode)) {
				// 背景動画の読み込み、再生設定
				_backgroundMov = new Movie(this, _properties.getProperty("file_background_movie"));
				_backgroundMov.loop();	// ループ再生
				_backgroundMov.play();	// 再生
			} else {
				// 背景なし
			}

			// アニメーション画像フォルダから拡張子が「.png」のファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(_properties.getProperty("dir_animated_image")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());

			// アニメーション画像クラスを作成・初期化してリストに追加
			_animatedImgList = Collections.synchronizedList(new ArrayList<Animation>());
			_animatedImgList = fileList.stream().map(file -> new Animation(file)).collect(Collectors.toList());

		} catch (Exception e) {
			_logger.error("*** System Error!! ***", e);
			exit();
		}
	}

	/**
	 * Processing描画
	 */
	@Override
	public void draw() {
		try {
			// zバッファの無効化
			hint(DISABLE_DEPTH_TEST);
			if ("0".equals(_backgroundMode)) {
				// 背景画像を描画
				image(_backgroundImg, 0, 0, width, height);
			} else if ("1".equals(_backgroundMode)) {
				// 背景動画を表示
				image(_backgroundMov, 0, 0, width, height);
			} else {
				// 背景なし
				background(0);
			}
			// zバッファの有効化
			hint(ENABLE_DEPTH_TEST);


			if (_animatedImgList.size() > _maxImageCount) {
				// UIDの昇順でソート
				_animatedImgList.sort((p1, p2) -> Long.compare(p1._uid, p2._uid));
				// 古い順に削除
				_animatedImgList.subList(0, _animatedImgList.size() -_maxImageCount).clear();
			}

			// アニメーション描画処理
			_animatedImgList.forEach(a -> a.draw());

			if (_recording) {
				// フレームを保存する
				saveFrame("ffmpeg/tga/######.tga");
				noStroke();
				fill(200, 0, 0);
				ellipse(15, 15, 20, 20);
			}

		} catch (Exception e) {
			_logger.error("***** System Error!! *****", e);
			exit();
		}
	}

	/**
	 * Processing exit
	 */
	@Override
	public void exit() {
		_logger.info("Exit...");
		super.exit();
	}

	/**
	 * Processing dispose
	 */
	@Override
	public void dispose() {
		_logger.info("Dispose...");
		super.dispose();
	}

	/**
	 * Processing KeyPressed
	 */
	@Override
	public void keyPressed() {
		switch (key) {
		case ENTER:
		case RETURN:
			if (!_recording) {
				// フレーム保存開始
				_recording = true;
			} else {
				// フレーム保存停止
				_recording = false;
			}
			break;
		case BACKSPACE:
			break;
		case TAB:
			break;
		case DELETE:
			break;
		case ESC:
			// ESCキーで終了
			exit();
			break;
		case ' ':
			break;
		default:
			break;
		}
	}

	/**
	 * movieEvent
	 *
	 * @param m
	 */
	public void movieEvent(Movie m) {
		m.read();
	}


	/**
	 * ファイル監視
	 *
	 * @throws Exception
	 */
	private  void fileMonitor() throws Exception {
		long interval = Long.parseLong(_properties.getProperty("dir_monitoring_interval"));
		// Monitorの生成。監視間隔はミリ秒。
		FileAlterationMonitor monitor = new FileAlterationMonitor(interval);
		// Observerの生成。監視するディレクトリを指定。
		File dir = new File(_properties.getProperty("dir_animated_image"));
		FileAlterationObserver observer = new FileAlterationObserver(dir);

		// Listenerを生成して監視する内容を実装
		FileAlterationListener listener = new FileAlterationListenerAdaptor() {
			@Override
			public void onFileCreate(File file) {
				if (file.canRead() && file.getPath().endsWith(".png")) {
					// アニメーション画像クラスを作成・初期化してリストに追加
					_animatedImgList.add(new Animation(file));
				} else {
					_logger.warn("Could not read image file or not 'PNG' file " + file.getName());
				}
			}
		};
		// ObserverにListerを登録
		observer.addListener(listener);
		// MonitorにObserverを登録
		monitor.addObserver(observer);
		// Monitorの起動
		monitor.start();
		_logger.info("File monitoring started...");
	}

	/**
	 * 初期処理
	 */
	private  void initProc() {

		// 背景モード
		_backgroundMode = _properties.getProperty("background_mode");
		// 最大表示数
		_maxImageCount = Integer.parseInt(_properties.getProperty("max_image_count"));
		// アニメーション画像の初期幅
		_defaultImageWidth = Integer.parseInt(_properties.getProperty("default_image_width"));
		// デフォルトアニメーション速度
		_animationSpeed = Integer.parseInt(_properties.getProperty("default_animation_speed"));

		_logger.info("--- System Settings ---------------------------------------");
		_logger.info("full_screen             : " + _properties.getProperty("background_mode"));
		_logger.info("display_no              : " + _properties.getProperty("display_no"));
		_logger.info("screen_size             : " + _properties.getProperty("screen_size"));
		_logger.info("dir_animated_image      : " + _properties.getProperty("dir_animated_image"));
		_logger.info("dir_monitoring_interval : " + _properties.getProperty("dir_monitoring_interval"));
		_logger.info("background_mode         : " + _properties.getProperty("background_mode"));
		_logger.info("file_background_image   : " + _properties.getProperty("file_background_image"));
		_logger.info("file_background_movie   : " + _properties.getProperty("file_background_movie"));
		_logger.info("max_image_count         : " + _properties.getProperty("max_image_count"));
		_logger.info("default_image_width     : " + _properties.getProperty("default_image_width"));
		_logger.info("default_animation_speed : " + _properties.getProperty("default_animation_speed"));
		_logger.info("-----------------------------------------------------------");

		File chk;
		// 各ファイルパスの疎通チェック
		chk = new File(_properties.getProperty("dir_animated_image"));
		if (!chk.exists()) {
			_logger.warn("Path not exists " + _properties.getProperty("dir_animated_image"));
		}
		if (!chk.isDirectory()) {
			_logger.warn("Path not directory " + _properties.getProperty("dir_animated_image"));
		}
		chk = new File(_properties.getProperty("file_background_image"));
		if (!chk.exists()) {
			_logger.warn("File not exists " + _properties.getProperty("file_background_image"));
		}
		chk = new File(_properties.getProperty("file_background_movie"));
		if (!chk.exists()) {
			_logger.warn("File not exists " + _properties.getProperty("file_background_movie"));
		}

	}



	/**
	 * アニメーション画像クラス
	 */
	public class Animation {
		private long _uid;					// ユニークID
		private PImage _imgF;				// イメージ前部
		private PImage _imgR;				// イメージ後部
		private PVector _pos;				// 現在座標
		private PVector _des;				// 目標座標
		private float _easing = 0.01f;	// 近づく速度

		private float _shakeAngle = 0;	// 後部振り角度
		private int _shakeDir = 1;			// 後部振り向き
		private float _maxAngle = 0;		// 後部振り最大角度
		private float _incAngle = 0;		// 後部振り増加角度


		public Animation(File file) {
			_logger.info("Create image " + file.getName());

			// ユニークID
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			_uid = Long.parseLong(sdf.format(Calendar.getInstance().getTime()));

			// 画像透過処理
			BufferedImage bimg = ImageUtil.Transparency(file);

			// PImage生成
			PImage pimg = new PImage(bimg);
			// デフォルトサイズに調整
			pimg.resize(_defaultImageWidth, 0);

			// 画像を前後に分ける
			_imgF = pimg.get(0, 0, floor(pimg.width / 2), pimg.height);
			_imgR = pimg.get(ceil(pimg.width / 2), 0, pimg.width, pimg.height);

			// 初期設定
			_pos = new PVector(random(0, width), random(0, height), 0);
			_des = new PVector(random(-100, width + 100), random(-100, height + 100), random(-900, 600));
			_easing = random(_animationSpeed * 0.001f, _animationSpeed * 0.02f);
			_maxAngle = ceil(random(10, 20));
			_incAngle = ceil(random(1, 3));


		}

		public void update() {

			_pos.x += _easing * (_des.x - _pos.x);
			_pos.y += _easing * (_des.y - _pos.y);
			_pos.z += _easing * (_des.z - _pos.z);

			// 目標座標に近づいたら目標座標を変更
			float distance = dist(_pos.x, _pos.y, _pos.z, _des.x, _des.y, _des.z);
			if (distance < 20) {
				_des.x = random(-100, width + 100);
				_des.y = random(-100, height + 100);
				_des.z = random(-900, 600);

				_easing = random(_animationSpeed * 0.001f, _animationSpeed * 0.02f);
				_maxAngle = ceil(random(10, 20));
				_incAngle = ceil(random(1, 3));
			}

			if (_shakeAngle > _maxAngle || _shakeAngle < -_maxAngle) {
				_shakeDir *= -1;
			}
			_shakeAngle += _incAngle * _shakeDir;
		}

		public void draw() {

			pushMatrix();
			translate(_pos.x, _pos.y, _pos.z);

			// X軸とZ軸進行方向を向く
			float angleY = atan2(_des.x - _pos.x, _des.z - _pos.z);
			rotateY(radians(90));
			rotateY(angleY);

			// Y軸進行方向を向く
			float angleZ = atan2(_des.y - _pos.y, _des.x - _pos.x);
			if (_pos.x > _des.x) {
				angleZ = atan2(_des.y - _pos.y, _pos.x - _des.x);
			}
			rotate(-angleZ);

			// 前部の描画
			image(_imgF, 0, 0);

			pushMatrix();
			translate(_imgF.width, 0, 0);
			// 後部だけヒラヒラさせる
			rotateY(radians(_shakeAngle));
			// 後部の描画
			image(_imgR, 0, 0);
			popMatrix();

			popMatrix();

			// 座標更新
			update();
		}

	}

}
