package com.prairie.eevernote.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.prairie.eevernote.Constants;

public class DateTimeUtil {

	public static String timestamp() {
		// 2014-02-21T18:35:32
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.SimpleDateFormat);
		return formatter.format(new Date());
	}

}
