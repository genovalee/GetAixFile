package demo;

import com.jcraft.jsch.SftpException;

import java.util.HashMap;
import java.util.Map;

public class DoGetAixFile {
    public static void main(String[] args) {
        GetAixFile gf = new GetAixFile();
        Map<String,String> map = new HashMap<>();
        //aixHost, userName, password, localFileName, remoteFileName
        map.put("aixHost", "tprs09");
        map.put("userName", "t078");
        map.put("password", "win_nerj");
        map.put("localFileName", "S:\\fsg001\\p20g01\\log\\output.log");
        map.put("remoteFileName", "20240403.log");
        gf.getAixFile(map);
    }
}
