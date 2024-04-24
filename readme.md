# 下載AIX上的檔案
<pre style="color:#000000;background:#ffffff;">
public class GetAixFile {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final int PORT = 22;
    private final int SESSION_TIMEOUT = 10000;
    private final int CHANNEL_TIMEOUT = 5000;

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
            // download file from remote server to local
            channelSftp.get(remoteFileName, localFileName);
            channelSftp.exit();
        } catch (JSchException | SftpException e) {
            if (e instanceof SftpException && ((SftpException) e).id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw new RuntimeException("遠端檔案不存在", e);
            } else {
                throw new RuntimeException("請檢查檔案路徑是否正確.", e);
            }
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }
}
</pre>
## 執行程式
<pre style="color:#000000;background:#ffffff;">>
public class DoGetAixFile {
    public static void main(String[] args) {
        GetAixFile gf = new GetAixFile();
        Map<String,String> map = new HashMap<>();
        //aixHost, userName, password, localFileName, remoteFileName
        map.put("aixHost", "server");
        map.put("userName", "username");
        map.put("password", "password");
        map.put("localFileName", "C:\\temp\\readme.txt");
        map.put("remoteFileName", "readme.txt");
        gf.getAixFile(map);
    }
}
</pre>