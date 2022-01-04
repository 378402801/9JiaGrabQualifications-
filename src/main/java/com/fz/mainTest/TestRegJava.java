package com.fz.mainTest;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Random;


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

    //其他
    private static int successTimes = 0;


    public static void main(String[] args) {
        String bs4Img = "";
        postHpvUrlForSelenium();
//        postHpvUrlForJsoup();
//        String position = getPosition(bs4Img);
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
            Thread.sleep(5000);
            //拖动滑块
            System.out.println("开始拖动滑块...");
            WebElement iframe = driver.findElement(By.xpath("//iframe"));
            //等待2秒加载
            Thread.sleep(2000);
            driver.switchTo().frame(iframe);

            Thread.sleep(2000);
            By moveBtn = By.id("tcaptcha_drag_thumb");//滑块按钮
            //waitForLoad(driver, moveBtn);
            WebElement moveElement = driver.findElement(moveBtn);
            int i = 0;
            String code = "";
            int distance = 0;
            while (i++ < 15) {
                code = getPosition("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");
                distance = Integer.parseInt(code.split(",")[0]);
                move(driver, moveElement, distance - 6);
//                By gtTypeBy = By.cssSelector(".gt_info_type");//验证结果类型
//                By gtInfoBy = By.cssSelector(".gt_info_content");//验证结果内容
//                waitForLoad(driver, gtTypeBy);
//                String gtType = driver.findElement(gtTypeBy).getText();
//                waitForLoad(driver, gtInfoBy);
//                String gtInfo = driver.findElement(gtInfoBy).getText();//StaleElementReferenceException
//                System.out.println(gtType + "---" + gtInfo);
//                if (gtType.contains("验证通过")) {
//                    successTimes++;
//                }

                String gtText = driver.findElement(By.className("tc-title")).getText();

                /**
                 * 再来一次：
                 * 验证失败：
                 */
//                if (!gtType.equals("再来一次:") && !gtType.equals("验证失败:")) {
//                    Thread.sleep(4000);
//                    System.out.println(driver);
//                    break;
//                }
                if (!gtText.equals("拖动下方滑块完成拼图")) {
                    Thread.sleep(4000);
                    System.out.println(driver);
                    break;
                }
                Thread.sleep(4000);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            //driver.close();
        }
    }


    /**
     * 等待元素加载，10s超时
     *
     * @param driver
     * @param by
     */
    public static void waitForLoad(final WebDriver driver, final By by) {
        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement element = driver.findElement(by);
                if (element != null) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 移动
     *
     * @param driver
     * @param element
     * @param distance
     * @throws InterruptedException
     */
    public static void move(WebDriver driver, WebElement element, int distance) throws InterruptedException {
        int xDis = distance;// distance to move
        int moveX = new Random().nextInt(10) - 5;
        int moveY = 1;
        Actions actions = new Actions(driver);
        new Actions(driver).clickAndHold(element).perform();//click and hold the moveButton
        Thread.sleep(2000);//slow down
        actions.moveByOffset((xDis + moveX) / 2, moveY).perform();
        Thread.sleep((int) (Math.random() * 2000));
        actions.moveByOffset((xDis + moveX) / 2, moveY).perform();//double move,to slow down the move and escape check
        Thread.sleep(500);
        actions.release(element).perform();
    }

}
