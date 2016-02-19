package bf.cg;

import com.beust.jcommander.JCommander;
import com.google.common.base.CaseFormat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
	private static String username;
	private static String baseDirServices;
	private static String commonPath;
	private static String jpa;
	private static String columns;
	private static String ddlPath;

	private static boolean inited;
	private static JCommander jCommander;

	public static void main(String[] args) throws Exception {
		init(args);

		GenSql.main(args);
		GenJpa.main(args);
		GenToplinkMap.main(args);
		GenToplinkTable.main(args);
		GenTopLinkJava.main(args);
	}

	public static String getDdlPath() {
		return ddlPath;
	}

	public static void init(String[] args) {
		if (inited) return;

		Option option = new Option();
		jCommander = null;
		try {
			jCommander = new JCommander(option);
			jCommander.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			if (jCommander != null) {
				jCommander.usage();
				System.exit(1);
			}
		}
		table = option.table.toUpperCase();
		commonPath = option.commonPath;
		String mainPath = option.mainPath;

		baseDirServices = mainPath + "/lc-services";

		boolean exists = Paths.get(baseDirServices).toFile().exists();
		if (!exists) {
			System.err.println(baseDirServices + " not exists");
			if (jCommander != null) {
				jCommander.usage();
			}
			System.exit(1);
		}

		jpa = option.jpa;
		columns = option.columns.toUpperCase();
		if (option.ticket != null)
			ticket = option.ticket;
		if (option.username != null)
			username = option.username;

		ddlPath = baseDirServices + "/db/lc/release111/schema_01_encryption_" + getShortTableName() + ".sql";

		inited = true;
	}

	public static String getTable() {
		return table;
	}

	public static String getTicket() {
		return ticket;
	}

	public static String getUsername() {
		return username;
	}

	public static String getBaseDirServices() {
		return baseDirServices;
	}

	public static String getCommonPath() {
		return commonPath;
	}

	public static String getJpa() {
		return jpa;
	}

	public static String getColumns() {
		return columns;
	}

	static List<String> readLines(String path) {
		boolean exists = Paths.get(path).toFile().exists();
		ArrayList<String> ret = new ArrayList<>();

		if (!exists) {
			System.err.println(path + " not exists");
			jCommander.usage();
			return ret;
		}
		try {
			return Files.lines(Paths.get(path)).collect(Collectors.<String>toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	static String toProperty(String str) {
		String to = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
		to = to.replace("Fname", "FName").replace("Mname", "MName").replace("Lname", "LName");
		return to;
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

	static PrintWriter getPrintWriter(String path) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(path);
		System.out.println(path);
		return pw;
	}

	static void usage() {
		jCommander.usage();
	}

}
