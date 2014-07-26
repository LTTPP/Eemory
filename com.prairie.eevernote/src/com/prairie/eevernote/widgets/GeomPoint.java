package com.prairie.eevernote.widgets;

public class GeomPoint implements Comparable<GeomPoint> {

	private int x;
	private int y;

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

	@Override
	public int compareTo(GeomPoint o) {
		if (this.getX() < o.getX() && this.getY() < o.getY()) {
			return -1;
		} else if (this.getX() > o.getX() && this.getY() > o.getY()) {
			return 1;
		}
		return 0;
	}
}
