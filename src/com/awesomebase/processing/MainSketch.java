package com.awesomebase.processing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

/**
 * Processingメインクラス
 *
 * @author
 *
 */
public class MainSketch {

	public static void main(String[] args) {

		System.out.println("**** 処理開始 ****");

		try {
			Properties properties = getProperties();

			// 設定ファイルの対象フォルダから「.png」ファイルを取得
			final List<File> fileList = (List<File>) FileUtils.listFiles(new File(properties.getProperty("img_dir")), FileFilterUtils.suffixFileFilter(".png"), FileFilterUtils.trueFileFilter());

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

		System.out.println("**** 処理終了 ****");

	}

	/**
	 * 設定ファイル取得
	 *
	 * @return
	 */
	private static Properties getProperties() {
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream("processing.properties");
			properties.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

}
