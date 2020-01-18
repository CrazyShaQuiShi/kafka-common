package cn.shazhengbo.kafka.utils;

import java.util.Collections;
import java.util.Random;

/**
 * 字符串辅助类
 */
public abstract class StringHelper {

    private static final String source = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

    /**
     * 生成随机长度的字符串，只包含大小写字母和数字
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String random(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        //长度为几就循环几次
        for (int i = 0; i < length; ++i) {
            //产生0-61的数字
            int number = random.nextInt(62);
            //将产生的数字通过length次承载到sb中
            sb.append(source.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }

    /**
     * 随机生成指定长度的数字
     * @param length 长度
     * @return 随机数字
     */
    public static String randomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sb.append(random.nextInt(9));
        }

        return sb.toString();
    }

    /**
     * 将指定范围内的字符转换成*
     * @param source 字符串
     * @param start 起始位置
     * @param length 截止位置
     * @return 转换后的字符串
     */
    public static String secret(String source, int start, int length) {
        if (source == null || source.trim().equals("")) {
            return source;
        }

        source = source.trim();
        if (start + length > source.length()) {
            throw new StringIndexOutOfBoundsException(start + length);
        }
        StringBuilder sb = new StringBuilder(source);
        sb.replace(start, start + length, String.join("", Collections.nCopies(length, "*")));
        return sb.toString();
    }

    /**
     * 获取去除头尾空格的字符串，如果为null则返回空字符串
     * @param source 字符串
     * @return 字符串或空字符串
     */
    public static String getOrEmpty(String source) {
        return source == null ? "" : source.trim();
    }

}
