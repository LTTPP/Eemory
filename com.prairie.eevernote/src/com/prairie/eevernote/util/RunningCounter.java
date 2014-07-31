package com.prairie.eevernote.util;

import com.prairie.eevernote.Constants;

public class RunningCounter {

	private int runTimes = 0;

	public boolean hasTimes() {
		return runTimes-- > Constants.ZERO;
	}

	public void resetTimes(int times) {
		this.runTimes = times;
	}

}
