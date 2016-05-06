package com.lttpp.eemory.dom;

import java.lang.reflect.Constructor;

public abstract class DocumentBuilderFactory {

    /**
     * Create a new instance of document builder factory with default bundled
     * implementation.
     *
     * @return new created instance of document builder factory
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static DocumentBuilderFactory newInstance() throws FactoryConfigurationError {
        try {
            String pkgName = DocumentBuilderFactory.class.getPackage().getName();
            String className = DocumentBuilderFactory.class.getSimpleName();
            Constructor<?> constructor = getImplementClass(pkgName + ".impl." + className + "Impl", null, true).getDeclaredConstructor();
            constructor.setAccessible(true);
            return (DocumentBuilderFactory) constructor.newInstance();
        } catch (Exception e) {
            throw new FactoryConfigurationError(e, e.getLocalizedMessage());
        }
    }

    /**
     * Create a new instance of document builder factory with given
     * implementation and class loader.
     *
     * @param factoryClassName
     *            factory implement class name
     * @param classLoader
     *            class loader to load this factory implement class
     * @return new created instance of document builder factory
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static DocumentBuilderFactory newInstance(final String factoryClassName, final ClassLoader classLoader) throws FactoryConfigurationError {
        try {
            return (DocumentBuilderFactory) getImplementClass(factoryClassName, classLoader, false).newInstance();
        } catch (Exception e) {
            throw new FactoryConfigurationError(e, e.getLocalizedMessage());
        }
    }

    private static Class<?> getImplementClass(final String className, final ClassLoader cl, final boolean doFallback) throws ClassNotFoundException {
        try {
            if (cl == null) {
                return Class.forName(className);
            } else {
                return cl.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            if (doFallback) {
                return Class.forName(className);
            } else {
                throw e;
            }
        }
    }

    /**
     * Create a new document builder which is instance of
     * {@code DocumentBuilder}.
     *
     * @return new created document builder
     */
    public abstract DocumentBuilder newDocumentBuilder();

}