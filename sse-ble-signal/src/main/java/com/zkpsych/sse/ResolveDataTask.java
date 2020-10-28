package com.zkpsych.sse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * @Description : 解析数据任务
 * @Author : ChangHao
 * @Date: 2020-10-12 17:57
 */
public class ResolveDataTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ResolveDataTask.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, List<RssiModel>> cache = new TreeMap<>();
    private final String filePath = "./sse.csv";
    /**
     * 单次写入值个数
     */
    private final int perCount;

    public ResolveDataTask(int perCount) {
        this.perCount = perCount;
    }

    @Override
    public void run() {

        while (true) {
            String sseData = NotifyData.take();

            SseModel sseModel = null;
            try {
                if (StrUtil.isEmpty(sseData)) {
                    continue;
                }
                sseModel = objectMapper.readValue(sseData, SseModel.class);


            } catch (JsonProcessingException e) {

                LOG.error("解析出错");
            }
            if (ObjectUtil.isEmpty(sseModel)) {
                continue;
            }
            String deviceId = sseModel.getId();
            int rssi = sseModel.getRssi();
            long currentTimeMillis = System.currentTimeMillis();

            if (StrUtil.isEmpty(deviceId)) {
                continue;
            }
            if (rssi == 0) {
                continue;
            }
            List<RssiModel> cacheList = cache.get(deviceId);
            if (CollUtil.isEmpty(cacheList)) {
                cacheList = new ArrayList<>();
                cache.put(deviceId, cacheList);
            }
            RssiModel rssiModel = new RssiModel();
            rssiModel.setRssi(rssi);
            rssiModel.setTimeStamp(currentTimeMillis);
            cacheList.add(rssiModel);
            if (cacheList.size() >= perCount) {
                write();
            }

        }
    }

    private void write() {
        Set<String> deviceSet = cache.keySet();
        StringBuilder sbTag = new StringBuilder();
        for (String deviceId : deviceSet) {

            sbTag.append(deviceId).append(StrUtil.COMMA).append(StrUtil.COMMA);

        }
        writeToFile(sbTag.toString(), filePath);
        writeToFile("\n",filePath);

        for (int i = 0; i < perCount; i++) {
            StringBuilder sb = new StringBuilder();
            for (String deviceId : deviceSet) {
                List<RssiModel> cacheList = cache.get(deviceId);
                if (cacheList.size() > i) {
                    RssiModel data = cacheList.get(i);
                    if (ObjectUtil.isNotEmpty(data)) {
                        sb.append(data.getRssi()).append(StrUtil.COMMA).append(data.getTimeStamp()).append(StrUtil.COMMA);
                    }
                } else {

                    sb.append(StrUtil.COMMA).append(StrUtil.COMMA);
                }

            }
            sb.append("\n");
            writeToFile(sb.toString(), filePath);
        }
        writeToFile("\n", filePath);

        cache.clear();
    }

    /**
     * 追加方式写文件
     *
     * @param content  写的内容
     * @param filePath 需要写入得文件路径
     */
    public static void writeToFile(String content, String filePath) {
        File file = new File(filePath);
        boolean exist = FileUtil.exist(file);
        if (!exist) {

            file = FileUtil.touch(file);
        }
        FileUtil.appendUtf8String(content, file);

    }
}
