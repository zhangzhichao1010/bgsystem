package com.managesystem.bgsystem.config.Interceptor.service;

public interface IPAdressService {
    Boolean ckeckIPDataBase(String IP);

    Boolean ckeckIPLocalFile(String IP);

    void saveBlackIPDataBase(String IP);

    void saveBlackLocalIP(String IP);
}
