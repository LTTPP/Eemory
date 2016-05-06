package com.lttpp.eemory.util;

public class Times {

    private int times = 0;

    public boolean hasTimes() {
        return times-- > 0;
    }

    public void resetTimes(final int times) {
        this.times = times;
    }

}
