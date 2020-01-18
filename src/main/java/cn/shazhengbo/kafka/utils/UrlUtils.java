package cn.shazhengbo.kafka.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * url工具类
 *
 * @author:CrazyShaQiuShi
 * @email: 1047812462@qq.com
 * @createTime: 2018-7-25 11:12
 * @version: 1.0
 */
public class UrlUtils {
    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<>(16);

        String[] arrSplit;

        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组 www.2cto.com
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;
        strURL = strURL.trim();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 拼接get请求的
     *
     * @param url    urL
     * @param params 参数
     * @return 完整的请求地址
     */
    public static String getUrl(String url, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (key != null && params.get(key) != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(params.get(key));
            }
        }
        return builder.toString();
    }

    /**
     *
     */
    private static final String DANWEI_GUID = "DanWeiGuid";
    private static final String UNIT_TYPE = "UnitType";

    /**
     * 获取请求地址
     *
     * @param guid     单位GUID
     * @param unitType 单位类型
     * @return string
     */
    public static String getUrl(String url, String guid, int unitType) {
        Map<String, Object> param = new HashMap<>(2);
        param.put(DANWEI_GUID, guid);
        param.put(UNIT_TYPE, unitType);
        System.out.println(getUrl(url,param));
        return getUrl(url, param);
    }
}
