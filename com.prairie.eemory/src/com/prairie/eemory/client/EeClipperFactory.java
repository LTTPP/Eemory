package com.prairie.eemory.client;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.prairie.eemory.client.impl.EeClipperImpl;
import com.prairie.eemory.client.impl.EeClipperNop;
import com.prairie.eemory.exception.OutOfDateException;
import com.prairie.eemory.util.MapUtil;

/**
 * Factory to create <code>EEclipper</code>.
 *
 */
public class EeClipperFactory {

    private static EeClipperFactory instance;

    private EeClipper nopEEClipper;

    private final Map<String, EeClipper> realEEClipperMap = MapUtil.map();

    private EeClipperFactory() {

    }

    /**
     * Return a instance of this factory.
     *
     * @return a instance of this factory
     */
    public static EeClipperFactory getInstance() {
        if (instance == null) {
            synchronized (EeClipperFactory.class) {
                if (instance == null) {
                    instance = new EeClipperFactory();
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
    public EeClipper getEEClipper(final String token) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
        if (StringUtils.isBlank(token)) {
            return this.getEEClipper();
        } else {
            EeClipper clipper = new EeClipperImpl(token);
            realEEClipperMap.put(token, clipper);
            return clipper;
        }
    }

    public EeClipper getEEClipper(final String token, final boolean newInstance) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
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
            EeClipper clipper = realEEClipperMap.get(token);
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
     * @see EeClipperFactory#getEEClipperNop
     *
     * @return existing or new created no-operation <code>EECliper</code>
     *         instance
     */
    public EeClipper getEEClipper() {
        return getEEClipperNop();
    }

    /**
     * Get or create a <code>EECliper</code> instance with empty implementation.
     *
     * @return existing or new created no-operation <code>EECliper</code>
     *         instance
     */
    public EeClipper getEEClipperNop() {
        if (nopEEClipper == null) {
            synchronized (this) {
                if (nopEEClipper == null) {
                    nopEEClipper = new EeClipperNop();
                }
            }
        }
        return nopEEClipper;
    }

}
