/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.loaders;

import chat.Main;
import com.sachin.core.ds.Command;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *  The file parses the given command xml file and loads the data into a hash map
 *
 *  @author sachin
 */
public class CommandLoader {

    private Document dom;
    private List commandList = new ArrayList();

    /**
     *  Constructor takes commands xml file and loads it in the List
     *
     *  @access public
     *  @param configFile
     *  @param list
     */
    public CommandLoader(String configFile) {
        parseXmlFile(configFile);
        parseDocument() ;
    }

    /**
     *  Helper getter to get the loaded list
     *
     *  @return List
     */
    public List getList() {
        return commandList ;
    }

    /**
     * Takes xmlfile as an argument loads it in DocumentBuilder
     *
     * @param xmlFile
     * @return void
     */
    private void parseXmlFile(String xmlFile) {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
                //Using factory get an instance of document builder
                DocumentBuilder db = dbf.newDocumentBuilder();

                //parse using builder to get DOM representation of the XML file
                String sPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() ;

                sPath = sPath.substring(1).replace('/', File.separatorChar).replace("%20", " ");
                System.out.println(sPath + " " + new File(sPath).getCanonicalPath()) ;
                String currentDir = new File(".").getCanonicalPath();
                System.out.println(currentDir + File.separatorChar + xmlFile) ;
                //System.out.println(getJarFolder()) ;
                dom = db.parse(new File(currentDir + File.separatorChar + xmlFile));
                //dom = db.parse(new File(sPath + "\\..\\..\\" + xmlFile));
                dom.getDocumentElement().normalize();
                System.out.println("Parsed XML file");
        }
        catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch(SAXException se) {
            se.printStackTrace();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     *  Method parses complete XML loaded into the DocumentBuilder. Extracts the command
     *  details, forms a command object and stores it in a Map
     *
     */
    private void parseDocument(){
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <command> elements
        NodeList nl = docEle.getElementsByTagName("command");
        if(nl != null && nl.getLength() > 0) {
            for(int i = 0 ; i < nl.getLength();i++) {

                    //get the Command instance from command elements command element
                    Command cmd = getCommand((Element)nl.item(i));
                    commandList.add(cmd);
            }
        }
    }

    /**
     * I take an command element and read the values in, create
     * an Command object and return it
     *
     * @param Element empEl
     * @return Command instance
     */
    private Command getCommand(Element empEl) {

            //for each <command> element get text or int values of
            //id ,pattern, description, sourcetype and source
            int id = getIntValue(empEl,"id");
            String pattern = getTextValue(empEl,"pattern");
            String description = getTextValue(empEl, "description") ;
            String usage = getTextValue(empEl, "usage") ;
            String sourcetype  = getTextValue(empEl, "sourcetype") ;
            String source = getTextValue(empEl, "source") ;
            String qFields = getTextValue(empEl, "queryfields") ;
            String dFields = getTextValue(empEl, "displayfields") ;
            String defOpr = getTextValue(empEl, "defaultoperator") ;
            String interactive = getTextValue(empEl, "interactive") ;
            List questions = getListValue(empEl, "question") ;
            String doneMessage = getTextValue(empEl, "donemessage") ;
            System.out.println(questions) ;

            //Create a new Command with the value read from the xml nodes
            Command e = new Command(id,pattern,description,usage,sourcetype, source, qFields, dFields, defOpr, questions, interactive, doneMessage);
            //System.out.println(e) ;
            return e;
    }

    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content
     * i.e for <command><pattern>show</pattern></command> xml snippet if
     * the Element points to command node and tagName is pattern I will return show
     *
     * @param ele
     * @param tagName
     * @return
     */
    private String getTextValue(Element ele, String tagName) {
        //System.out.println(tagName + " " + ele) ;
        String textVal = null;
        try {
            NodeList nl = ele.getElementsByTagName(tagName);
            if(nl != null && nl.getLength() > 0) {
                Element el = (Element)nl.item(0);
                //System.out.println("---" + el) ;
                textVal = el.getFirstChild().getNodeValue();
            }

        }
        catch(NullPointerException e) {
            System.out.println(e + " " + tagName) ;
        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     *
     * @param ele
     * @param tagName
     * @return
     */
    private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
    }

    /**
     * Functions traverses through the tag, finds out all the child and return
     * a list containing child of the element
     *
     * @param ele
     * @param tagName
     * @return
     */
    private List getListValue(Element ele, String tagName) {
        ArrayList childList = new ArrayList() ;
        try {
            NodeList nl = ele.getElementsByTagName(tagName);
            if(nl != null && nl.getLength() > 0) {
                for(int i = 0; i < nl.getLength(); i++) {
                    childList.add(nl.item(i).getTextContent()) ;
                }
            }
        }
        catch(NullPointerException e) {
            System.out.println(e + " " + tagName) ;
        }
        return childList ;
    }

    private String getJarFolder() {
            String name = this.getClass().getName().replace('.', '/');
            System.out.println("Name " + name);
            System.out.println(this.getClass());
            String s = this.getClass().getResource("/" + name + ".class").getPath().toString();
            // String s = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            System.out.println(name + " -- " + s) ;
            s = s.replace('/', File.separatorChar);
            System.out.println(name + " *** " + s) ;
            s = s.substring(0, s.indexOf(".jar")+4);
            System.out.println(name + " == " + s) ;
            s = s.substring(s.lastIndexOf(':')-1);
            return s.substring(0, s.lastIndexOf(File.separatorChar)+1);
    }


}
