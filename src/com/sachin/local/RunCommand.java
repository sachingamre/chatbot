/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.local;

import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import java.io.IOException;
import java.security.PublicKey;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

/**
 *
 * @author saching
 */
public class RunCommand implements IDataSource {

    protected static enum ALLOWED_COMMANDS {
        ls, cat
    }

    public String pullData(Command command, String[] args, int page, int totalRecords) {
        System.out.println("Argument count " + args.length);
        System.out.println("server " + args[0]);
        System.out.println("command " + args[1]);
        if(args.length > 1) {
            String server = args[0];
            String commandToRun = args[1];
            if(!"".equals(server) && !"".equals(commandToRun)) {
                String[] commandParts = commandToRun.split("\\s+");
                try {
                    ALLOWED_COMMANDS rCmd = ALLOWED_COMMANDS.valueOf(commandParts[0]);
                    switch(rCmd) {
                        case ls  :   return run(commandToRun, server);
                        case cat  : return run(commandToRun, server);
                        default :   return "Command not supported";
                    }
                } catch(Exception ex) {
                    return "Unsupported command";
                }
            }
        } else {
            return "Usage: isup servicename serverip";
        }
        return "";
    }

    protected String run(String serviceCmd, String server) {
        String statistics = "";
        if(serviceCmd != null && !serviceCmd.equalsIgnoreCase("") && server != null && !server.equalsIgnoreCase("")) {
            try {
                final SSHClient ssh = new SSHClient();
                // ssh.loadKnownHosts();
                // ssh.addHostKeyVerifier("ff:4a:0d:eb:e1:48:a4:f8:f4:3c:90:04:29:c5:4f:31");
                ssh.addHostKeyVerifier(
                    new HostKeyVerifier() {
                        public boolean verify(String arg0, int arg1, PublicKey arg2) {
                            return true;
                        }
                    }
                );
                ssh.connect(server);
                try {
                    ssh.authPassword("glmstudio", "studiorules");
                    final Session session = ssh.startSession();
                    try {
                        final net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session.exec(serviceCmd);
                        statistics = IOUtils.readFully(cmd.getInputStream()).toString();
                        System.out.println(statistics);
                        cmd.join(5, TimeUnit.SECONDS);
                        System.out.println("\n** exit status: " + cmd.getExitStatus());
                        return statistics;
                    } finally {
                        session.close();
                    }
                } finally {
                    ssh.disconnect();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServiceMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }

    public String pullAds() {
        return "";
    }
}
