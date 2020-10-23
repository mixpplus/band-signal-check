package com.zkpsych.sse;

import cn.hutool.core.thread.ThreadUtil;
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

        ThreadUtil.newThread(new SseTask(), "sseTask").start();

        ThreadUtil.newThread(new ResolveDataTask(), "resolveDataTask").start();
    }
}
