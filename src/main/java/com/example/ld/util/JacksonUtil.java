package com.example.ld.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * @author HeYiyu
 * @date 2023/2/13
 */
public class JacksonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static byte[] toBytes(Object object) {
        return objectMapper.writeValueAsBytes(object);
    }

    @SneakyThrows
    public static String toString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    public static <T> T toObject(byte[] bytes, Class<T> clazz) {
        return objectMapper.readValue(bytes, clazz);
    }

    @SneakyThrows
    public static <T> T toObject(byte[] bytes, TypeReference<T> valueTypeRef) {
        return objectMapper.readValue(bytes, valueTypeRef);
    }
}
