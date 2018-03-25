package sharing.code.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	private Connection conn = null;

	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鎹峰尅鎷烽敓鏂ゆ嫹閿熸枻鎷�
	 * @param DBDRIVER - 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
	 * @param DBURL - 閿熸枻鎷烽敓鎹峰尅鎷烽敓琛楋拷
	 * @param DBPASSWD - 閿熸枻鎷烽敓鏂ゆ嫹
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private ConnectDB(String DBURL, String DBUSER, String DBPASSWD) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn =  DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
	}

	//閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹瀹為敓鏂ゆ嫹
	public static Connection getConn( String DBURL, String DBUSER, String DBPASSWD)
			throws ClassNotFoundException, SQLException {
		return new ConnectDB(DBURL, DBUSER, DBPASSWD).conn;
	}

}
