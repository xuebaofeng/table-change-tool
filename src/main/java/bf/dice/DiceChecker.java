package bf.dice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Baofeng(Shawn) Xue on 4/20/16.
 *         public Date getCrDob() {
 *         return entity.getCrDob();
 *         }
 *
 *         public void setCrDob(Date crDob) {
 *         entity.setCrDob(crDob);
 *         }
 */
public class DiceChecker {


    public static void main(String[] args) throws Exception {

        Set<String> diceSet = collectGetSetters("/Users/bxue/tlc/underwriting-automation/dice/dice-domain/src/main/java/com/lendingclub/dice/domain/model/CreditInfo.java");
        Set<String> lcSet = collectGetSetters("/Users/bxue/CreditInfo.java");
        System.out.println(diceSet);

        for (String s : lcSet) {
            if (!diceSet.contains(s)) {
                System.err.println(s);
            }
        }

    }

    private static Set<String> collectGetSetters(String file) throws IOException {
        return Files.lines(Paths.get(file))
            .filter(s -> s.contains("public") && s.contains("get") && s.contains("{") && !s.contains("static")
                || s.contains("public void set"))
            .map(s -> {
                s = s.trim();
                s = s.replace("  ", " ");
                String[] split = s.split(" ");
                String prop = split[2];
                String ret = prop.split("\\(")[0].toLowerCase();
                if (ret.equals("long")) System.out.println(s);
                return ret;
            }).collect(Collectors.toSet());
    }
}
