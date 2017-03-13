package com.awesomebase.processing;

import processing.core.PImage;

/**
 * アニメーション画像クラス
 *
 * @author
 *
 */
public class AnimatedImage {
	private PImage _img;		// アニメーションイメージ
	private int _x = 0;		// X座標
	private int _y = 0;		// Y座標
	private int _dirX = 1;	//
	private int _dirY = 1;	//

	public PImage getImg() {
		return _img;
	}

	public void setImg(PImage img) {
		this._img = img;
	}

	public int getX() {
		return _x;
	}

	public void setX(int x) {
		this._x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		this._y = y;
	}

	public int getDirX() {
		return _dirX;
	}

	public void setDirX(int dirX) {
		_dirX = dirX;
	}

	public int getDirY() {
		return _dirY;
	}

	public void setDirY(int dirY) {
		_dirY = dirY;
	}

}
