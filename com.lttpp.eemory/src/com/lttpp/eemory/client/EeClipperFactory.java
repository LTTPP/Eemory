package com.lttpp.eemory.client;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.lttpp.eemory.Messages;
import com.lttpp.eemory.client.impl.EeClipperImpl;
import com.lttpp.eemory.client.impl.EeClipperNop;
import com.lttpp.eemory.exception.OutOfDateException;
import com.lttpp.eemory.util.LogUtil;
import com.lttpp.eemory.util.MapUtil;

/**
 * Factory to create <code>EEclipper</code>.
 *
 */
public class EeClipperFactory {

    private static EeClipperFactory instance;

    private EeClipper nopEeClipper;

    private final Map<String, EeClipper> realEeClipperMap = MapUtil.map();

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
    public EeClipper getEeClipper(final String token) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
        if (StringUtils.isBlank(token)) {
            return this.getEeClipper();
        } else {
            EeClipper clipper = new EeClipperImpl(token);
            realEeClipperMap.put(token, clipper);
            return clipper;
        }
    }

    public EeClipper getEeClipper(final String token, final boolean newInstance) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
        if (newInstance) {
            return this.getEeClipper(token);
        } else {
            if (realEeClipperMap.get(token) == null) {
                synchronized (realEeClipperMap) {
                    if (realEeClipperMap.get(token) == null) {
                        return this.getEeClipper(token);
                    }
                }
            }
            EeClipper clipper = realEeClipperMap.get(token);
            if (!clipper.isValid()) {
                synchronized (realEeClipperMap) {
                    if (!clipper.isValid()) {
                        clipper = getEeClipper(token);
                        LogUtil.debug(Messages.bind(Messages.Plugin_Debug_NewClipper, token));
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
    public EeClipper getEeClipper() {
        return getEeClipperNop();
    }

    /**
     * Get or create a <code>EECliper</code> instance with empty implementation.
     *
     * @return existing or new created no-operation <code>EECliper</code>
     *         instance
     */
    public EeClipper getEeClipperNop() {
        if (nopEeClipper == null) {
            synchronized (this) {
                if (nopEeClipper == null) {
                    nopEeClipper = new EeClipperNop();
                }
            }
        }
        return nopEeClipper;
    }

}
