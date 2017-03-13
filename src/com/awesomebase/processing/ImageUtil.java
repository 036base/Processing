package com.awesomebase.processing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * 画像関連ユーティリティクラス
 *
 * @author
 *
 */
public class ImageUtil {

	/**
	 * 透過処理
	 *
	 * @param file
	 * @param color
	 */
	public static BufferedImage Transparency(File file, Color color) {

		BufferedImage writeImg = null;
		try {
			// 画像を取得
			BufferedImage readImg = ImageIO.read(file);

			int width = readImg.getWidth(); // 横の幅取得
			int height = readImg.getHeight(); // 縦の高さ取得

			writeImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			for (int y = 0; y < height; ++y) {
				for (int x = 0; x < width; ++x) {
					if (readImg.getRGB(x, y) == color.getRGB()) {
						// 白を透過
						writeImg.setRGB(x, y, 0);
					} else {
						// 白以外はそのまま
						writeImg.setRGB(x, y, readImg.getRGB(x, y));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			writeImg = null;
		}

		return writeImg;

	}
}
