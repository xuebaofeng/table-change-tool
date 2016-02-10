package bf.cg;

import com.beust.jcommander.Parameter;

/**
 * @author Baofeng Xue at 2016/2/8 21:44.
 */
public class Option {


    @Parameter(names = "-table", description = "Database table name: lc$addr", required = true)
    String table;

    @Parameter(names = "-cp", description = "Project lc-common path: /Users/bxue/lc-common", required = true)
    String commonPath;
    @Parameter(names = "-mp", description = "Project main path: /Users/bxue/main", required = true)
    String mainPath;

    @Parameter(names = "-jpa", description = "Jpa class name: LcAddress", required = true)
    String jpa;
    @Parameter(names = "-ticket", description = "Database project jira number: 66666")
    String ticket;
    @Parameter(names = "-col", description = "Columns: city,zip,street,street_no", required = false)
    String columns;
    @Parameter(names = "-username", description = "Username: Baofeng Xue")
    String username;
}
