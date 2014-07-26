package com.prairie.eevernote.widgets;

public class GeomRectangle {

	private GeomPoint topLeftPoint;
	private GeomPoint bottomRightPoint;

	public GeomRectangle() {
		this.topLeftPoint = new GeomPoint();
		this.bottomRightPoint = new GeomPoint();
	}

	public GeomPoint getTopLeftPoint() {
		refactor();
		return topLeftPoint;
	}

	public void setTopLeftPoint(GeomPoint topLeftPoint) {
		this.topLeftPoint = topLeftPoint;
	}

	public GeomPoint getBottomRightPoint() {
		refactor();
		return bottomRightPoint;
	}

	public void setBottomRightPoint(GeomPoint bottomRightPoint) {
		this.bottomRightPoint = bottomRightPoint;
	}

	public int getWidth() {
		return getBottomRightPoint().getX() - getTopLeftPoint().getX();
	}

	public int getHeight() {
		return getBottomRightPoint().getY() - getTopLeftPoint().getY();
	}

	private void refactor() {
		if (topLeftPoint.compareTo(bottomRightPoint) < 0) {
			return;
		}
		if (topLeftPoint.getX() > bottomRightPoint.getX()) {
			int temp = topLeftPoint.getX();
			topLeftPoint.setX(bottomRightPoint.getX());
			bottomRightPoint.setX(temp);
		}

		if (topLeftPoint.getY() > bottomRightPoint.getY()) {
			int temp = topLeftPoint.getY();
			topLeftPoint.setY(bottomRightPoint.getY());
			bottomRightPoint.setY(temp);
		}
	}
}
