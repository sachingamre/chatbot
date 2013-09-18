/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.classes;

import com.sachin.app.App;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author sachin.gamre
 */
public class SacXMLParser {

    private File xmlFile = null ;
    private String xmlString = null ;
    private Document dom = null ;

    /**
     * Constructor takes file as an argument
     *
     * @param file
     */
    public SacXMLParser(File file) {
        try {
            xmlFile = file;
            // parse the XML output, format it and show it to client
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dom = dBuilder.parse(file);
            dom.getDocumentElement().normalize();
        }
        catch (SAXException ex) {
            ex.printStackTrace() ;
            App.logger.error(ex.getMessage());
        }
        catch (IOException ex) {
            ex.printStackTrace() ;
            App.logger.error(ex.getMessage());
        }
        catch (ParserConfigurationException ex) {
            ex.printStackTrace() ;
            App.logger.error(ex.getMessage());
        }
    }

    /**
     * Constructor takes XML string as an argument
     * 
     * @param xml
     */
    public SacXMLParser(String xml) {
        xmlString = xml ;

        try {
            // parse the XML output, format it and show it to client
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dom = dBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
            //dom.getDocumentElement().normalize();
        }
        catch (SAXException ex) {
            ex.printStackTrace() ;
            App.logger.error(ex.getMessage());
        }
        catch (IOException ex) {
            ex.printStackTrace() ;
            App.logger.error(ex.getMessage());
        }
        catch (ParserConfigurationException ex) {
            ex.printStackTrace() ;
            App.logger.error(ex.getMessage()) ;
        }
    }

    /**
     * One of the core method which traverse the XML dom and converts the dom
     * node to SacXMLObject object and returns it
     *
     * @param Node node
     * @return SacXMLObject sxo
     */
    private SacXMLObject _getNode(Node node) {

        SacXMLObject sxo = new SacXMLObject() ;
        if(node.getNodeType() == Node.TEXT_NODE) {
            sxo.setValue(node.getNodeValue().trim());
        }
        else if(node.getNodeType() == Node.ELEMENT_NODE) {
            sxo.setName(node.getNodeName()) ;
            sxo.setValue(node.getNodeValue()) ;
            if(node.hasChildNodes()) {
                NodeList childNodes = node.getChildNodes() ;
                for(int i = 0; i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i) ;
                    sxo.addChild(_getNode(childNode)) ;
                }
            }
            if(node.hasAttributes()) {
                NamedNodeMap attrs = node.getAttributes();
                for(int j = 0 ; j < attrs.getLength() ; j++) {
                    Attr attribute = (Attr) attrs.item(j) ;
                    sxo.addAttribute(attribute.getName(), attribute.getValue());
                }
            }
        }
        return sxo ;
    }

    /**
     * Uses XML dom to reach to particular tag nodes.
     * Loop through all the found nodes, convert them into
     * SacXMLObject and returns a list
     *
     * @param String tagName
     * @return List<SacXMLObject> resultantNodes
     */
    public List<SacXMLObject> getDocuments(String tagName) {
        
        List<SacXMLObject> resultantNodes = new ArrayList<SacXMLObject>() ;
        NodeList listOfObjects = dom.getElementsByTagName(tagName) ;
        int totalObjects = listOfObjects.getLength() ;

        for(int s=0; s < listOfObjects.getLength(); s++) {
            Node node = listOfObjects.item(s) ;
            
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                SacXMLObject sObj1 = _getNode(node) ;
                resultantNodes.add(sObj1) ;
            }
        }
        return resultantNodes ;
    }

}
