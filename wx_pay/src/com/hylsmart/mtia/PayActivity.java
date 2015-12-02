package com.hylsmart.mtia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import net.sourceforge.simcpux.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import java.io.StringReader;
import java.util.*;

public class PayActivity extends Activity {

	private static final String TAG = "MicroMsg.SDKSample.PayActivity";
	PayReq req;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	Map<String,String> resultunifiedorder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		req = new PayReq();
		msgApi.registerApp(Constants.APP_ID);
		//生成prepay_id
		Button payBtn = (Button) findViewById(R.id.unifiedorder_btn);
		payBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
				getPrepayId.execute();
			}
		});
		Button appayBtn = (Button) findViewById(R.id.appay_btn);
		appayBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendPayReq();
			}
		});

		//生成签名参数
		Button appay_pre_btn = (Button) findViewById(R.id.appay_pre_btn);
		appay_pre_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				genPayReq();
			}
		});

	}
	
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<"+params.get(i).getName()+">");
			sb.append(params.get(i).getValue());
			sb.append("</"+params.get(i).getName()+">");
		}
		sb.append("</xml>");
		return sb.toString();
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {

		private ProgressDialog dialog;


		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(PayActivity.this, getString(R.string.app_tip), getString(R.string.getting_prepayid));
		}

		@Override
		protected void onPostExecute(Map<String,String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			resultunifiedorder=result;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String,String>  doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
		    PayMessage	mPayMessage = new PayMessage();
			mPayMessage.setAppid(Constants.APP_ID);
			mPayMessage.setMch_id(Constants.MCH_ID);
			mPayMessage.setNonce_str(genNonceStr());

			/******body,Out_trade_no,Total_fee都是请求后台的接口得到的，即在“统一下单”前，要去请求后台接口*******/
			mPayMessage.setBody("body");//订单描述
			mPayMessage.setOut_trade_no(genOutTradNo());//订单号，两次下单的订单号不能相同
			mPayMessage.setTotal_fee(10);//价格
			/******body,Out_trade_no,Total_fee都是请求后台的接口得到的，即在“统一下单”前，要去请求后台接口*******/

			mPayMessage.setSpbill_create_ip("127.0.0.1");
			mPayMessage.setNotify_url("http://114.215.187.74:10601/meitong/common/tenpay");//支付完成后的回调地址
			mPayMessage.setTrade_type("APP");
			String sign = SignUtil.sign(SignUtil.sortedMapToSortedParams(SignUtil.orderParams(mPayMessage)), Constants.API_KEY);
			mPayMessage.setSign(sign);

			String entity = genProductArgs(mPayMessage);
			byte[] buf = Util.httpPost(url, entity);
			String content = new String(buf);
			System.out.println("下单返回==="+content);
			return decodeXml(content);
		}
	}

	public Map<String,String> decodeXml(String content) {
		try {

			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if("xml".equals(nodeName)==false){
							//实例化student对象
							xml.put(nodeName,parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				event = parser.next();
			}
			return xml;
		} catch (Exception e) {
			Log.e("orion",e.toString());
		}
		return null;

	}

	/**
	 * 封装“统一下单”的请求参数
	 * @param payMessage
	 * @return
	 */
	private String genProductArgs(PayMessage payMessage) {
		try {
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid",payMessage.getAppid()));
			packageParams.add(new BasicNameValuePair("body", payMessage.getBody()));
			packageParams.add(new BasicNameValuePair("mch_id",payMessage.getMch_id()));
			packageParams.add(new BasicNameValuePair("nonce_str",payMessage.getNonce_str()));
			packageParams.add(new BasicNameValuePair("notify_url", payMessage.getNotify_url()));
			packageParams.add(new BasicNameValuePair("out_trade_no",payMessage.getOut_trade_no()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",payMessage.getSpbill_create_ip()));
			packageParams.add(new BasicNameValuePair("total_fee", payMessage.getTotal_fee()+""));
			packageParams.add(new BasicNameValuePair("trade_type", payMessage.getTrade_type()));
			packageParams.add(new BasicNameValuePair("sign", payMessage.getSign()));
		    String xmlstring =toXml(packageParams);
			System.out.println("统一下单para:"+xmlstring);
			return xmlstring;
		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}
	}

	/**
	 * 封装“调起支付”的请求参数
	 */
	private void genPayReq() {
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		SortedMap<String, String> params = new TreeMap<String, String>();
		params.put("appid", req.appId);
		params.put("noncestr", req.nonceStr);
		params.put("package", req.packageValue);
		params.put("partnerid", req.partnerId);
		params.put("prepayid", req.prepayId);
		params.put("timestamp", req.timeStamp);
		String sign = SignUtil.sign(SignUtil.sortedMapToSortedParams(params), Constants.API_KEY);
		req.sign = sign;
	}
	private void sendPayReq() {
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}


	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}


}

