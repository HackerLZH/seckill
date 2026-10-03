package com.lzh.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 其他服务由于GracefulResponse，返回Json，需要提取data
 */
public class ResponseDecoder implements Decoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (type == Boolean.class || type == boolean.class) {
            // 读取响应体，按实际 JSON 结构解析
            String body = Util.toString(response.body().asReader(Util.UTF_8));
            JsonNode root = objectMapper.readTree(body);
            JsonNode dataNode = root.get("data");
            if (dataNode != null && dataNode.isBoolean()) {
                return dataNode.asBoolean();
            }
            return Boolean.parseBoolean(body.trim());
        }
        // 其他类型交给默认解码器
        return null;
    }
}