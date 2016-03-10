package bf.cg;

import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Baofeng Xue on 2016/2/5.
 */
public class GenToplinkMap {

    static String template_class_des = "            <opm:attribute-mapping xsi:type=\"toplink:direct-mapping\">\n" +
            "               <opm:attribute-name>%sEnc</opm:attribute-name>\n" +
            "               <opm:field table=\"%s\" name=\"%s_ENC\" xsi:type=\"opm:column\"/>\n" +
            "            </opm:attribute-mapping>";

    public static void main(String[] args) throws Exception {
        Main.init(args);

		if (Main.getMainPath() == null) {
			return;
		}

        String baseDirMain = Main.getBaseDirServices() + "/lc-main";
        String table = Main.getTable();
        String[] columns = Main.getColumns().split(",");

        String inputFile = baseDirMain + "/src/main/resources/META-INF/tlc_tlMap.xml";
        List<String> lines = Main.readLines(inputFile);

        String currentColumn = null;

        String nodeBeginMatch = String.format("<opm:field table=\"%s\" name=\"%s\" xsi:type=\"opm:column\"/>", table.toUpperCase(), "%s");

        PrintWriter pw = Main.getPrintWriter(inputFile + ".bak");

        for (String line : lines) {
            for (String column : columns) {

                if (line.contains(String.format(nodeBeginMatch, column.toUpperCase()))) {
                    currentColumn = column;
                }
            }
            pw.println(line);
            if (currentColumn != null) {
                if (line.contains("</opm:attribute-mapping>")) {
                    String nodeText = String.format(template_class_des, Main.toProperty(currentColumn), table.toUpperCase(), currentColumn.toUpperCase());
                    pw.println(nodeText);
                    currentColumn = null;
                }
            }
        }

        pw.close();
    }


}
