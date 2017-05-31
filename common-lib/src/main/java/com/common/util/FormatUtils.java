package com.common.util;

/**
 * Created by tianshutong on 16/8/23.
 */

public class FormatUtils {

    public static char formatDigit(char sign) {
        if (sign == '0')
            sign = '零';
        if (sign == '1')
            sign = '一';
        if (sign == '2')
            sign = '二';
        if (sign == '3')
            sign = '三';
        if (sign == '4')
            sign = '四';
        if (sign == '5')
            sign = '五';
        if (sign == '6')
            sign = '六';
        if (sign == '7')
            sign = '七';
        if (sign == '8')
            sign = '八';
        if (sign == '9')
            sign = '九';
        return sign;
    }
}
