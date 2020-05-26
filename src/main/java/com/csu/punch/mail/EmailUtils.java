package com.csu.punch.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.csu.punch.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {

    @Autowired
    MailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(User user, JSONObject jsonObject) {
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom(from);
        //邮件接收人
        message.setTo(user.getMail());
        //邮件主题
        if (jsonObject.getInt("e") == 0) { // 打卡成功
            message.setSubject(DateUtil.format(DateUtil.date(), "yyyy-MM-dd") + "打卡成功-CSU自动打卡提醒");
        } else {
            message.setSubject(DateUtil.format(DateUtil.date(), "yyyy-MM-dd") + "打卡失败-CSU自动打卡提醒");
        }

        //邮件内容
        message.setText(jsonObject.toString()); // 直接将json结果作为正文返回算了
        //发送邮件
        mailSender.send(message);

    }
}
