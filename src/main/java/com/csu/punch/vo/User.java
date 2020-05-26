package com.csu.punch.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * 用户
 */
@Data
@ToString
@AllArgsConstructor
public class User {
    /**
     * 系统学号
     */
    public String username;

    /**
     * 系统密码
     */
    public String password;

    /**
     * 用户自定义的cron表达式
     */
    public String cronExp;

    /**
     * 用户设置的需要提醒的邮箱
     */
    public String mail;
}
