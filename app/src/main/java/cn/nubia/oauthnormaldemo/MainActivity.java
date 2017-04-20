package cn.nubia.oauthnormaldemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

import cn.nubia.oauthnormaldemo.net.DemoNetApis;
import cn.nubia.oauthnormaldemo.net.NetConfig;
import cn.nubia.oauthnormaldemo.oAuth.Define;
import cn.nubia.oauthnormaldemo.oAuth.OAuthTestManager;
import cn.nubia.oauthsdk.api.NetResponseListener;
import cn.nubia.oauthsdk.utils.PackageUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IMainCtrl {
    private static final String TAG = "MainActivity ->";
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean mIsSupportApk = false;
    private ProgressDialog mProgressDialog;
    private OAuthTestManager oAuthTestManager;
    private TextView mResponseView;
    private EditText mClientIdEt;
    private EditText mRedirectUrl;

    private CheckBox mIsSkipCb;
    private CheckBox mOpenUserInfoCb;
    private CheckBox mAllUserInfoCb;

    private Button mGetTokenByCodeBt;
    private Button mRefreshTokenBt;
    private Button mInternalTokenBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置环境
        cn.nubia.oauthsdk.api.NetConfig.setEnvironment(cn.nubia.oauthsdk.api.NetConfig.Environment.TEST);
        mIsSupportApk = PackageUtils.isNubiaSupportOAuthVersion(this);
        findIds();
        oAuthTestManager = new OAuthTestManager(this);
        if (lacksPermission(Manifest.permission.GET_ACCOUNTS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, Define.PERMISSION_GET_ACCOUNT);
        }
    }

    private void findIds() {
        Button getToken = (Button) findViewById(R.id.bt_get_token);
        getToken.setOnClickListener(this);
        Button getCode = (Button) findViewById(R.id.bt_get_code);
        getCode.setOnClickListener(this);

        Button getOpen = (Button) findViewById(R.id.bt_get_open);
        getOpen.setOnClickListener(this);
        Button getAll = (Button) findViewById(R.id.bt_get_all);
        getAll.setOnClickListener(this);

        mIsSkipCb = (CheckBox) findViewById(R.id.skipConfirm);
        mOpenUserInfoCb = (CheckBox) findViewById(R.id.cb_open);
        mAllUserInfoCb = (CheckBox) findViewById(R.id.cb_all);

        findViewById(R.id.bt_to_certification).setOnClickListener(this);
        findViewById(R.id.bt_web_syn_login).setOnClickListener(this);

        mRefreshTokenBt = (Button) findViewById(R.id.bt_refresh_token);
        mRefreshTokenBt.setOnClickListener(this);
        mRefreshTokenBt.setVisibility(View.GONE);
        mGetTokenByCodeBt = (Button) findViewById(R.id.bt_get_token_by_code);
        mGetTokenByCodeBt.setOnClickListener(this);
        mGetTokenByCodeBt.setVisibility(View.GONE);

        mInternalTokenBt = (Button) findViewById(R.id.bt_internal_token);
        mInternalTokenBt.setOnClickListener(this);
        mInternalTokenBt.setVisibility(View.GONE);

        mResponseView = (TextView) findViewById(R.id.tv_response);
        mResponseView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mClientIdEt = (EditText) findViewById(R.id.appId);
        mClientIdEt.setText(Define.CLIENT_ID);
        //设置光标位置到文本未
        mClientIdEt.setSelection(mClientIdEt.getText().toString().length());

        mRedirectUrl = (EditText) findViewById(R.id.et_redirect_url);
        mRedirectUrl.setText(Define.REDIRECT_URL);
    }

    @Override
    public void onClick(View view) {
        this.showProcess(Define.PROCESS_MSG);
        boolean skip = false;
        String clientId = mClientIdEt.getText().toString();
        String redirectUrl = mRedirectUrl.getText().toString();
        if (mIsSkipCb.isChecked()) {
            skip = true;
        }
        switch (view.getId()) {
            case R.id.bt_get_token:
                oAuthTestManager.getWebToken(skip, clientId, getScope(), redirectUrl);
                showTokenNext();
                break;
            case R.id.bt_get_code:
                oAuthTestManager.getCode(skip, clientId, getScope(), redirectUrl);
                break;
            case R.id.bt_get_open:
                oAuthTestManager.getUserOpenInfo();
                break;
            case R.id.bt_get_all:
                oAuthTestManager.getUserAllInfo();
                break;
            case R.id.bt_to_certification:
                this.closeProcess();
                oAuthTestManager.userCertification(this);
                break;

            case R.id.bt_web_syn_login:
                this.closeProcess();
                oAuthTestManager.webSynLogin(this);
                break;

            case R.id.bt_get_token_by_code:
                if (TextUtils.isEmpty(Define.CURRENT_CODE)) {
                    this.showResponse("请先获取授权码！");
                    this.closeProcess();
                    break;
                }
                DemoNetApis.getInstance().getTokenByCode(clientId, redirectUrl,
                        Define.CLIENT_SECRET, NetConfig.GRANT_GET_TOKEN, Define.CURRENT_CODE, netResponseListener);
                break;
            case R.id.bt_refresh_token:
                if (TextUtils.isEmpty(Define.REFRESH_TOKEN)) {
                    this.showResponse("请先获取refreshToken！");
                    this.closeProcess();
                    break;
                }
                DemoNetApis.getInstance().refreshToken(clientId, redirectUrl,
                        Define.CLIENT_SECRET, NetConfig.GRANT_REFRESH_TOKEN, Define.REFRESH_TOKEN, netResponseListener);
                break;

            case R.id.bt_internal_token:
                if (TextUtils.isEmpty(Define.CURRENT_CODE)) {
                    this.showResponse("请先获取授权码！");
                    this.closeProcess();
                    break;
                }
                DemoNetApis.getInstance().getTokenByCode(clientId, redirectUrl,
                        Define.CLIENT_SECRET, NetConfig.GRANT_INTERNAL_TOKEN, Define.CURRENT_CODE, netResponseListener);
                break;
            default:
                break;
        }
    }

    @Override
    public void showProcess(String msg) {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(msg);
            mProgressDialog.show();
        }
    }

    @Override
    public void closeProcess() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void showAlert(String msg) {
        new AlertDialog.Builder(this).setTitle(Define.TIPS_TITLE).setMessage(msg).setPositiveButton(Define.CONFIRM, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void showResponse(String response) {
        DateFormat df = DateFormat.getTimeInstance(); //设置日期格式
        String time = df.format(new Date());
        mResponseView.setText(getString(R.string.response_default) + time + "\n" + response);
    }

    @Override
    public void showCodeNext() {
        mRefreshTokenBt.setVisibility(View.VISIBLE);
        mGetTokenByCodeBt.setVisibility(View.VISIBLE);
        mInternalTokenBt.setVisibility(View.VISIBLE);
        Define.REFRESH_TOKEN = "";
        Define.TOKEN = "";
    }

    public void showTokenNext() {
        mRefreshTokenBt.setVisibility(View.GONE);
        mGetTokenByCodeBt.setVisibility(View.GONE);
        mInternalTokenBt.setVisibility(View.GONE);
    }

    private String getScope() {
        StringBuilder temp = new StringBuilder();
        if (mOpenUserInfoCb.isChecked()) {
            temp.append("userinfo_profile");
        }
        if (mAllUserInfoCb.isChecked()) {
            if (mOpenUserInfoCb.isChecked())
                temp.append(",");
            temp.append("userinfo_all");
        }
        Log.d("scope", "scope=" + temp.toString());
        return temp.toString();
    }

    private NetResponseListener<String> netResponseListener = new NetResponseListener<String>() {
        @Override
        public void onResult(String result) {
            Log.d(TAG, "openuserinfo=" + result);
            resolve(result);
        }
    };


    private void resolve(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String code = json.getString("code");
            if (TextUtils.equals(code, "0")) {
                String response = json.getString("response");
                JSONObject jsonReponse = new JSONObject(response);
                //noinspection StringBufferMayBeStringBuilder
                StringBuffer stringBuffer = new StringBuffer();
                if (jsonReponse.has("open_id")) {
                    String openid = jsonReponse.getString("open_id");
                    stringBuffer.append("open_id:").append(openid).append("\n");
                }
                if (jsonReponse.has("access_token")) {
                    String accessToken = jsonReponse.getString("access_token");
                    Define.TOKEN = accessToken;
                    stringBuffer.append("accessToken:").append(accessToken).append("\n");
                }
                if (jsonReponse.has("expires_in")) {
                    String espiresIn = jsonReponse.getString("expires_in");
                    stringBuffer.append("espiresIn:").append(espiresIn).append("\n");
                }
                if (jsonReponse.has("refresh_token")) {
                    String refreshToken = jsonReponse.getString("refresh_token");
                    Define.REFRESH_TOKEN = refreshToken;
                    stringBuffer.append("refreshToken:").append(refreshToken).append("\n");
                }
                if (jsonReponse.has("scope")) {
                    String scope = jsonReponse.getString("scope");
                    stringBuffer.append("scope:").append(scope).append("\n");
                }
                if (jsonReponse.has("user_id")) {
                    String uid = jsonReponse.getString("user_id");
                    stringBuffer.append("user_id:").append(uid).append("\n");
                }
                showResponse(stringBuffer.toString());
                this.closeProcess();
            } else {
                Define.REFRESH_TOKEN = "";
                Define.TOKEN = "";
                showResponse(json.toString());
                this.closeProcess();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Define.REFRESH_TOKEN = "";
            Define.TOKEN = "";
            showResponse("返回数据异常");
            this.closeProcess();
        }
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10001) {
            if (resultCode == 1000) {
                showResponse("certification success.");
            } else if(resultCode != 0){
                showResponse(String.valueOf(resultCode));
            }
        }
    }

    @Override
    public void loadWebView(String webUrl,String synUrl){
        Intent intent = new Intent();
        intent.putExtra("url",webUrl);
        intent.putExtra("syn_url",synUrl);
        intent.setClass(MainActivity.this, WebActivity.class);
        startActivity(intent);
    }
}
