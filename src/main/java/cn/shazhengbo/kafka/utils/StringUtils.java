package cn.shazhengbo.kafka.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author:CrazyShaQiuShi
 * @email: 1047812462@qq.com
 * @createTime: 2018-7-30 11:02
 * @version: 1.0
 */
public class StringUtils {
    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

    /**
     * 截取字符串字段
     *
     * @param org
     * @param len
     * @return
     */
    public static String substring(String org, int len) {
        return substring(org, 0, len);
    }

    /**
     * 根据字符串位置截取
     *
     * @param org        字符串内容
     * @param startIndex 截取开始位置
     * @param endIndex   截取结束位置
     * @return 截取到的内容
     * @throws Exception
     */
    public static String substring(String org, int startIndex, int endIndex) {
        try {
            if (StringUtils.isEmpty(org)) {
                return "";
            }


            if (org.length() > endIndex) {
                if (startIndex >= 0) {
                    return org.substring(startIndex, endIndex).trim();
                } else {
                    throw new Exception("截取开始不能小于0");
                }
            } else {
                return org.substring(org.length());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }
}
