package com.managesystem.bgsystem.config.Interceptor.service;

import com.managesystem.bgsystem.Utils.RedisUtils;
import com.managesystem.bgsystem.config.Interceptor.Entity.IPFilterBean;
import com.managesystem.bgsystem.config.Interceptor.Repository.IPAdressRepository;
import com.managesystem.bgsystem.config.Interceptor.Entity.IPAdress;
import com.managesystem.bgsystem.config.Interceptor.Entity.IPAdressType;
import com.managesystem.bgsystem.config.Interceptor.utils.IpUtils;
import com.managesystem.bgsystem.config.Interceptor.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Properties;

@Service
public class IPAdressServiceImpl implements IPAdressService {
    @Autowired
    private IPAdressRepository ipAdressRepository;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void saveBlackIPDataBase(String IP) {
        IPAdress adress = new IPAdress();
        adress.setRomoteIP(IpUtils.ipToLong(IP));
        adress.setType(0);
        ipAdressRepository.save(adress);
        String blackIps = redisUtils.get("BlackIPOnly_PERSIST", String.class);
        blackIps += "," + IP;
        redisUtils.set("BlackIPOnly_PERSIST", blackIps);
    }
}
