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
    private IPFilterBean ipFilterBean;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Boolean ckeckIPDataBase(String IP) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        IPAdress blackIPAdress = new IPAdress();
        blackIPAdress.setRomoteIP(IpUtils.ipToLong(IP));
        matcher = matcher.withMatcher("romoteIP", ExampleMatcher.GenericPropertyMatchers.exact());
        Example example = Example.of(blackIPAdress, matcher);
        if (ipFilterBean.getType().equals(IPAdressType.BlackIPOnly)) {
            blackIPAdress.setType(0);
            matcher = matcher.withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
            return ipAdressRepository.findAll(example).size() == 0 ? true : false;
        } else if (ipFilterBean.getType().equals(IPAdressType.WhiteIPOnly)) {
            blackIPAdress.setType(1);
            matcher = matcher.withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
            return ipAdressRepository.findAll(example).size() == 0 ? false : true;
        } else {
            List<IPAdress> IpStatus = ipAdressRepository.findAll(example);
            if (IpStatus == null) return true;
            for (IPAdress ipAdr : IpStatus
            ) {
                if (ipAdr.getType() == 0) return false;
            }
            return true;
        }
    }

    @Override
    public void saveBlackIPDataBase(String IP) {
        IPAdress adress = new IPAdress();
        adress.setRomoteIP(IpUtils.ipToLong(IP));
        adress.setType(0);
        ipAdressRepository.save(adress);
    }

    @Override
    public void saveBlackLocalIP(String IP) {
        Properties properties = new Properties();
        String path = ipFilterBean.getDataPath() + "BlackIPOnly.properties";
        try {
            File f = new File(path);
            InputStream in = new FileInputStream(f);
            OutputStream out = new FileOutputStream(f);
            properties.load(in);
            String filedata = properties.getProperty("BlackIPOnly");
            if (filedata == null || "".equals(filedata)) {
                properties.setProperty("BlackIPOnly", IP);
                redisUtils.set("BlackIPOnly", IP);
            } else {
                filedata += "," + IP;
                properties.setProperty("BlackIPOnly", filedata);
                redisUtils.set("BlackIPOnly", filedata);
            }
            properties.store(out, "Black IP List which to be refused");
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean ckeckIPLocalFile(String IP) {
        Boolean canpass = false;
        String blackIps = redisUtils.get("BlackIPOnly", String.class);
        String whiteIps = redisUtils.get("WhiteIPOnly", String.class);
        if (blackIps == null || "".equals(whiteIps)) return true;
        IPAdressType type = ipFilterBean.getType();
        switch (type) {
            case WhiteIPOnly:
                canpass = StringUtils.isEmpty(whiteIps) ? false : whiteIps.indexOf(IP) == -1 ? false : true;
                break;
            case BlackAndWhite:
                canpass = StringUtils.isEmpty(whiteIps) ? StringUtils.isEmpty(blackIps) ? true : blackIps.indexOf(IP) == -1 ? true : false : whiteIps.indexOf(IP) == -1 ? false : true;
                break;
            case BlackIPOnly:
                canpass = StringUtils.isEmpty(blackIps) ? true : blackIps.indexOf(IP) == -1 ? true : false;
                break;
        }
        return canpass;
    }
}
