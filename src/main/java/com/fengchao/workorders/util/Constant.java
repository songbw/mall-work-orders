package com.fengchao.workorders.util;

/**
 * @Author Clark
 * @Date 2018/11/29 15:10
 * @Description
 */

public class Constant {
    /**
     * 版本号
     */
    public static final String V1 = "/v1";

    /**
     * 字符串转换编码
     */
    public static String USER_STRING_CHARSET = "UTF-8";

    /**
     * 已删除
     */
    public static String DEL_FLAG_YES = "1";
    /**
     * 未删除
     */
    public static String DEL_FLAG_NO = "0";

    public static final String SHIRO_CACHE_PREFIX = "shiro-cache-";
    public static final String SHIRO_CACHE_PREFIX_KEYS = "shiro-cache-*";

    public static final String NO_ONE_ID = "0";
    public static final long NO_PERMISSION_ID = 0L;
    public static final long NO_ORGANIZATION_ID = 0L;
    public static final long ADVERTISER_ID = 6L;

    public static final String PERMISSION_GROUP_CODE = "permission";
    public static final String PERMISSION_QUERY_CODE = "list";
    public static final String PERMISSION_INSERT_CODE = "insert";
    public static final String PERMISSION_DELETE_CODE = "delete";
    public static final String PERMISSION_UPDATE_CODE = "update";
    public static final String PERMISSION_SELECT_CODE = "select";

    public static final String NORMAL_SELLER_STRING = "vendor";
}
