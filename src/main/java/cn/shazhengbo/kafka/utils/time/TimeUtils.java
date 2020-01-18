package cn.shazhengbo.kafka.utils.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * 时间处理工具类
 *
 * @author:CrazyShaQiuShi
 * @email: 1047812462@qq.com
 * @createTime: 2018-7-24 9:19
 * @version: 1.0
 */
public class TimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(TimeUtils.class);

    /**
     * 字符串转日期
     *
     * @param date 日期字符串
     * @return 转换后的日期
     */
    public static LocalDate strToLocalDate(String date) {
        return strToLocalDate(date, "yyyy-MM-dd");
    }

    /**
     * 字符串转日期
     *
     * @param date    日期字符串
     * @param pattern 日期格式
     * @return 转换后的日期
     */
    public static LocalDate strToLocalDate(String date, String pattern) {
        if (StringUtils.isEmpty(date)) {
            return null;
        } else {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
        }
    }

    /**
     * 字符串转日期
     *
     * @param date
     * @return localDate
     */
    public static LocalDate autoStrToLocalDate(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        } else {
            String strDate = date.trim().split(" ")[0];
            return TimeUtils.strToLocalDateTime(strDate, getPatternDate(strDate));
        }
    }


    /**
     * 字符串转日期时间
     *
     * @param dateTime 日期字符串
     * @param pattern  日期格式
     * @return 转换后的日期
     */
    public static LocalDate strToLocalDateTime(String dateTime, String pattern) {
        if (StringUtils.isEmpty(dateTime)) {
            return null;
        } else {
            LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern(pattern.trim()));
            return LocalDate.of(localDate.getYear(), localDate.getMonth().getValue(), localDate.getDayOfMonth());
        }
    }

    /**
     * 计算日期差
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差天数
     */
    public static int diffDays(LocalDate startDate, LocalDate endDate) {
        return Period.between(startDate, endDate).getDays();
    }

    private static final String SLASH_SEGMENTATION = "/";
    private static final String HORIZONTAL_PARTITIONING = "-";

    /**
     * 获取当前日期格式
     *
     * @param date
     * @return
     */
    public static String getPatternDate(String date) {
        StringBuilder patternDate = new StringBuilder();
        try {
            if (StringUtils.isEmpty(date)) {
                return "";
            } else {
                String splitCharacter = "";
                String[] dataArr = null;
                if (date.contains(HORIZONTAL_PARTITIONING)) {
                    splitCharacter = HORIZONTAL_PARTITIONING;
                    dataArr = date.split(splitCharacter);
                } else if (date.contains(SLASH_SEGMENTATION)) {
                    splitCharacter = SLASH_SEGMENTATION;
                    dataArr = date.split(splitCharacter);
                } else {
                    throw new Exception("目前不支持该日期格式获取!");
                }

                if (dataArr.length > 0) {
                    dataArr = date.split(splitCharacter);
                    patternDate.append(getArrayChar(dataArr[0].length(), "y").append(splitCharacter).toString());
                    patternDate.append(getArrayChar(dataArr[1].length(), "M").append(splitCharacter).toString());
                    patternDate.append(getArrayChar(dataArr[2].length(), "d").toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return patternDate.toString();
    }

    /**
     * 拼接格式语句
     *
     * @param len
     * @param character
     * @return
     */
    private static StringBuilder getArrayChar(int len, String character) {
        StringBuilder builder = new StringBuilder();
        for (int n = 0; n < len; n++) {
            builder.append(character);
        }
        return builder;
    }


}
