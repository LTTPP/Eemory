package com.prairie.eevernote.widgets;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.NumberUtil;

/*
 * ......                   ......                   ......
 * .    .....................    .....................    .
 * ......   	            ......                   ......
 *   .                                                 .
 *   .                                                 .
 *   .                                                 .
 * ......                                            ......
 * .    .          SAMPLE SCREENSHOT MODEL           .    .
 * ......                                            ......
 *   .                                                 .
 *   .                                                 .
 *   .                                                 .
 * ......					 ......                  ......
 * .    .....................      ..................     .
 * ......		             ......                  ......
 *
 */
public class GeomRectangle implements Constants {

	private GeomPoint startPoint;
	private GeomPoint endPoint;

	public GeomRectangle() {
		this.startPoint = new GeomPoint();
		this.endPoint = new GeomPoint();
	}

	public GeomRectangle(final GeomPoint startPoint, final GeomPoint endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	public GeomPoint getTopLeftPoint() {
		return new GeomPoint(Math.min(startPoint.getX(), endPoint.getX()), Math.min(startPoint.getY(), endPoint.getY()));
	}

	public GeomPoint getStartPoint() {
		return this.startPoint;
	}

	public void setStartPoint(final GeomPoint topLeftPoint) {
		this.startPoint = topLeftPoint;
	}

	public GeomPoint getBottomRightPoint() {
		return new GeomPoint(Math.max(startPoint.getX(), endPoint.getX()), Math.max(startPoint.getY(), endPoint.getY()));
	}

	public GeomPoint getEndPoint() {
		return this.endPoint;
	}

	public void setEndPoint(final GeomPoint bottomRightPoint) {
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

	public GeomPoint getTopPoint() {
		return new GeomPoint((this.getTopLeftPoint().getX() + this.getBottomRightPoint().getX()) / TWO, this.getTopLeftPoint().getY());
	}

	public GeomPoint getLeftPoint() {
		return new GeomPoint(this.getTopLeftPoint().getX(), (this.getTopLeftPoint().getY() + this.getBottomRightPoint().getY()) / TWO);
	}

	public GeomPoint getBottomPoint() {
		return new GeomPoint((this.getTopLeftPoint().getX() + this.getBottomRightPoint().getX()) / TWO, this.getBottomRightPoint().getY());
	}

	public GeomPoint getRightPoint() {
		return new GeomPoint(this.getBottomRightPoint().getX(), (this.getTopLeftPoint().getY() + this.getBottomRightPoint().getY()) / TWO);
	}

	public GeomRectangle getTopRectangle() {
		return new GeomRectangle(new GeomPoint(this.getTopPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(this.getTopPoint()).move(TWO, TWO));
	}

	public GeomRectangle getBottomRectangle() {
		return new GeomRectangle(new GeomPoint(this.getBottomPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(this.getBottomPoint()).move(TWO, TWO));
	}

	public GeomRectangle getLeftRectangle() {
		return new GeomRectangle(new GeomPoint(this.getLeftPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(this.getLeftPoint()).move(TWO, TWO));
	}

	public GeomRectangle getRightRectangle() {
		return new GeomRectangle(new GeomPoint(this.getRightPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(this.getRightPoint()).move(TWO, TWO));
	}

	public GeomRectangle getTopLeftRectangle() {
		return new GeomRectangle(new GeomPoint(this.getTopLeftPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(this.getTopLeftPoint()).move(TWO, TWO));
	}

	public GeomRectangle getTopRightRectangle() {
		return new GeomRectangle(new GeomPoint(this.getTopRightPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(this.getTopRightPoint()).move(TWO, TWO));
	}

	public GeomRectangle getBottomLeftRectangle() {
		return new GeomRectangle(new GeomPoint(this.getBottomLeftPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(this.getBottomLeftPoint()).move(TWO, TWO));
	}

	public GeomRectangle getBottomRightRectangle() {
		return new GeomRectangle(new GeomPoint(this.getBottomRightPoint()).move(NumberUtil.signedNumber(TWO, NEGATIVE), NumberUtil.signedNumber(TWO, NEGATIVE)), new GeomPoint(this.getBottomRightPoint()).move(TWO, TWO));
	}

	public GeomPoint pointAt(final Position position) {
		if (position == Position.EAST) {
			return new GeomPoint(this.getBottomRightPoint().getX(), (this.getTopLeftPoint().getY() + this.getBottomRightPoint().getY()) / TWO);
		} else if (position == Position.SOUTH) {
			return new GeomPoint((this.getTopLeftPoint().getX() + this.getBottomRightPoint().getX()) / TWO, this.getBottomRightPoint().getY());
		} else if (position == Position.WEST) {
			return new GeomPoint(this.getTopLeftPoint().getX(), (this.getTopLeftPoint().getY() + this.getBottomRightPoint().getY()) / TWO);
		} else if (position == Position.NORTH) {
			return new GeomPoint((this.getTopLeftPoint().getX() + this.getBottomRightPoint().getX()) / TWO, this.getTopLeftPoint().getY());
		} else if (position == Position.SOUTHEAST) {
			return this.getBottomRightPoint();
		} else if (position == Position.SOUTHWEST) {
			return this.getBottomLeftPoint();
		} else if (position == Position.NORTHEAST) {
			this.getTopRightPoint();
		} else if (position == Position.NORTHWEST) {
			this.getTopLeftPoint();
		}
		return null;
	}

	public Position positionOf(final GeomPoint point) {
		if (point.equals(this.getTopLeftPoint()) || this.getTopLeftRectangle().contains(point)) {
			return Position.NORTHWEST;
		} else if (point.equals(this.getTopRightPoint()) || this.getTopRightRectangle().contains(point)) {
			return Position.NORTHEAST;
		} else if (point.equals(this.getBottomLeftPoint()) || this.getBottomLeftRectangle().contains(point)) {
			return Position.SOUTHWEST;
		} else if (point.equals(this.getBottomRightPoint()) || this.getBottomRightRectangle().contains(point)) {
			return Position.SOUTHEAST;
		} else if ((point.getX() == this.getTopLeftPoint().getX()) && (point.getY() > this.getTopLeftPoint().getY()) && (point.getY() < this.getBottomRightPoint().getY()) || this.getTopLeftRectangle().contains(point)) {
			return Position.WEST;
		} else if ((point.getX() == this.getBottomRightPoint().getX()) && (point.getY() > this.getTopLeftPoint().getY()) && (point.getY() < this.getBottomRightPoint().getY()) || this.getBottomRightRectangle().contains(point)) {
			return Position.EAST;
		} else if ((point.getY() == this.getTopLeftPoint().getY()) && (point.getX() > this.getTopLeftPoint().getX()) && (point.getY() < this.getBottomRightPoint().getX()) || this.getTopLeftRectangle().contains(point)) {
			return Position.NORTH;
		} else if ((point.getY() == this.getBottomRightPoint().getY()) && (point.getX() > this.getTopLeftPoint().getX()) && (point.getX() < this.getBottomRightPoint().getX()) || this.getBottomRightRectangle().contains(point)) {
			return Position.SOUTH;
		} else if (point.getX() > this.getTopLeftPoint().getX() && point.getX() < this.getBottomRightPoint().getX() && point.getY() > this.getTopLeftPoint().getY() && point.getY() < this.getBottomRightPoint().getY()) {
			return Position.INSIDE;
		} else {
			return Position.OUTSIDE;
		}
	}

	public boolean contains(GeomPoint point) {
		return point.getX() >= this.getTopLeftPoint().getX() && point.getX() <= this.getBottomRightPoint().getX() && point.getY() >= this.getTopLeftPoint().getY() && point.getY() <= this.getBottomRightPoint().getY();
	}

	public GeomRectangle move(int x, int y) {
		if (!this.startPoint.isMovable(x, y) || !this.endPoint.isMovable(x, y)) {
			return this;
		}
		this.startPoint.move(x, y);
		this.endPoint.move(x, y);
		return this;
	}

	public GeomRectangle resize(Position position, int x, int y) {
		if (position == Position.EAST) {
			if (getStartPoint().equals(getTopRightPoint()) || getStartPoint().equals(getBottomRightPoint())) {
				startPoint.move(x, y);
			} else if (getEndPoint().equals(getTopRightPoint()) || getEndPoint().equals(getBottomRightPoint())) {
				endPoint.move(x, y);
			}
		} else if (position == Position.SOUTH) {
			if (getStartPoint().equals(getBottomLeftPoint()) || getStartPoint().equals(getBottomRightPoint())) {
				startPoint.move(x, y);
			} else if (getEndPoint().equals(getBottomLeftPoint()) || getEndPoint().equals(getBottomRightPoint())) {
				endPoint.move(x, y);
			}
		} else if (position == Position.WEST) {
			if (getStartPoint().equals(getTopLeftPoint()) || getStartPoint().equals(getBottomLeftPoint())) {
				startPoint.move(x, y);
			} else if (getEndPoint().equals(getTopLeftPoint()) || getEndPoint().equals(getBottomLeftPoint())) {
				endPoint.move(x, y);
			}
		} else if (position == Position.NORTH) {
			if (getStartPoint().equals(getTopLeftPoint()) || getStartPoint().equals(getTopRightPoint())) {
				startPoint.move(x, y);
			} else if (getEndPoint().equals(getTopLeftPoint()) || getEndPoint().equals(getTopRightPoint())) {
				endPoint.move(x, y);
			}
		} else if (position == Position.SOUTHWEST) {
			if (getStartPoint().equals(getBottomLeftPoint())) {
				startPoint.move(x, y);
			} else if (getStartPoint().equals(getTopRightPoint())) {
				endPoint.move(x, y);
			} else if (getStartPoint().equals(getTopLeftPoint())) {
				startPoint.move(x, 0);
				endPoint.move(0, y);
			} else if (getStartPoint().equals(getBottomRightPoint())) {
				startPoint.move(0, y);
				endPoint.move(x, 0);
			}
		} else if (position == Position.SOUTHEAST) {
			if (getStartPoint().equals(getBottomRightPoint())) {
				startPoint.move(x, y);
			} else if (getStartPoint().equals(getTopLeftPoint())) {
				endPoint.move(x, y);
			} else if (getStartPoint().equals(getTopRightPoint())) {
				startPoint.move(x, 0);
				endPoint.move(0, y);
			} else if (getStartPoint().equals(getBottomLeftPoint())) {
				startPoint.move(0, y);
				endPoint.move(x, 0);
			}
		} else if (position == Position.NORTHWEST) {
			if (getStartPoint().equals(getTopLeftPoint())) {
				startPoint.move(x, y);
			} else if (getStartPoint().equals(getBottomRightPoint())) {
				endPoint.move(x, y);
			} else if (getStartPoint().equals(getTopRightPoint())) {
				startPoint.move(0, y);
				endPoint.move(x, 0);
			} else if (getStartPoint().equals(getBottomLeftPoint())) {
				startPoint.move(x, 0);
				endPoint.move(0, y);
			}
		} else if (position == Position.NORTHEAST) {
			if (getStartPoint().equals(getTopRightPoint())) {
				startPoint.move(x, y);
			} else if (getStartPoint().equals(getBottomLeftPoint())) {
				endPoint.move(x, y);
			} else if (startPoint.equals(getTopLeftPoint())) {
				startPoint.move(0, y);
				endPoint.move(x, 0);
			} else if (startPoint.equals(getBottomRightPoint())) {
				startPoint.move(x, 0);
				endPoint.move(0, y);
			}
		}
		return this;
	}

	public boolean isRealRectangle() {
		return getWidth() > 0 && getHeight() > 0;
	}

	public void clear() {
		this.startPoint.clear();
		this.endPoint.clear();
	}

	@Override
	public String toString() {
		return LEFT_PARENTHESIS + getTopLeftPoint() + COMMA + getWidth() + COMMA + getHeight() + RIGHT_PARENTHESIS;
	}

	enum Position {
		SOUTHWEST, SOUTHEAST, NORTHWEST, NORTHEAST, NORTH, SOUTH, WEST, EAST, OUTSIDE, INSIDE
	}
}
