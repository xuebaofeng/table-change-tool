package bf.cg;

import com.google.common.base.CaseFormat;

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
	private static String ticket;
	private static String userName;
	private static String baseDirServices;
	private static String commonPath;
	private static String javaClassName;
	private static String columns;
	private static String ddlPath;

	private static boolean inited;

	public static void main(String[] args) throws Exception {
		init(args);

		GenSql.main(args);
		GenJpa.main(args);
		GenToplinkMap.main(args);
		GenToplinkTable.main(args);
	}

	public static String getDdlPath() {
		return ddlPath;
	}

	public static void init(String[] args) {
		if (inited) return;

		table = "LC$ADDR";
		ticket = "63428";
		userName = "Baofeng Xue";
		baseDirServices = "/Users/bxue/tlc/main/lc-services";
		commonPath = "/Users/bxue/tlc/lc-common";
		javaClassName = "LcAddress";
		columns = "city,zip,street,street_no";
		ddlPath = baseDirServices + "/db/lc/release111/schema_01_encryption_addr.sql";

/*        Option option = new Option();
		new JCommander(option, args);
        table = option.table;
        commonPath = option.commonPath;*/
		inited = true;
	}


	public static String getTable() {
		return table;
	}

	public static String getTicket() {
		return ticket;
	}

	public static String getUserName() {
		return userName;
	}

	public static String getBaseDirServices() {
		return baseDirServices;
	}

	public static String getCommonPath() {
		return commonPath;
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

	static String toProperty(String str) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
	}

	static String toCap(String str) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, str);
	}

	static String getShortTableName() {
		return table.split("\\$")[1];
	}

	static String getShortCapTableName() {
		return toCap(getShortTableName());
	}

}
