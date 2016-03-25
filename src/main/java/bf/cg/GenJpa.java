package bf.cg;

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

        if (Main.getCommonPath() == null) {
            return;
        }

        String baseDir = Main.getCommonPath();
        String className = Main.getJpa();
        String[] columns = Main.getColumns().split(",");

        String inputFile = baseDir + String.format("/lc-dao/src/main/java/com/lendingclub/data/domain/jpa/%s.java", className);
        List<String> lines = Main.readLines(inputFile);
        String currentColumn = null;
        PrintWriter pw = Main.getPrintWriter(inputFile + ".bak");

        for (String line : lines) {
            for (String column : columns) {
                if (line.contains("name") && line.contains(column.toUpperCase())) {

                    currentColumn = column;
                    pw.print("    @LCEncryptionCleartextField(LCEncryptionCleartextField.EncryptionState.TRANSITIONAL)\n");
                }
            }
            pw.println(line);
            if (currentColumn != null) {
                if (line.contains(";")) {
                    String insertedText = String.format(template, currentColumn.toUpperCase(), Main.toProperty(currentColumn));
                    pw.println(insertedText);
                    currentColumn = null;
                }
            }
        }

        pw.close();
    }

}
