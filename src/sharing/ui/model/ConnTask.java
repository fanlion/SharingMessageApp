package sharing.ui.model;

import java.util.Observable;
import java.util.Observer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sharing.code.bean.RemoteTask;

public class ConnTask implements Observer{
	//连接的绘画ID
	private StringProperty sessionId;
	//连接远程地址
	private StringProperty localAddress;
	//任务进行
	private StringProperty process;
	private StringProperty status;
	private StringProperty cerInfo;


	@Override
	public void update(Observable arg0, Object arg1) {
		RemoteTask task = (RemoteTask) arg0;
		process.set(task.getProcess());
		status.set(task.getStatus());
		cerInfo.set(task.getCerInfo());
	}

	public ConnTask() {
		this.sessionId = new SimpleStringProperty("null");
		this.localAddress = new SimpleStringProperty("null");
		this.process = new SimpleStringProperty("null");
		this.status = new SimpleStringProperty("null");
		this.status = new SimpleStringProperty("null");
		this.cerInfo = new SimpleStringProperty("null");
	}


	public StringProperty sessionIdProperty() { return this.sessionId; }
	public StringProperty localAddressProperty() { return this.localAddress; }
	public StringProperty statusProperty() { return this.status; }
	public StringProperty processProperty() { return this.process; }
	public StringProperty cerInfoProperty() { return this.cerInfo; }


	public void setSessionIdProperty(String sessionId) {
		this.sessionId.set(sessionId);
	}

	public void setLocalAddressProperty(String localAddress) {
		this.localAddress.set(localAddress);
	}

	public void setCerInfoProperty(String cerInfo) {
		this.cerInfo.set(cerInfo);
	}
}
