package com.managesystem.bgsystem.Utils;

public class DWZJsonUtils {
    /**
     * 表单提交返回json
     *
     * @param statusCode
     * @param message
     * @param navTabId
     * @param callbackType
     * @param forwardUrl
     * @return
     */
    public static String getJson(String statusCode, String message, String navTabId, String callbackType, String forwardUrl) {
        return "{\n" +
                "\t\"statusCode\":\"" + statusCode + "\",\n" +
                "\t\"message\":\"" + message + "\",\n" +
                "\t\"navTabId\":\"" + navTabId + "\",\n" +
                "\t\"callbackType\":\"" + callbackType + "\",\n" +
                "\t\"forwardUrl\":\"" + forwardUrl + "\"\n" +
                "}";
    }

    public static String getJson(String statusCode, String message, String navTabId, String callbackType, String forwardUrl, String panelID) {
        return "{\n" +
                "\t\"statusCode\":\"" + statusCode + "\",\n" +
                "\t\"message\":\"" + message + "\",\n" +
                "\t\"navTabId\":\"" + navTabId + "\",\n" +
                "\t\"callbackType\":\"" + callbackType + "\",\n" +
                "\t\"panelID\":\"#" + panelID + "\",\n" +
                "\t\"forwardUrl\":\"" + forwardUrl + "\"\n" +
                "}";
    }

    public static String getJson(String statusCode, String message) {
        return "{\n" +
                "\t\"statusCode\":\"" + statusCode + "\",\n" +
                "\t\"message\":\"" + message + "\"\n" +
                "}";
    }


    public static String getJson(String statusCode, String message, String callbackType) {
        return "{\n" +
                "\t\"statusCode\":\"" + statusCode + "\",\n" +
                "\t\"message\":\"" + message + "\",\n" +
                "\t\"callbackType\":\"" + callbackType + "\"\n" +
                "}";
    }

    public static String getJson(String statusCode, String message, String callbackType, String navtab) {
        return "{\n" +
                "\t\"statusCode\":\"" + statusCode + "\",\n" +
                "\t\"message\":\"" + message + "\",\n" +
                "\t\"callbackType\":\"" + callbackType + "\",\n" +
                "\t\"navTabId\":\"" + navtab + "\"\n" +
                "}";
    }
}
