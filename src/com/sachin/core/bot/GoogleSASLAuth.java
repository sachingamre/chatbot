/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.core.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

/**
 *
 * @author saching
 */
public class GoogleSASLAuth extends SASLMechanism {

    public static final String NAME="X-GOOGLE-TOKEN";

    public GoogleSASLAuth( SASLAuthentication saslAuthentication ) {
        super( saslAuthentication );
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    public void authenticate( String username, String host, String password ) throws IOException, XMPPException {
        super.authenticate( username, host, password );
    }

    @Override
    protected void authenticate() throws IOException, XMPPException {
        String authCode = getAuthCode( authenticationId, password );
        String jidAndToken = "\0" + URLEncoder.encode( authenticationId, "utf-8" ) + "\0" + authCode;

        final StringBuilder stanza = new StringBuilder();
        stanza.append( "<auth mechanism=\"" ).append( getName() );
        stanza.append( "\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" );
        stanza.append( Base64.encodeBytes( jidAndToken.getBytes( "UTF-8" ), Base64.DONT_BREAK_LINES ) );
        stanza.append( "</auth>" );
        System.out.println("--------------------------------------------");
        System.out.println(stanza.toString());
        System.out.println("--------------------------------------------");
        // Send the authentication to the server
        Packet p=new Packet() {
            @Override
            public String toXML() {
                return stanza.toString();
            }
        };
        getSASLAuthentication().send(p);
    }

    public String getAuthCode( String username, String password ) throws IOException {
        StringBuilder urlToRead = new StringBuilder();
        urlToRead.append( "https://www.google.com/accounts/ClientLogin?accountType=GOOGLE&service=mail&" );
        urlToRead.append("Email=").append(username).append( "&");
        urlToRead.append("Passwd=").append( password);

        URL url = new URL( urlToRead.toString() );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod( "GET" );

        BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );

        try {
            String line;
            while ( ( line = rd.readLine() ) != null ) {
                if ( line.startsWith( "Auth=" ) )
                    return line.substring( 5 );
            }
            return null;
        }
        finally {
            rd.close();
        }
    }
}
