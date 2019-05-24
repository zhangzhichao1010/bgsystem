package com.managesystem.bgsystem.config.Interceptor.service;

public interface IPAdressService {
    void saveBlackIPDataBase();

    String findBlackIPDataBase();

    String findWhiteIPDataBase();

    void saveBlackIPRemote();

    String findBlackIPRemote();

    String findWhiteIPRemote();
}
