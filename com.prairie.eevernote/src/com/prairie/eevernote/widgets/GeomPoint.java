package com.prairie.eevernote.widgets;

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

	public GeomPoint move(int x, int y) {
		this.x = this.getX() + x;
		this.x = this.getX() >= 0 ? this.getX() : 0;

		this.y = this.getY() + y;
		this.y = this.getY() >= 0 ? this.getY() : 0;

		return this;
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
