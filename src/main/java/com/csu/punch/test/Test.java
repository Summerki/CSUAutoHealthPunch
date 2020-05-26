package com.csu.punch.test;

import cn.hutool.core.lang.Console;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;

/**
 * 供测试
 */
public class Test {



    @org.junit.Test
    public void test2() {
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() / 1000.000);
        System.out.println(Math.round(System.currentTimeMillis() / 1000.000));
//        System.out.println(Calendar.getInstance().getTimeInMillis());
    }

    @org.junit.Test
    public void test3() {
//        CronUtil.schedule("*/2 * * * * *", new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("来了");
//            }
//        });
//
//        // 支持秒级别定时任务
//        CronUtil.setMatchSecond(true);
//        CronUtil.start();
        CronUtil.schedule("*/2 * * * * *", new Task() {
            @Override
            public void execute() {
                Console.log("Task excuted.");
            }
        });

// 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

}
