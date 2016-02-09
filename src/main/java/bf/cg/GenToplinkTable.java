package bf.cg;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Baofeng Xue on 2016/2/5.
 */
public class GenToplinkTable {

	static String template_class_des = "      <mapping>\n" +
		"         <inherited>false</inherited>\n" +
		"         <instance-variable-name>%sEnc</instance-variable-name>\n" +
		"         <default-field-names>\n" +
		"            <default-field-name>direct field=</default-field-name>\n" +
		"         </default-field-names>\n" +
		"         <uses-method-accessing>false</uses-method-accessing>\n" +
		"         <read-only>false</read-only>\n" +
		"         <get-method-handle>\n" +
		"            <method-handle/>\n" +
		"         </get-method-handle>\n" +
		"         <set-method-handle>\n" +
		"            <method-handle/>\n" +
		"         </set-method-handle>\n" +
		"         <direct-mapping-field-handle>\n" +
		"            <field-handle>\n" +
		"               <field-table>TLC.%s</field-table>\n" +
		"               <field-name>%s_ENC</field-name>\n" +
		"            </field-handle>\n" +
		"         </direct-mapping-field-handle>\n" +
		"         <mapping-class>MWDirectToFieldMapping</mapping-class>\n" +
		"      </mapping>";

	public static void main(String[] args) throws Exception {
		Main.init(args);

		String baseDirMain = Main.getBaseDirServices() + "/lc-main";
		String table = Main.getTable();
		String[] columns = Main.getColumns().split(",");


		String classDescriptor = baseDirMain + String.format("/toplink/dm/toplink/tlc_tlMap/descriptor/" +
			"lc.accessDB.tl.TL%s.ClassDescriptor.xml", Main.getShortCapTableName());
		List<String> lines = Main.readLines(classDescriptor);

		String currentColumn = null;
		String fileName = classDescriptor + ".bak";
		PrintWriter pw = new PrintWriter(new FileWriter(fileName));
		System.out.println(fileName);
		for (String line : lines) {
			for (String column : columns) {
				if (line.contains(">" + Main.toProperty(column) + "<")) {
					currentColumn = column;
				}
			}
			pw.println(line);
			if (currentColumn != null) {
				if (line.contains("</mapping>")) {
					String nodeText = String.format(template_class_des, Main.toProperty(currentColumn), table.toUpperCase(), currentColumn.toUpperCase());
					pw.println(nodeText);
					currentColumn = null;
				}
			}
		}

		pw.close();
	}


}
