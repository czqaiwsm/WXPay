package net.sourceforge.simcpux;

/**
 * 自定义实体类，“调起支付”的参数
 */
public class PayRequest {
	private String appId;
	private String timeStamp;
	private String nonceStr;
	private String prepayId;
	private String signType;
	private String paySign;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getPrepayId() {
		return prepayId;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	public String getPaySign() {
		return paySign;
	}
	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}
	@Override
	public String toString() {
		return "PayRequest [appId=" + appId + ", timeStamp=" + timeStamp
				+ ", nonceStr=" + nonceStr + ", prepayId=" + prepayId
				+ ", signType=" + signType + ", paySign=" + paySign + "]";
	}
}
