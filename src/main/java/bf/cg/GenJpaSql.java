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
public class GenJpaSql extends SqlSupport {
	static Logger logger = LoggerFactory.getLogger(GenJpaSql.class);

	public static void main(String[] args) throws Exception {
		Main.init(args);
		String tableName = Main.getTable();

		if (Main.getCommonPath() == null) {
			return;
		}

		String[] columns = Main.getColumns().split(",");
		List<String> list = Arrays.asList(columns);

		Map<String, Integer> sizeMap = getSizeMap(tableName);
		Map<Integer, Integer> calculateMap = getCalculateMap();

		List<String> lines = list.stream().map(String::toUpperCase).map(columnName -> {
			int size = sizeMap.get(columnName);
			return "\n    " + columnName + "_ENC VARCHAR2(" + calculateMap.get(size) + " CHAR)";
		}).collect(Collectors.toList());

		String columnClause = String.join(",", lines);

		String tableName1 = Main.getTable();

		List<String> lines1;
		String inputFile = Main.getCommonPath() + "/lc-dao/src/test/resources/lc-dao-full-schema.sql";
		PrintWriter pw = Main.getPrintWriter(inputFile + ".bak");
		lines1 = Main.readLines(inputFile);
		String tableBegin = String.format("create table %s (", tableName1.toUpperCase());
		boolean tableFound = false;
		for (String line : lines1) {
			if (line.contains(tableBegin) || line.contains(tableBegin.toUpperCase())) {
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

}
