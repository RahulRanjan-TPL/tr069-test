/***
 * print debug infomation
 * 
 * auth: Freeman Liang
 * date: 2014.06.26
 * 
 */

package com.inspur.tools;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DebugInfo {
    public static final Boolean DEBUG = false;
    private static final DebugInfo debug = new DebugInfo();

    private DebugInfo() {
    }

    public static DebugInfo getDebug() {
        return debug;
    }

    private String getDocumentType(Document doc) {
        StringBuffer sb = new StringBuffer();
        DocumentType type = doc.getDoctype();
        String name = type.getName();
        String publicId = type.getPublicId();
        String systemId = type.getSystemId();
        if (name == null)
            return "";

        sb.append("<!DOCTYPE " + name);
        sb.append(publicId == null ? "" : " PUBLIC \"" + publicId + "\"");
        sb.append(systemId == null ? "" : " \"" + systemId + "\"");
        sb.append(">\n");
        return sb.toString();
    }

    private String getElementAttrs(Node node) { // 获取一个Element的所有Attribute。
        StringBuffer attrs = new StringBuffer();
        if (node.hasChildNodes()) {
            NamedNodeMap map = node.getAttributes();
            for (int i = 0; i < map.getLength(); i++) {
                Node attr = map.item(i);
                attrs.append(" " + attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
            }
        }
        return attrs.toString();
    }

    private String getXMLHeader(Document doc) { // 获取XML头定义。
        StringBuffer header = new StringBuffer();
        String encoding = doc.getXmlEncoding();
        String version = doc.getXmlVersion();
        boolean standalone = doc.getXmlStandalone();
        if (version == null || encoding == null)
            return ""; // 如果没有定义XML头，则返回空串。
        header.append("<?xml");
        header.append(" version=\"" + version + "\"");
        header.append(" encoding=\"" + encoding + "\"");
        header.append(standalone == false ? "" : " standalone=\"true\"");
        header.append("?>\n");
        return header.toString();
    }

    private StringBuffer circle(NodeList list, StringBuffer sb, int tmp) {

        for (int i = 0; i < list.getLength(); i++) {

            Node node = list.item(i);
            if (node.hasChildNodes()) {
                String nodeName = node.getNodeName();

                for (int j = 0; j < tmp; j++) {
                    sb.append("   ");
                }
                sb.append("<" + nodeName + this.getElementAttrs(node) + ">");
                sb.append("\n");
                sb = this.circle(node.getChildNodes(), sb, tmp + 1);
                for (int j = 0; j < tmp; j++) {
                    sb.append("   ");
                }
                sb.append("</" + nodeName + ">");
                sb.append("\n");
            } else if (node.getNodeType() == Node.COMMENT_NODE) {
                sb.append("<!--" + node.getNodeValue() + "-->");
            } else {
                for (int j = 0; j < tmp; j++) {
                    sb.append("   ");
                }
                sb.append(node.getNodeValue());
                sb.append("\n");
            }
        }
        return sb;
    }

    public String parseXML(String xmlString) {
        StringBuffer sb = new StringBuffer();
        try {
            javax.xml.parsers.DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();

            // org.w3c.dom.Document doc = builder.parse(xmlFile);
            InputSource is = new InputSource(new StringReader(xmlString));

            org.w3c.dom.Document doc = builder.parse(is);
            sb.append(this.getXMLHeader(doc));
            // sb.append(this.getDocumentType(doc)); // 获取DocumentType信息。
            org.w3c.dom.Element rootElement = doc.getDocumentElement();
            String rootTagName = rootElement.getTagName();
            sb.append("<" + rootTagName + this.getElementAttrs(rootElement) + ">");
            sb.append("\n");
            if (rootElement.hasChildNodes()) {
                int tmp = 1;
                sb = this.circle(rootElement.getChildNodes(), sb, tmp);
            }
            sb.append("</" + rootTagName + ">");
            sb.append("\n");
        } catch (Exception exc) {

        }
        return sb.toString();
    }
}
