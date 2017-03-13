package com.awesomebase.processing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

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

	private PImage _backgroundImg;
	private List<AnimatedImage> _animatedImgList;

	private int ANIMATION_SPEED = 2;

	public static void main(String[] args) {

		try {
			// 設定ファイル読み込み
			_properties = new Properties();
			try {
				InputStream inputStream = new FileInputStream("processing.properties");
				_properties.load(inputStream);
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Processing起動
			PApplet.main("com.awesomebase.processing.MainSketch");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Processing設定
	 */
	public void settings() {
		size(1024, 593);
	}

	/**
	 * Processing初期設定
	 */
	public void setup() {
		// 背景画像の読み込み、描画
		_backgroundImg = loadImage(_properties.getProperty("file_background_image"));
		background(_backgroundImg);

		// アニメーション画像フォルダから拡張子が「.png」のファイルを取得
		final List<File> fileList = (List<File>) FileUtils.listFiles(new File(_properties.getProperty("dir_animated_image")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());

		// アニメーション画像をリストに追加
		_animatedImgList = new ArrayList<AnimatedImage>();
		for (final File file : fileList) {
			// 画像透過処理
			BufferedImage img = ImageUtil.Transparency(file, Color.WHITE);

			Random rand = new Random();

			// アニメーション画像の読み込み
			AnimatedImage anmImg = new AnimatedImage();
			anmImg.setImg(new PImage(img));

			// 乱数によって描画する初期座標を設定
			anmImg.setX(rand.nextInt(500) + 1);
			anmImg.setY(rand.nextInt(200) + 1);
			if (_animatedImgList.size() % 2 == 0) {
				anmImg.setDirX(-1);
				anmImg.setDirY(-1);
			} else {
				anmImg.setDirX(1);
				anmImg.setDirY(1);
			}
			_animatedImgList.add(anmImg);
		}
	}

	/**
	 * Processing描画
	 */
	public void draw() {

		// 背景画像を描画
		background(_backgroundImg);

		PImage img;
		int x, y, dirX, dirY;
		for (int i = 0; i < _animatedImgList.size(); i++) {
			img = _animatedImgList.get(i).getImg();
			x = _animatedImgList.get(i).getX();
			y = _animatedImgList.get(i).getY();
			dirX = _animatedImgList.get(i).getDirX();
			dirY = _animatedImgList.get(i).getDirY();

			x += dirX * ANIMATION_SPEED;
			y += dirY * ANIMATION_SPEED;

			if ((x < 0) || (x > width - img.width)) {
				dirX = -dirX;
			}

			if ((y < 0) || (y > height - img.height)) {
				dirY = -dirY;
			}

			// 画像を描画
			image(img, x, y);

			// 座標情報を保持
			_animatedImgList.get(i).setX(x);
			_animatedImgList.get(i).setY(y);
			_animatedImgList.get(i).setDirX(dirX);
			_animatedImgList.get(i).setDirY(dirY);

		}

	}

	/**
	 * 透過処理テスト用
	 */
	private void Transparency() {
		try {
			// 設定ファイルの対象フォルダから「.png」ファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(_properties.getProperty("dir_animated_image")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());

			for (final File file : fileList) {

				System.out.println(file.getPath());

				// 画像透過処理
				BufferedImage writeImg = ImageUtil.Transparency(file, Color.WHITE);

				// 画像を書き出し
				ImageIO.write(writeImg, "png", new File(file.getAbsolutePath().replace(".png", "_t.png")));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
