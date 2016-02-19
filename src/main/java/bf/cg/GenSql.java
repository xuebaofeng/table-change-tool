package bf.cg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Baofeng(Shawn) Xue on 12/10/15.
 */
public class GenSql extends DbSupport {
	static Logger logger = LoggerFactory.getLogger(GenSql.class);

	public static void main(String[] args) throws Exception {
		Main.init(args);
		String tableName = Main.getTable();
		String ticketId = Main.getTicket();
		String user = Main.getUsername();

		String path = Main.getDdlPath();

		if (Main.getCommonPath() == null) {
			Main.usage();
			return;
		}

		PrintWriter pw = Main.getPrintWriter(path);

		final String[] addSql = {String.format("--liquibase formatted sql\n" +
			"--============================================================================\n" +
			"-- LMS-%s add encrypted columns for %s\n" +
			"--============================================================================\n" +
			"--changeset %s:LMS-%s_%s\n" +
			"\n" +
			"ALTER TABLE  %s ADD(", ticketId, tableName, user, ticketId, tableName, tableName)};

		String[] columns = Main.getColumns().split(",");
		List<String> list = Arrays.asList(columns);

		Map<String, Integer> sizeMap = getSizeMap(tableName);
		Map<Integer, Integer> calculateMap = getCalculateMap();

		List<String> lines = list.stream().map(String::toUpperCase).map(columnName -> {
			int size = sizeMap.get(columnName);
			return "\n    " + columnName + "_ENC VARCHAR2(" + calculateMap.get(size) + " CHAR)";
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
		String inputFile = Main.getCommonPath() + "/lc-dao/src/test/resources/lc-dao-full-schema.sql";
		PrintWriter pw = Main.getPrintWriter(inputFile + ".bak");
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
		logger.debug("table:{}", tableName);

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
				sizeMap.put(colName, colSize);
			}
		}
		connection.close();
		return sizeMap;
	}

}
