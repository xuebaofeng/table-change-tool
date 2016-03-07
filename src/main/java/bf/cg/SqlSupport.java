package bf.cg;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Baofeng(Shawn) Xue on 12/23/15.
 */
public class SqlSupport {
	private static Logger logger = LoggerFactory.getLogger(SqlSupport.class);

	private static BoneCPDataSource dataSource;
	private static String env = "dev";

	static {
		BoneCPConfig config = new BoneCPConfig();

		dataSource.setDriverClass("oracle.jdbc.OracleDriver");
	}

	protected static DataSource getDataSource() {
		return dataSource;
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

		ResultSet rslt = connection.createStatement().executeQuery("SELECT * FROM " + tableName);
		ResultSetMetaData rsmd = rslt.getMetaData();
		int noCol = rsmd.getColumnCount();
		String colName = "";
		int colSize = 0;

		if (rslt.next()) {

			for (int i = 1; i <= noCol; ++i) {
				colName = rsmd.getColumnName(i);
				colSize = rsmd.getColumnDisplaySize(i);
				int columnType = rsmd.getColumnType(i);
				logger.debug("name:{},type:{},size:{}", colName, columnType, colSize);
				if (Types.DATE == columnType || Types.TIMESTAMP == columnType) {
					sizeMap.put(colName, 48);
				} else sizeMap.put(colName, colSize);
			}
		}
		connection.close();
		return sizeMap;
	}
}
