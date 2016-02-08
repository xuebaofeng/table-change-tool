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
public class GenJava {

    static String template = "\n    @Column(name=\"%s_ENC\")\n" +
            "    @LCEncryptionCiphertextField\n" +
            "    private String %sEnc;";

    public static void main(String[] args) throws Exception {
        String baseDir = args[0];
        String table = args[1];
        String className =args[2];
        String[] columns = args[3].split(",");


        String tableShort = table.split("\\$")[1];
        String classDescriptor = baseDir + String.format("/lc-dao/src/main/java/com/lendingclub/data/domain/jpa" +
                "/%s.java", className);
        List<String> lines = Files.lines(Paths.get(classDescriptor)).collect(Collectors.<String>toList());
        String currentColumn = null;
		String fileName = classDescriptor + ".bak";
		System.out.println(fileName);
		PrintWriter pw = new PrintWriter(new FileWriter(fileName));
        for (String line : lines) {
            for (String column : columns) {
                if (line.contains("name=\"" + column.toUpperCase()+"\"")) {
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
