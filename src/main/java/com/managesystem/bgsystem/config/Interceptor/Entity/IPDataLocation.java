package com.managesystem.bgsystem.config.Interceptor.Entity;

public enum IPDataLocation {
    /*
     * @database 数据库存储
     * @locationfile 本地文件存储，适用于单节点服务
     * @romotefile 远程存储，给于rest接口调用
     * */
    database,
    locationfile,
    romotefile;

    private IPDataLocation() {
    }
}
