package cn.nubia.oauthnormaldemo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        ChineseNameTest("高炳秋");
    }

    static boolean ChineseNameTest(String name) {
        if (!name.matches("[\u4e00-\u9fa5]{2,4}")) {
            System.out.println("只能输入2到4个汉字");
            return false;
        } else return true;
    }
}