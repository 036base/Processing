package com.awesomebase.processing;

import processing.core.PImage;

/**
 * アニメーション画像クラス
 *
 * @author
 *
 */
public class AnimatedImage {
	private PImage _img;			// アニメーションイメージ
	private int _x = 0;			// X座標
	private int _y = 0;			// Y座標
	private int _dirX = 1;			// X軸進行方向
	private int _dirY = 1;			// Y軸進行方向
	private double _scale = 1.0;	// 倍率
	private int _speed = Constants.ANIMATION_SPEED;	// 速度

	public PImage getImg() {
		return _img;
	}

	public void setImg(PImage img) {
		_img = img;
	}

	public int getX() {
		return _x;
	}

	public void setX(int x) {
		_x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		_y = y;
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

	public double getScale() {
		return _scale;
	}

	public void setScale(double scale) {
		_scale = scale;
	}

	public int getSpeed() {
		return _speed;
	}

	public void setSpeed(int speed) {
		_speed = speed;
	}

}
