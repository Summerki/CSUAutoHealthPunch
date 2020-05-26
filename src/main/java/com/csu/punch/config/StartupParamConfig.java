package com.csu.punch.config;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.csu.punch.core.CoreOperating;
import com.csu.punch.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 接收启动参数
 * 1.  --username="x;xx;xxx"
 * 2.  --password="x;xx;xxx"
 * 3.  --cron="x;xx;xxx"  0 0 7 * * ? => 每天早上7点自动执行
 * 4.  --mail="x;xx;xxx"
 * 5.  help
 */
@Slf4j
@Component
public class StartupParamConfig implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    CoreOperating coreOperating;

    /**
     * 全局userList
     */
    public static List<User> userList = new ArrayList<>();

    /**
     * 接收用户传来的username List
     */
    private List<String> usernameList = new ArrayList<>();
    /**
     * 接收用户传来的password List
     */
    private List<String> passwordList = new ArrayList<>();
    /**
     * 接收用户传来的cronExp List
     */
    private List<String> cronExpList = new ArrayList<>();
    /**
     * 接收用户传来的mail List
     */
    private List<String> mailList = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        SpringApplication.exit(applicationContext, () -> 0);

        // 获取帮助 --help
        List<String> nonOptionArgs = args.getNonOptionArgs();
        if (nonOptionArgs.size() > 1) {
            System.out.println("启动参数中无配置参数只有[help]一项，请重新启动！");
            SpringApplication.exit(applicationContext, () -> 0);
        } else if (nonOptionArgs.size() == 1) {
            if (!nonOptionArgs.get(0).equals("help")) {
                System.out.println("启动参数中无配置参数只有[help]一项，请重新启动！");
                SpringApplication.exit(applicationContext, () -> 0);
            } else { // 真正
                System.out.println("--------------------------------------------------------------------------");
                System.out.println("| --------------------------- CSU Auto Health Punch ---------------------|");
                System.out.println("| 1. --username=\"user1;user2;...\"                                        |");
                System.out.println("| 2. --password=\"password1;password2;...\"                                |");
                System.out.println("| 3. --cron=\"cron1;cron2;...\"                                            |");
                System.out.println("| 4. --mail=\"mail1;mail2;...\"                                            |");
                System.out.println("| Demo : [java -jar xxx.jar help]                                        |");
                System.out.println("| Demo : [java -jar xxx.jar --username=x --password=x --cron=x --mail=x] |");
                System.out.println("--------------------------------------------------------------------------");

                SpringApplication.exit(applicationContext, () -> 0);
            }
        } else { // 没有输入 --help
            // Do Nothing
        }

        // 获取配置参数
        Set<String> optionNames = args.getOptionNames();
        List<String> optionValues;
        for (String item : optionNames) {
            if (item.equals("username")) {
                optionValues = args.getOptionValues(item);
                String[] usernameArr = optionValues.get(0).split(";");
                for (String username : usernameArr) {
                    usernameList.add(username);
                }
            } else if (item.equals("password")) {
                optionValues = args.getOptionValues(item);
                String[] passwordArr = optionValues.get(0).split(";");
                for (String password : passwordArr) {
                    passwordList.add(password);
                }
            } else if (item.equals("cron")) {
                optionValues = args.getOptionValues(item);
                String[] cronArr = optionValues.get(0).split(";");
                for (String cron : cronArr) {
                    cronExpList.add(cron);
                }
            } else if (item.equals("mail")) {
                optionValues = args.getOptionValues(item);
                String[] mailArr = optionValues.get(0).split(";");
                for (String mail : mailArr) {
                    mailList.add(mail);
                }
            } else { // 用户输入了其他的信息
                System.out.println("配置参数只支持[--username][--password][--cron][--mail]四种,请重新启动应用");
                SpringApplication.exit(applicationContext, () -> 0);
            }
        }

        // 将信息存入全局的userList中
        for (int i = 0; i < usernameList.size(); i++) {
            userList.add(new User(usernameList.get(i), passwordList.get(i), cronExpList.get(i), mailList.get(i)));
        }
        // 输出所有的user 检查是否正确
        System.out.println("----- 输出所有的User Start -----");
        for (int i = 0; i < userList.size(); i++) {
            System.out.println(userList.get(i));
        }
        System.out.println("----- 输出所有的User End -----");

        // 加入到cron schedule之中
        for (int i = 0; i < usernameList.size(); i++) {
            int finalI = i;
            CronUtil.schedule(userList.get(i).getCronExp(), new Task() {
                @Override
                public void execute() {
                    coreOperating.run(userList.get(finalI));
                }
            });
        }
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
