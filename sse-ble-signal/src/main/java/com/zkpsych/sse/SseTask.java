package com.zkpsych.sse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Description : 接收sse数据
 * @Author : ChangHao
 * @Date: 2020-10-12 17:54
 */
public class SseTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SseTask.class);
    private final String GATEWAY_URL;

    public SseTask(String GATEWAY_URL) {
        this.GATEWAY_URL = "http://" + GATEWAY_URL + "/gap/rssi";
    }

    @Override
    public void run() {

        Request request = new Request.Builder().url(GATEWAY_URL).build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.DAYS)
                .readTimeout(1, TimeUnit.DAYS) // 这边需要将超时显示设置长一点，不然刚连上就断开，之前以为调用方式错误被坑了半天
                .build();

        // 实例化EventSource，注册EventSource监听器
        RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {


            @Override
            public void onOpen(EventSource eventSource, Response response) {
                LOG.info("======open signal data listener====");
            }

            /**
             *
             * @param eventSource
             * @param id  发送的事件id
             * @param type 发送的事件名称
             * @param data 发送的真实数据
             */
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                NotifyData.offer(data);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                LOG.info("=====onClosed====");
                eventSource.cancel();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                LOG.error("====onFailure====");//这边可以监听并重新打开
                eventSource.cancel();
                okHttpClient.dispatcher().executorService().shutdown();

            }
        });
        realEventSource.connect(okHttpClient);//真正开始请求的一步
    }
}
