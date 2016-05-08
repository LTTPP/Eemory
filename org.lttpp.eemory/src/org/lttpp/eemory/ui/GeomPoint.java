package org.lttpp.eemory.ui;

import java.awt.Toolkit;

import org.lttpp.eemory.util.ConstantsUtil;

public class GeomPoint {

    private int x;
    private int y;

    public GeomPoint() {
        this(0, 0);
    }

    public GeomPoint(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public GeomPoint(final GeomPoint point) {
        x = point.getX();
        y = point.getY();
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public void setLocation(final int x, final int y) {
        setX(x);
        setY(y);
    }

    public boolean isMovable(final int x, final int y) {
        if (getX() + x >= 0 && getX() + x <= Toolkit.getDefaultToolkit().getScreenSize().getWidth() && getY() + y >= 0 && getY() + y <= Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
            return true;
        }
        return false;
    }

    public GeomPoint move(final int x, final int y) {
        if (isMovable(x, y)) {
            this.x = getX() + x;
            this.y = getY() + y;
        }
        return this;
    }

    public void clear() {
        x = 0;
        y = 0;
    }

    @Override
    public int hashCode() {
        return getX() * getX() + getY() * getY();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GeomPoint)) {
            return false;
        }
        GeomPoint p = (GeomPoint) obj;
        if (x == p.x && y == p.y) {
            return true;
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
        return ConstantsUtil.LEFT_PARENTHESIS + getX() + ConstantsUtil.COMMA + getY() + ConstantsUtil.RIGHT_PARENTHESIS;
    }

}
