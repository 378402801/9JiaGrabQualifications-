package com.fz.mainTest;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fz.Entity.MoveEntity;
import com.fz.Entity.Person;
import com.fz.Utils.PropertyUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.SSLContexts;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * selenium破解顺丰滑动验证码
 */
public class TencentCrawler {
    //    private static String BASE_PATH = "D:/javaprojectmyself/selenium-geetest-crack-master/";
    private static final String BASE_PATH = "E:/TencentImg/";
    //小方块距离左边界距离，对应到原图的距离
    private static final int START_DISTANCE = (22 + 16) * 2;

    private boolean flag = true;

//    private static ChromeDriver driver = null;

    static {
        System.setProperty("webdriver.chrome.driver", "./libs/chromedriver.exe");
    }

    public static void main(String[] args) {
        TencentCrawler tencentCrawler = new TencentCrawler();
        //指定日期调用
        String time = PropertyUtil.getProperties("readyTime");
        Timer timer = new Timer();
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tencentCrawler.getPersonAndStart();
                }
            }, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 脚本入口
     */
    public void getPersonAndStart() {
        Person person = null;
        JSONArray person_array = PropertyUtil.TraversePropertiesInfo();
        for (Object obj : person_array) {
            person = new Person();
            person.setName(new JSONObject(obj).get("person.name").toString());
            person.setIdCard(new JSONObject(obj).get("person.idCard").toString());
            person.setPhone(new JSONObject(obj).get("person.phone").toString());
            person.setType(new JSONObject(obj).get("person.type").toString());
            System.out.println("person:" + person);
            Person finalPerson = person;
            new Thread(() -> {
                new TencentCrawler().crawl(finalPerson);
            }, person.getName()).start();
        }
    }

    /**
     * 脚本入口
     *
     * @param person 人员信息
     */
    public void crawl(Person person) {
        TencentCrawler tencentCrawler = new TencentCrawler();
        long startTime = System.currentTimeMillis();   //获取开始时间
        String type = person.getType();
        ChromeOptions chromeOptions = new ChromeOptions();
        // 设置禁止加载项
        Map<String, Object> prefs = new HashMap<>();
        // 禁止加载js
        prefs.put("profile.default_content_settings.javascript", 2); // 2就是代表禁止加载的意思
        // 禁止加载css
        prefs.put("profile.default_content_settings.images", 2); // 2就是代表禁止加载的意思
        chromeOptions.setExperimentalOption("prefs", prefs);
        //chromeOptions.addArguments("disable-infobars");
        ChromeDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().setSize(new Dimension(1024, 768));
        driver.get("http://health-exp.ayyy.cn/ayhpv//hpv/info");

        String time = PropertyUtil.getProperties("writeTime");
        Timer timer = new Timer(person.getName());
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tencentCrawler.scriptEntry(driver, type, startTime);
                }
            }, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * 脚本入口代码
     *
     * @param driver
     * @param type
     * @param startTime
     */
    public void scriptEntry(ChromeDriver driver, String type, long startTime) {
        long currentTimeMillis = System.currentTimeMillis();
        try {
//            driver.navigate().refresh();  //刷新页面
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.SECONDS);
            long startTime5 = System.currentTimeMillis();   //获取开始时间
            driver.findElement(By.id("customerName")).sendKeys("方清");
            driver.findElement(By.id("idCard")).sendKeys("340923199908128128");
            driver.findElement(By.id("mobile")).sendKeys("13358049091");
//                driver.findElement(By.id("customerName")).sendKeys(person.getName());
//                driver.findElement(By.id("idCard")).sendKeys(person.getIdCard());
//                driver.findElement(By.id("mobile")).sendKeys(person.getPhone());

            //写JS方法让界面显示单选框
            String inputShow = "$('#selectType9,#selectType4,#selectType2').show()";
            ((JavascriptExecutor) driver).executeScript(inputShow);

            //勾选九价
            String input9JChecked = "$(\"input:radio[id='type" + type + "']\").attr('checked','true')";
            ((JavascriptExecutor) driver).executeScript(input9JChecked);
            //点击下一步按钮
            driver.findElement(By.id("TencentCaptcha")).sendKeys(Keys.SPACE);
            Thread.sleep(1000);
            Actions actions = new Actions(driver);
            //移动滑块
            WebElement element = null;
            long result = 0;
            driver.switchTo().frame("tcaptcha_iframe");
            //判断滑块是否成功,未成功则继续滑块
            while (flag) {
                result += moveTXVerification(driver, startTime5, currentTimeMillis, element, actions);
            }
            System.out.println("执行移动程序运行时间： " + result + "ms");

            String clickTime = PropertyUtil.getProperties("clickTime");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    clickSure(driver, startTime);
                }
            }, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(clickTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移动腾讯滑块方法
     *
     * @param driver            页面
     * @param startTime5        开始时间
     * @param currentTimeMillis 当前毫秒值
     * @param element           标签对象
     * @param actions           动作时间
     * @return 整体滑动时间计算
     * @throws IOException
     * @throws InterruptedException
     */
    public long moveTXVerification(ChromeDriver driver, long startTime5, long currentTimeMillis, WebElement element, Actions actions) throws IOException, InterruptedException {
        String originalUrl = Jsoup.parse(driver.getPageSource()).select("[id=slideBg]").first().attr("src");
        long endTime5 = System.currentTimeMillis(); //获取结束时间
        System.out.println("加载网页以及图片程序运行时间： " + (endTime5 - startTime5) + "ms");
        long startTime3 = System.currentTimeMillis();   //获取开始时间
        downloadOriginalImg(0, originalUrl, driver.manage().getCookies(), currentTimeMillis);
        long endTime3 = System.currentTimeMillis(); //获取结束时间
        System.out.println("下载图片程序运行时间： " + (endTime3 - startTime3) + "ms");
        float bgWrapWidth = driver.findElement(By.className("tc-drag-track")).getSize().getWidth();
        System.out.println("bgWrapWidth:" + bgWrapWidth);
        long startTime1 = System.currentTimeMillis();   //获取开始时间
        int distance = calcMoveDistance(0, bgWrapWidth, currentTimeMillis);
        System.out.println("distance:" + distance);
        long endTime1 = System.currentTimeMillis(); //获取结束时间
        System.out.println("计算小方块需要移动的距离程序运行时间： " + (endTime1 - startTime1) + "ms");
        long startTime2 = System.currentTimeMillis(); //获取开始时间
        List<MoveEntity> list = getMoveEntity1(distance);
        long endTime2 = System.currentTimeMillis(); //获取结束时间
        System.out.println("计算移动算法程序运行时间： " + (endTime2 - startTime2) + "ms");
        element = driver.findElement(By.id("tcaptcha_drag_button"));
        actions.clickAndHold(element).perform();
        int d = 0;
        long startTime4 = System.currentTimeMillis(); //获取开始时间
        for (MoveEntity moveEntity : list) {
            actions.moveByOffset(moveEntity.getX(), moveEntity.getY()).perform();
            System.out.println(Thread.currentThread().getName() + "向右总共移动了:" + (d = d + moveEntity.getX()) + "高度:" + moveEntity.getY());
            Thread.sleep(moveEntity.getSleepTime());
        }
        actions.release(element).perform();
        Thread.sleep(2000);
        //判断是否成功
        try {
            WebElement element1 = driver.findElement(By.xpath("//a[@class='layui-layer-btn0']"));
            if (element1.isDisplayed()) {
                flag = false;
            } else {
                System.out.println("滑块失败,重新启动滑块!");
            }
        } catch (NoSuchElementException e) {
            System.out.println("滑块失败,重新启动滑块!");
        }
        long endTime4 = System.currentTimeMillis(); //获取结束时间
        return endTime4 - startTime4;
    }


    /**
     * 最后的确认按键
     *
     * @param driver 页面
     */
    public void clickSure(ChromeDriver driver, Long startTime) {
        //点击确认
        WebElement sureBtn = driver.findElement(By.className("layui-layer-btn0"));
        sureBtn.click();
        //判断是否成功
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String title = driver.getTitle();
        System.out.println("标题:" + title);
        if ("预约失败".equals(title)) {
            //发送失败邮件
        } else {
            //发送成功邮件
        }
        List<WebElement> elements = driver.findElements(By.className("route-list"));
        for (WebElement webElement : elements) {
            System.out.println(webElement.getText());
        }

        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序总运行时间： " + (endTime - startTime) + "ms");
        driver.quit();
    }


    private void downloadOriginalImg(int i, String originalUrl, Set<Cookie> cookieSet, Long currentTimeMillis) throws IOException {
        CookieStore cookieStore = new BasicCookieStore();
        cookieSet.forEach(c -> {
            BasicClientCookie cookie = new BasicClientCookie(c.getName(), c.getValue());
            cookie.setPath(c.getPath());
            cookie.setDomain(c.getDomain());
            cookie.setExpiryDate(c.getExpiry());
            cookie.setSecure(true);
            cookieStore.addCookie(cookie);
        });
        InputStream is = null;
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType())
                    , (chain, authType) -> true).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.INSTANCE)
                            .register("https", new SSLConnectionSocketFactory(sslContext))
                            .build();
            is = HttpClients.custom()
//                    .setProxy(new HttpHost("127.0.0.1", 8888))
                    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36")
                    .setDefaultCookieStore(cookieStore)
                    .setConnectionManager(new PoolingHttpClientConnectionManager(socketFactoryRegistry))
                    .build()
                    .execute(new HttpGet(originalUrl))
                    .getEntity().getContent();
            FileUtils.copyInputStreamToFile(is, new File(BASE_PATH + "tencent-original" + i + "-" + currentTimeMillis + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算小方块需要移动的距离
     *
     * @param i
     * @param bgWrapWidth 背景图片div对应的width
     * @return
     * @throws IOException
     */
    public int calcMoveDistance(int i, float bgWrapWidth, Long currentTimeMillis) throws IOException {
        BufferedImage fullBI = ImageIO.read(new File(BASE_PATH + "tencent-original" + i + "-" + currentTimeMillis + ".png"));
        for (int w = 340; w < fullBI.getWidth() - 18; w++) {
            int whiteLineLen = 0;
            for (int h = 0; h < fullBI.getHeight(); h++) {
//                int[] fullRgb = new int[3];
//                fullRgb[0] = (fullBI.getRGB(w, h) & 0xff0000) >> 16;
//                fullRgb[1] = (fullBI.getRGB(w, h) & 0xff00) >> 8;
//                fullRgb[2] = (fullBI.getRGB(w, h) & 0xff);
                if (isBlack28(fullBI, w, h) && isWhite(fullBI, w, h)) {
                    whiteLineLen++;
                } else {
//                    whiteLineLen = 0;
                    continue;
                }
                if (whiteLineLen >= 50) {
                    return (int) ((w - START_DISTANCE) / (fullBI.getWidth() / bgWrapWidth) + 5);
                }
            }

        }
        throw new RuntimeException("计算缺口位置失败");
    }

    /**
     * 当前点的后28个是不是黑色
     *
     * @return 后28个中有80%是黑色返回true, 否则返回false
     */
    private boolean isBlack28(BufferedImage fullBI, int w, int h) {
        int[] fullRgb = new int[3];
        double blackNum = 0;
        int num = Math.min(fullBI.getWidth() - w, 28);
        for (int i = 0; i < num; i++) {
            fullRgb[0] = (fullBI.getRGB(w + i, h) & 0xff0000) >> 16;
            fullRgb[1] = (fullBI.getRGB(w + i, h) & 0xff00) >> 8;
            fullRgb[2] = (fullBI.getRGB(w + i, h) & 0xff);
            if (isBlack(fullRgb)) {
                blackNum = blackNum + 1;
            }
        }
        return blackNum / num > 0.8;
    }

    /**
     * 当前点是不是白色
     *
     * @param fullBI
     * @param w
     * @param h
     * @return
     */
    private boolean isWhite(BufferedImage fullBI, int w, int h) {
        int[] fullRgb = new int[3];
        fullRgb[0] = (fullBI.getRGB(w, h) & 0xff0000) >> 16;
        fullRgb[1] = (fullBI.getRGB(w, h) & 0xff00) >> 8;
        fullRgb[2] = (fullBI.getRGB(w, h) & 0xff);
        return isWhite(fullRgb);
    }

    private boolean isWhite(int[] fullRgb) {
        return (Math.abs(fullRgb[0] - 0xff) + Math.abs(fullRgb[1] - 0xff) + Math.abs(fullRgb[2] - 0xff)) < 125;
    }

    private boolean isBlack(int[] fullRgb) {
        return fullRgb[0] * 0.3 + fullRgb[1] * 0.6 + fullRgb[2] * 0.1 <= 125;
    }

    /**
     * 默认移动算法
     *
     * @param distance
     * @return
     */
    public List<MoveEntity> getMoveEntity(int distance) {
        List<MoveEntity> list = new ArrayList<>();
        for (int i = 0; i < distance; i++) {

            MoveEntity moveEntity = new MoveEntity();
            moveEntity.setX(1);
            moveEntity.setY(0);
            moveEntity.setSleepTime(0);
            list.add(moveEntity);
        }
        return list;
    }

    /**
     * 移动算法
     *
     * @param distance
     * @return
     */
    public List<MoveEntity> getMoveEntity1(int distance) {
        List<MoveEntity> list = new ArrayList<>();
        for (int i = 0; i < distance / 10; i++) {
            MoveEntity moveEntity = new MoveEntity();
            moveEntity.setX(10);
            moveEntity.setY(ThreadLocalRandom.current().nextBoolean() ? 10 : -10);
            moveEntity.setSleepTime(1);
            list.add(moveEntity);
        }

        MoveEntity moveEntity = new MoveEntity();
        moveEntity.setX(distance % 10);
        moveEntity.setY(0);
        moveEntity.setSleepTime(1);
        list.add(moveEntity);
        return list;
    }
}
