/*
 * Copyright 2004-2014 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
*/

package com.eviware.soapui.support.scripting.groovy;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.propertyexpansion.PropertyExpansion;
import com.eviware.soapui.support.scripting.SoapUIScriptEngine;
import com.eviware.soapui.support.scripting.SoapUIScriptEngineFactory;
import com.eviware.soapui.support.scripting.SoapUIScriptGenerator;
import com.eviware.soapui.support.scripting.groovy.holders.GroovyStaticClassLoaderHolder;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.soapui.support.xml.XPathData;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Factory for creating Groovy ScriptEngines
 *
 * @author ole.matzura
 */

public class GroovyScriptEngineFactory implements SoapUIScriptEngineFactory, SoapUIScriptGenerator {
    public static final String EXT_DIR_PROPERTY_NAME = "SYSTEM:EXT_DIR";
    public static final String ID = "Groovy";

    public SoapUIScriptEngine createScriptEngine(ModelItem modelItem) {
        String extDir = modelItem.getProject().getPropertyValue(EXT_DIR_PROPERTY_NAME);

        SoapUIGroovyScriptEngine engine = new SoapUIGroovyScriptEngine();

        if (extDir != null && !extDir.equals("")) {
            GroovyStaticClassLoaderHolder.getInstance().addClassPath(extDir);
        }

        return engine;
    }

    public SoapUIScriptGenerator createCodeGenerator(ModelItem modelItem) {
        return this;
    }

    public String createContextExpansion(String name, PropertyExpansion expansion) {
        String exp = expansion.toString();
        StringBuffer buf = new StringBuffer();

        for (int c = 0; c < exp.length(); c++) {
            char ch = exp.charAt(c);

            switch (ch) {
                case '\'':
                case '\\':
                    buf.append('\\');
                default:
                    buf.append(ch);
            }
        }

        return "def " + name + " = context.expand( '" + buf.toString() + "' )\n";
    }

    public String createScriptAssertionForExists(XPathData xpathData) {
        String script = "import com.eviware.soapui.support.XmlHolder\n\n"
                + "def holder = new XmlHolder( messageExchange.responseContentAsXml )\n";

        StringToStringMap nsMap = xpathData.getNamespaceMap();
        for (String ns : nsMap.keySet()) {
            script += "holder.namespaces[\"" + nsMap.get(ns) + "\"] = \"" + ns + "\"\n";
        }

        script += "def node = holder.getDomNode( \"" + xpathData.getPath() + "\" )\n";
        script += "\nassert node != null\n";

        return script;
    }

    private void addScriptsToClassLoader(String extDir, GroovyClassLoader extClassLoader) {
        try {
            File dir = new File(extDir);

            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.getName().toLowerCase().endsWith(".groovy")) {
                            extClassLoader.addURL(file.toURI().toURL());
                            SoapUI.log.info("Adding [" + file.getAbsolutePath() + "] to extensions classpath");
                        }
                    }
                }
            } else {
                SoapUI.log.warn("Missing folder [" + dir.getAbsolutePath() + "] for external libraries");
            }
        } catch (MalformedURLException e) {
            SoapUI.log.error("External scripts adding failed", e);
        }
    }
}
