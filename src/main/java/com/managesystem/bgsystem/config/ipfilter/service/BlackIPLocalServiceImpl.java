package com.managesystem.bgsystem.config.ipfilter.service;

import com.managesystem.bgsystem.Utils.RedisUtils;
import com.managesystem.bgsystem.config.ipfilter.Entity.IPAdressType;
import com.managesystem.bgsystem.config.ipfilter.Entity.IPFilterBean;
import com.managesystem.bgsystem.config.ipfilter.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

@Service

public class BlackIPLocalServiceImpl implements BlackIPLocalService {
    @Autowired
    private IPFilterBean ipFilterBean;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void saveBlackLocalIP(String IP) {
        Properties properties = new Properties();
        String path = ipFilterBean.getDataPath() + "BlackIPOnly_PERSIST.properties";
        try {
            File f = new File(path);
            InputStream in = new FileInputStream(f);
            OutputStream out = new FileOutputStream(f);
            properties.load(in);
            String filedata = properties.getProperty("BlackIPOnly_PERSIST");
            if (filedata == null || "".equals(filedata)) {
                properties.setProperty("BlackIPOnly_PERSIST", IP);
                redisUtils.set("BlackIPOnly_PERSIST", IP);
            } else {
                filedata += "," + IP;
                properties.setProperty("BlackIPOnly_PERSIST", filedata);
                redisUtils.set("BlackIPOnly_PERSIST", filedata);
            }
            properties.store(out, "Black IP List which to be refused");
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean ckeckIP(String IP) {
        Boolean canpass = false;
        String blackIps = redisUtils.get("BlackIPOnly_PERSIST", String.class) + redisUtils.get("BlackIPOnly_Temporary", String.class);
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
