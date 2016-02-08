package bf.cg;

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


	/*
	--liquibase formatted sql
	--============================================================================
	-- LMS-62829 add encrypted column for lc$ira_beneficiary
	--============================================================================
	--changeset plockwood:LMS-62829_ira_beneficiary

	ALTER TABLE
	   LC$IRA_BENEFICIARY
	ADD(
	   NAME_ENC VARCHAR2(384 CHAR),
	   ADDR1_ENC VARCHAR2(768 CHAR),
	   ADDR2_ENC VARCHAR2(768 CHAR),
	   CITY_ENC VARCHAR2(384 CHAR),
	   DOB_ENC VARCHAR2(32 CHAR),
	   PH_ENC VARCHAR2(128 CHAR),
	   ZIP_ENC VARCHAR2(32 CHAR));

	--rollback alter table LC$IRA_BENEFICIARY drop (NAME_ENC, ADDR1_ENC, ADDR2_ENC, CITY_ENC, DOB_ENC, PH_ENC, ZIP_ENC);
	* */
	public static void main(String[] args) throws Exception {
		String tableName = args[0];
		String ticketId = args[1];
		String user = args[2];

		String path = args[3];
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
		List<String> list = Arrays.asList(new String[]{"CITY", "STREET", "STREET_NO", "ZIP"});

		Map<String, Integer> sizeMap = getSizeMap(tableName);


		List<String> lines = list.stream().map(columnName -> {
			Double size = sizeMap.get(columnName) * 1.0;
			size *= 6;
			return "\n\t" + columnName + "_ENC VARCHAR2(" + size.intValue() + " CHAR)";
		}).collect(Collectors.toList());

		addSql[0] += String.join(",", lines);

		addSql[0] += "\n);";


		final String[] rollbackSql = {String.format("\n--rollback alter table %s drop (", tableName)};


		rollbackSql[0] += String.join("_ENC,", list);
		rollbackSql[0] += "_ENC);";

		pw.println(addSql[0] + rollbackSql[0]);
		pw.close();

	}

	private static Map<String, Integer> getSizeMap(String tableName) throws Exception {
		Connection connection = getConnection();
		ResultSet rslt = connection.createStatement().executeQuery("SELECT * FROM tlc." + tableName);
		ResultSetMetaData rsmd = rslt.getMetaData();
		int noCol = rsmd.getColumnCount();
		String colName = "";
		int colSize = 0;


		Map<String, Integer> sizeMap = new HashMap<>();
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
