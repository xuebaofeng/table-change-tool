package bf.cg;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Baofeng(Shawn) Xue on 12/10/15.
 */
public class GenSql extends DbSupport {

    public static void main(String[] args) throws Exception {
        Main.init();
        String tableName = Main.getTable();
        String ticketId = Main.getTicketDatabase();
        String user = Main.getUserName();
        String[] columns = Main.getColumns().split(",");

        String path = Main.getDdlPath();
        PrintWriter pw = new PrintWriter(path);
        System.out.println(path);

        final String[] addSql = {String.format("--liquibase formatted sql\n" +
                "--============================================================================\n" +
                "-- LMS-%s add encrypted columns for %s\n" +
                "--============================================================================\n" +
                "--changeset %s:LMS-%s_%s\n" +
                "\n" +
                "ALTER TABLE  %s ADD(", ticketId, tableName, user, ticketId, tableName, tableName)};

//		List<String> list = newJdbcTemplate().queryForList("select COLUMN_NAME from LC_ENC_COLUMN_CONFIG where table_name=?", String.class, tableName);
        List<String> list = Arrays.asList(columns);

        Map<String, Integer> sizeMap = getSizeMap(tableName);
        Map<Integer, Integer> calculateMap = getCalculateMap();

        List<String> lines = list.stream().map(String::toUpperCase).map(columnName -> {
            int size = sizeMap.get(columnName);
            return "\n\t" + columnName + "_ENC VARCHAR2(" + calculateMap.get(size) + " CHAR)";
        }).collect(Collectors.toList());

        String columnClause = String.join(",", lines);
        addSql[0] += columnClause;

        addSql[0] += "\n);";

        final String[] rollbackSql = {String.format("\n--rollback alter table %s drop (", tableName)};

        rollbackSql[0] += String.join("_ENC,", list);
        rollbackSql[0] += "_ENC);";

        pw.println(addSql[0] + rollbackSql[0]);
        pw.close();

        generateTestSchema(columnClause);

    }

    private static void generateTestSchema(String columnClause) throws FileNotFoundException {
        String tableName = Main.getTable();

        List<String> lines;
        String inputFile = Main.getBaseDirCommon() + "/lc-dao/src/test/resources/lc-dao-full-schema.sql";
        String outputFile = inputFile + ".bak";
        PrintWriter pw = new PrintWriter(outputFile);
        System.out.println(outputFile);
        lines = Main.readLines(inputFile);
        String tableBegin = String.format("create table %s (", tableName.toUpperCase());
        boolean tableFound = false;
        for (String line : lines) {
            if (line.contains(tableBegin)) {
                tableFound = true;
            }

            if (tableFound) {

                if (line.contains("primary key")) {
                    pw.println(columnClause + ",");
                    tableFound = false;
                }
            }

            pw.println(line);

        }
        pw.close();
    }

    private static Map<Integer, Integer> getCalculateMap() {
        Map<Integer, Integer> map = new HashMap<>();
        List<String> lines = Main.readLines("EncryptedFieldSizeCalculator.csv");
        for (String line : lines) {
            String[] parts = line.split(",");
            map.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        return map;
    }

    private static Map<String, Integer> getSizeMap(String tableName) throws Exception {
        Map<String, Integer> sizeMap = new HashMap<>();
       /* Connection connection = getConnection();
        ResultSet rslt = connection.createStatement().executeQuery("SELECT * FROM tlc." + tableName);
        ResultSetMetaData rsmd = rslt.getMetaData();
        int noCol = rsmd.getColumnCount();
        String colName = "";
        int colSize = 0;

        if (rslt.next()) {

            for (int i = 1; i <= noCol; ++i) {
                colName = rsmd.getColumnName(i);
                colSize = rsmd.getColumnDisplaySize(i);
                sizeMap.put(colName, colSize);
            }
        }
        connection.close();*/
        sizeMap.put("CITY", 100);
        sizeMap.put("STREET", 200);
        sizeMap.put("STREET_NO", 300);
        sizeMap.put("ZIP", 400);
        return sizeMap;
    }

}
