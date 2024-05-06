package com.longqinx.xdevtool.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object data) {
        try {
//            return MAPPER.writeValueAsString(data);
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            //
        }
        return null;
    }

    public static <T> T toObject(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            //
        }
        return null;
    }

    public static <T> List<T> toList(String jsonData, Class<T> clazz) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            //
        }
        return null;
    }
}
