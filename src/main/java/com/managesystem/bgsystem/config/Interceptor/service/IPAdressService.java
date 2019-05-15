package com.managesystem.bgsystem.config.Interceptor.service;

public interface IPAdressService {
    Boolean ckeckRemoteIP(String IP);

    void saveBlackRemoteIP(String IP);
}
