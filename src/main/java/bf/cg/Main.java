package bf.cg;

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

	public static void main(String[] args) throws Exception {
		table = "LC$ADDR";
		ticketDatabase = "63428";
		userName = "Baofeng Xue";
		baseDirServices = "/Users/bxue/tlc/main/lc-services";
		baseDirCommon = "/Users/bxue/tlc/lc-common";
		javaClassName = "LcAddress";
		columns = "city,zip,street,street_no";

		GenSql.main(new String[]{table, ticketDatabase, userName, baseDirServices + "/db/lc/release111/schema_01_encryption_addr.sql"});
		GenJava.main(new String[]{baseDirCommon, table, javaClassName, columns});
		GenToplinkMap.main(new String[]{baseDirServices + "/lc-main", table, columns});
		GenToplinkTable.main(new String[]{baseDirServices + "/lc-main", table, columns});
	}
}
