package com.prairie.eevernote.ui;

import java.awt.Toolkit;

import com.prairie.eevernote.Constants;

public class GeomPoint implements Constants {

	private int x;
	private int y;

	public GeomPoint() {
		this(0, 0);
	}

	public GeomPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public GeomPoint(final GeomPoint point) {
		this.x = point.getX();
		this.y = point.getY();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setLocation(int x, int y) {
		setX(x);
		setY(y);
	}

	public boolean isMovable(int x, int y) {
		if (getX() + x >= 0 && getX() + x <= Toolkit.getDefaultToolkit().getScreenSize().getWidth() && getY() + y >= 0 && getY() + y <= Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
			return true;
		}
		return false;
	}

	public GeomPoint move(int x, int y) {
		if (isMovable(x, y)) {
			this.x = getX() + x;
			this.y = getY() + y;
		}
		return this;
	}

	public void clear() {
		this.x = 0;
		this.y = 0;
	}

	@Override
	public int hashCode() {
		return this.getX() * this.getX() + this.getY() * this.getY();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeomPoint) {
			GeomPoint p = (GeomPoint) obj;
			if ((p.getX() == this.getX()) && (p.getY() == this.getY())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object clone() {
		GeomPoint o = null;
		try {
			o = (GeomPoint) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return o;
	}

	@Override
	public String toString() {
		return LEFT_PARENTHESIS + this.getX() + COMMA + this.getY() + RIGHT_PARENTHESIS;
	}

}
