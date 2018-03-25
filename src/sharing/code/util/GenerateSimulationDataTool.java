package sharing.code.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import sharing.code.jdbc.ConnectDB;
import sharing.code.key.PrivateKey;
import sharing.code.key.PrivateKeyFactory;

public class GenerateSimulationDataTool {
	private static String DBURL_R = "jdbc:mysql://120.27.107.220:3306/R";
	private static String DBURL_S = "jdbc:mysql://120.27.107.220:3306/S";

	private static String DBUSER = "root";
	private static String DBPASSWD = "root";

	private static final String[] Common_Data_Of_S = {
		"13787687742 18 湖南",
		"13787687742 88 天津",
		"4782317 34 北京",
		"15091528705 28 上海",
		"15111101249 23 宁夏"
	};

	private static final String[] Common_Data_Of_R = {
		"张三 13787687742 9980",
		"李四 4782317 7800",
		"王五 15091528705 6700",
		"赵六 15111101249 8000"
	};

	private static Connection conn_r = null;
	private static Connection conn_s = null;


	static {
		try {
			conn_r = ConnectDB.getConn(DBURL_R, DBUSER, DBPASSWD);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //閿熸枻鎷峰彇閿熸枻鎷烽敓鎹峰尅鎷峰疄閿熸枻鎷�
		try {
			conn_s = ConnectDB.getConn(DBURL_S, DBUSER, DBPASSWD);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException {
		String userdir = System.getProperty("user.dir") + "/securitykey/";
		PreparedStatement ps1 = conn_s.prepareStatement("truncate s");
		PreparedStatement ps2 = conn_r.prepareStatement("truncate r");
		ps1.executeUpdate();
		ps2.executeUpdate();

		String path_s = GenerateDataToFile("s_source_data", 3, 1000, 0);
		String path_r = GenerateDataToFile("r_source_data", 3, 1000, 1);
		PrivateKey key_s = PrivateKeyFactory.readKey(new File(userdir + "S/Se1.dat"));
		PrivateKey key_r = PrivateKeyFactory.readKey(new File(userdir + "R/Re1.dat"));
		String sql_r = "insert into r (name, tel, salary) values (?, ?, ?)";
		String sql_s = "insert into s (tel, age, address) values (?, ?, ?)";
		System.out.println("正在写入Ts表中...................................");
		WriteDataToDataBase(path_s, key_s, sql_s, conn_s);
		System.out.println("正写入Tr表中.....................................");
		WriteDataToDataBase(path_r, key_r, sql_r, conn_r);
		System.out.println("数据写入完成!");
	}

	public static void WriteDataToDataBase(String path, PrivateKey key, String sql, Connection conn) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(path)));
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			String tmp = null;
			int count = 0;
			while ((tmp = br.readLine()) != null) {
				count++;
				String[] data = tmp.split(" ");
				System.out.println("第" + count + "条：" + tmp);
				for (int i = 0; i < 3; i++) {
					byte[] encry = PrivateKey.encryp(data[i].getBytes(), key).toByteArray();
					ps.setBytes(i + 1, encry);
					if (i == 0) {
						System.out.print("第" + count + "条：" + new String(encry) + " ");
					} else if (i == 1) {
						System.out.println(new String(encry) + " ");
						System.out.println(new String(encry));
					}
				}
				ps.executeUpdate();
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}


	private static void WriteCommDataTOFile(String file_path, String[] data) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(new File(file_path), true));
			for (String s : data) {
				pw.println(s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}
	}


	public static String GenerateDataToFile(String file_path, int dataLen, int count, int mode) {
		PrintWriter pw = null;
		file_path = file_path + "_" + dataLen + "_" + count + ".txt";

		DeleteFileTool.DeleteFolder(file_path);

		if (mode == 0)
			WriteCommDataTOFile(file_path, Common_Data_Of_S);
		if (mode == 1)
			WriteCommDataTOFile(file_path, Common_Data_Of_R);

		try {
			Random random = new Random();
			pw = new PrintWriter(new FileOutputStream(new File(file_path), true));

			for (int i = 0; i < count; i++) {
				String temp = "";
				for (int j = 0; j < dataLen; j++) {
					temp += System.currentTimeMillis() + "" + random.nextLong() + " ";
				}
				temp = temp.trim();
				pw.println(temp);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(pw != null)
				pw.close();
		}
		return file_path;

	}

}
