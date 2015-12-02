package net.sourceforge.simcpux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * 自定义工具类
 */
public class SignUtil {
	public static SortedMap<String, String> paramsToSortedMap(String params) {
		// 取出URL中参数
		SortedMap<String, String> map = new TreeMap<String, String>();
		String s1[] = params.split("&");
		String s2[] = null;
		for (int i = 0; i < s1.length; i++) {
			s2 = s1[i].split("=");
			map.put(s2[0], s2[1]);
		}
		return map;
	}

	public static String sortedMapToSortedParams(SortedMap<String, String> sortedMap) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (null != value && !"".equals(value) && !"sign".equals(key) && !"key".equals(key)) {
				sb.append(key).append("=").append(value).append("&");
			}
		}
		String str=sb.toString();
		return str;
	}

	public static String sign(String sortedParams,String appSecurity) {
		StringBuilder sb = new StringBuilder(sortedParams);
		sb.append("key=").append(appSecurity);
		// 创建签名
//		String sign1 = Utils.getMD5Str(sb.toString()).toUpperCase();
		System.out.println("创建singParam===\n"+sb.toString());
		String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
		return sign;
	}

	/**
	 * 发送post请求
	 * @param urlStr 请求url
	 * @param xmlInfo 请求的xml string参数
	 * @return
	 */
    public static BufferedReader sendPost(String urlStr,String xmlInfo) {
        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());

            out.write(new String(xmlInfo.getBytes("UTF-8")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con
                    .getInputStream()));
            return br;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 为“统一下单”生成sign做准备
     * @param entity
     * @return
     */
	public static SortedMap<String, String> orderParams(PayMessage entity) {
		SortedMap<String, String> params = new TreeMap<String, String>();
		params.put("appid", entity.getAppid());
		params.put("mch_id", entity.getMch_id());
		params.put("nonce_str", entity.getNonce_str());
		params.put("body",entity.getBody());
		params.put("out_trade_no", entity.getOut_trade_no());
		params.put("total_fee", entity.getTotal_fee()+"");
		params.put("spbill_create_ip",entity.getSpbill_create_ip());
		params.put("notify_url", entity.getNotify_url());
		params.put("trade_type", entity.getTrade_type());
//		params.put("openid",entity.getOpenid());
//		params.put("attach",entity.getAttach());
		return params;
	}

    /**
     * 为“调起支付”生成sign做准备
     * @param entity
     * @return
     */
	public static SortedMap<String, String> payParams(PayRequest entity) {
		SortedMap<String, String> params = new TreeMap<String, String>();
		params.put("appId", entity.getAppId());
		params.put("timeStamp", entity.getTimeStamp());
		params.put("nonceStr", entity.getNonceStr());
		params.put("package","prepay_id="+entity.getPrepayId());
		params.put("signType", entity.getSignType());
		return params;
	}

/*	public static boolean isValidSign(String sortedParams, String sign) {
		String newSign = sign(sortedParams);
		return sign.equals(newSign);
	}*/
}
