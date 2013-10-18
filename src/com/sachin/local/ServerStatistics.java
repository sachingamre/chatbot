/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.local;


import com.sachin.core.interfaces.IDataSource;
import java.io.IOException;
import java.security.PublicKey;
import java.util.concurrent.TimeUnit;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

/**
 *
 * @author saching
 */
public class ServerStatistics implements IDataSource {

    public String pullData(com.sachin.core.ds.Command command, String[] args, int page, int totalRecords) {
        System.out.println("Fetching statistics for " + args[0]);
        try {
            if(args[0] != null && !args[0].equalsIgnoreCase("")) {
                return "\nStatistics------------------------\n" + getStatistics(args[0]);
            } else {
                return "\nUsage: serverstats <server ip>";
            }
        } catch(Exception ex) {
            return ex.getMessage();
        }
    }

    public String pullAds() {
        return "";
    }

    public String getStatistics(String server) throws IOException {
        //  public static void main (String... args) throws IOException {
        String statistics = "";
        if(server != null && !server.equalsIgnoreCase("")) {
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
                    final net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session.exec("/usr/bin/top -b -n 1 | /usr/bin/head -5");
                    // final net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session.exec("/bin/ls -l /home");
                    // final net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session.exec("ping -c 1 www.google.com");
                    statistics = IOUtils.readFully(cmd.getInputStream()).toString();
                    System.out.println(statistics);
                    cmd.join(5, TimeUnit.SECONDS);
                    System.out.println("\n** exit status: " + cmd.getExitStatus());
                } finally {
                    session.close();
                }
            } finally {
                ssh.disconnect();
            }
        }
        return statistics;
    }


    protected String uptime(Session session) {
        String uptime = "";
        try {
            net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session.exec("uptime");
            uptime = IOUtils.readFully(cmd.getInputStream()).toString();
            //cmd.join(5, TimeUnit.SECONDS);
            // System.out.println("\n** exit status: " + cmd.getExitStatus());
        } catch (ConnectionException ex) {
            return "ConnectionException while fetching uptime " + ex.getMessage();
        } catch (TransportException ex) {
            return "TransportException while fetching uptime " + ex.getMessage();
        } catch (IOException ex) {
            return "IOException while fetching uptime " + ex.getMessage();
        }
        return uptime;
    }

    protected String diskUsage(Session session) {
        String diskUsage = "";
        try {
            net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session.exec("df -h");
            diskUsage = IOUtils.readFully(cmd.getInputStream()).toString();
            //cmd.join(5, TimeUnit.SECONDS);
            // System.out.println("\n** exit status: " + cmd.getExitStatus());
        } catch (ConnectionException ex) {
            return "ConnectionException while fetching disk usage " + ex.getMessage();
        } catch (TransportException ex) {
            return "TransportException while fetching disk usage " + ex.getMessage();
        } catch (IOException ex) {
            return "IOException while fetching disk usage " + ex.getMessage();
        }
        return diskUsage;
    }

    protected String cpuinfo(Session session) {
        String cpuinfo = "";
        try {
            net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session.exec("df -h");
            cpuinfo = IOUtils.readFully(cmd.getInputStream()).toString();
            //cmd.join(5, TimeUnit.SECONDS);
            // System.out.println("\n** exit status: " + cmd.getExitStatus());
        } catch (ConnectionException ex) {
            return "ConnectionException while fetching cpuinfo " + ex.getMessage();
        } catch (TransportException ex) {
            return "TransportException while fetching cpuinfo " + ex.getMessage();
        } catch (IOException ex) {
            return "IOException while fetching cpuinfo " + ex.getMessage();
        }
        return cpuinfo;
    }

    protected String meminfo(Session session) {
        return "";
    }
}
