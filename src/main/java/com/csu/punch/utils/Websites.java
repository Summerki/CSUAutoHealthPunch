package com.csu.punch.utils;

public class Websites {

    /**
     * CSU移动校园登陆首页
     */
    public static final String CSU_MOMILE_CAMPUS_INDEX = "http://ca.its.csu.edu.cn/home/login/215";


    /**
     * 获取tokenId
     */
    public static final String CSU_SSOSERVICE = "http://ca.its.csu.edu.cn/SysInfo/SsoService/215";


    /**
     * 验证CSU身份
     */
    public static final String CSU_VALIDATE = "https://wxxy.csu.edu.cn/a_csu/api/sso/validate";


    /**
     * 在获取UUKEY时需要在请求头中加入的referer对应的网址
     */
    public static final String CSU_UUKEY_REFERER = "https://wxxy.csu.edu.cn/site/applicationSquare/index?sid=1";

    /**
     * 获取UUKEY的网址
     */
    public static final String CSU_UUKEY = "https://wxxy.csu.edu.cn/appsquare/wap/default/index?sid=1";

    /**
     * CSU健康打卡首页
     */
    public static final String CSU_HEALTH_PUNCH_INDEX = "https://wxxy.csu.edu.cn/ncov/wap/default/index";

    /**
     * CSU健康打卡提交数据网址
     */
    public static final String CSU_HEALTH_PUNCH_SAVE = "https://wxxy.csu.edu.cn/ncov/wap/default/save";
}
