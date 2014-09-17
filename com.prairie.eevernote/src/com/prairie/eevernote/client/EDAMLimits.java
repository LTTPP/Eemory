package com.prairie.eevernote.client;

import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.NumberUtil;

/*
 * https://dev.evernote.com/doc/reference/Limits.html
 */
public class EDAMLimits {

    public static final int EDAM_NOTE_TITLE_LEN_MAX = NumberUtil.number(ConstantsUtil.TWO, ConstantsUtil.FIVE, ConstantsUtil.FIVE);
    public static final int EDAM_TAG_NAME_LEN_MAX = NumberUtil.number(ConstantsUtil.ONE, ConstantsUtil.ZERO, ConstantsUtil.ZERO);

}
