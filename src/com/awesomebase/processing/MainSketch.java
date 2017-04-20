package com.awesomebase.processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

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
import processing.video.Movie;

/**
 * Processingメインクラス
 *
 * @author
 *
 */
public class MainSketch extends PApplet {

	private Logger _logger = LogManager.getLogger();

	private Properties _properties;	// 設定ファイル

	private String _backgroundMode;	// 背景設定
	private PImage _backgroundImg;		// 背景イメージ
	private Movie _backgroundMov;		// 背景ムービー
	private int _maxImageCount;		// 最大表示数
	private int _defaultImageWidth;	// アニメーション画像の初期幅
	private int _animationSpeed;		// デフォルトアニメーション速度
	private float _maxImageScale;		// 最大倍率
	private float _minImageScale;		// 最小倍率

	private  List<Animation> _animatedImgList;


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
				fullScreen(P2D, display);
			} else {
				// デフォルト画面サイズで表示
				String[] scrennSize = _properties.getProperty("screen_size").split(",");
				size(Integer.parseInt(scrennSize[0]), Integer.parseInt(scrennSize[1]), P2D);
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
			_animatedImgList = Collections.synchronizedList(new ArrayList<Animation>());
			for (final File file : fileList) {
				if (file.canRead()) {
					// アニメーション画像クラスを作成・初期化してリストに追加
					_animatedImgList.add(new Animation(file));
				} else {
					_logger.warn("Could not read image file " + file.getName());
				}
			}
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

			if (_animatedImgList.size() > _maxImageCount) {
				// UIDの昇順でソート
				Collections.sort(_animatedImgList, new UidComparator());
				// 古い順に削除
				_animatedImgList.subList(0, _animatedImgList.size() -_maxImageCount).clear();
			}

			// 倍率の昇順でソート（大きいものが手前にくるように）
			Collections.sort(_animatedImgList, new ScaleComparator());

			// アニメーション描画処理
			for (int i = 0; i < _animatedImgList.size(); i++) {
				_animatedImgList.get(i).update();
				_animatedImgList.get(i).draw();
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
		// 最大倍率
		_maxImageScale = Float.parseFloat(_properties.getProperty("max_image_scale"));
		// 最小倍率
		_minImageScale = Float.parseFloat(_properties.getProperty("min_image_scale"));

		_logger.info("--- System Settings ---------------------------------------");
		_logger.info("full_screen             : " + _properties.getProperty("background_mode"));
		_logger.info("display_no              : " + _properties.getProperty("display_no"));
		_logger.info("screen_size             : " + _properties.getProperty("screen_size"));
		_logger.info("dir_animated_image      : " + _properties.getProperty("dir_animated_image"));
		_logger.info("dir_monitoring_interval : " + _properties.getProperty("dir_monitoring_interval"));
		_logger.info("background_mode         : " + _properties.getProperty("background_mode"));
		_logger.info("file_background_image   : " + _properties.getProperty("file_background_image"));
		_logger.info("file_background_movie   : " + _properties.getProperty("file_background_movie"));
		_logger.info("default_image_width     : " + _properties.getProperty("default_image_width"));
		_logger.info("max_image_count         : " + _properties.getProperty("max_image_count"));
		_logger.info("max_image_scale         : " + _properties.getProperty("max_image_scale"));
		_logger.info("min_image_scale         : " + _properties.getProperty("min_image_scale"));
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
		private long _uid;				// ユニークID
		private PImage _img;			// アニメーションイメージ
		private int _x = 0;			// X座標
		private int _y = 0;			// Y座標
		private int _dirX = 1;			// X軸進行方向
		private int _dirY = 1;			// Y軸進行方向
		private int _speed = 1;		// 速度
		private float _scale = 1.0f;	// 倍率

		public Animation(File file) {
			_logger.info("Create image " + file.getName());

			// ユニークID
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			_uid = Long.parseLong(sdf.format(Calendar.getInstance().getTime()));

			// 画像透過処理
			BufferedImage bimg = ImageUtil.Transparency(file);

			// PImage生成
			_img = new PImage(bimg);
			// デフォルトサイズに調整
			_img.resize(_defaultImageWidth, 0);

			// 初期座標を設定
			_x = (int) random(_img.width, width - _img.width);
			_y = (int) random(_img.height, height - _img.height);

			// 進行方向を設定
			_dirX = 1;
			_dirY = 1;
			if (random(2) > 1) {
				_dirX *= -1;
			}
			if (random(2) > 1) {
				_dirY *= -1;
			}

			// アニメーション速度
			_speed = _animationSpeed;

		}

		public void update() {

			int rand = (int) random(1, 1000);

			if (rand <= 50) {
				// 速度をアップ
				_speed = _animationSpeed + ceil(rand / 100);
			} else if (rand >= 950) {
				// 速度を戻す
				_speed = _animationSpeed;
			}

			if (((_x + _dirX * _speed) < 0) || ((_x + _dirX * _speed) > width - _img.width)) {
				// X軸進行方向を逆転
				_dirX = -_dirX;
			} else {
				// ランダムに方向転換
				if (rand >= 1 && rand <= 5) {
					// X軸進行方向を逆転
					_dirX = -_dirX;
				}
			}

			if (((_y + _dirY * _speed) < 0) || ((_y + _dirY * _speed) > height - _img.height)) {
				// Y軸進行方向を逆転
				_dirY = -_dirY;
			} else {
				// ランダムに方向転換
				if (rand >= 11 && rand <= 15) {
					// Y軸進行方向を逆転
					_dirY = -_dirY;
				}
			}

			_x += _dirX * _speed;
			_y += _dirY * _speed;

		}

		public void draw() {

			pushMatrix();

			// 画像中央を回転の中心にする
			translate(_x + _img.width / 2, _y + _img.height / 2);
			// 進行方向に画像が向くように回転する
			float deg = 45 * _dirX * _dirY;
			rotate(radians(deg));

			if (_dirX > 0) {
				// 左右反転（※基本画像は左向き）
				scale(-1, 1);
			}

			// 回転の中心が画像中央なので、画像描画原点も画像中央にする
			// こうすると、(0,0)に配置すれば期待した位置に画像が置ける
			// これをしないと、image()命令で配置する座標計算が面倒になる
			imageMode(CENTER);

			// 拡大縮小
			_scale += (0.01f * _dirX * _dirY);
			if (_scale > _maxImageScale) {
				_scale = _maxImageScale;
			}
			if (_scale < _minImageScale) {
				_scale = _minImageScale;
			}
			scale(_scale);

			// 画像を描画
			image(_img, 0, 0);

			// 画像描画原点を元（画像の左上隅）に戻す
			imageMode(CORNER);

			popMatrix();

		}

	}

	/**
	 * ユニークIDによるソート用比較クラス
	 */
	public class UidComparator implements Comparator<Animation> {
		@Override
		public int compare(Animation p1, Animation p2) {
			return Long.compare(p1._uid, p2._uid);

		}
	}

	/**
	 * 倍率によるソート用比較クラス
	 */
	public class ScaleComparator implements Comparator<Animation> {
		@Override
		public int compare(Animation p1, Animation p2) {
			return Float.compare(p1._scale, p2._scale);

		}
	}

}
