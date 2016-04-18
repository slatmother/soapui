package com.eviware.soapui.support.scripting.groovy.holders;

import com.eviware.soapui.SoapUI;
import groovy.lang.GroovyClassLoader;

/**
 * Class:
 * Description:
 * <p/>
 * Created by: geal0913
 * Date: 14.04.2016
 */
public class GroovyStaticClassLoaderHolder {
    private final GroovyClassLoader CLASS_LOADER;
    private static GroovyStaticClassLoaderHolder INSTANCE;

    private GroovyStaticClassLoaderHolder(ClassLoader parent) {
       CLASS_LOADER = new GroovyClassLoader(parent);
    }

    public static synchronized GroovyStaticClassLoaderHolder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GroovyStaticClassLoaderHolder(SoapUI.getSoapUICore().getExtensionClassLoader());
        }
        return INSTANCE;
    }

    public GroovyClassLoader getClassLoader() {
        return CLASS_LOADER;
    }

    public void addClassPath(String classPath) {
        CLASS_LOADER.addClasspath(classPath);
    }
}
