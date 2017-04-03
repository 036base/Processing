package com.awesomebase.processing;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

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

	private static Properties _properties;	// 設定ファイル
	private static int _width, _height;		// 画面の幅、高さ

	private static int _backgroundMode;	// 背景設定
	private static PImage _backgroundImg;	// 背景イメージ
	private static Movie  _backgroundMov;	// 背景ムービー

	private static List<AnimatedImage> _animatedImgList;


	public static void main(String[] args) {
		try {

			// 設定ファイル読み込み
			loadProperties();

			// ファイル監視
			fileMonitor();

			// Processing起動
			PApplet.main("com.awesomebase.processing.MainSketch");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processing設定
	 */
	@Override
	public void settings() {
		try {
			if ("1".equals(_properties.getProperty("full_screen"))) {
				//----------------------------
				// フルスクリーンモードで表示
				//----------------------------
				int display = Integer.parseInt(_properties.getProperty("display"));
				fullScreen(display);
			} else {
				//----------------------------
				// デフォルト画面サイズで表示
				//----------------------------
				String[] scrennSize = _properties.getProperty("screen_size").split(",");
				size(Integer.parseInt(scrennSize[0]), Integer.parseInt(scrennSize[1]));
			}

			// 背景モード
			_backgroundMode = Integer.parseInt(_properties.getProperty("background_mode"));

			// Processingメソッド以外で変数が読み取れないため保持
			_width = width;
			_height = height;

		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	/**
	 * Processing初期設定
	 */
	@Override
	public void setup() {
		try {
			// 画面のタイトル、リサイズ許可設定
			surface.setTitle("");
			surface.setResizable(true);

			// Processingメソッド以外で変数が読み取れないため保持
			_width = width;
			_height = height;

			if (_backgroundMode == Constants.BACKGROUND_IMAGE) {
				// 背景画像の読み込み、リサイズ
				_backgroundImg = loadImage(_properties.getProperty("file_background_image"));
				_backgroundImg.resize(width, height);
			} else if (_backgroundMode == Constants.BACKGROUND_MOVIE) {
				// 背景画像の読み込み、リサイズ
				_backgroundMov = new Movie(this, _properties.getProperty("file_background_movie"));
				_backgroundMov.loop();	// ループ再生
				_backgroundMov.play();	// 再生
			} else {
				// 背景なし
			}

			// アニメーション画像フォルダから拡張子が「.png」のファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(_properties.getProperty("dir_animated_image")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());

			// アニメーション画像をリストに追加
			_animatedImgList = new ArrayList<AnimatedImage>();
			for (final File file : fileList) {
				// アニメーション画像クラス作成・初期化
				AnimatedImage aimg = createAnimatedImage(file);
				_animatedImgList.add(aimg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	/**
	 * Processing描画
	 */
	@Override
	public void draw() {
		try {
			// Processingメソッド以外で変数が読み取れないため保持
			_width = width;
			_height = height;

			if (_backgroundMode == Constants.BACKGROUND_IMAGE) {
				// 背景画像をリサイズ、描画
				_backgroundImg.resize(width, height);
				image(_backgroundImg, 0, 0);
			} else if (_backgroundMode == Constants.BACKGROUND_MOVIE) {
				 // 背景動画を表示
				 image(_backgroundMov, 0, 0, _width, _height);
			} else {
				// 背景なし
				background(0);
			}

			PImage pimg;
			WritableRaster wr;
			int x, y, dirX, dirY, speed;
			double scale;

			if (randomInt(1000) < 5) {
				// リストの順序をシャッフルして画像の重なりを変更する
				Collections.shuffle(_animatedImgList);
			}

		    for (int i = 0; i < _animatedImgList.size(); i++) {
				pimg = _animatedImgList.get(i).getImg();
				x = _animatedImgList.get(i).getX();
				y = _animatedImgList.get(i).getY();
				dirX = _animatedImgList.get(i).getDirX();
				dirY = _animatedImgList.get(i).getDirY();
				scale = _animatedImgList.get(i).getScale();
				speed = _animatedImgList.get(i).getSpeed();

				int randomSpeed = randomInt(100) + 1;
				if (randomSpeed <= 3) {
					// 速度をアップ
					speed = randomSpeed;
				} else if (randomSpeed >= 90) {
					// 速度を戻す
					speed = Constants.ANIMATION_SPEED;
				}

				x += dirX * speed;
				y += dirY * speed;


				boolean turn = false;
				int randomDir = randomInt(1000);
				// ランダムに方向転換
				if (randomDir < 5) {
					// X軸進行方向を逆転
					dirX = -dirX;
					turn  = true;
				} else if (randomDir >= 995) {
					// Y軸進行方向を逆転
					dirY = -dirY;
				}

				if ((x < 0) || (x > width - pimg.width)) {
					// X軸進行方向を逆転
					dirX = -dirX;
					turn = true;
				}

				if ((y < 0) || (y > height - pimg.height)) {
					// Y軸進行方向を逆転
					dirY = -dirY;
				}

				if (turn) {
					// 画像を左右反転
					BufferedImage bimgFH = ImageUtil.PImage2BImage(pimg);
					bimgFH = ImageUtil.FlipHorizontal(bimgFH);
					//pimg = new PImage(bimgFH);
					//TODO:Processingでメモリ消費を抑えてBufferedImageの内容をPImageにコピーする
					// 参考：http://junkato.jp/ja/blog/2013/01/28/processing-efficient-copy-from-bufferedimage-to-pimage/
					DataBufferInt dbi = new DataBufferInt(pimg.pixels, pimg.pixels.length);
					wr = Raster.createWritableRaster(bimgFH.getSampleModel(), dbi, new Point(0, 0));
					bimgFH.copyData(wr);
					pimg.updatePixels();
				}

				// リサイズ前の画像を保持
				_animatedImgList.get(i).setImg(pimg.copy());

				// 画像リサイズ
				int randomSize = randomInt(1000);
				if (randomSize < 10) {
					if (scale < Constants.MAX_SCALE) {
						scale = scale + 0.01;
					}
				} else if (randomSize >= 990) {
					if (scale > Constants.MIN_SCALE) {
						scale = scale - 0.01;
					}
				}
				int resize = (int)(pimg.width * scale);
				pimg.resize(resize , 0); // 0を指定すると自動で比率を計算してくれる


				// 画像の情報を保持
				_animatedImgList.get(i).setX(x);
				_animatedImgList.get(i).setY(y);
				_animatedImgList.get(i).setDirX(dirX);
				_animatedImgList.get(i).setDirY(dirY);
				_animatedImgList.get(i).setScale(scale);
				_animatedImgList.get(i).setSpeed(speed);

				// 画像を描画
				image(pimg, x, y);
			}

		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
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
	 * アニメーション画像クラス作成・初期化
	 *
	 * @param file
	 * @return
	 */
	private static AnimatedImage createAnimatedImage(File file) {

		// 画像透過処理
		BufferedImage bimg = ImageUtil.Transparency(file, Color.WHITE);
		// 補正値により画像透過処理（★処理に時間がかかる...）
		//String correction_value = _properties.getProperty("correction_value");
		//bimg = ImageUtil.Transparency(file, Integer.parseInt(correction_value));


		AnimatedImage aimg = new AnimatedImage();

		// 初期座標を設定
		aimg.setX((randomInt(_width - bimg.getWidth()) + 1));
		aimg.setY((randomInt(_height - bimg.getHeight()) + 1));

		// 進行方向を設定
		if (randomInt(2) == 1) {
			aimg.setDirX(1);
		} else {
			aimg.setDirX(-1);
		}
		if (randomInt(2) == 0) {
			aimg.setDirY(1);
		} else {
			aimg.setDirY(-1);
		}

		// アニメーション画像設定（※基本画像は左向き）
		if (aimg.getDirX() > 0) {
			// 左右反転
			bimg = ImageUtil.FlipHorizontal(bimg);
		}

		PImage pimg = new PImage(bimg);

		// デフォルトサイズに調整
		int defaultImageWidth = Integer.parseInt(_properties.getProperty("default_image_width"));
		pimg.resize(defaultImageWidth, 0);


		aimg.setImg(pimg);

		return aimg;
	}


	/**
	 * ランダム値取得
	 */
	private static int randomInt(int max) {

		Random rand = new Random();
		return rand.nextInt(max);

	}

	/**
	 * 設定ファイル読み込み
	 */
	private static void loadProperties() {
		// 設定ファイル読み込み
		_properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream("processing.properties");
			_properties.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ファイル監視
	 *
	 * @throws Exception
	 */
	private static void fileMonitor() throws Exception {
		// Monitorの生成。監視間隔はミリ秒。
		FileAlterationMonitor monitor = new FileAlterationMonitor(5000);

		// Observerの生成。監視するディレクトリを指定。
		File dir = new File(_properties.getProperty("dir_animated_image"));
		FileAlterationObserver observer = new FileAlterationObserver(dir);

		// Listenerを生成して監視する内容を実装
		FileAlterationListener listener = new FileAlterationListenerAdaptor() {
			public void onDirectoryChange(File directory) {
//				System.out.println("onDirectoryChange:" + directory.getName());
//				super.onDirectoryChange(directory);
			}

			@Override
			public void onDirectoryCreate(File directory) {
//				System.out.println("onDirectoryCreate:" + directory.getName());
//				super.onDirectoryCreate(directory);
			}

			@Override
			public void onDirectoryDelete(File directory) {
//				System.out.println("onDirectoryDelete:" + directory.getName());
//				super.onDirectoryDelete(directory);
			}

			@Override
			public void onFileChange(File file) {
//				System.out.println("onFileChange:" + file.getName());
//				super.onFileChange(file);
			}

			@Override
			public void onFileCreate(File file) {
//				System.out.println("onFileCreate:" + file.getName());
//				super.onFileCreate(file);

				// アニメーション画像クラス作成・初期化
				AnimatedImage aimg = createAnimatedImage(file);
				_animatedImgList.add(aimg);
			}

			@Override
			public void onFileDelete(File file) {
//				System.out.println("onFileDelete:" + file.getName());
//				super.onFileDelete(file);
			}

			@Override
			public void onStart(FileAlterationObserver observer) {
//				System.out.println("onStart:" + observer.toString());
//				super.onStart(observer);
			}

			@Override
			public void onStop(FileAlterationObserver observer) {
//				System.out.println("onStop:" + observer.toString());
//				super.onStop(observer);
			}
		};
		// ObserverにListerを登録
		observer.addListener(listener);
		// MonitorにObserverを登録
		monitor.addObserver(observer);
		// Monitorの起動
		monitor.start();
	}

}
