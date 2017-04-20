package cn.nubia.oauthnormaldemo.oAuth;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import cn.nubia.oauthnormaldemo.MainActivity;
import cn.nubia.oauthnormaldemo.base.LogUtils;
import cn.nubia.oauthsdk.IOAuthManager;
import cn.nubia.oauthsdk.OAuthError;
import cn.nubia.oauthsdk.OAuthInfo;
import cn.nubia.oauthsdk.OAuthManager;
import cn.nubia.oauthsdk.OAuthToken;
import cn.nubia.oauthsdk.UserInfo;
import cn.nubia.oauthsdk.response.OAuthTokenCallBack;
import cn.nubia.oauthsdk.response.OAuthUseInfoCallBack;
import cn.nubia.oauthsdk.response.WebSynLoginCallBack;
import cn.nubia.oauthsdk.utils.CetificationLackingException;

/**
 * oAuth2.0的调用管理者
 * Created by gbq on 2016-10-26.
 */

public class OAuthTestManager {
    private IOAuthManager mManager;
    private OAuthInfo mOAuthInfo = new OAuthInfo.Builder()
            .setClientId(Define.CLIENT_ID)
            .setState(Define.STR_STATE)
            .build();

    private MainActivity context;
    private String mExpireIn = null;

    public OAuthTestManager(MainActivity context) {
        this.context = context;
    }

    private OAuthTokenCallBack oAuthTokenCallBack = new OAuthTokenCallBack() {
        @Override
        public void onSuccess(OAuthToken oAuthToken) {
            //context.showAlert(oAuthToken.toString());
            context.showResponse(oAuthTokenString(oAuthToken));
            Define.TOKEN = oAuthToken.getAccessToken();
            mExpireIn = oAuthToken.getExpireIn();
            Define.CURRENT_CODE = oAuthToken.getCode();
            if (!TextUtils.isEmpty(Define.CURRENT_CODE)) {
                Define.TOKEN = "";
                context.showCodeNext();
            }
            context.closeProcess();
        }

        @Override
        public void onError(OAuthError oAuthError) {
            //context.showAlert(oAuthError.getErrorDescription());
            Define.TOKEN = "";
            Define.CURRENT_CODE = "";
            context.showResponse(oAuthErrorString(oAuthError));
            context.closeProcess();
        }
    };

    public void getWebToken(boolean isSkip, String clientId, String scope, String redirectUrl) {
        mOAuthInfo.setResponseType(Define.RESPONSE_TOKEN);
        mOAuthInfo.setSkipConfirm(isSkip);
        mOAuthInfo.setClientId(clientId);
        mOAuthInfo.setScope(scope);
        mOAuthInfo.setRedirectUri(redirectUrl);
        mManager = new OAuthManager(mOAuthInfo);
        mManager.webOAuthImplicitToken(oAuthTokenCallBack, context);
    }

    public void getCode(boolean isSkip, String clientId, String scope, String redirectUrl) {
        mOAuthInfo.setResponseType(Define.RESPONSE_CODE);
        mOAuthInfo.setSkipConfirm(isSkip);
        mOAuthInfo.setClientId(clientId);
        mOAuthInfo.setScope(scope);
        mOAuthInfo.setRedirectUri(redirectUrl);
        mManager = new OAuthManager(mOAuthInfo);
        mManager.ssOAuthCode(oAuthTokenCallBack, context);
    }

    public void getUserOpenInfo() {
        if (!getIsOAuth()) {
            context.showAlert(Define.SHOULD_OAUTH);
            context.closeProcess();
            return;
        }
        mManager.getUserOpenInfo(Define.CLIENT_ID, Define.TOKEN,
                new OAuthUseInfoCallBack() {
                    @Override
                    public void onUserInfo(UserInfo userInfo) {
                        //context.showAlert(userInfo.toString());
                        context.showResponse(userInfoString(userInfo));
                        context.closeProcess();
                    }

                    @Override
                    public void onError(OAuthError oAuthError) {
                        //context.showAlert(oAuthError.toString());
                        context.showResponse(oAuthErrorString(oAuthError));
                        context.closeProcess();
                    }
                });
    }

    public void getUserAllInfo() {
        if (!getIsOAuth()) {
            context.showAlert(Define.SHOULD_OAUTH);
            context.closeProcess();
            return;
        }
        mManager.getUserInfo(Define.CLIENT_ID, Define.TOKEN,
                new OAuthUseInfoCallBack() {

                    @Override
                    public void onUserInfo(UserInfo userInfo) {
                        //context.showAlert(userInfo.toString());
                        context.showResponse(userInfoString(userInfo));
                        context.closeProcess();
                    }

                    @Override
                    public void onError(OAuthError oAuthError) {
                        //context.showAlert(oAuthError.toString());
                        context.showResponse(oAuthErrorString(oAuthError));
                        context.closeProcess();
                    }
                });
    }

    private boolean getIsOAuth() {
        return !TextUtils.isEmpty(Define.TOKEN);
    }

    private String oAuthTokenString(OAuthToken oAuthToken) {
        //noinspection StringBufferReplaceableByString
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(oAuthToken.getAccessToken())) {
            stringBuilder.append("accessToken:").append(oAuthToken.getAccessToken()).append("\n");
        }
        if (!TextUtils.isEmpty(oAuthToken.getExpireIn())) {
            stringBuilder.append("expireIn:").append(oAuthToken.getExpireIn()).append("\n");
        }
        if (!TextUtils.isEmpty(oAuthToken.getScope())) {
            stringBuilder.append("scope:").append(oAuthToken.getScope()).append("\n");
        }
        if (!TextUtils.isEmpty(oAuthToken.getClientKey())) {
            stringBuilder.append("clientKey:").append(oAuthToken.getClientKey()).append("\n");
        }
        if (!TextUtils.isEmpty(oAuthToken.getState())) {
            stringBuilder.append("state:").append(oAuthToken.getState()).append("\n");
        }
        if (!TextUtils.isEmpty(oAuthToken.getCode())) {
            stringBuilder.append("code:").append(oAuthToken.getCode()).append("\n");
        }
        return stringBuilder.toString();
    }

    private String oAuthErrorString(OAuthError oAuthError) {
        //noinspection StringBufferReplaceableByString
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(oAuthError.getErrorType())) {
            stringBuilder.append("errorType:").append(oAuthError.getErrorType()).append("\n");
        }
        if (!TextUtils.isEmpty(oAuthError.getErrorDescription())) {
            stringBuilder.append("errorDescription:").append(oAuthError.getErrorDescription()).append("\n");
        }
        if (!TextUtils.isEmpty(oAuthError.getState())) {
            stringBuilder.append("state:").append(oAuthError.getState()).append("\n");
        }
        return stringBuilder.toString();
    }

    private String userInfoString(UserInfo userInfo) {
        //noinspection StringBufferReplaceableByString
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(userInfo.getOpenid())) {
            stringBuilder.append("openid:").append(userInfo.getOpenid()).append("\n");
        }
        if (!TextUtils.isEmpty(userInfo.getNickName())) {
            stringBuilder.append("nickName:").append(userInfo.getNickName()).append("\n");
        }
        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
            stringBuilder.append("avatar:").append(userInfo.getAvatar()).append("\n");
        }
        if (!TextUtils.isEmpty(userInfo.getEmail())) {
            stringBuilder.append("email:").append(userInfo.getEmail()).append("\n");
        }
        if (!TextUtils.isEmpty(userInfo.getMobile())) {
            stringBuilder.append("mobile:").append(userInfo.getMobile()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void userCertification(Activity Activity) {
        mManager = new OAuthManager(mOAuthInfo);
        try {
            mManager.jumptoCertificationActivity(Activity);
        } catch (CetificationLackingException e) {
            context.showAlert("CetificationLackingException");
        }
    }

    public void webSynLogin(MainActivity Activity) {
        showSingleChoiceDialog(Activity);
    }

    private int mChoice;

    private void showSingleChoiceDialog(final MainActivity activity) {
        final AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(activity);
        singleChoiceDialog.setTitle("请选择url：");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(Define.WEB_URL, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mManager = new OAuthManager(null);
                        mManager.appWebSynlogin(Define.WEB_URL[mChoice], new WebSynLoginCallBack() {
                            @Override
                            public void onError(String s) {
                                LogUtils.d(s);
                                activity.showResponse(s);
                            }

                            @Override
                            public void onSuccess(String s) {
                                LogUtils.d(s);
                                activity.loadWebView(Define.WEB_URL[mChoice],s);
                            }
                        }, activity);
                    }
                });
        singleChoiceDialog.show();
    }
}
