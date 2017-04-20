package cn.nubia.oauthnormaldemo;

/**
 * main的一些方法
 * Created by gbq on 2016-10-26.
 */

public interface IMainCtrl {
    public void showProcess(String msg);

    public void closeProcess();

    public void showAlert(String msg);

    void showResponse(String response);

    void showCodeNext();

    void loadWebView(String webUrl,String synUrl);
}
