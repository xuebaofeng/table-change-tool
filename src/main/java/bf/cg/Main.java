package bf.cg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Baofeng(Shawn) Xue on 2/8/16.
 */
public class Main {


    private static String table;
    private static String ticketDatabase;
    private static String userName;
    private static String baseDirServices;
    private static String baseDirCommon;
    private static String javaClassName;
    private static String columns;
    private static String ddlPath;

    private static boolean inited;

    public static void main(String[] args) throws Exception {
        init();

        GenSql.main(null);
        GenJava.main(null);
        GenToplinkMap.main(null);
        GenToplinkTable.main(null);
    }

    public static String getDdlPath() {
        return ddlPath;
    }

    public static void init() {
        if (inited) return;

        table = "LC$ADDR";
        ticketDatabase = "63428";
        userName = "Baofeng Xue";
        baseDirServices = "z:";
        baseDirCommon = "z:/lc-common";
        javaClassName = "LcAddress";
        columns = "city,zip,street,street_no";
        ddlPath = baseDirServices + "/db/lc/release111/schema_01_encryption_addr.sql";

        inited = true;
    }


    public static String getTable() {
        return table;
    }

    public static String getTicketDatabase() {
        return ticketDatabase;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getBaseDirServices() {
        return baseDirServices;
    }

    public static String getBaseDirCommon() {
        return baseDirCommon;
    }

    public static String getJavaClassName() {
        return javaClassName;
    }

    public static String getColumns() {
        return columns;
    }

    static List<String> readLines(String path) {
        try {
            return Files.lines(Paths.get(path)).collect(Collectors.<String>toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
