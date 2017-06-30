package com.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public RegexUtil() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 验证手机号
     */
    public static boolean checkMobile(String mobile) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 验证邮箱
     */
    public static boolean checkEmail(String email) {
        String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 验证帐号
     */
    public static boolean checkAccount(String account, int minimum, int maximum) {
        StringBuilder sb = new StringBuilder();
        // sb.append("^[a-zA-Z]\\w{5,17}$");
        sb.append("^[a-zA-Z]\\w{");
        sb.append(minimum);
        sb.append(",");
        sb.append(maximum);
        sb.append("}$");
        Pattern p = Pattern.compile(sb.toString());
        Matcher m = p.matcher(account);
        return m.matches();
    }

    /**
     * 验证汉字
     */
    public static boolean checkChinese(String character) {
        Pattern p = Pattern.compile("^[\u4e00-\u9fa5]*$");
        Matcher m = p.matcher(character);
        return m.matches();
    }

    /**
     * 过滤汉字以外的字符
     *
     * @param str
     * @return
     */
    public static String filterUnChinese(String str) {
        Pattern p = Pattern.compile("[^\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 过滤数字以外的字符
     *
     * @param str
     * @return 过滤后的字符串
     */
    public static String filterUnNumber(String str) {
        // 只允数字
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        // 替换与模式匹配的所有字符（即非数字的字符将被""替换）
        return m.replaceAll("").trim();
    }

    /**
     * 过滤汉字
     *
     * @param str
     * @return
     */
    public static String filterChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 验证是否为移动139邮箱
     *
     * @param email
     * @return
     */
    public static boolean check139Email(String email) {
        String[] strArray = email.split("@");
        return checkMobile(strArray[0]) && "139.com".equals(strArray[1]);
    }

    /**
     * 从原字符串中提取字符串List
     *
     * @param source            原字符串
     * @param regularExpression 正则表达式
     * @return 字符串List
     */
    public static List<String> extractStrings(String source,
                                              String regularExpression) {
        List<String> strs = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            strs.add(matcher.group());
        }
        return strs;
    }

    /**
     * 从原字符串中提取字符串
     *
     * @param source            原字符串
     * @param regularExpression 正则表达式
     * @return 字符串
     */
    public static String extractString(String source, String regularExpression) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        return sb.toString();
    }

    /**
     * 验证url
     *
     * @param url
     * @return boolean
     * @deprecated It's not correct,because it's not checked by examples.
     */
    @Deprecated
    public static boolean checkUrl(String url) {
        // String reg = "^((http|https|ftp)://)?([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
        // + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
        // + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
        // + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
        // + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
        // + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
        // + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
        // + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
        String reg = "^((http|ftp|https)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(url);
        return m.matches();
    }

    /**
     * 从url中提取域名
     *
     * @param url
     * @return
     */
    public static String getHost(String url) {
        if (url == null || url.trim().equals("")) {
            return "";
        }
        String host = "";
        Pattern p = Pattern.compile("([\\w-]+\\.)+[\\w-]+(?<=/?)");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }

    /**
     * 提取字符串中的所有网址
     *
     * @param url
     * @return
     */
    public static List<String> getHostArrayList(String url) {
        if (url == null || url.trim().equals("")) {
            return null;
        }
        List<String> hostArray = new ArrayList<String>();
        String reg = "((http|ftp|https)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(url);
        while (matcher.find()) {
            String host = matcher.group();
            hostArray.add(host);
        }
        return hostArray;
    }

    /**
     * 提取中文
     *
     * @param source
     * @return
     */
    public static String extractChinese(String source) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile("([\u4e00-\u9fa5]+)");
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        return sb.toString();
    }
}
