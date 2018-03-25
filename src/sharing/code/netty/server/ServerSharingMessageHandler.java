package sharing.code.netty.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import sharing.code.bean.DataBaseProperty;
import sharing.code.bean.KeyProperty;
import sharing.code.bean.RemoteTask;
import sharing.code.bean.Triples;
import sharing.code.fun.K;
import sharing.code.jdbc.ConnectionManager;
import sharing.code.key.PrivateKey;
import sharing.code.key.PrivateKeyFactory;
import sharing.code.netty.struct.Header;
import sharing.code.netty.struct.NettyMessage;
import sharing.code.netty.val.MessageType;
import sharing.code.util.DeleteFileTool;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerSharingMessageHandler extends ChannelHandlerAdapter{
	private static String userdir = System.getProperty("user.dir") + "/securitykey/";
	//任务队列，用于标识任务的状态
	private Map<String, RemoteTask> taskMap;

	//主密钥
	private  PrivateKey e1;
	//次密钥
	private  PrivateKey e2;

	private ObjectOutputStream oosw1 = null;
	private ObjectOutputStream oosw2 = null;
	private ObjectInputStream oisw1 = null;
	private ObjectInputStream oisw2 = null;
	private ObjectOutputStream oosu = null;
	//数据库配置
	private DataBaseProperty dbp;


	public ServerSharingMessageHandler(DataBaseProperty dbp, KeyProperty kp, Map<String, RemoteTask> taskMap) {
		this.taskMap = taskMap;
		this.dbp = dbp;
		this.e1 =  PrivateKeyFactory.readKey(kp.getPrimaryKey());
		this.e2 =  PrivateKeyFactory.readKey(kp.getSecondaryKey());
		DeleteFileTool.DeleteFolder(userdir + "S/W2.dat");
		DeleteFileTool.DeleteFolder(userdir + "S/W1.dat");
		DeleteFileTool.DeleteFolder(userdir + "S/U.dat");
		try {
			oosw1 = new ObjectOutputStream(new FileOutputStream(new File(userdir + "S/W1.dat"), true));
			oosw2 = new ObjectOutputStream(new FileOutputStream(new File(userdir + "S/W2.dat"), true));
			oisw1 = new ObjectInputStream(new FileInputStream(new File(userdir + "S/W1.dat")));
			oisw2 = new ObjectInputStream(new FileInputStream(new File(userdir + "S/W2.dat")));
			oosu = new ObjectOutputStream(new FileOutputStream(new File(userdir + "S/U.dat"), true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private long startTime = System.currentTimeMillis();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		super.channelRead(ctx, msg);
		String sessionId = ctx.channel().id().asShortText();
		RemoteTask task = taskMap.get(sessionId);

		NettyMessage message = (NettyMessage) msg;
		//接收请求发送Y的报文
		if (message != null && message.getHeader().getType() == MessageType.Y_REQ.value()) {
			NettyMessage respMessage = buildYResp();
			task.setProcess("正发送共有数据");
			ctx.write(respMessage); //立即发送响应报文
			//String url = "jdbc:mysql://120.27.107.220:3306/S";
			String url = "jdbc:mysql://" + dbp.getUrl() + ":" + dbp.getPort() + "/" + dbp.getDbname();
			Connection conn = ConnectionManager.getInstance().getConnection(url, dbp.getUser(), dbp.getPasswd());

			String sql = "select " + dbp.getCommon() + " from " + dbp.getTable();

			//String sql = "select tel from s";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			boolean isLast = false;
			while(rs.next()) {
				isLast = rs.isLast();
				BigInteger y= new BigInteger(rs.getBytes("tel"));
				//构造一条消息
				NettyMessage ymsg = new NettyMessage();
				Header header = new Header();
				header.setType(MessageType.Y_MESSAGE.value());
				header.setFin(isLast); //是否为最后一条信息
				ymsg.setHeader(header);
				ymsg.setBody(y);
				//发送该消息
				ctx.write(ymsg);
			}
		}
		//接受对方发送过来的Y
		else if (message.getHeader().getType() == MessageType.Y_MESSAGE.value()) {
			task.setProcess("正发送W数据");
			BigInteger yr = (BigInteger)message.getBody();
			Triples t = new Triples();
			t.setFirst(yr);
			t.setSecond(PrivateKey.encryp(yr, e1));
			t.setThird(PrivateKey.encryp(PrivateKey.encryp(yr, e1), e2));

			message.getHeader().setType(MessageType.W_MESSAGE.value());
			message.setBody(t);
			//向对方发送w
			ctx.write(message);
		}
		//接受对方发送过来的w
		else if (message.getHeader().getType() == MessageType.W_MESSAGE.value()) {
			task.setProcess("正接受W数据");
			Triples w = (Triples)message.getBody();
			Triples w2 = new Triples();

			w2.setFirst(PrivateKey.decryp(w.getFirst(), e1));
			w2.setSecond(PrivateKey.decryp(w.getSecond(), e1));
			w2.setThird(PrivateKey.decryp(w.getThird(), e1));
			if (message.getHeader().getFin() != true) {//不是最后一条记录
				oosw2.writeObject(w2);
			} else {
				//写入最后一条记录后写入结尾符null
				oosw2.writeObject(w2);
				oosw2.writeObject(null);
				//发送W1_REQ报文，请求对方发送W1
				NettyMessage W1_REQ = buildW1Req();
				ctx.write(W1_REQ);
			}

		}
		//接受对方发送过来的w1_REQ
		else if (message.getHeader().getType() == MessageType.W1_REQ.value()) {
			task.setProcess("正发送W1数据");
			Iterator<String> it = dbp.getNuncommon().iterator();
			String sql = "select " + dbp.getCommon() + ",";
			while (it.hasNext()) {
				String field =	it.next();
				sql += field + ",";
			}
			//remove the last ","
			sql = sql.substring(0, sql.length() - 1);
			sql += " from " + dbp.getTable();
			//			String sql = "select tel, age, address from s";
			String url = "jdbc:mysql://" + dbp.getUrl() + ":" + dbp.getPort() + "/" + dbp.getDbname();
			Connection conn = ConnectionManager.getInstance().getConnection(url, dbp.getUser(), dbp.getPasswd());
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int columnCount = rs.getMetaData().getColumnCount();
			boolean isLast = false;
			while (rs.next()) {
				isLast = rs.isLast();
				BigInteger[] bg = new BigInteger[columnCount];
				BigInteger ys = new BigInteger(rs.getBytes(1));
				bg[0] = ys;
				BigInteger kvs = PrivateKey.encryp(ys, e2);
				for (int i = 1; i < bg.length; i++) {
					BigInteger fes_ext = new BigInteger(rs.getBytes(i+1));
					bg[i] = new BigInteger(K.encrypt(fes_ext.toByteArray(), kvs.toString().toCharArray()));
				}
				NettyMessage w1msg = buildW1Message(bg, isLast);
				//向对方发送w1
				ctx.write(w1msg);
			}
			//向对方发送ureq
			NettyMessage ureq = buildUreq();
			ctx.write(ureq);
		}
		else if (message.getHeader().getType() == MessageType.W1_MESSAGE.value()) {
			task.setProcess("正接受W1数据");
			if (message.getHeader().getFin() != true) {
				oosw1.writeObject(message.getBody());
			} else {
				oosw1.writeObject(message.getBody());
				oosw1.writeObject(null);
			}
		}
		else if (message.getHeader().getType() == MessageType.U_REQ.value()) {
			task.setProcess("正发送U数据");
			Map<BigInteger, BigInteger[]> w1Map = new HashMap<BigInteger, BigInteger[]>();
			Map<BigInteger, Triples> w2Map = new HashMap<BigInteger, Triples>();
			Object obj = null;
			BigInteger[] bg = null;
			while ((obj = oisw1.readObject()) != null) {
				bg = (BigInteger[]) obj;
				w1Map.put(bg[0], bg);
			}

			Triples tp = null;
			obj = null;
			while ((obj = oisw2.readObject()) != null) {
				tp = (Triples)obj;
				w2Map.put(tp.getSecond(), tp);
			}
			Iterator<String> it = dbp.getNuncommon().iterator();
			String sql = "select ";
			while(it.hasNext()) {
				String field = it.next();
				sql += field + ",";
			}
			//remove the last ","
			sql = sql.substring(0, sql.length() - 1);
			sql += " from " + dbp.getTable() + " where " + dbp.getCommon() + "=?";
			//String sql = "select age, address from s where tel=?";
			String url = "jdbc:mysql://" + dbp.getUrl() + ":" + dbp.getPort() + "/" + dbp.getDbname();
			Connection conn = ConnectionManager.getInstance().getConnection(url, dbp.getUser(), dbp.getPasswd());
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = null;

			Iterator<Entry<BigInteger, Triples>> iter = w2Map.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<BigInteger, Triples> w2 = iter.next();
				BigInteger key = w2.getKey();
				BigInteger[] w1 = null;
				if ((w1 = w1Map.get(key)) != null) {
					BigInteger[] fer_v_ext = new BigInteger[w1.length];
					BigInteger v = w2.getValue().getFirst(); // v
					fer_v_ext[0] = v;
					for (int i = 1; i < fer_v_ext.length; i++) {
						BigInteger k = w2.getValue().getThird();
						fer_v_ext[i] = new BigInteger(K.decrypt(w1[i].toByteArray(), k.toString().toCharArray()));
						//TODO
					}
					ps.setBytes(1, PrivateKey.encryp(v, e1).toByteArray());
					rs = ps.executeQuery();
					int columnCount = rs.getMetaData().getColumnCount();
					BigInteger[] ur = new BigInteger[columnCount + 2];
					ur[0] = v; // v
					ur[1] = key; // fer(v)
					while (rs.next()) {
						for (int i = 0; i < columnCount; i++) {
							ur[i + 2] = PrivateKey.decryp(new BigInteger(rs.getBytes(i + 1)), e1);
						}
						NettyMessage umsg = buildUMessage(ur);
						ctx.write(umsg);
					}
				}
			}

			NettyMessage ufinMsg = new NettyMessage();
			Header header = new Header();
			header.setType(MessageType.U_FIN.value());
			ufinMsg.setHeader(header);
			ctx.write(ufinMsg);
		}
		else if (message.getHeader().getType() == MessageType.U_MESSAGE.value()) {
			task.setProcess("正接受U数据");
			oosu.writeObject(message.getBody());
		}

		else if (message.getHeader().getType() == MessageType.U_FIN.value()) {
			task.setProcess("正在等值连接");
			oosu.writeObject(null);
			//ctx.close();

			Iterator<String> it = dbp.getNuncommon().iterator();
			String sql = "select ";
			while(it.hasNext()) {
				String field = it.next();
				sql += field + ",";
			}
			//remove the last ","
			sql = sql.substring(0, sql.length() - 1);
			sql += " from " + dbp.getTable() + " where " + dbp.getCommon() + "=?";


			//String sql = "select age, address from s where tel=?";
			ObjectInputStream ois = null;
			PrintWriter pw = null;
			String url = "jdbc:mysql://" + dbp.getUrl() + ":" + dbp.getPort() + "/" + dbp.getDbname();
			Connection conn = ConnectionManager.getInstance().getConnection(url, dbp.getUser(), dbp.getPasswd());
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = null;
			ois = new ObjectInputStream(new FileInputStream(new File(userdir + "S/U.dat")));

			DeleteFileTool.DeleteFolder("S.txt");

			pw = new PrintWriter(new FileOutputStream(new File("S.txt"), true));
			Object obj = null;
			while ((obj = ois.readObject()) != null) {
				BigInteger[] bgs = (BigInteger[]) obj;
				BigInteger v = bgs[0];
				ps.setBytes(1, PrivateKey.encryp(v, e1).toByteArray());
				rs = ps.executeQuery();
				int columnCount = rs.getMetaData().getColumnCount();
				BigInteger[] ext = new BigInteger[columnCount];
				String equijoinStr = new String(v.toByteArray() ) + " ";
				while (rs.next()) {
					for (int i = 0; i < ext.length; i++) {
						equijoinStr += new String(PrivateKey.decryp(new BigInteger(rs.getBytes(i + 1)), e1).toByteArray() ) + " ";
					}
				}
				for (int i = 2; i < bgs.length; i++) {
					equijoinStr += new String(bgs[i].toByteArray() ) + " ";
				}
				equijoinStr = equijoinStr.trim();
				pw.println(equijoinStr);
			}
			if (pw != null)
				pw.close();
			if (ois != null)
				ois.close();
			String useTime = (System.currentTimeMillis() - startTime) / 1000 + "s";
			task.setProcess("任务完成，耗时" + useTime);
			ctx.channel().close();
			task.setStatus("任务完成断开连接");
		}
		else {
			//否则推送到下一个Handler
			ctx.fireChannelRead(msg);
		}
	}

	private NettyMessage buildUMessage(Object body) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.U_MESSAGE.value());
		message.setHeader(header);
		message.setBody(body);
		return message;
	}

	private NettyMessage buildW1Message(Object body, boolean isLast) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.W1_MESSAGE.value());
		header.setFin(isLast);
		message.setHeader(header);
		message.setBody(body);
		return message;
	}

	private NettyMessage buildYResp() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.Y_RESP.value());
		message.setHeader(header);
		return message;
	}

	private NettyMessage buildW1Req() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.W1_REQ.value());
		message.setHeader(header);
		return message;
	}

	private NettyMessage buildUreq() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.U_REQ.value());
		message.setHeader(header);
		return message;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		ctx.flush();
	}

}
