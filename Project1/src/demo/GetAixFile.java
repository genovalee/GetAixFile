package demo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.util.Map;
import java.util.logging.Logger;

public class GetAixFile {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final int PORT = 22;
    private final int SESSION_TIMEOUT = 10000;
    private final int CHANNEL_TIMEOUT = 5000;

    public GetAixFile() {
        super();
    }

    public void getAixFile(Map<String, String> args) {
        String aixHost = "", userName = "", password = "", localFileName = "", remoteFileName = "";
        for (Map.Entry<String, String> arg : args.entrySet()) {
            switch (arg.getKey().toUpperCase()) {
            case "AIXHOST":
                aixHost = arg.getValue();
                break;
            case "USERNAME":
                userName = arg.getValue();
                break;
            case "PASSWORD":
                password = arg.getValue();
                break;
            case "LOCALFILENAME":
                localFileName = arg.getValue();
                break;
            case "REMOTEFILENAME":
                remoteFileName = arg.getValue();
                break;
            }
        }
        Session jschSession = null;
        try {
            JSch jsch = new JSch();
            //            jsch.setKnownHosts("/home/mkyong/.ssh/known_hosts");
            jschSession = jsch.getSession(userName, aixHost, PORT);

            // not recommend, please use jsch.setKnownHosts, security concern
            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using password
            jschSession.setPassword(password);
            // 10 seconds session timeout
            jschSession.connect(SESSION_TIMEOUT);
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            sftp.connect(CHANNEL_TIMEOUT);
            ChannelSftp channelSftp = (ChannelSftp) sftp;

            //             transfer file from local to remote server
            //            channelSftp.put(localFileName, remoteFileName);

            // download file from remote server to local
            channelSftp.get(remoteFileName, localFileName);
            channelSftp.exit();
        } catch (JSchException | SftpException e) {
            if (e instanceof SftpException && ((SftpException) e).id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw new RuntimeException("遠端檔案不存在", e);
            } else {
                throw new RuntimeException("請檢查檔案路徑是否正確.", e);
                //                e.printStackTrace();
            }
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
        //        System.out.println("Done");
    }
}
