package sharing.code.bean;

import java.util.Observable;

public class RemoteTask extends Observable{

	public RemoteTask() {
		status = "null";
	}

	private String status;
	private String process;
	private String cerInfo;

	public void setCerInfo(String cerInfo) {
		this.cerInfo = cerInfo;
		setChanged();
		notifyObservers();
	}

	public void setStatus(String status) {
		this.status = status;
		setChanged();
		notifyObservers();
	}


	public void setProcess(String process) {
		this.process = process;
		setChanged();
		notifyObservers();
	}

	public String getCerInfo() {
		return this.cerInfo;
	}

	public String getStatus() {
		return this.status;
	}

	public String getProcess() {
		return this.process;
	}
}
