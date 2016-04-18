package com.eviware.soapui.support.scripting.groovy.holders;

import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;

import java.util.HashMap;
import java.util.Map;

/**
 * Class:
 * Description:
 * <p/>
 * Created by: geal0913
 * Date: 14.04.2016
 */
public class GroovyStaticBindingsHolder {
    public static final String STATIC_VAR_DEF = "staticContext";

    private static final Map<String, Map> ADDITIONAL_BINDING_VARS = new HashMap<>();
    private static final GroovyStaticBindingsHolder INSTANCE = new GroovyStaticBindingsHolder();

    static {
        ADDITIONAL_BINDING_VARS.put(STATIC_VAR_DEF, new HashMap<>());
    }

    private GroovyStaticBindingsHolder() {

    }

    public static GroovyStaticBindingsHolder getInstance() {
        return INSTANCE;
    }

    public Binding getBinding() {
        Map map = new HashMap();
        map.putAll(ADDITIONAL_BINDING_VARS);

        return new Binding(map);
    }

    public Map getVarsMap() {
        return ImmutableMap.copyOf(ADDITIONAL_BINDING_VARS);
    }
}
