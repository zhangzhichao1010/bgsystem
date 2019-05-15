package com.managesystem.bgsystem.config.Interceptor.utils;

public class IpUtils {
    /**
     * 将字符串类型的IP转换为整型：
     * 1.IP中每个"."的位置(第二个"."以后的起始位置需要在第一个位置的基础上+1)
     * 2.使用String的substring方法截取"."分隔的每段，总共四段
     *         另：可以使用"."分隔为四段
     * 3.第一、第二、第三、第四段分别使用Long的parseLong方法转换为long类型后，依次向左移动24位、16位、8位、0位
     * 4.四段相加的结果为最终的整型值
     * @param str
     * @return
     */
    public static long ipToLong(String str){
        int point1 = str.indexOf(".");
        int point2 = str.indexOf(".", point1+1);
        int point3 = str.indexOf(".", point2+1);
        long[] lon = new long[4];
//        lon[0] = Long.parseLong(str.substring(0, point1));
//        lon[1] = Long.parseLong(str.substring(point1+1, point2));
//        lon[2] = Long.parseLong(str.substring(point2+1, point3));
//        lon[3] = Long.parseLong(str.substring(point3+1, str.length()));
//        long result = (lon[0]<<24) + (lon[1]<<16) + (lon[2]<<8) + (lon[3]);

        String[] ip = str.split("\\.", 4);
        lon[0] = Long.parseLong(ip[0]);
        lon[1] = Long.parseLong(ip[1]);
        lon[2] = Long.parseLong(ip[2]);
        lon[3] = Long.parseLong(ip[3]);
        long result = (lon[0]<<24) + (lon[1]<<16) + (lon[2]<<8) + (lon[3]);
        return result;
    }

    /**
     * 整型转换为IP：
     * 1.将long类型值右移24位得到第一段字符
     * 2.将long类型值与0x00FFFFFF按位与运算，得到第二段
     * 3.将long类型值与0x0000FFFF按位与运算，得到第三段
     * 4.将long类型值与0x000000FF按位与运算，得到第四段
     * 5.从第一段开始，每段拼接".",最后一段不需要拼接".",拼接结果为最终的IP字符串
     * @param lon
     * @return
     */
    public static String longToIp(long lon){
        StringBuffer sb = new StringBuffer();
        sb.append(lon>>24).append(".").append(((lon & 0x00FFFFFF)>>16)).append(".").append(((lon & 0x0000FFFF)>>8))
                .append(".").append((lon & 0x000000FF));
        System.out.print("长整型转换为字符串后>");
        return sb.toString();
    }
}
