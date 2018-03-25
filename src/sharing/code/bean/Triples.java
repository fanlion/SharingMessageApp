package sharing.code.bean;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * ��Ԫ�飬���ڱ�ʾһ����Ԫ��
 * @author lifan
 */
public class Triples implements Serializable{
	private static final long serialVersionUID = 224840281395192308L;
	private BigInteger first;
	private BigInteger second;
	private BigInteger third;
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
	public BigInteger getThird() {
		return third;
	}
	public void setThird(BigInteger third) {
		this.third = third;
	}

	public String toString() {
		return "Triples[first= " + first + ", second= " + second + ", third= " + third ;
	}



}
