package com.optedoblivion.MinimalNewsWidget.xmltool;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class XMLTool {
    public static String getElementValue(Element parent, String name){
        NodeList children = getNodesNamed(parent, name);
        String value = null;
        if (children.getLength() > 0){
            Element e = (Element) children.item(0);
            value = getValue(e);
        }
        return value;
    }

    public static NodeList getNodesNamed(Element parent, String name){
        NodeList children = parent.getElementsByTagName(name);
        return children;
    }

    public static String getValue(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return null;
    }

    public static String getAttribute(Element parent, String name,
                                                                  String attr){
        NodeList children = getNodesNamed(parent, name);
        String value = null;
        if (children.getLength() > 0){
            Element e = (Element) children.item(0);
            value = e.getAttribute(attr);
        }
        return value;
    }
}
