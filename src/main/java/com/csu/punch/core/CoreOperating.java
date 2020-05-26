package com.csu.punch.core;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.csu.punch.mail.EmailUtils;
import com.csu.punch.utils.Websites;
import com.csu.punch.vo.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核心操作类
 */
@Component
public class CoreOperating {

    @Autowired
    EmailUtils emailUtils;


    /**
     * 接收每次请求的response
     */
    private HttpResponse response;

    /**
     * 打开CSU_MOMILE_CAMPUS_INDEX时的Cookie
     * BIGipServerpool_ca_80
     */
    private String indexCookie;


    /**
     * 登录CSU_MOMILE_CAMPUS_INDEX时的Cookie
     * ASP.NET_SessionId
     */
    private String loginCookie;


    /**
     * 通过CSU_VALIDATE返回的Cookie
     * BIGipServerpool_wxxy.csu.edu.cn
     */
    private String validateCookie1;

    /**
     * 通过CSU_VALIDATE返回的Cookie
     * eai-sess
     */
    private String validateCookie2;

    /**
     * 通过CSU_UUKEY网址获取UUKEY Cookie
     */
    private String UUKeyCookie;

    /**
     * Step1 : 获取打开CSU_MOMILE_CAMPUS_INDEX网页的Cookie
     */
    private void getIndexCookie() {
        System.out.println("----- getIndexCookie Start -----");
        response = HttpRequest.get(Websites.CSU_MOMILE_CAMPUS_INDEX).execute();
        System.out.println(response.header("Set-Cookie"));
        indexCookie = response.header("Set-Cookie").split(";")[0];
        System.out.println("----- getIndexCookie End -----");
    }

    /**
     * Step2 : 登录CSU_MOMILE_CAMPUS_INDEX
     * @param user
     */
    private void login(User user) {
        System.out.println("----- login Start -----");
        Map<String, Object> map = new HashMap<>();
        map.put("userName", user.getUsername());
        map.put("passWord", user.getPassword());
        map.put("enter", true);

        response = HttpRequest.post(Websites.CSU_MOMILE_CAMPUS_INDEX)
                .form(map)
                .cookie(indexCookie)
                .execute();
        System.out.println(response.header("Set-Cookie"));
        loginCookie = response.header("Set-Cookie").split(";")[0];
        System.out.println("----- login End -----");
    }


    /**
     * Step3 : 验证CSU身份的正确性
     */
    private void validate() {
        System.out.println("----- validate Start -----");
        response = HttpRequest.get(Websites.CSU_SSOSERVICE)
                .cookie(indexCookie + ";" + loginCookie)
                .execute();

        String tokenId = "";
        String account = "";
        String thirdsys = "";
        Document document = Jsoup.parse(response.body());
        Elements inputTagEles = document.getElementsByTag("input");
        for (Element inputEle : inputTagEles) {
            if (inputEle.attr("name").equals("tokenId")) {
                tokenId = inputEle.attr("value");
            } else if (inputEle.attr("name").equals("account")) {
                account = inputEle.attr("value");
            } else if (inputEle.attr("name").equals("Thirdsys")) {
                thirdsys = inputEle.attr("value");
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("tokenId", tokenId);
        map.put("account", account);
        map.put("Thirdsys", thirdsys);

        response = HttpRequest.post(Websites.CSU_VALIDATE)
                .form(map)
                .execute();
        System.out.println(response.getCookies().get(0).toString());
        System.out.println(response.getCookies().get(1).toString());
        validateCookie1 = response.getCookies().get(0).toString();
        validateCookie2 = response.getCookies().get(1).toString();
        System.out.println("----- validate End -----");
    }

    /**
     * Step4 : 获取UUKEY Cookie
     */
    private void getUUKEY() {
        System.out.println("----- getUUKEY Start -----");
        response = HttpRequest.get(Websites.CSU_UUKEY)
                .cookie(validateCookie1 + ";" + validateCookie2)
                .header(Header.REFERER, Websites.CSU_UUKEY_REFERER)
                .header("X-Requested-With", "XMLHttpRequest")
                .execute();
        System.out.println(response.header("Set-Cookie"));
        UUKeyCookie = response.header("Set-Cookie").split(";")[0];
        System.out.println("----- getUUKEY End -----");
    }

    /**
     * Step5 : 健康打卡
     */
    private void healthPunch(User user) {
        System.out.println("----- healthPunch Start -----");
        // 获取到上一次个人的打卡信息->json字符串
        response = HttpRequest.get(Websites.CSU_HEALTH_PUNCH_INDEX)
                .cookie(validateCookie1 + ";" + validateCookie2 + ";" + UUKeyCookie)
                .execute();
        List<String> list = ReUtil.findAll("oldInfo: [\\s\\S]*tipMsg", response.body(), 0);
        String originJson = StrUtil.removePrefix(list.get(0), "oldInfo: ");
        originJson = StrUtil.sub(originJson, 0, originJson.lastIndexOf('}') + 1);

        // 获取真实姓名
        List<String> list1 = ReUtil.findAll("realname: \"([^\\\"]+)\",", response.body(), 0);
        String name = list1.get(0);
        name = StrUtil.sub(name, name.indexOf('"') + 1, name.lastIndexOf('"'));

        // 构造出提交的新的json字符串
        JSONObject jsonObject = JSONUtil.parseObj(originJson);
        jsonObject.set("date", DateUtil.format(DateUtil.date(), "yyyyMMdd"));
        jsonObject.set("created", Math.round(System.currentTimeMillis() / 1000.000)); // 四舍五入 秒级时间戳
        jsonObject.put("name", name);
        jsonObject.put("number", user.getUsername());
        Map<String, Object> map = jsonObject; // 将构造好的jsonObject字符串直接转换为map

        // post 提交数据
        response = HttpRequest.post(Websites.CSU_HEALTH_PUNCH_SAVE)
                .cookie(indexCookie + ";" + loginCookie + ";" + validateCookie2 + ";" + UUKeyCookie)
                .form(map)
                .execute();

        /**
         * 这里response.body()返回结果：
         * {"e":0,"m":"操作成功","d":{}}
         * {"e":1,"m":"今天已经填报了","d":{}}
         * 可见e为0时操作成功
         * e为1时操作失败，m中记录了失败的原因
         */
        String res = response.body();
        JSONObject resJsonObj = JSONUtil.parseObj(res);
        System.out.println(resJsonObj.toString());
        // 最后发送邮件
        emailUtils.sendMail(user, resJsonObj);
        System.out.println("----- healthPunch End -----");
    }



    /**
     * 向外暴露的方法，CSU健康打卡
     * @param user
     */
    public void run(User user) {
        getIndexCookie();
        login(user);
        validate();
        getUUKEY();
        healthPunch(user);
    }
}
