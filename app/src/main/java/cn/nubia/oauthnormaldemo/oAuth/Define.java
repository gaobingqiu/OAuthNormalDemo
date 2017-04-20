package cn.nubia.oauthnormaldemo.oAuth;

/**
 * 定义一些常量
 * Created by gbq on 2016-10-27.
 */

public class Define {
    //正式
//    public final static String CLIENT_ID = "SID-100000001553";
//    public static String REDIRECT_URL = "https://passport.server.nubia.cn";
//    public final static String CLIENT_SECRET = "QFISzgv6h0iB0MN3LlBVJ6xsIWdYASSV";

    //集测
    public final static String CLIENT_ID = "Ba5tw2bmEPZTmUe9";
    public static String REDIRECT_URL = "https://zuid-test.server.nubia.cn";
    public final static String CLIENT_SECRET = "anA1zMLrgmEBdh7cOWjJlKrg6cEc5W4z";

    //    public static String REDIRECT_URL = "http://passport-dev.server.nubia.cn/nubia_zuid";


    public final static String STR_STATE = "state";
    //简化模式
    public final static String RESPONSE_TOKEN = "token";
    //授权码模式
    public final static String RESPONSE_CODE = "code";
    public final static String TIPS_TITLE = "返回信息：";

    public final static String SHOULD_OAUTH = "请先授权";
    public final static String CONFIRM = "确定";
    public final static String PROCESS_MSG = "玩命加载中";

    /**
     * 该参数用于存储token信息
     */
    public static String TOKEN = null;

    public static String REFRESH_TOKEN = "";
    public static String CURRENT_CODE = "";

    public final static int PERMISSION_GET_ACCOUNT = 1;

    //正式
//	public final static String[] WEB_URL = {
//			"http://bbs.nubia.cn/",
//			"http://m.nubia.com/",
//			"https://account.ztehn.com/",
//			"http://open.nubia.com/",
//			"http://m.ztehn.com/",
//			"http://bbs.ui.nubia.cn/",
//	};

    //集测
    public final static String[] WEB_URL = {
            "http://pre.m.nubia.com/",
            "http://bbs3.server.ztemt.com.cn/",
    };
}
