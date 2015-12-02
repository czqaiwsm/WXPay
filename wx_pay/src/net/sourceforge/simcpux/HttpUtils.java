package net.sourceforge.simcpux;

import android.content.Context;
import android.util.Log;

import com.hylsmart.mtia.App;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtils {

	// private static AsyncHttpClient asyncClient = new AsyncHttpClient();
	private static AsyncHttpClient asyncClient;

	static {
		asyncClient = new AsyncHttpClient();
		asyncClient.setTimeout(30 * 1000);
	}

	public static void get(String url, AsyncHttpResponseHandler responseHandler) {
		Log.d("HTTP GET", url);
		asyncClient.get(url, null, responseHandler);
	}

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		initParams(params);
		Log.d("HTTP GET", url + "?" + params.toString());
		asyncClient.get(url, params, responseHandler);
	}

	public static void get(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		initParams(params);
		Log.d("HTTP GET", url + "?" + params.toString());
		asyncClient.get(context, url, params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		Context context = App.getInstance().getApplicationContext();
		if (!NetworkUtils.isConnect(context)) {
			// DialogUtils.getInstance().showOkDialog(context,
			// R.string.dialog_title, R.string.network_error);
			return;
		}
		initParams(params);
		Log.d("HTTP post", url + "?" + params.toString());
		asyncClient.post(url, params, responseHandler);
	}

	public static void postNoParam(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		Context context = App.getInstance().getApplicationContext();
		if (!NetworkUtils.isConnect(context)) {
			return;
		}
		Log.d("HTTP post", url + "?" + params.toString());
		asyncClient.post(url, params, responseHandler);
	}

	public static void download(String url, FileAsyncHttpResponseHandler handler) {
		AsyncHttpClient asyncClient = new AsyncHttpClient();
		asyncClient.get(url, handler);
	}

	private static void initParams(RequestParams params) {
	}
}
