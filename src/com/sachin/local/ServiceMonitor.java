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
public class ServiceMonitor implements IDataSource {

    protected static enum ALLOWED_SERVICES {
        mysql, httpd
    }

    public String pullData(Command command, String[] args, int page, int totalRecords) {
        System.out.println("Argument count " + args.length);
        System.out.println("service " + args[0]);
        System.out.println("server " + args[1]);
        if(args.length > 1) {
            String serviceName = args[0];
            String server = args[1];
            if(!"".equals(serviceName)) {
                try {
                    ALLOWED_SERVICES service = ALLOWED_SERVICES.valueOf(serviceName);
                    switch(service) {
                        case mysql  : if(isUpMysql(server)) {
                                                return "Mysql service is running";
                                            } else {
                                                return "Mysql service is down";
                                            }
                                            //break;
                        case httpd  : if(isUpApache(server)) {
                                                return "Apache is running";
                                            } else {
                                                return "Apache is down";
                                            }
                                            //break;
                        default :   return "Service not supported";
                    }
                } catch(Exception ex) {
                    return "Unsupported service";
                }
            }
        } else {
            return "Usage: isup servicename serverip";
        }
        return "";
    }

    protected boolean isUp(String serviceCmd, String server) {
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
                        if(cmd.getExitStatus() == 0) {
                            return true;
                        }
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
        return false;
    }

    protected boolean isUpMysql(String server) {
        String cmd = "ps faux | grep mysql | grep -v grep";
        return isUp(cmd, server);
    }

    protected boolean isUpApache(String server) {
        String cmd = "ps faux | grep httpd | grep -v grep";
        return isUp(cmd, server);
    }

    public String pullAds() {
        return "";
    }
}
