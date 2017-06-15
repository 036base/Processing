package com.awesomebase.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 起動クラス
 *
 * @author
 *
 */
public class StartUp {

	//---------------------------------------------------------------------------------------------
	// 【備考】
	//
	//  各クラスのmainメソッドを起動する。
	//  起動するクラスはパラメータで指定する。
	//
	//---------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		Logger logger = LogManager.getLogger();
		try {

			if (args == null || args.length == 0) {
				throw new IllegalArgumentException("パラメータが不正です。");
			}

			String[] sendArgs = new String[args.length - 1];
			for (int i = 0; i < sendArgs.length; i++) {
				sendArgs[i] = args[i + 1];
			}

			String className = args[0];

			Class<?> main = Class.forName(className);
			Object[] vargs = { sendArgs };
			main.getMethod("main", new Class[] { String[].class }).invoke(null, vargs);

		} catch (Exception e) {
			logger.error("*** System Error!! ***", e);
		}
	}

}
