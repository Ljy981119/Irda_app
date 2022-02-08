package com.example.myapplication.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 *   定时获取串口数据的定时器
 * @author 钟三
 */
public class SerialDataTimer {

    public static void start() {
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                System.out.println(DateUtil.getDateTime()+":开启定时器============");
//				定时向COM1发送命令获取数据
                //COM01.timingPortData();
//				定时向COM1发送命令获取数据
//				COM02.timingPortData();
////				定时向COM1发送命令获取数据
//				COM03.timingPortData();
////				定时向COM1发送命令获取数据
//				COM04.timingPortData();
////				定时向COM1发送命令获取数据
//				COM05.timingPortData();
////				定时向COM1发送命令获取数据
//				COM06.timingPortData();
////				定时向COM1发送命令获取数据
//				COM07.timingPortData();
////				定时向COM1发送命令获取数据
//				COM08.timingPortData();

            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时10秒时间，第三个参数为定时执行的间隔时间300（单位：秒）
        service.scheduleAtFixedRate(runnable, 10, 300, TimeUnit.SECONDS);

    }

}
