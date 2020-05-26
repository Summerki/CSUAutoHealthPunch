# CSU自动健康打卡

> 再三提醒，本项目仅供技术研究！身体如有不适请尽快就医检查！

基于`SpringBoot`，因此本项目可以很方便的扩展各种各样的功能（作者没时间继续扩展了，够用就行）

+ 已实现的功能：
  + 每日自动打卡
  + 将打卡情况通过邮件发送给用户通知
  + 多用户支持
+ 使用方法：

```shell
# 查看本项目的帮助
java -jar xxx.jar help

# 必选参数的命令
java -jar xxx.jar --username=[学号1];[学号2] --password=[密码1];[密码2] --cron=["cron表达式1"];["cron表达式2"] --mail=[邮箱1];[邮箱2]
# SpringBoot的可选参数都可以使用
```

+ 适用：将JAR包放在云服务器上运行最为合适
+ 编译好的JAR包放在`res->CSUAutoHealthPunch.jar`里