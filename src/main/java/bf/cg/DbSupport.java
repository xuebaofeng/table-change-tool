package bf.cg;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Baofeng(Shawn) Xue on 12/23/15.
 */
public class DbSupport {
	private static BoneCPDataSource dataSource;

	static {
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl("jdbc:oracle:thin:@//lcscrubgg.tlcinternal.com:1521/LCSCRUBGG.lendingclub.com");
//		config.setJdbcUrl("jdbc:oracle:thin:@//lcdev.tlcinternal.com:1521/lcdev.lendingclub.com");

		dataSource = new BoneCPDataSource(config);
		dataSource.setUsername("lc_bxue");
		dataSource.setPassword("atera123");
//		dataSource.setUsername("tlc");
//		dataSource.setPassword("tlc");
		dataSource.setDriverClass("oracle.jdbc.OracleDriver");
	}

	protected JdbcTemplate jdbcTemplate = newJdbcTemplate();
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected static JdbcTemplate newJdbcTemplate() {
		return new JdbcTemplate(getDataSource());
	}

	protected static DataSource getDataSource() {
		return dataSource;
	}

	protected static Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
