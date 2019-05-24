package com.managesystem.bgsystem.config.ipfilter.service;

public interface IPAdressService {
    void saveBlackIPDataBase();

    String findBlackIPDataBase();

    String findWhiteIPDataBase();

    void saveBlackIPRemote();

    String findBlackIPRemote();

    String findWhiteIPRemote();
}
