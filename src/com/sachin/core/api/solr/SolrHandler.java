/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.api.solr;

import com.sachin.app.App;
import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import com.sachin.core.utils.Utils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;



/**
 * Class Handles communication between Solr server and chatter
 * for retrieval of data
 *
 * @author sachin
 */
public class SolrHandler implements IDataSource {
    public static CommonsHttpSolrServer  SERVER = null ;
    private String _solrUrl = null ;
    private String[] _queryFields = null ;
    private String[] _displayFields = null ;
    private String _defaultOperator = "OR" ;

    /**
     * Takes Solr URL as a string parameter and assigns it to
     * class's private member variable
     *
     * @param solrUrl
     */
    public SolrHandler(String solrUrl, String queryFields, String displayFields, String defaultOperator) {
        if(solrUrl.equals("") || solrUrl == null) {
            throw new NullPointerException("Solr url cannot be empty") ;
        }
        _queryFields = queryFields.split(",") ;
        _displayFields = displayFields.split(",") ;
        _defaultOperator = StringUtils.trim(defaultOperator) ;
        _solrUrl = solrUrl ;
    }

    /**
     * Factory method to get Solr Server
     * The method keeps singleton instance of SolrServer.
     *
     * @return
     */
    private SolrServer getSolrServer() {
        if(SERVER == null) {
            try {
                SERVER = new CommonsHttpSolrServer(_solrUrl) ;
                SERVER.setSoTimeout(1000);  // socket read timeout
                SERVER.setConnectionTimeout(100);
                SERVER.setDefaultMaxConnectionsPerHost(100);
                SERVER.setMaxTotalConnections(100);
                SERVER.setFollowRedirects(false);  // defaults to false
                // allowCompression defaults to false.
                // Server side must support gzip or deflate for this to have any effect.
                SERVER.setAllowCompression(true);
                SERVER.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
                SERVER.setParser(new XMLResponseParser());
            }
            catch (MalformedURLException ex) {
                App.logger.info(ex.getMessage());
            }
            catch(Exception ex) {
                App.logger.info(ex.getMessage());
            }
        }
        return SERVER ;
    }

    public String pullData(Command command, String[] args, int page, int totalRecords) {
        String response = "";
        System.out.println("Printing args") ;
        Utils.printArray(args);
        String page1 = page + "" ;
        String records = totalRecords + "" ;
        try {
            SolrQuery query = new SolrQuery();
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.clear();
            // Build search query
            int i = 0;
            int j = 0 ;
            String q = "" ;
            System.out.println("args length " + args.length + "  query length " + _queryFields.length) ;
            for(i=0,j=0; i < args.length && j < _queryFields.length; i++, j++) {
                if(!StringUtils.trim(args[i]).equals("") && StringUtils.trim(args[i]) != null) {
                    if(i == (args.length - 1) || j == (_queryFields.length - 1)) {
                        q += StringUtils.trim(_queryFields[j]) + ":" + StringUtils.trim(args[i]) ;
                    }
                    else {
                        q += StringUtils.trim(_queryFields[j]) + ":" + StringUtils.trim(args[i]) + " " + _defaultOperator + " " ;
                    }
                }
            }
            if(q.equals("")) {
                q = "*:*" ;
            }
            System.out.println(q) ;
            // params.set("q", "moviename:shor OR theatername:pvr");
            params.set("q", q);
            // params.set("fl", "id,showid,moviename,theatername,theateraddress,showtimings");
            System.out.println("Display fields " + StringUtils.join(_displayFields, ","));
            params.set("fl", StringUtils.join(_displayFields, ","));
            params.set("start", page1);
            params.set("rows", records);
            params.set("wt", "xml");
            System.out.println(params.toString());

            QueryResponse queryResponse = getSolrServer().query(params);
            Iterator<SolrDocument> iter = queryResponse.getResults().iterator();
            String content, id, theateraddress, showtimings ;
            while (iter.hasNext()) {
                SolrDocument resultDoc = iter.next();
                for(i=0; i<_displayFields.length;i++) {
                    System.out.println("field " + _displayFields[i]) ;
                    response += _displayFields[i] + ":" + resultDoc.getFieldValue(StringUtils.trim(_displayFields[i])).toString() + " ";
                }
                response += "\n\r" ;
                //content = resultDoc.getFieldValue("moviename").toString();
                //id = resultDoc.getFieldValue("showid").toString();
                ///theateraddress = (String) resultDoc.getFieldValue("theateraddress").toString() ;
                //showtimings = resultDoc.getFieldValues("showtimings").toString() ;

                //response += "showid:" + id + ", moviename:" + content + ", theateraddress:" + theateraddress + ", timings:" + showtimings + "\n\r" ;
            }
            System.out.println("Done") ;
        }
        catch (SolrServerException ex) {
            App.logger.info(ex.getMessage());
        }
        catch(Exception ex) {
            App.logger.info(ex.getMessage());
        }
        System.out.println("Returning output") ;
        return response;
    }

    public boolean createSampleServer() {
        try {
            getSolrServer().deleteByQuery("*:*");  // delete everything!
            SolrInputDocument doc1 = new SolrInputDocument();
            doc1.addField( "id", "id1", 1.0f );
            doc1.addField( "showid", "1");
            doc1.addField( "moviename", "Shor in the city" );
            doc1.addField( "theatername", "inorbit" );
            doc1.addField( "theateraddress", "malad, link road" );
            doc1.addField( "showtimings", "2011-05-16 13:23:43" );
            doc1.addField( "showtimings", "2011-05-16 16:23:43" );
            doc1.addField( "showtimings", "2011-05-16 19:23:43" );
            doc1.addField( "showtimings", "2011-05-16 20:23:43" );

            SolrInputDocument doc2 = new SolrInputDocument();
            doc2.addField( "id", "id2", 1.0f );
            doc2.addField( "showid", "2");
            doc2.addField( "moviename", "stanley ka dabba" );
            doc2.addField( "theatername", "pvr goregoan" );
            doc2.addField( "theateraddress", "goregoan" );
            doc2.addField( "showtimings", "2011-05-16 13:23:43" );
            doc2.addField( "showtimings", "2011-05-16 16:23:43" );
            doc2.addField( "showtimings", "2011-05-16 19:23:43" );
            doc2.addField( "showtimings", "2011-05-16 20:23:43" );

            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            docs.add( doc1 );
            docs.add( doc2 );

            getSolrServer().add(docs);
            getSolrServer().commit();

        }
        catch (SolrServerException ex) {
            Logger.getLogger(SolrHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(SolrHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public String pullAds() {
        return "" ;
    }

}
