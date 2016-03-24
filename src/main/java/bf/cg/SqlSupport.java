package bf.cg;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Baofeng(Shawn) Xue on 12/23/15.
 */
public class SqlSupport {

    private static BoneCPDataSource dataSource;

    static {
        BoneCPConfig config = new BoneCPConfig();

        String env = "dev";
        if ("dev".equals(env)) {
            config.setJdbcUrl("jdbc:oracle:thin:@//lcdev.tlcinternal.com:1521/lcdev.lendingclub.com");
        } else {
            config.setJdbcUrl("jdbc:oracle:thin:@//lcscrubgg.tlcinternal.com:1521/LCSCRUBGG.lendingclub.com");
        }

        dataSource = new BoneCPDataSource(config);

        dataSource.setDriverClass("oracle.jdbc.OracleDriver");
    }


    protected static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static Map<Integer, Integer> getCalculateMap() {
        Map<Integer, Integer> map = new HashMap<>();
        List<String> lines = Main.readLines("EncryptedFieldSizeCalculator.csv");
        for (String line : lines) {
            String[] parts = line.split(",");
            map.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        return map;
    }

    protected static Map<String, Integer> getSizeMap(String tableName) throws Exception {
        GenTopLinkSql.logger.debug("table:{}", tableName);

        Map<String, Integer> sizeMap = new HashMap<>();
        Connection connection = getConnection();

        assert connection != null;
        ResultSet rslt = connection.createStatement().executeQuery("SELECT * FROM " + tableName);
        ResultSetMetaData rsmd = rslt.getMetaData();
        int noCol = rsmd.getColumnCount();
        String colName;
        int colSize;

        if (rslt.next()) {

            for (int i = 1; i <= noCol; ++i) {
                colName = rsmd.getColumnName(i);
                colSize = rsmd.getColumnDisplaySize(i);
                int columnType = rsmd.getColumnType(i);
                if (Types.DATE == columnType || Types.TIMESTAMP == columnType) {
                    sizeMap.put(colName, -1);
                } else sizeMap.put(colName, colSize);
            }
        }
        connection.close();
        return sizeMap;
    }
}
