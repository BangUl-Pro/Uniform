package com.songjin.usum.utils;

import java.util.ArrayList;

public class StringUtil {
    public static boolean isEmptyString(String str) {
        // 문자열 자체가 없는지 체크
        if (str == null) {
            return true;
        }

        // 화이트 스페이스를 제거
        String whitespaces[] = {" ", "\t", "\n"};
        for (String whitespace : whitespaces) {
            str = str.replace(whitespace, "");
            str = str.replace(whitespace, "");
            str = str.replace(whitespace, "");
        }

        // 문자열이 비었는지 체크
        return str.isEmpty();

    }

    public static String join(ArrayList<String> strings) {
        StringBuilder result = new StringBuilder();
        for (String string : strings) {
            result.append(string);
            result.append(",");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
    }
}
