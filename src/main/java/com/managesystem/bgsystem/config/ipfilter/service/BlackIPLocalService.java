package com.managesystem.bgsystem.config.ipfilter.service;

public interface BlackIPLocalService {
    Boolean ckeckIP(String IP);

    void saveBlackLocalIP(String IP);
}
