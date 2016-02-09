package bf.cg;

import com.google.common.base.CaseFormat;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Baofeng Xue on 2016/2/5.
 */
public class GenJpa {

    static String template = "\n    @Column(name=\"%s_ENC\")\n" +
            "    @LCEncryptionCiphertextField\n" +
            "    private String %sEnc;";

    public static void main(String[] args) throws Exception {
        Main.init(args);

        String baseDir = Main.getCommonPath();
        String className = Main.getJavaClassName();
        String[] columns = Main.getColumns().split(",");


        String inputFile = baseDir + String.format("/lc-dao/src/main/java/com/lendingclub/data/domain/jpa" +
                "/%s.java", className);
        List<String> lines = Main.readLines(inputFile);
        String currentColumn = null;
        String fileName = inputFile + ".bak";
        System.out.println(fileName);
        PrintWriter pw = new PrintWriter(new FileWriter(fileName));
        for (String line : lines) {
            for (String column : columns) {
                if (line.contains("name=\"" + column.toUpperCase() + "\"")) {
                    currentColumn = column;
                    pw.print("    @LCEncryptionCleartextField(EncryptionState.TRANSITIONAL)\n");
                }
            }
            pw.println(line);
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
