package bf.dice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Baofeng(Shawn) Xue on 4/20/16.
 *
 *
 *
 *         this.id = entity.getId();
 *
 *         entity.setId(this.id);
 */
public class DiceHelper {

    static boolean isGetter = false;
    private static Map<String, String> map = new HashMap<>();

    public static void main(String[] args) throws Exception {

        if (isGetter)
            createStream().forEach(s -> {
                String prop = s.substring(0, 1).toUpperCase() + s.substring(1);
                prop = transfer(prop);
                String type = map.get(s);
                String formatted = "        ";
                switch (type) {
                    case "Long":
                        formatted += String.format("if(entity.get%s() != null) this.%s = entity.get%s().longValue();", prop, s, prop);
                        break;
                    case "Double":
                        formatted += String.format("if(entity.get%s() != null) this.%s = entity.get%s().doubleValue();", prop, s, prop);
                        break;
                    default:
                        formatted += String.format("this.%s = entity.get%s();", s, prop);
                        break;
                }
                System.out.println(formatted);
            });

        if (!isGetter)
            createStream().forEach(s -> {
                String prop = s.substring(0, 1).toUpperCase() + s.substring(1);
                prop = transfer(prop);
                String formatted = "        ";
                String type = map.get(s);
                switch (type) {
                    case "Long":
                        formatted += String.format("if (this.%s != null) entity.set%s(this.%s.intValue());", s, prop, s);
                        break;
                    case "Double":
                        formatted += String.format("if (this.%s != null) entity.set%s(BigDecimal.valueOf(this.%s));", s, prop, s);
                        break;
                    default:
                        formatted += String.format("entity.set%s(this.%s);", prop, s);
                        break;
                }
                System.out.println(formatted);
            });

    }

    private static String transfer(String prop) {
        if (prop.startsWith("A"))
            prop = prop.replaceFirst("A", "a");
        prop = prop.replace("month", "Month");
        prop = prop.replace("aDob", "aDOB");
        prop = prop.replace("name", "Name");
        if (!prop.contains("Answers"))
            prop = prop.replace("Answer", "Answers");
        if (!prop.contains("Questions"))
            prop = prop.replace("Question", "Questions");
        prop = prop.replace("CrExpRefNumber", "LcCrExpRefNumber");
        prop = prop.replace("Date", "D");
        prop = prop.replace("Phone", "CrPh");
        return prop;
    }

    private static Stream<String> createStream() throws IOException {
        return Files.lines(Paths.get("/Users/bxue/tlc/underwriting-automation/dice/dice-domain/src/main/java/com/lendingclub/dice/domain/model/CreditInfo.java"))
            .filter(s -> s.contains("private") && s.contains(";") && !s.contains("serialVersionUID"))
            .map(s -> {
                s = s.trim();
                s = s.replace("  ", " ");
                String[] split = s.split(" ");
                String prop = split[2];
                prop = prop.substring(0, prop.length() - 1);
                map.put(prop, split[1]);
                return prop;
            })
            .sorted();
    }
}
