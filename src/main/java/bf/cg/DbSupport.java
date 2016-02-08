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

		dataSource = new BoneCPDataSource(config);
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
