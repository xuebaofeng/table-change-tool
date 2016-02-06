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
public class MappingJava {

    static String template = "\n    @Column(name=\"%s_ENC\")\n" +
            "    @LCEncryptionCiphertextField\n" +
            "    private String %sEnc;";

    public static void main(String[] args) throws Exception {
        String baseDir = "z:/lc-common";

        String table = "lc$addr";
        String className = "LcAddress";
        String[] columns = {"city", "zip", "street", "street_no"};


        String tableShort = table.split("\\$")[1];
        String classDescriptor = baseDir + String.format("\\lc-dao\\src\\main\\java\\com\\lendingclub\\data\\domain\\jpa" +
                "\\%s.java", className);
        List<String> lines = Files.lines(Paths.get(classDescriptor)).collect(Collectors.toList());

        String currentColumn = null;
        PrintWriter pw = new PrintWriter(new FileWriter(classDescriptor + ".bak"));
        for (String line : lines) {
            for (String column : columns) {
                if (line.contains("name=\"" + column.toUpperCase()+"\"")) {
                    currentColumn = column;
                    pw.print("    @LCEncryptionCleartextField(EncryptionState.TRANSITIONAL)\n");
                }
            }
            pw.println(line);
            System.out.println(line);
            if (currentColumn != null) {
                if (line.contains(";")) {
                    String insertedText = String.format(template, currentColumn.toUpperCase(), toProperty(currentColumn));
                    pw.println(insertedText);
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
