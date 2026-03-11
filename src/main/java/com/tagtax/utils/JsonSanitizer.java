package com.tagtax.utils;

/**
 * JSON清理工具类
 * 用于处理AI返回的JSON中的全角字符问题
 */
public class JsonSanitizer {

    /**
     * 清理JSON字符串中的全角字符
     */
    public static String sanitize(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }

        return json
                // 全角转半角 - 标点符号
                .replace("：", ":")
                .replace("，", ",")
                .replace("（", "(")
                .replace("）", ")")
                .replace("【", "[")
                .replace("】", "]")
                .replace("！", "!")
                .replace("？", "?")
                .replace("；", ";")
                // 全角空格转半角
                .replace("\u3000", " ");
    }
}
