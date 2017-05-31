package com.common.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式
 * Created by Stefen on 2016/7/26.
 */
public class RegexUtil {

    /**
     * 验证手机号
     *
     * @param mobile String 手机号
     * @return boolean
     */
    public static boolean checkMobile(String mobile) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 验证密码
     *
     * @param passwd String 密码
     * @return boolean
     */
    public static boolean checkPasswd(String passwd, int minimum, int maximum) {
        StringBuilder sb = new StringBuilder();
//        sb.append("^[A-Za-z0-9-`=\\[\\];'\\,\\.\\/~!@#\\$%\\^&\\*\\(\\)_\\+\\|\\{\\}:\\\"\\<\\>\\?]{");
        sb.append("^[a-zA-Z0-9]{");
        sb.append(minimum);
        sb.append(",");
        sb.append(maximum);
        sb.append("}$");
        Pattern p = Pattern.compile(sb.toString());
        Matcher m = p.matcher(passwd);
        return m.matches();
    }

}
