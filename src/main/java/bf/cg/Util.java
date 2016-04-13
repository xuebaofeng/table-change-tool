package bf.cg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Baofeng(Shawn) Xue on 2/8/16.
 */
public class Util {

    public static void main(String[] args) throws IOException {
        Files.lines(Paths.get("test")).distinct().sorted().forEach((s -> {
            System.out.println(String.format("'%s',", s));
        }));
    }
}
