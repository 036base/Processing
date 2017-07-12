package com.awesomebase.processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
 * Processing描画クラス（散歩）
 *
 * @author
 *
 */
public class SketchWalk extends PApplet {

	private final Logger _logger = LogManager.getLogger();

	private final Properties _properties = new Properties();

	private String _backgroundMode;	// 背景設定
	private PImage _backgroundImg;		// 背景イメージ
	private Movie _backgroundMov;		// 背景ムービー
	private int _maxImageCount;		// 最大表示数
	private int _defaultImageWidth;	// 画像の初期幅

	private  List<Character> _characterList;

	private  SimpleDateFormat _sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

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
			_properties.load((new InputStreamReader(new FileInputStream("conf/processing.properties"), "UTF-8")));

			// 初期処理
			if (initProc() == false) {
				exit();
			}

			if ("1".equals(_properties.getProperty("full_screen"))) {
				// フルスクリーンモードで表示
				int display = Integer.parseInt(_properties.getProperty("display_no"));
				fullScreen(P3D, display);
			} else {
				// デフォルト画面サイズで表示
				String[] screenSize = _properties.getProperty("screen_size").split(",");
				size(Integer.parseInt(screenSize[0]), Integer.parseInt(screenSize[1]), P3D);
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

			// 画面のタイトル
			surface.setTitle("");

			// 画像フォルダから拡張子が「.png」のファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(_properties.getProperty("dir_animated_image")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.falseFileFilter());

			// キャラクタークラスを作成・初期化してリストに追加
			_characterList = Collections.synchronizedList(new ArrayList<Character>());
			_characterList = fileList.stream().sorted(Comparator.comparing(File::getName)).map(file -> new Character(file)).collect(Collectors.toList());

			// 背景の設定
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

			// リサイズ許可設定
			surface.setResizable(true);

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

			synchronized (_characterList) {
				if (_characterList.size() > _maxImageCount) {
					// UIDの昇順でソート
					_characterList.sort((p1, p2) -> Long.compare(p1._uid, p2._uid));
					// 古い順に削除
					_characterList.subList(0, _characterList.size() -_maxImageCount).clear();
					// ガベージ・コレクタを実行
					System.gc();
				}

				// SIDの昇順でソート
				_characterList.sort((p1, p2) -> Long.compare(p1._sid, p2._sid));
				if (_characterList.size() == 1) {
					_characterList.get(0).draw(0);
				} else if (_characterList.size() > 1) {
					_characterList.get(0).draw(0);
					_characterList.get(1).draw(1);
				}
		    }

			if (_recording) {
				// フレームを保存する
				saveFrame("output/tga/######.tga");
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
		if (surface == null) {
			surface = initSurface();
		}
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
					synchronized (_characterList) {
						// キャラクタークラスを作成・初期化してリストに追加
						_characterList.add(new Character(file));
				    }
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
	 * @return
	 */
	private boolean initProc() {
		boolean ret = true;

		// 背景モード
		_backgroundMode = _properties.getProperty("background_mode");
		// 最大表示数
		_maxImageCount = Integer.parseInt(_properties.getProperty("max_image_count"));
		// 画像の初期幅
		_defaultImageWidth = Integer.parseInt(_properties.getProperty("default_image_width"));

		_logger.info("--- System Settings ---------------------------------------");
		_logger.info("full_screen             : " + _properties.getProperty("full_screen"));
		_logger.info("display_no              : " + _properties.getProperty("display_no"));
		_logger.info("screen_size             : " + _properties.getProperty("screen_size"));
		_logger.info("dir_animated_image      : " + _properties.getProperty("dir_animated_image"));
		_logger.info("dir_monitoring_interval : " + _properties.getProperty("dir_monitoring_interval"));
		_logger.info("background_mode         : " + _properties.getProperty("background_mode"));
		_logger.info("file_background_image   : " + _properties.getProperty("file_background_image"));
		_logger.info("file_background_movie   : " + _properties.getProperty("file_background_movie"));
		_logger.info("max_image_count         : " + _properties.getProperty("max_image_count"));
		_logger.info("default_image_width     : " + _properties.getProperty("default_image_width"));
		_logger.info("-----------------------------------------------------------");

		File chk;
		// 各ファイルパスの疎通チェック
		chk = new File(_properties.getProperty("dir_animated_image"));
		if (!chk.exists()) {
			ret = false;
			_logger.warn("Path not exists " + _properties.getProperty("dir_animated_image"));
		}
		if (!chk.isDirectory()) {
			ret = false;
			_logger.warn("Path not directory " + _properties.getProperty("dir_animated_image"));
		}
		chk = new File(_properties.getProperty("file_background_image"));
		if (!chk.exists()) {
			ret = false;
			_logger.warn("File not exists " + _properties.getProperty("file_background_image"));
		}
		chk = new File(_properties.getProperty("file_background_movie"));
		if (!chk.exists()) {
			ret = false;
			_logger.warn("File not exists " + _properties.getProperty("file_background_movie"));
		}

		return ret;
	}



	/**
	 * キャラクタークラス
	 */
	public class Character {
		private long _uid;					// ユニークID
		private long _sid;					// ソートID
		private PImage _img;				// イメージ
		private PVector _pos;				// 座標
		private Float _interval;			// 表示間隔
		private float _scale = 0.1f;		// 倍率
		private float _maxScale = 0.0f;	// 最大倍率

		public Character(File file) {
			_logger.info("Create image " + file.getName());

			// ユニークID
			_uid = Long.parseLong(_sdf.format(Calendar.getInstance().getTime()));
			// ソートID
			_sid = Long.parseLong(_sdf.format(Calendar.getInstance().getTime()));

			// 画像透過処理
			BufferedImage bimg = ImageUtil.Transparency(file);

			// PImage生成
			_img = new PImage(bimg);
			// デフォルトサイズに調整（※SketchWalkに関しては高さ基準で調整）
			_img.resize(0, _defaultImageWidth);

			// 初期設定
			_pos = new PVector(0, 0, 0);
			_interval = 60f;
			_scale = 1.0f;
			_maxScale = ceil(height / _img.height);
		}

		public boolean update(int index) {

			if (_pos.x == 0) {
				if (index == 0) {
					_pos.x = width * 0.3f - (_img.width / 2);
				} else {
					_pos.x = width * 0.7f - (_img.width / 2);
				}
			}
			if (frameCount % _interval == 0) {
				if (_scale >= _maxScale) {
					// 設定リセット
					_sid = Long.parseLong(_sdf.format(Calendar.getInstance().getTime()));
					_pos.x = 0;
					_interval = 60f;
					_scale = 1.0f;
					return false;
				}

				// 倍率更新
				_scale += 0.5f;
				if (_scale > _maxScale) {
					_scale = _maxScale;
				}

			}

			return true;
		}

		public void draw(int index) {

			// 座標更新
			if (!update(index)) {
				return;
			}

			// zバッファの無効化
			hint(DISABLE_DEPTH_TEST);
			pushMatrix();

			translate(_pos.x - (_img.width * _scale / 2), height / 2 - (_img.height * _scale / 2));

			// 拡大縮小
			scale(_scale);

			// 描画
			image(_img, 0, 0);

			popMatrix();

			// zバッファの有効化
			hint(ENABLE_DEPTH_TEST);
		}

		/* ----- getter / setter -----*/
		public float getScale() {
			return _scale;
		}

	}

}
