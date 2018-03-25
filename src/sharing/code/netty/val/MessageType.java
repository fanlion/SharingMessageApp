package sharing.code.netty.val;

public enum MessageType {

	/**
	 * SENDW_REQ--请求对方发送W数据
	 *
	 */
	SERVICE_REQ((byte) 0), SERVICE_RESP((byte) 1), ONE_WAY((byte) 2), LOGIN_REQ(
			(byte) 3), LOGIN_RESP((byte) 4), HEARTBEAT_REQ((byte) 5), HEARTBEAT_RESP(
					(byte) 6), Y_REQ((byte) 7), Y_RESP((byte) 8), Y_MESSAGE((byte) 9),
	W_MESSAGE((byte) 10), W1_REQ((byte) 11), W1_MESSAGE((byte) 12), W1_FIN((byte) 13), U_REQ((byte) 14),
	U_MESSAGE((byte) 15), W_FIN((byte) 16), U_FIN((byte) 17), ALL_FIN((byte) 18), DB_MESSAGE((byte) 19);

	private byte value;

	private MessageType(byte value) {
		this.value = value;
	}

	public byte value() {
		return this.value;
	}
}

