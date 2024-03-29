package com.common.util;

/**
 * MD5算法
 */
public class MD5Tool {

    /**
     * 获取MD5 结果字符串
     *
     * @param source
     * @return
     */
    public static String encode(byte[] source) {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte bt = tmp[i];
                str[k++] = hexDigits[bt >>> 4 & 0xf];
                str[k++] = hexDigits[bt & 0xf];
            }
            s = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getMD5(String source) {
        return (source == null || "".equals(source)) ? "" : encode(source.getBytes());
    }

}