package com.prairie.eevernote.ui;

import java.awt.Toolkit;

import com.prairie.eevernote.util.ConstantsUtil;

public class GeomPoint implements ConstantsUtil {

    private int x;
    private int y;

    public GeomPoint() {
        this(ZERO, ZERO);
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
        if (getX() + x >= ZERO && getX() + x <= Toolkit.getDefaultToolkit().getScreenSize().getWidth() && getY() + y >= ZERO && getY() + y <= Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
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
        x = ZERO;
        y = ZERO;
    }

    @Override
    public int hashCode() {
        return getX() * getX() + getY() * getY();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof GeomPoint) {
            GeomPoint p = (GeomPoint) obj;
            if (p.getX() == getX() && p.getY() == getY()) {
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
        return LEFT_PARENTHESIS + getX() + COMMA + getY() + RIGHT_PARENTHESIS;
    }

}
