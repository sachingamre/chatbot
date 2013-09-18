/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.core.bot;

import com.sachin.core.interfaces.IDataSource;
import java.io.File;
import java.io.Serializable;

import org.jivesoftware.smack.XMPPException;

import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

/**
 *
 * @author suman.holani
 */
public class CustomFileHandler implements Serializable {

    protected String status = null;
    protected FileTransferManager manager = null;
    protected OutgoingFileTransfer transfer = null;

    public CustomFileHandler() {
    }

    public void fileTransfer(String fileName, String destination) throws XMPPException {

        manager = new FileTransferManager(Chatter.connection);
        // Create the file transfer manager
        //FileTransferManager manager = new FileTransferManager(connection);
        FileTransferNegotiator.setServiceEnabled(Chatter.connection, true);


        // Create the outgoing file transfer
        transfer = manager.createOutgoingFileTransfer(destination);
       // suman.holani@gmail.com/Smack05B73129
          //      transfer = manager.createOutgoingFileTransfer("suman.holani@gmail.com/Smack05B73129");



        // Send the file
       File fl=new File(fileName);
       System.out.println("file path is "+fl.getPath()+"and contains "+fl.length() + "destination is "+destination);
    
        transfer.sendFile(fl, "You won't believe this!");

        while (!transfer.isDone()) {

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            // getStatus();
        }

        System.out.println("Is it done? " + transfer.isDone());

    }

    public String pullData(String[] args, int page, int totalRecords) {
         if (transfer.getStatus().equals(Status.error)) {

        System.out.println("ERROR!!! " + transfer.getError());
        } else {
        System.out.println(transfer.getStatus());
        System.out.println(transfer.getProgress());
        }

        System.out.println("Status :: " + transfer.getStatus() + " | Error :: " + transfer.getError() + " | Exception :: " + transfer.getException());
        status = String.valueOf(transfer.getStatus());
        
        return status;
    }

    public String pullAds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
