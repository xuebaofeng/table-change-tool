package bf.cg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Baofeng(Shawn) Xue on 12/10/15.
 */
public class GenTopLinkSql extends SqlSupport {
	static Logger logger = LoggerFactory.getLogger(GenTopLinkSql.class);

	public static void main(String[] args) throws Exception {
		Main.init(args);
		String tableName = Main.getTable();
		String ticketId = Main.getTicket();
		String user = Main.getUsername();

		String path = Main.getDdlPath();

		if (Main.getMainPath() == null) {
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

	}

}
