package cn.nubia.oauthnormaldemo.net;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import cn.nubia.oauthnormaldemo.oAuth.Define;
import cn.nubia.oauthsdk.api.HttpPostGet;
import cn.nubia.oauthsdk.api.NetResponseListener;

/**
 * 网络请求
 * Created by gbq on 2016-10-17.
 */

public class DemoNetApis {
	private static final String TAG = "DemoNetApis";
	private static DemoNetApis mInstance;

	public static DemoNetApis getInstance() {
		if (null == mInstance) {
			synchronized (DemoNetApis.class) {
				mInstance = new DemoNetApis();
			}
		}
		return mInstance;
	}

	public void getTokenByCode(final String clientId, final String redirectUrl, final String clientSecret,
							   final String grantType, final String code, final NetResponseListener<String> listener) {
		final HashMap<String, String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("redirect_uri", redirectUrl);
		params.put("client_secret", clientSecret);
		params.put("grant_type", grantType);
		params.put("code", code);
		Log.d(TAG, params.toString());
		HttpAsyncTask<String> task = new HttpAsyncTask<String>(listener) {
			@Override
			public String processRequest() {
				return HttpPostGet.doPost(NetConfig.GET_TOKEN, params);
			}
		};
		task.execute();
	}

	public void refreshToken(final String clientId, final String redirectUrl, final String clientSecret,
							 final String grantType, final String refreshToken, final NetResponseListener<String> listener) {
		final HashMap<String, String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("redirect_uri", redirectUrl);
		params.put("client_secret", clientSecret);
		params.put("grant_type", grantType);
		params.put("refresh_token", refreshToken);
		Log.d(TAG, params.toString());
		HttpAsyncTask<String> task = new HttpAsyncTask<String>(listener) {
			@Override
			public String processRequest() {
				return HttpPostGet.doPost(NetConfig.GET_TOKEN, params);
			}
		};
		task.execute();
	}

	private abstract class HttpAsyncTask<T> extends AsyncTask<Void, Void, Void> {
		private final NetResponseListener<T> mResultListener;
		private Looper mCurLooper;

		public HttpAsyncTask(NetResponseListener<T> listener) {
			mResultListener = listener;
			mCurLooper = Looper.myLooper();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (!isCancelled()) {
				T result = processRequest();
				handResult(result);
			}
			return null;
		}

		private void handResult(final T result) {
			if (isCancelled()) {
				return;
			}
			if (hasLooper()) {
				Handler handler = new Handler(mCurLooper);
				handler.post(new Runnable() {
					@Override
					public void run() {
						notifyListener(result);
					}
				});
			}
		}

		public abstract T processRequest();

		private boolean hasLooper() {
			return mCurLooper != null;
		}

		private void notifyListener(T result) {
			if (mResultListener != null) {
				mResultListener.onResult(result);
			}
		}
	}

}
