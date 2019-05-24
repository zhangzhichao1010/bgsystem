package com.managesystem.bgsystem.config.Interceptor.service;

public interface BlackIPLocalService {
    Boolean ckeckIP(String IP);

    void saveBlackLocalIP(String IP);
}
