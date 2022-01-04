package com.fz.mainTest;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.sql.Time;


/**
 * 描述：
 *
 * @author FireLang
 * 创建时间：2020-09-27 20:59
 */

public class TestRegJava {

    //无限打码地址数据
    private static final String username = "起个名很难";
    private static final String password = "fangzhen,7410";
    private static final String url = "http://51learn.vip:3001/pub/login";
    private static final String interfaceUrl = "http://51learn.vip:3001/slider/reg";

    //澳洋九价地址
    private static final String aoYangUrl = "http://health-exp.ayyy.cn/ayhpv//hpv/info";

    //浏览器驱动
//    private static WebDriver driver = new ChromeDriver();


    public static void main(String[] args) {
        String bs4Img = "";
//        postHpvUrlForSelenium();
//        postHpvUrlForJsoup();
        String position = getPosition(bs4Img);
    }

    /**
     * 访问无限打码接口,获取定位
     *
     * @return 定位数据
     */
    private static String getPosition(String bs4Img) {
        String code = "";
        // 登录
        System.out.println("账号:" + username);
        System.out.println("密码:" + password);
        System.out.println("登录中..");
        HttpResponse response = HttpRequest.post(url).contentType("application/json;charset=UTF-8").body(String.format("{\"username\": \"%s\", \"pwd\": \"%s\"}", username, password)).execute();
        String token = (String) JSONUtil.parseObj(response.body()).getByPath("data.authentication");
        System.out.println("登录成功..");
        System.out.println("token:" + token);

        // 识别验证码
        System.out.println("识别中..");
        // 图片的Base64
        //String bs4Img = "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";
        response = HttpRequest.post(interfaceUrl).contentType("application/json;charset=UTF-8").header("Authorization", String.format("Bearer %s", token)).body(String.format("{\"captchaData\": \"%s\"}", bs4Img)).execute();
        // 获取识别结果
        if (response.body().length() > 0) {
            code = (String) JSONUtil.parseObj(response.body()).getByPath("data");
        }
        System.out.println("识别结果:" + code);
        return code;
    }

    /**
     * Jsoup方式
     */
    private static void postHpvUrlForJsoup() {
        try {
            Document document = Jsoup.connect(aoYangUrl).header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36").get();
            if (document.body().hasText()) {
                //获取到对应的姓名框
                document.getElementById("customerName").attr("value", "方清");
                document.getElementById("idCard").attr("value", "340923199908128118");
                document.getElementById("mobile").attr("value", "123123123");
                document.getElementById("type9").attr("checked", "true");

                Element customerName = document.getElementById("customerName");
                Element idCard = document.getElementById("idCard");
                Element mobile = document.getElementById("mobile");
                System.out.println("customerName:" + customerName.val() + ",idCard:" + idCard.val() + ",mobile:" + mobile.val());
                Elements radios = document.select("input[name='type']");
                for (Element radio : radios) {
                    System.out.println(radio.val() + "价:" + radio.attr("checked"));
                }

                //触发点击事件"下一步"
                document.getElementById("TencentCaptcha").attr("click");
            }
        } catch (IOException e) {
            System.out.println("出现错误:" + e.getMessage());
        }

    }


    private static void postHpvUrlForSelenium() {
        //加载驱动，后面的路径自己要选择正确，也可以放在本地
        System.setProperty("webdriver.chrome.driver", "./libs/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        //初始化一个谷歌浏览器实例，实例名称叫driver
        try {
            driver.get(aoYangUrl);
            Thread.sleep(1000);
            driver.findElement(By.id("customerName")).sendKeys("方清");
            driver.findElement(By.id("idCard")).sendKeys("340923199908128118");
            driver.findElement(By.id("mobile")).sendKeys("123123123");

            //写JS方法让界面显示单选框
            String inputShow = "$('#selectType9,#selectType4,#selectType2').show()";
            JavascriptExecutor jsShow = (JavascriptExecutor) driver;
            jsShow.executeScript(inputShow);

            //勾选九价
            String input9JChecked = "$(\"input:radio[id='type9']\").attr('checked','true')";
            JavascriptExecutor js9JChecked = (JavascriptExecutor) driver;
            js9JChecked.executeScript(input9JChecked);

            System.out.println(driver.findElement(By.id("type9")).isSelected()); //单选框

            //点击下一步按钮
            driver.findElement(By.id("TencentCaptcha")).sendKeys(Keys.SPACE);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            //driver.close();
        }
    }

}
