package bf.cg;

import com.beust.jcommander.Parameter;

/**
 * @author Baofeng Xue at 2016/2/8 21:44.
 */
public class Option {


    @Parameter(names = "-table", description = "database table name: lc$addr")
    String table;

    @Parameter(names = "-lc-common-path", description = "project lc-common path: /Users/bxue/lc-common")
    String commonPath;
}
