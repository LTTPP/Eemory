package com.prairie.eevernote.util;


public class Times {

    private int times = ConstantsUtil.ZERO;

    public boolean hasTimes() {
        return times-- > ConstantsUtil.ZERO;
    }

    public void resetTimes(final int times) {
        this.times = times;
    }

}
