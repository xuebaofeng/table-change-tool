package bf.cg;

import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Baofeng Xue on 2016/2/5.
 */
public class GenTopLinkJava {

	static String template = "\n  @LCEncryptionCiphertextField\n" +
		"  private String %sEnc;\n";

	public static void main(String[] args) throws Exception {
		Main.init(args);

		String[] columns = Main.getColumns().split(",");

		String inputFile = Main.getBaseDirServices() + String.format("/lc-main/src/main/java/lc/accessDB/tl/TL%s.java", Main.getShortCapTableName());
		List<String> lines = Main.readLines(inputFile);
		String currentColumn = null;

		PrintWriter pw = Main.getPrintWriter(inputFile + ".bak");
		for (String line : lines) {
			for (String column : columns) {
				if (line.contains(String.format(" %s;", Main.toProperty(column)))) {
					currentColumn = column;
					pw.print("  @LCEncryptionCleartextField(EncryptionState.TRANSITIONAL)\n");
				}
			}
			pw.println(line);
			if (currentColumn != null) {
				if (line.contains(";")) {
					String insertedText = String.format(template, Main.toProperty(currentColumn));
					pw.println(insertedText);
					currentColumn = null;
				}
			}
		}

		pw.close();
	}

}
