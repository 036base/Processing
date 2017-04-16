package com.awesomebase.processing;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
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

//	private  List<AnimatedImage> _animatedImgList;
//	private  CollectionsComparator _comparator = new CollectionsComparator();
	private  List<Animation> _animatedImgList;


	public static void main(String[] args) {
		Logger logger = LogManager.getLogger();
		try {
			logger.info("Main start...");

			// Processing起動
			PApplet.main("com.awesomebase.processing.MainSketch");

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
			// 設定ファイル読み込み
			_properties = new Properties();
			InputStream inputStream = new FileInputStream("processing.properties");
			_properties.load(inputStream);
			inputStream.close();

			// 初期処理
			initProc();

			// ファイル監視
			fileMonitor();

			_logger.info("Settings...");

			if ("1".equals(_properties.getProperty("full_screen"))) {
				//----------------------------
				// フルスクリーンモードで表示
				//----------------------------
				int display = Integer.parseInt(_properties.getProperty("display_no"));
//				fullScreen(display);
				fullScreen(P2D,display);
			} else {
				//----------------------------
				// デフォルト画面サイズで表示
				//----------------------------
				String[] scrennSize = _properties.getProperty("screen_size").split(",");
//				size(Integer.parseInt(scrennSize[0]), Integer.parseInt(scrennSize[1]));
				size(Integer.parseInt(scrennSize[0]), Integer.parseInt(scrennSize[1]),P2D);
			}

			// アンチエイリアス無効
			noSmooth();

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
				// 背景画像の読み込み、リサイズ
				_backgroundImg = loadImage(_properties.getProperty("file_background_image"));
				if (_backgroundImg == null) {
					_logger.error("Could not load image file " + _properties.getProperty("file_background_image"));
					exit();
				}
				_backgroundImg.resize(width, height);
			} else if ("1".equals(_backgroundMode)) {
				// 背景画像の読み込み、リサイズ
				_backgroundMov = new Movie(this, _properties.getProperty("file_background_movie"));
				_backgroundMov.loop();	// ループ再生
				_backgroundMov.play();	// 再生
			} else {
				// 背景なし
			}

			// アニメーション画像フォルダから拡張子が「.png」のファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(_properties.getProperty("dir_animated_image")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());
//			_animatedImgList = Collections.synchronizedList(new ArrayList<AnimatedImage>());
//			for (final File file : fileList) {
//				if (file.canRead()) {
//					// アニメーション画像クラス作成・初期化
//					AnimatedImage aimg = createAnimatedImage(file);
//					// リストに追加
//					_animatedImgList.add(aimg);
//				} else {
//					_logger.warn("Could not read image file " + file.getName());
//				}
//			}
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
				// 背景画像をリサイズ、描画
				_backgroundImg.resize(width, height);
				image(_backgroundImg, 0, 0);
			} else if ("1".equals(_backgroundMode)) {
				 // 背景動画を表示
				 image(_backgroundMov, 0, 0, width, height);
			} else {
				// 背景なし
				background(0);
			}

//			PImage pimg;
//			WritableRaster wr;
//			int x, y, dirX, dirY, speed;
//			float scale;
//			boolean turn;
//
//			// 古い順に削除
//			if (_animatedImgList.size() > _maxImageCount) {
//				// UIDの昇順でソート
//				Collections.sort(_animatedImgList, _comparator.new UidComparator());
//				_animatedImgList.subList(0, _animatedImgList.size() -_maxImageCount).clear();
//			}
//
//			// 倍率の昇順でソート（大きいものが手前にくる）
//			Collections.sort(_animatedImgList, _comparator.new ScaleComparator());
//
//		    for (int i = 0; i < _animatedImgList.size(); i++) {
//		    	// 画像情報を取得
//				pimg = _animatedImgList.get(i).getImg();
//				x = _animatedImgList.get(i).getX();
//				y = _animatedImgList.get(i).getY();
//				dirX = _animatedImgList.get(i).getDirX();
//				dirY = _animatedImgList.get(i).getDirY();
//				scale = _animatedImgList.get(i).getScale();
//				speed = _animatedImgList.get(i).getSpeed();
//
//				int randomSpeed = randomInt(100) + 1;
//				if (randomSpeed <= 3) {
//					// 速度をアップ
//					speed = _animationSpeed + randomSpeed;
//				} else if (randomSpeed >= 90) {
//					// 速度を戻す
//					speed = _animationSpeed;
//				}
//
//				turn = false;
//				int randomDir = randomInt(1000);
//				if (((x + dirX * speed) < 0) || ((x + dirX * speed) > width - pimg.width)) {
//					// X軸進行方向を逆転
//					dirX = -dirX;
//					turn = true;
//				} else {
//					// ランダムに方向転換
//					if (randomDir >= 1 && randomDir <= 5) {
//						// X軸進行方向を逆転
//						dirX = -dirX;
//						turn = true;
//					}
//				}
//
//				if (((y + dirY * speed) < 0) || ((y + dirY * speed) > height - pimg.height)) {
//					// Y軸進行方向を逆転
//					dirY = -dirY;
//				} else {
//					// ランダムに方向転換
//					if (randomDir >= 11 && randomDir <= 15) {
//						// Y軸進行方向を逆転
//						dirY = -dirY;
//					}
//				}
//
//				x += dirX * speed;
//				y += dirY * speed;
//
//
//				if (turn) {
//					// 画像を左右反転
//					BufferedImage bimgFH = ImageUtil.PImage2BImage(pimg);
//					bimgFH = ImageUtil.FlipHorizontal(bimgFH);
//					DataBufferInt dbi = new DataBufferInt(pimg.pixels, pimg.pixels.length);
//					wr = Raster.createWritableRaster(bimgFH.getSampleModel(), dbi, new Point(0, 0));
//					bimgFH.copyData(wr);
//					pimg.updatePixels();
//				}
//
//				// リサイズ前の画像を保持
//				_animatedImgList.get(i).setImg(pimg.copy());
//
//
//				// 進行方向に画像が向くように回転する
//				pushMatrix();
//				// 画像中央を回転の中心にする
//				translate(x + pimg.width / 2, y + pimg.height / 2);
//				// 回転する
//				float deg = 45 * dirX * dirY;
//				rotate(radians(deg));
//
//				// 回転の中心が画像中央なので、画像描画原点も画像中央にする
//				// こうすると、(0,0)に配置すれば期待した位置に画像が置ける
//				// これをしないと、image()命令で配置する座標計算が面倒になる
//				imageMode(CENTER);
//
//				// 拡大縮小
//				scale += (0.01f * dirX * dirY);
//				if (scale > _maxImageScale) {
//					scale = _maxImageScale;
//				}
//				if (scale < _minImageScale) {
//					scale = _minImageScale;
//				}
//				scale(scale);
//
//				// 画像を描画
//				image(pimg, 0, 0);
//
//				// 画像描画原点を元（画像の左上隅）に戻す
//				imageMode(CORNER);
//
//				popMatrix();
//
//
//				// 画像情報を保持
//				_animatedImgList.get(i).setX(x);
//				_animatedImgList.get(i).setY(y);
//				_animatedImgList.get(i).setDirX(dirX);
//				_animatedImgList.get(i).setDirY(dirY);
//				_animatedImgList.get(i).setScale(scale);
//				_animatedImgList.get(i).setSpeed(speed);
//
//		    }

			if (_animatedImgList.size() > _maxImageCount) {
				// UIDの昇順でソート
				Collections.sort(_animatedImgList, new UidComparator());
				// 古い順に削除
				_animatedImgList.subList(0, _animatedImgList.size() -_maxImageCount).clear();
			}

			// 倍率の昇順でソート（大きいものが手前にくるように）
			Collections.sort(_animatedImgList, new ScaleComparator());

			for (int i = 0; i < _animatedImgList.size(); i++) {
				_animatedImgList.get(i).update();
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



//	/**
//	 * アニメーション画像クラス作成・初期化
//	 *
//	 * @param file
//	 * @return
//	 */
//	private  AnimatedImage createAnimatedImage(File file) {
//
//		_logger.info("Create animated image " + file.getName());
//
//		// 画像透過処理
//		BufferedImage bimg = ImageUtil.Transparency(file);
//		// 補正値により画像透過処理（★処理に時間がかかる...）
//		//String correction_value = _properties.getProperty("correction_value");
//		//bimg = ImageUtil.Transparency(file, Integer.parseInt(correction_value));
//
//
//		AnimatedImage aimg = new AnimatedImage();
//
//		// UID
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//		aimg.setUid(Long.parseLong(sdf.format( Calendar.getInstance().getTime())));
//
//		// 初期座標を設定
//		aimg.setX((randomInt(width - bimg.getWidth()) + 1));
//		aimg.setY((randomInt(height - bimg.getHeight()) + 1));
//
//		// 進行方向を設定
//		if (randomInt(2) == 1) {
//			aimg.setDirX(1);
//		} else {
//			aimg.setDirX(-1);
//		}
//		if (randomInt(2) == 0) {
//			aimg.setDirY(1);
//		} else {
//			aimg.setDirY(-1);
//		}
//
//		// アニメーション画像設定（※基本画像は左向き）
//		if (aimg.getDirX() > 0) {
//			// 左右反転
//			bimg = ImageUtil.FlipHorizontal(bimg);
//		}
//
//		PImage pimg = new PImage(bimg);
//
//		// デフォルトサイズに調整
//		pimg.resize(_defaultImageWidth, 0);
//
//		// アニメーション速度
//		aimg.setSpeed(_animationSpeed);
//
//		aimg.setImg(pimg);
//
//		return aimg;
//	}


//	/**
//	 * ランダム値取得
//	 */
//	private  int randomInt(int max) {
//
//		Random rand = new Random();
//		return rand.nextInt(max);
//
//	}


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
//					// アニメーション画像クラス作成・初期化
//					AnimatedImage aimg = createAnimatedImage(file);
//					// リストに追加
//					_animatedImgList.add(aimg);
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

		/**
		 * コンストラクタ
		 *
		 * @param file
		 */
		public Animation(File file) {
			_logger.info("Create image " + file.getName());

			// ユニークID
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			_uid = Long.parseLong(sdf.format(Calendar.getInstance().getTime()));

			// 画像透過処理
			BufferedImage bimg = ImageUtil.Transparency(file);

			// 初期座標を設定
			_x = (int) random(1, width - bimg.getWidth());
			_y = (int) random(1, height - bimg.getHeight());

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

			// アニメーション画像設定（※基本画像は左向き）
			if (_dirX > 0) {
				// 左右反転
				bimg = ImageUtil.FlipHorizontal(bimg);
			}

			// PImage生成
			PImage pimg = new PImage(bimg);
			// デフォルトサイズに調整
			pimg.resize(_defaultImageWidth, 0);
			_img = pimg;

		}

		/**
		 * アニメーション更新
		 */
		public void update() {

			int rand = (int) random(1, 1000);
			if (rand <= 50) {
				// 速度をアップ
				_speed = _animationSpeed + ceil(rand / 100);
			} else if (rand >= 950) {
				// 速度を戻す
				_speed = _animationSpeed;
			}

			boolean turn = false;
			if (((_x + _dirX * _speed) < 0) || ((_x + _dirX * _speed) > width - _img.width)) {
				// X軸進行方向を逆転
				_dirX = -_dirX;
				turn = true;
			} else {
				// ランダムに方向転換
				if (rand >= 1 && rand <= 5) {
					// X軸進行方向を逆転
					_dirX = -_dirX;
					turn = true;
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

			if (turn) {
				// 画像を左右反転
				BufferedImage bimgFH = ImageUtil.PImage2BImage(_img);
				bimgFH = ImageUtil.FlipHorizontal(bimgFH);
				DataBufferInt dbi = new DataBufferInt(_img.pixels, _img.pixels.length);
				WritableRaster wr = Raster.createWritableRaster(bimgFH.getSampleModel(), dbi, new Point(0, 0));
				bimgFH.copyData(wr);
				_img.updatePixels();
			}

			pushMatrix();

			// 画像中央を回転の中心にする
			translate(_x + _img.width / 2, _y + _img.height / 2);
			// 進行方向に画像が向くように回転する
			float deg = 45 * _dirX * _dirY;
			rotate(radians(deg));

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
