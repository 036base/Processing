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
 * Processingメインクラス
 *
 * @author
 *
 */
public class MainSketch3D extends PApplet {

	private final Logger _logger = LogManager.getLogger();

	private final Properties _properties = new Properties();

	private String _backgroundMode;	// 背景設定
	private PImage _backgroundImg;		// 背景イメージ
	private Movie _backgroundMov;		// 背景ムービー
	private int _maxImageCount;		// 最大表示数
	private int _defaultImageWidth;	// 画像の初期幅
	private float _animationSpeed;	// デフォルトアニメーション速度

	private  List<Character> _characterList;

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
			initProc();

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

			// 画面のタイトル、リサイズ許可設定
			surface.setTitle("");
			surface.setResizable(true);

			// 画像フォルダから拡張子が「.png」のファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(_properties.getProperty("dir_animated_image")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());

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


			if (_characterList.size() > _maxImageCount) {
				// UIDの昇順でソート
				_characterList.sort((p1, p2) -> Long.compare(p1._uid, p2._uid));
				// 古い順に削除
				_characterList.subList(0, _characterList.size() -_maxImageCount).clear();
				// ガベージ・コレクタを実行
				System.gc();
			}

			// Z座標位置の昇順でソートして描画
			_characterList.stream()
					.sorted(Comparator.comparing(Character::getPosZ))
					.forEach(a -> a.draw());

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
					// キャラクタークラスを作成・初期化してリストに追加
					_characterList.add(new Character(file));
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
		// 画像の初期幅
		_defaultImageWidth = Integer.parseInt(_properties.getProperty("default_image_width"));
		// デフォルトアニメーション速度
		_animationSpeed = Float.parseFloat(_properties.getProperty("default_animation_speed"));

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
	 * キャラクタークラス
	 */
	public class Character {
		private long _uid;					// ユニークID
		private PImage _imgF;				// イメージ前部
		private PImage _imgR;				// イメージ後部
		private PVector _pos;				// 座標
		private PVector _dir;				// 進行方向
		private float _speed = 1.0f;		// 速度

		private float _shakeAngle = 0;	// 後部振り角度
		private int _shakeDir = 1;			// 後部振り向き
		private float _maxAngle = 0;		// 後部振り最大角度
		private float _incAngle = 0;		// 後部振り増加角度
		private boolean _shaking = false;	// 後部振り

		// 周回情報
		private PVector _point;			// 位置
		private int _pointDir;				// 向き
		private float _theta;				// 角度
		private float _radius;			// 半径
		private float _radiusDir;			// 半径増減

		private float _tempCosTheta;

		public Character(File file) {
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
			_pos = new PVector(random(width * 0.1f, width * 0.9f), random(height * 0.1f, height * 0.9f), random(-500, 100));
			_dir = new PVector(1, 1, 1);
			if (ceil(random(2)) == 1) {
				_dir.x = -1;
			}
			if (ceil(random(2)) == 1) {
				_dir.y = -1;
			}
			if (ceil(random(2)) == 1) {
				_dir.z = -1;
			}
			_speed = _animationSpeed;

			_maxAngle = ceil(random(10, 30));
			_incAngle = ceil(random(1, 3));
			if (ceil(random(1, 100)) < 30) {
				_shaking = true;
			}

			_point = new PVector(0, 0, 0);
			_theta = ceil(random(1, 180) / 5);
			_radius = ceil(random(0, 5)) * 100;
			_radiusDir = 1;
			_pointDir = 1;
			if (ceil(random(2)) == 1) {
				_pointDir = -1;
			}

		}

		public void update() {

			int rand = ceil(random(0, 1000));

			if (rand <= 50) {
				// 速度をアップ
				_speed = _animationSpeed + ceil(rand / 100);
			} else if (rand >= 950) {
				// 速度を戻す
				_speed = _animationSpeed;
			}

			if ((_pos.x + _dir.x * _speed) < width * 0.1f || (_pos.x + _dir.x * _speed) > width * 0.9f) {
				// X軸進行方向を逆転
				_dir.x = -_dir.x;
			}

			if ((_pos.y + _dir.y * _speed < height * 0.1f) || (_pos.y + _dir.y * _speed) > height * 0.9f) {
				// Y軸進行方向を逆転
				_dir.y = -_dir.y;
			}

			if ((_pos.z + _dir.z * _speed) < -500 || (_pos.z + _dir.z * _speed) > 100) {
				// Z軸進行方向を逆転
				_dir.z = -_dir.z;
			}

			// 中心点の座標を更新
			_pos.x += _dir.x * (_speed * 0.2f);
			_pos.y += _dir.y * (_speed * 0.5f);
			_pos.z += _dir.z * (_speed * 0.5f);

			// 後部振り角度を更新
			if (_shakeAngle > _maxAngle || _shakeAngle < -_maxAngle) {
				_shakeDir *= -1;
			}
			_shakeAngle += _incAngle * _shakeDir;

			// 回転角度を更新
			_theta += 0.015 * _speed * _pointDir;

			// 周回点の座標を更新
			_point.x = _radius * cos(_theta);
			_point.z = _radius * sin(_theta);

			// １周ごとに半径を変更
			if (cos(_theta) > 0 && _tempCosTheta < 0) {
				_radius += 5 * _radiusDir;
				if (rand % 10 == 0) {
					// 逆回転
					_pointDir = -_pointDir;
				}
				if (rand % 150 == 0) {
					// 後部振り設定
					_maxAngle = ceil(random(10, 30));
					_incAngle = ceil(random(1, 3));
					_shakeAngle = 0;
				}
				if (rand < 200) {
					_shaking = !_shaking;
				}
			}
			_tempCosTheta = cos(_theta);

			if (_radius > 300 || _radius < 50) {
				_radiusDir = -_radiusDir;
			} else if (rand % 20 == 0) {
				_radiusDir = -_radiusDir;
			}

		}

		public void draw() {

			// 座標更新
			update();

			// zバッファの無効化
			hint(DISABLE_DEPTH_TEST);
			pushMatrix();

			translate(_pos.x + _point.x, _pos.y, _pos.z + _point.z);
			rotateY(-_theta);
			if (_pointDir > 0) {
				rotateY(90);
			} else {
				rotateY(-90);
			}

			// 前部の描画
			image(_imgF, 0, 0);

			pushMatrix();
			translate(_imgF.width, 0, 0);
			if (_shaking) {
				// 後部だけヒラヒラさせる
				rotateY(radians(_shakeAngle));
			}
			// 後部の描画
			image(_imgR, 0, 0);
			popMatrix();

			popMatrix();
			// zバッファの有効化
			hint(ENABLE_DEPTH_TEST);

		}

		/* ----- getter / setter -----*/
		public float getPosZ() {
			return _pos.z + _point.z;
		}


	}

}
