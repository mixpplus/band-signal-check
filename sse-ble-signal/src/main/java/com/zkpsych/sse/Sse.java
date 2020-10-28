package com.zkpsych.sse;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description :
 * @Author : ChangHao
 * @Date: 2020-10-12 17:46
 */
public class Sse {
    private static final Logger LOG = LoggerFactory.getLogger(Sse.class);
    public static void main(String[] args) {

        String url = System.getProperty("sse.url");
        String perCount = System.getProperty("sse.perCount");
        if (StrUtil.isEmpty(url)) {
            throw new RuntimeException("请在启动命令设置sse连接的url，格式 -Dsse.url=xxx");
        }
        if (StrUtil.isEmpty(perCount)) {
            throw new RuntimeException("请在启动命令设置单次采集个数，格式 -Dsse.perCount=xxx");
        }
        int count = Integer.parseInt(perCount);
        ThreadUtil.newThread(new SseTask(url), "sseTask").start();

        ThreadUtil.newThread(new ResolveDataTask(count), "resolveDataTask").start();
    }
}
