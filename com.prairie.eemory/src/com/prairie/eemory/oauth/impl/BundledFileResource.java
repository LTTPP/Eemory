package com.prairie.eemory.oauth.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;

import org.eclipse.jetty.util.resource.Resource;

import com.prairie.eemory.util.ConstantsUtil;

public class BundledFileResource extends Resource {

    private final String name;
    private Class<?> loadingClass;
    private InputStream stream;

    public BundledFileResource(final String name) {
        this.name = name;
    }

    public BundledFileResource(final String name, final Class<?> clasz) {
        this.name = name;
        loadingClass = clasz;
    }

    @Override
    public boolean exists() {
        if (name == null) {
            return false;
        }
        InputStream stream = getLoadingClass().getResourceAsStream(name);
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public InputStream getInputStream() {
        return stream = getLoadingClass().getResourceAsStream(name);
    }

    @Override
    public ReadableByteChannel getReadableByteChannel() {
        return null;
    }

    @Override
    public URI getAlias() {
        return null;
    }

    @Override
    public Resource addPath(final String path) throws MalformedURLException {
        if (path == null) {
            throw new MalformedURLException();
        }

        if (ConstantsUtil.SLASH.equals(path)) {
            return this;
        }

        return new BundledFileResource(name + path);
    }

    public void setLoadingClass(final Class<?> clasz) {
        loadingClass = clasz;
    }

    private Class<?> getLoadingClass() {
        if (loadingClass == null) {
            loadingClass = getClass();
        }
        return loadingClass;
    }

    @Override
    public void close() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public boolean delete() throws SecurityException {
        return false;
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public boolean isContainedIn(final Resource arg0) {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return name.endsWith(ConstantsUtil.SLASH);
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public String[] list() {
        return null;
    }

    @Override
    public boolean renameTo(final Resource arg0) {
        return false;
    }

}
