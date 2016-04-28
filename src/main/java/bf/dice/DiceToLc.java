package bf.dice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Baofeng(Shawn) Xue on 4/28/16.
 */
public class DiceToLc {

    private static Map<String, String> diceMap = new HashMap<>();
    private static Map<String, String> lcCommonMap = new HashMap<>();
    private static Map<String, String> diceToCommonMap = new HashMap<>();


    public static void main(String[] args) throws Exception {
        initMapping();

        String lcCommonFile = "/Users/bxue/tlc/lc-common/lc-dao/src/main/java/com/lendingclub/data/domain/jpa/LcCredit.java";
        Files.lines(Paths.get(lcCommonFile))
            .filter(s -> s.contains("private") && s.contains(";") && !s.contains("serialVersionUID"))
            .map(s -> {
                s = s.trim();
                s = s.replaceAll("  ", " ");
                String[] split = s.split(" ");
                String prop = split[2];
                prop = prop.substring(0, prop.length() - 1);
                lcCommonMap.put(prop, split[1]);
                return prop;
            }).count();
        System.out.println("properties in common:" + lcCommonMap);

        createStream().forEach(diceProp -> {
            String diceType = diceMap.get(diceProp);
            String lcProp = diceProp;
            if (diceToCommonMap.containsKey(lcProp)) {
                lcProp = diceToCommonMap.get(diceProp);
                if (!lcCommonMap.containsKey(lcProp)) {
                    System.err.println("Not found the prop in lc-common error:" + diceProp);
                    return;
                }
            }
            String lcType = lcCommonMap.get(lcProp);

            String formatted = "";
            if (diceType.equals("Double") && lcType.equals("BigDecimal")) {
                formatted += String.format("    public %1$s get%2$s() {\n" +
                    "        return entity.get%2$s() == null ? null : entity.get%2$s().%5$sValue();\n" +
                    "    }\n" +
                    "\n" +
                    "    public void set%2$s(%1$s %3$s) {\n" +
                    "        entity.set%2$s(%3$s == null ? null : %4$s.valueOf(%3$s));\n" +
                    "    }", diceType, toCap(diceProp), diceProp, lcType, toPrimitive(diceType));
                System.out.println(formatted);
            } else if (diceType.equals("Long") && lcType.equals("Integer")
                || diceType.equals("Integer") && lcType.equals("Double")
                || diceType.equals("Double") && lcType.equals("Integer")) {
                formatted += String.format("    public %1$s get%2$s() {\n" +
                    "        return entity.get%6$s() == null ? null : entity.get%6$s().%5$sValue();\n" +
                    "    }\n" +
                    "\n" +
                    "    public void set%2$s(%1$s %3$s) {\n" +
                    "        entity.set%6$s(%3$s == null ? null : %3$s.%7$sValue());\n" +
                    "    }", diceType, toCap(diceProp), diceProp, lcType, toPrimitive(diceType), lcGetSetter(lcProp), toPrimitive(lcType));
                System.out.println(formatted);
            } else if (diceType.equals("Long") && lcType.equals("String")) {
                formatted += String.format("    public %1$s get%2$s() {\n" +
                    "        return entity.get%6$s() == null ? null : Long.valueOf(entity.get%6$s());\n" +
                    "    }\n" +
                    "\n" +
                    "    public void set%2$s(%1$s %3$s) {\n" +
                    "        entity.set%2$s(%3$s == null ? null : %3$s.toString());\n" +
                    "    }", diceType, toCap(diceProp), diceProp, lcType, toPrimitive(lcType), lcGetSetter(lcProp));
                System.out.println(formatted);
            } else {
                formatted += String.format("    public %1$s get%2$s() {\n" +
                    "        return entity.get%4$s();\n" +
                    "    }\n\n" +
                    "    public void set%2$s(%1$s %3$s) {\n" +
                    "        entity.set%4$s(%3$s);\n" +
                    "    }\n", diceType, toCap(diceProp), diceProp, lcGetSetter(lcProp));
                System.out.println(formatted);

            }
        });


    }

    private static String toPrimitive(String diceType) {
        if (diceType.equals("Integer")) return "int";
        return diceType.toLowerCase();
    }

    private static String lcGetSetter(String lcProp) {
        if (lcProp.startsWith("a")) return lcProp;
        return toCap(lcProp);
    }

    private static String toCap(String diceProp) {
        return diceProp.substring(0, 1).toUpperCase() + diceProp.substring(1);
    }

    private static void initMapping() {
        diceToCommonMap.put("phone1", "crPh1");
        diceToCommonMap.put("phone2", "crPh2");
        diceToCommonMap.put("actorId", "borrower");
        diceToCommonMap.put("startDate", "startD");
        diceToCommonMap.put("endDate", "endD");
        diceToCommonMap.put("aFname", "aFName");
        diceToCommonMap.put("aMname", "aMName");
        diceToCommonMap.put("aDob", "aDOB");
        diceToCommonMap.put("ccQuestionTime", "ccQuestionsTime");
        diceToCommonMap.put("ccAnswerTime", "ccAnswersTime");
        diceToCommonMap.put("crInq6months", "crInq6Months");
        diceToCommonMap.put("crLate24months", "crLate24Months");
        diceToCommonMap.put("crFirstAccountDate", "crFirstAccountD");
        diceToCommonMap.put("crCollections12monthsExmed", "crCollections12MonthsExmed");
        diceToCommonMap.put("crChargeoffs12months", "crChargeoffs12Months");
        diceToCommonMap.put("crChargeoffs36months", "crChargeoffs36Months");
        diceToCommonMap.put("crExpRefNumber", "lcCrExpRefNumber");
    }


    private static Stream<String> createStream() throws IOException {
        String diceFile = "/Users/bxue/tlc/underwriting-automation/dice/dice-domain/src/main/java/com/lendingclub/dice/domain/model/CreditInfo.java";
        return Files.lines(Paths.get(diceFile))
            .filter(s -> s.contains("private") && s.contains(";") && !s.contains("serialVersionUID") && !s.contains("transient"))
            .map(s -> {
                s = s.trim();
                s = s.replace("  ", " ");
                String[] split = s.split(" ");
                String prop = split[2];
                prop = prop.substring(0, prop.length() - 1);
                diceMap.put(prop, split[1]);
                return prop;
            }).sorted();
    }
}
