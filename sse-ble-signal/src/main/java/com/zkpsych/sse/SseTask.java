package com.zkpsych.sse;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * 接收sse数据
 * @author ChangHao
 * @date 2020-10-12 17:54
 */
public class SseTask {
    private static final Logger LOG = LoggerFactory.getLogger(SseTask.class);
    private final String GATEWAY_URL;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String broadcastPackagePrefix = "0201060FFFA801010003000000";

    public SseTask(String GATEWAY_URL) {
        this.GATEWAY_URL = "http://" + GATEWAY_URL + "/gap/rssi";
    }

    public static void main(String[] args) {
        Request request = new Request.Builder().url("http://192.168.7.7/gap/nodes?filter_rssi=-74&active=1&event=1").build();
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
//                NotifyData.offer(data);

                try {
                    BroadcastPackageModel broadcastPackageModel = objectMapper.readValue(data, BroadcastPackageModel.class);
                    String adData = broadcastPackageModel.getAdData();
                    int evtType = broadcastPackageModel.getEvtType();
                    if (StrUtil.isEmpty(adData) ||
                            0 != evtType ||
                            38 != adData.length()) {
                        return;
                    }
                    if (adData.startsWith(broadcastPackagePrefix)) {
                        // 属于公司网关设备 需要记录
                        String macId = getMacId(adData);
                        LOG.info("macId: {}", macId);
                        LOG.info("rssi: {}", broadcastPackageModel.getRssi());

                    }
                } catch (JsonProcessingException e) {


                }
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

    /**
     * 检查是否是真实的mac地址
     *
     * @param macId 被检查的mac地址
     */
    private static boolean isValidMacId(String macId) {

        if (macId == null || macId.equals("")) {
            return false;
        }
        String macAddressRule = "([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}";
        // 这是真正的MAC地址；正则表达式；
        return macId.matches(macAddressRule);
    }

    /**
     * 根据adData获取出macId
     *
     * @param adData 广播包中的adData
     */
    private static String getMacId(String adData) {
        String bigEndianMacId = adData.substring(26);

        StringBuilder stringBuilder = new StringBuilder();

        for (int index = bigEndianMacId.length() - 2; index >= 0; index -= 2) {
            if (index != 0) {
                stringBuilder.append(bigEndianMacId, index, index + 2).append(":");
            } else {
                stringBuilder.append(bigEndianMacId, index, index + 2);
            }
        }

        return stringBuilder.toString();
    }
}
