package com.prairie.eevernote.client;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.prairie.eevernote.client.impl.EEClipperImpl;
import com.prairie.eevernote.client.impl.EEClipperNop;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.MapUtil;

/**
 * Factory to create <code>EEclipper</code>.
 *
 * @author Liu, Jianwei
 * @version 1.0
 */
public class EEClipperFactory {

    private static EEClipperFactory instance;

    private EEClipper nopEEClipper;

    private final Map<String, EEClipper> realEEClipperMap = MapUtil.map();

    private EEClipperFactory() {

    }

    /**
     * Return a instance of this factory.
     *
     * @return a instance of this factory
     */
    public static EEClipperFactory getInstance() {
        if (instance == null) {
            synchronized (EEClipperFactory.class) {
                if (instance == null) {
                    instance = new EEClipperFactory();
                }
            }
        }
        return instance;
    }

    /**
     * Create a new <code>EECliper</code> instance with given token.
     *
     * @param token
     *            used to auth
     * @return the new created EEClipper instance
     * @throws TException
     *             Please refer to Evernote SDK
     * @throws EDAMUserException
     *             Please refer to Evernote SDK
     * @throws EDAMSystemException
     *             Please refer to Evernote SDK
     * @throws OutOfDateException
     *             This plug-in is out of date
     */
    public EEClipper getEEClipper(final String token) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
        if (StringUtils.isBlank(token)) {
            return this.getEEClipper();
        } else {
            EEClipper clipper = new EEClipperImpl(token);
            realEEClipperMap.put(token, clipper);
            return clipper;
        }
    }

    public EEClipper getEEClipper(final String token, final boolean newInstance) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
        if (newInstance) {
            return this.getEEClipper(token);
        } else {
            if (realEEClipperMap.get(token) == null) {
                synchronized (realEEClipperMap) {
                    if (realEEClipperMap.get(token) == null) {
                        return this.getEEClipper(token);
                    }
                }
            }
            EEClipper clipper = realEEClipperMap.get(token);
            if (!clipper.isValid()) {
                synchronized (realEEClipperMap) {
                    if (!clipper.isValid()) {
                        clipper = getEEClipper(token);
                    }
                }
            }
            return clipper;
        }
    }

    /**
     * Get or create a <code>EECliper</code> instance with empty implementation.
     * the same effect to <code>getEEClipperNop</code>.
     *
     * @see EEClipperFactory#getEEClipperNop
     *
     * @return existing or new created no-operation <code>EECliper</code>
     *         instance
     */
    public EEClipper getEEClipper() {
        return getEEClipperNop();
    }

    /**
     * Get or create a <code>EECliper</code> instance with empty implementation.
     *
     * @return existing or new created no-operation <code>EECliper</code>
     *         instance
     */
    public EEClipper getEEClipperNop() {
        if (nopEEClipper == null) {
            synchronized (this) {
                if (nopEEClipper == null) {
                    nopEEClipper = new EEClipperNop();
                }
            }
        }
        return nopEEClipper;
    }

}
