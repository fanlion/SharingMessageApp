package sharing.code.bean;

import java.io.File;

public class KeyProperty {
	private File primaryKey;
	private File secondaryKey;
	public File getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(File primaryKey) {
		this.primaryKey = primaryKey;
	}
	public File getSecondaryKey() {
		return secondaryKey;
	}
	public void setSecondaryKey(File secondaryKey) {
		this.secondaryKey = secondaryKey;
	}
	public KeyProperty(File primaryKey, File secondaryKey) {
		super();
		this.primaryKey = primaryKey;
		this.secondaryKey = secondaryKey;
	}

}
