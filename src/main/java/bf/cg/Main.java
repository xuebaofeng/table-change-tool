package bf.cg;

import com.google.common.base.CaseFormat;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/2/5.
 */
public class Main {

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
        String baseDirMain = "z:/lc-main";

        String table = "lc$addr";
        String[] columns = {"city", "zip", "street", "street_no"};


        String tableShort = table.split("\\$")[1];
        String classDescriptor = baseDirMain + String.format("\\toplink\\dm\\toplink\\tlc_tlMap\\descriptor\\" +
                "lc.accessDB.tl.TL%s.ClassDescriptor.xml", tableShort);
        List<String> lines = Files.lines(Paths.get(classDescriptor)).collect(Collectors.toList());

        String currentColumn = null;
        PrintWriter pw = new PrintWriter(new FileWriter(classDescriptor + ".bak"));
        for (String line : lines) {
            for (String column : columns) {
                if (line.contains(">" + toProperty(column) + "<")) {
                    currentColumn = column;
                }
            }
            pw.println(line);
            if (currentColumn != null) {
                if (line.contains("</mapping>")) {
                    String nodeText = String.format(template_class_des, toProperty(currentColumn), table.toUpperCase(), currentColumn.toUpperCase());
                    pw.println(nodeText);
                    currentColumn = null;
                }
            }
        }

        pw.close();
    }

    private static String toProperty(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }
}
