package com.prairie.eevernote.client;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.client.impl.EEClipperDummy;
import com.prairie.eevernote.client.impl.EEClipperImpl;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.StringUtil;

public class EEClipperManager {

	private static EEClipperManager instance;

	private EEClipper dummyEEClipper;

	private EEClipper realEEClipper;

	private EEClipperManager() {

	}

	public static EEClipperManager getInstance() {
		if (instance == null) {
			synchronized (EEProperties.class) {
				if (instance == null) {
					instance = new EEClipperManager();
				}
			}
		}
		return instance;
	}

	public EEClipper getEEClipper(String token) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
		if (StringUtil.nullOrEmptyOrBlankString(token)) {
			return this.getEEClipper();
		} else {
			return new EEClipperImpl(token);
		}
	}

	public EEClipper getEEClipper(String token, boolean newInstance) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
		if (newInstance) {
			return this.getEEClipper(token);
		} else {
			if (this.realEEClipper == null) {
				EEClipper clipper = this.getEEClipper(token);
				if (clipper instanceof EEClipperImpl) {
					this.realEEClipper =clipper;
				} else {
					return this.getDummyEEClipper();
				}
			}
			return this.realEEClipper;
		}
	}

	public EEClipper getEEClipper() {
		return this.getDummyEEClipper();
	}

	public EEClipper getDummyEEClipper() {
		if (this.dummyEEClipper == null) {
			this.dummyEEClipper = new EEClipperDummy();
		}
		return this.dummyEEClipper;
	}
}
