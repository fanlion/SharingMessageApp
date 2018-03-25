package sharing.code.bean;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * ��Ԫ�飬���ڱ�ʾһ����Ԫ��
 * @author lifan
 *
 */
public class TwoTuples implements Serializable{
	private static final long serialVersionUID = 4700101489303662735L;
	private BigInteger first;
	private BigInteger second;
	public BigInteger getFirst() {
		return first;
	}
	public void setFirst(BigInteger first) {
		this.first = first;
	}
	public BigInteger getSecond() {
		return second;
	}
	public void setSecond(BigInteger second) {
		this.second = second;
	}

}
