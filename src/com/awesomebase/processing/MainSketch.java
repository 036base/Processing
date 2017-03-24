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

/**
 * Processingメインクラス
 *
 * @author
 *
 */
public class MainSketch extends PApplet {

	private static Properties _properties;
	private static PImage _backgroundImg;
	private static List<AnimatedImage> _animatedImgList;

	private static int _width, _height;



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
			// TODO:可能であれば背景画像のサイズにする、もしくは設定ファイルに定義する
			size(1600, 900);
			//size(displayWidth, displayHeight);
			//fullScreen();
		} catch (Exception e) {
			exit();
			e.printStackTrace();
		}
	}

	/**
	 * Processing初期設定
	 */
	@Override
	public void setup() {
		try {
			// Processingメソッド以外で変数が読み取れないため保持
			_width = width;
			_height = height;

			// 背景画像の読み込み、描画
			_backgroundImg = loadImage(_properties.getProperty("file_background_image"));
			background(_backgroundImg);

			// アニメーション画像フォルダから拡張子が「.png」のファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(_properties.getProperty("dir_animated_image")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());

			// アニメーション画像をリストに追加
			_animatedImgList = new ArrayList<AnimatedImage>();
			for (final File file : fileList) {

				// 画像透過処理
				BufferedImage bimg = ImageUtil.Transparency(file, Color.WHITE);
				// 補正値により画像透過処理（★処理に時間がかかる...）
				//String correction_value = _properties.getProperty("correction_value");
				//bimg = ImageUtil.Transparency(file, Integer.parseInt(correction_value));


				Random rand = new Random();
				AnimatedImage anmImg = new AnimatedImage();

				// 初期座標を設定
				anmImg.setX((rand.nextInt(width - bimg.getWidth()) + 1));
				anmImg.setY((rand.nextInt(height - bimg.getHeight()) + 1));

				// 進行方向を設定
				if (rand.nextBoolean()) {
					anmImg.setDirX(1);
				} else {
					anmImg.setDirX(-1);
				}
				if (rand.nextBoolean()) {
					anmImg.setDirY(1);
				} else {
					anmImg.setDirY(-1);
				}

				// アニメーション画像設定（※基本画像は左向き）
				if (anmImg.getDirX() > 0) {
					// 左右反転
					bimg = ImageUtil.FlipHorizontal(bimg);
				}
				anmImg.setImg(new PImage(bimg));

				_animatedImgList.add(anmImg);
			}
		} catch (Exception e) {
			exit();
			e.printStackTrace();
		}
	}

	/**
	 * Processing描画
	 */
	@Override
	public void draw() {
		try {
			// 背景画像を描画
			background(_backgroundImg);

			PImage pimg;
			WritableRaster wr;
			int x, y, dirX, dirY;
			double scale;
			for (int i = 0; i < _animatedImgList.size(); i++) {
				pimg = _animatedImgList.get(i).getImg();
				x = _animatedImgList.get(i).getX();
				y = _animatedImgList.get(i).getY();
				dirX = _animatedImgList.get(i).getDirX();
				dirY = _animatedImgList.get(i).getDirY();
				scale = _animatedImgList.get(i).getScale();

				x += dirX * Constants.ANIMATION_SPEED;
				y += dirY * Constants.ANIMATION_SPEED;

				if ((x < 0) || (x > width - pimg.width)) {
					// X軸進行方向を逆転
					dirX = -dirX;

					// 画像を左右反転
					BufferedImage bimgFH = ImageUtil.PImage2BImage(pimg);
					bimgFH = ImageUtil.FlipHorizontal(bimgFH);
//					pimg = new PImage(bimgFH);
					//TODO:Processingでメモリ消費を抑えてBufferedImageの内容をPImageにコピーする
					// 参考：http://junkato.jp/ja/blog/2013/01/28/processing-efficient-copy-from-bufferedimage-to-pimage/
					DataBufferInt dbi = new DataBufferInt(pimg.pixels, pimg.pixels.length);
					wr = Raster.createWritableRaster(bimgFH.getSampleModel(), dbi, new Point(0, 0));
					bimgFH.copyData(wr);
					pimg.updatePixels();
				}

				if ((y < 0) || (y > height - pimg.height)) {
					// Y軸進行方向を逆転
					dirY = -dirY;
				}

				// リサイズ前の画像を保持
				_animatedImgList.get(i).setImg(pimg.copy());

				// 画像リサイズ
				if (random(1000) < 15) {
					if (scale < Constants.MAX_SCALE) {
						scale = scale + 0.02;
					}
				} else if (random(1000) >= 995) {
					if (scale > Constants.MIN_SCALE) {
						scale = scale - 0.02;
					}
				}
				int reWidth = (int)(pimg.width * scale);
				int reHeight = (int)(pimg.height * scale);
				pimg.resize(reWidth , reHeight);


				// 画像の情報を保持
				_animatedImgList.get(i).setX(x);
				_animatedImgList.get(i).setY(y);
				_animatedImgList.get(i).setDirX(dirX);
				_animatedImgList.get(i).setDirY(dirY);
				_animatedImgList.get(i).setScale(scale);

				//TODO:描画順を変えて画像の重なり順が変えれたら。。。
				// 画像を描画
				image(pimg, x, y);

			}
		} catch (Exception e) {
			exit();
			e.printStackTrace();
		}
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

				// 画像透過処理
				BufferedImage bimg = ImageUtil.Transparency(file, Color.WHITE);

				Random rand = new Random();
				AnimatedImage anmImg = new AnimatedImage();

				// 初期座標を設定
				anmImg.setX((rand.nextInt(_width - bimg.getWidth()) + 1));
				anmImg.setY((rand.nextInt(_height - bimg.getHeight()) + 1));

				// 進行方向を設定
				if (rand.nextBoolean()) {
					anmImg.setDirX(1);
				} else {
					anmImg.setDirX(-1);
				}
				if (rand.nextBoolean()) {
					anmImg.setDirY(1);
				} else {
					anmImg.setDirY(-1);
				}

				// アニメーション画像設定（※基本画像は左向き）
				if (anmImg.getDirX() > 0) {
					// 左右反転
					bimg = ImageUtil.FlipHorizontal(bimg);
				}
				anmImg.setImg(new PImage(bimg));

				_animatedImgList.add(anmImg);

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
