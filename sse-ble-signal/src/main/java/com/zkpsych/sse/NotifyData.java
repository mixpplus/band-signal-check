package com.zkpsych.sse;

import cn.hutool.core.util.StrUtil;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 收到的所有notify数据都先往该队列中存储
 * @author ChangHao
 * @date 2020-09-29 17:50
 */
public class NotifyData {
    private static final LinkedBlockingQueue<String> NOTIFY_QUEUE = new LinkedBlockingQueue<>();

    /**
     * 添加数据
     */
    public static void offer(String data) {
        if (StrUtil.isEmpty(data)) {
            return;
        }
        NOTIFY_QUEUE.offer(data);
    }

    /**
     * 取出数据, 没有数据会阻塞
     */
    public static String take() {
        String data = "";
        try {
            data = NOTIFY_QUEUE.take();
        } catch (InterruptedException ignored) {
        }
        return data;
    }
}
