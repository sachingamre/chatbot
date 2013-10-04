/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.local.games;

import com.sachin.app.App;
import com.sachin.core.api.http.RequestMethod;
import com.sachin.core.api.http.RestClient;
import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 * @author sachin.gamre
 */
public class GameFinder implements IDataSource {

    //private String gameSearchURL = "http://172.16.50.60:8983/solr/zapaksite/select?q=contextpath_t:arcade" ;
    private String gameSearchURL = "http://172.16.50.60:8983/solr/zapaksite/select" ;
    private String topGameURL    = "http://stage-www.zapak.com/getgamelist.php" ;
    private String latestGameURL = "http://stage-www.zapak.com/getgamelist.php" ;

    /**
     * The methods returns result for command pattern "find"
     * It first check the number of arguments passed to the method
     * If the arguments are greater than 1 then it checks for particular
     * sub command pattern, else it goes with the other method
     *
     * @param String[] args  subPattern and its arguments
     * @param int page
     * @param int totalRecords
     * @return String commandResponse
     */
    public String pullData(Command command, String[] args, int page, int totalRecords) {

        // If have specific pattern with more than 1 arguments
        String commandResponse = "" ;
        String subPattern = args[0] ;
        // Utils.printArray(args) ;

        // genre
        if(args.length > 0) {
            if(subPattern.equalsIgnoreCase("genre")) {
                // check if genre keyword is given
                if(args.length > 1 && args[1] != null) {
                    RestClient restClient = new RestClient(this.gameSearchURL) ;
                    restClient.addParam("q", "contextpath_t:" + args[1].trim()) ;
                    restClient.addParam("rows", App.PAGE_RECORDS + "") ;
                    restClient.addParam("start", page + "") ;
                    try {
                        restClient.execute(RequestMethod.GET) ;
                        String output = restClient.getResponse() ;
                        commandResponse = _getFormatResultset(output) ;
                   }
                    catch (Exception ex) {
                        ex.printStackTrace() ;
                        App.logger.info(ex.getMessage()) ;
                    }
                }
                else {
                    commandResponse = "Action\n"
                            + "Arcade\n"
                            + "Racing\n"
                            + "Strategy\n" ;
                }
            }
            else if(subPattern.equalsIgnoreCase("top")) {
                RestClient restClient = new RestClient(this.topGameURL) ;
                restClient.addParam("cat", "topgame") ;
                restClient.addParam("count", 5 + "") ;
                try {
                    restClient.execute(RequestMethod.GET) ;
                    String output = restClient.getResponse() ;
                    commandResponse = _getFormatResultset(output) ;
                }
                catch (Exception ex) {
                    ex.printStackTrace() ;
                    App.logger.info(ex.getMessage()) ;
                }
            }
            else if(subPattern.equalsIgnoreCase("latest")) {
                RestClient restClient = new RestClient(this.topGameURL) ;
                restClient.addParam("cat", "latest") ;
                restClient.addParam("count", 5 + "") ;
                try {
                    restClient.execute(RequestMethod.GET) ;
                    String output = restClient.getResponse() ;
                    commandResponse = _getFormatResultset(output) ;
                }
                catch (Exception ex) {
                    ex.printStackTrace() ;
                    App.logger.info(ex.getMessage()) ;
                }
            }
            else {
                RestClient restClient = new RestClient(this.gameSearchURL) ;
                System.out.println("q" + "contextpath_t:" + args[0].trim() + " OR content_type_s:" + args[0].trim()
                        + " OR tags_t:" + args[0].trim() + " OR body_t:" + args[0].trim()
                        + " OR title_t:" + args[0].trim() + " OR author_s:" + args[0].trim()) ;
                restClient.addParam("q", "contextpath_t:" + args[0].trim() + " OR content_type_s:" + args[0].trim()
                        + " OR tags_t:" + args[0].trim() + " OR body_t:" + args[0].trim()
                        + " OR title_t:" + args[0].trim() + " OR author_s:" + args[0].trim()) ;
                restClient.addParam("rows", App.PAGE_RECORDS + "") ;
                restClient.addParam("start", page + "") ;
                try {
                    restClient.execute(RequestMethod.GET) ;
                    String output = restClient.getResponse() ;
                    commandResponse = _getFormatResultset(output) ;
                }
                catch (Exception ex) {
                    ex.printStackTrace() ;
                    App.logger.info(ex.getMessage()) ;
                }
            }
        }
        else {
            HashMap<String, Object> cmd= (HashMap<String, Object>) App.COMMANDS.get("find") ;
            //System.out.println("Value = " + cmd.get("usage").toString()) ;
            commandResponse = cmd.get("usage").toString().concat("\n") ;
        }
        return commandResponse ;
    }

    /**
     * Extracts certain element nodes from the resultants XML, formats it and
     * returns the formatted string
     *
     * @param String foundRecords
     * @return String formattedRecords
     */
    private String _getFormatResultset(String foundRecords) {
        String formattedRecords = "\n";
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document dom = dBuilder.parse(new ByteArrayInputStream(foundRecords.getBytes()));
            XPath xpath = XPathFactory.newInstance().newXPath();
            // XPath Query for showing all nodes value
            XPathExpression expr = xpath.compile("//result/doc/str[@name='title_t'] | //result/doc/str[@name='id'] | //result/doc/arr[@name='url_s']");
            Object result = expr.evaluate(dom, XPathConstants.NODESET) ;
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i += 3) {
                String[] idParts = nodes.item(i).getTextContent().split("_"); // nodes.item(i).getAttributes().item(0).getNodeValue().split("_") ;
                formattedRecords += idParts[0] + ": " + nodes.item(i + 1).getTextContent() + "\n" + "game url: " + nodes.item(i + 2).getFirstChild().getTextContent() + "\n\r";
            }

        }
        catch (XPathExpressionException ex) {
            ex.printStackTrace() ;
            App.logger.info(ex.getMessage());
        }
        catch (SAXException ex) {
            ex.printStackTrace() ;
            App.logger.info(ex.getMessage());
        }
        catch (IOException ex) {
            ex.printStackTrace() ;
            App.logger.info(ex.getMessage());
        }
        catch (ParserConfigurationException ex) {
            ex.printStackTrace() ;
            App.logger.info(ex.getMessage());
        }
        return formattedRecords;
    }

    public String pullAds() {
        throw new UnsupportedOperationException("Not supported yet.") ;
    }

}