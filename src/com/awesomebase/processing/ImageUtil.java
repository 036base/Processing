package com.awesomebase.processing;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * 画像関連ユーティリティクラス
 *
 * @author
 *
 */
public class ImageUtil {

	/**
	 * PImage => BufferedImage 変換
	 *
	 * @param pImg
	 * @return
	 */
	public static BufferedImage PImage2BImage(PImage pImg) {
		BufferedImage bImg = new BufferedImage(pImg.width, pImg.height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < bImg.getHeight(); y++) {
			for (int x = 0; x < bImg.getWidth(); x++) {
				bImg.setRGB(x, y, pImg.pixels[y * pImg.width + x]);
			}
		}
		return bImg;
	}

	/**
	 * BufferedImage => PImage 変換
	 *
	 * @param bImg
	 * @return
	 */
	public static PImage BImage2PImage(BufferedImage bImg) {
		PApplet pa = new PApplet();
		PImage pImg = pa.createImage(bImg.getWidth(), bImg.getHeight(), PConstants.ARGB);
		for (int y = 0; y < pImg.height; y++) {
			for (int x = 0; x < pImg.width; x++) {
				pImg.pixels[y * pImg.width + x] = bImg.getRGB(x, y);
			}
		}
		return pImg;
	}

	/**
	 * 透過処理
	 *
	 * @param file
	 * @param color
	 */
	public static BufferedImage Transparency(File file, Color color) {

		BufferedImage bimg = null;
		try {
			// 画像を取得
			BufferedImage readImg = ImageIO.read(file);

			int width = readImg.getWidth(); // 横の幅取得
			int height = readImg.getHeight(); // 縦の高さ取得

			bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			for (int y = 0; y < height; ++y) {
				for (int x = 0; x < width; ++x) {
					if (readImg.getRGB(x, y) == color.getRGB()) {
						// 白を透過
						bimg.setRGB(x, y, 0);
					} else {
						// 白以外はそのまま
						bimg.setRGB(x, y, readImg.getRGB(x, y));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			bimg = null;
		}

		return bimg;

	}

	/**
	 * 左右反転処理
	 *
	 * @param pBimg
	 */
	public static BufferedImage FlipHorizontal(BufferedImage pBimg) {

		BufferedImage bimg = null;

		try {
			int w = pBimg.getWidth(null);

			AffineTransform at = AffineTransform.getScaleInstance(-1.0, 1.0);
			at.translate(-w, 0);
			AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
			bimg = op.filter(pBimg, null);

		} catch (Exception e) {
			e.printStackTrace();
			bimg = null;
		}

		return bimg;

	}






	/**
	 * 透過処理テスト用
	 */
	public void Transparency(String dir_animated_image) {
		try {
			// 設定ファイルの対象フォルダから「.png」ファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(dir_animated_image), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());

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
