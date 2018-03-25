package sharing.code.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * C3P0数据库连接管理
 * @author 李繁
 *
 */
public final class ConnectionManager {

	private static ConnectionManager instance;
	private static ComboPooledDataSource dataSource;

	private ConnectionManager() throws SQLException, PropertyVetoException {
		dataSource = new ComboPooledDataSource();

		//        dataSource.setUser("root");
		//        dataSource.setPassword("root");
		//        dataSource.setJdbcUrl("jdbc:mysql://120.27.107.220:3306/S");
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setInitialPoolSize(2);
		dataSource.setMinPoolSize(1);
		dataSource.setMaxPoolSize(10);
		dataSource.setMaxStatements(50);
		dataSource.setMaxIdleTime(60);
	}

	public static final ConnectionManager getInstance() {
		if (instance == null) {
			try {
				instance = new ConnectionManager();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public synchronized final Connection getConnection(String url, String user, String passwd) {
		Connection conn = null;
		dataSource.setUser(user);
		dataSource.setPassword(passwd);
		dataSource.setJdbcUrl(url);
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}

