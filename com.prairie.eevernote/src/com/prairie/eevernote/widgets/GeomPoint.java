package com.prairie.eevernote.widgets;

public class GeomPoint implements Comparable<GeomPoint> {

	private int x;
	private int y;

	public GeomPoint() {
		this(0, 0);
	}

	public GeomPoint(int x, int y) {
		this.x = x;
		this.y = y;
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

	@Override
	public int compareTo(GeomPoint o) {
		if (this.getX() < o.getX() && this.getY() < o.getY()) {
			return -1;
		} else if (this.getX() > o.getX() && this.getY() > o.getY()) {
			return 1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (this.getX() * this.getX() + this.getY() * this.getY());
		return result;
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

}
