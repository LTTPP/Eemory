package com.prairie.eevernote.widgets;

public class GeomRectangle {

	private GeomPoint startPoint;
	private GeomPoint endPoint;

	public GeomRectangle() {
		this.startPoint = new GeomPoint();
		this.endPoint = new GeomPoint();
	}

	public GeomPoint getTopLeftPoint() {
		return new GeomPoint(Math.min(startPoint.getX(), endPoint.getX()), Math.min(startPoint.getY(), endPoint.getY()));
	}

	public GeomPoint getStartPoint() {
		return this.startPoint;
	}

	public void setStartPoint(GeomPoint topLeftPoint) {
		this.startPoint = topLeftPoint;
	}

	public GeomPoint getBottomRightPoint() {
		return new GeomPoint(Math.max(startPoint.getX(), endPoint.getX()), Math.max(startPoint.getY(), endPoint.getY()));
	}

	public GeomPoint getEndPoint() {
		return this.endPoint;
	}

	public void setEndPoint(GeomPoint bottomRightPoint) {
		this.endPoint = bottomRightPoint;
	}

	public int getWidth() {
		return getBottomRightPoint().getX() - getTopLeftPoint().getX();
	}

	public int getHeight() {
		return getBottomRightPoint().getY() - getTopLeftPoint().getY();
	}

	public GeomPoint getTopRightPoint() {
		return new GeomPoint(getBottomRightPoint().getX(), getTopLeftPoint().getY());
	}

	public GeomPoint getBottomLeftPoint() {
		return new GeomPoint(getTopLeftPoint().getX(), getBottomRightPoint().getY());
	}

	public Position positionOfPoint(GeomPoint point) {
		if (point.equals(this.getTopLeftPoint())) {
			return Position.NORTHWEST;
		} else if (point.equals(this.getTopRightPoint())) {
			return Position.NORTHEAST;
		} else if (point.equals(this.getBottomLeftPoint())) {
			return Position.SOUTHWEST;
		} else if (point.equals(this.getBottomRightPoint())) {
			return Position.SOUTHEAST;
		} else if ((point.getX() == this.getTopLeftPoint().getX()) && (point.getY() > this.getTopLeftPoint().getY()) && (point.getY() < this.getBottomRightPoint().getY())) {
			return Position.WEST;
		} else if ((point.getX() == this.getBottomRightPoint().getX()) && (point.getY() > this.getTopLeftPoint().getY()) && (point.getY() < this.getBottomRightPoint().getY())) {
			return Position.EAST;
		} else if ((point.getY() == this.getTopLeftPoint().getY()) && (point.getX() > this.getTopLeftPoint().getX()) && (point.getY() < this.getBottomRightPoint().getX())) {
			return Position.NORTH;
		} else if ((point.getY() == this.getBottomRightPoint().getY()) && (point.getX() > this.getTopLeftPoint().getX()) && (point.getX() < this.getBottomRightPoint().getX())) {
			return Position.SOUTH;
		} else if (point.getX() > this.getTopLeftPoint().getX() && point.getX() < this.getBottomRightPoint().getX() && point.getY() > this.getTopLeftPoint().getY() && point.getY() < this.getBottomRightPoint().getY()) {
			return Position.INSIDE;
		} else {
			return Position.OUTSIDE;
		}
	}

	enum Position {
		SOUTHWEST, SOUTHEAST, NORTHWEST, NORTHEAST, NORTH, SOUTH, WEST, EAST, OUTSIDE, INSIDE
	}
}
