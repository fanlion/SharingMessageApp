package sharing.code.bean;

import java.util.List;

public class DataBaseProperty {
	private String url;
	private String dbname;
	private String port;
	private String user;
	private String passwd;

	private String table;
	private String common;
	private List<String> nuncommon;

	public DataBaseProperty(String url, String dbname, String port, String user, String passwd, String table,
			String common, List<String> nuncommon) {
		super();
		this.url = url;
		this.dbname = dbname;
		this.port = port;
		this.user = user;
		this.passwd = passwd;
		this.table = table;
		this.common = common;
		this.nuncommon = nuncommon;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getCommon() {
		return common;
	}
	public void setCommon(String common) {
		this.common = common;
	}
	public List<String> getNuncommon() {
		return nuncommon;
	}
	public void setNuncommon(List<String> nuncommon) {
		this.nuncommon = nuncommon;
	}

}
