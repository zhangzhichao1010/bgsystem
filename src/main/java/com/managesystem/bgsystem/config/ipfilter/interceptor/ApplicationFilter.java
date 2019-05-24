package com.managesystem.bgsystem.config.ipfilter.interceptor;

import com.managesystem.bgsystem.Utils.RedisUtils;
import com.managesystem.bgsystem.config.ipfilter.Entity.IPAdressType;
import com.managesystem.bgsystem.config.ipfilter.Entity.IPDataLocation;
import com.managesystem.bgsystem.config.ipfilter.Entity.IPFilterBean;
import com.managesystem.bgsystem.config.ipfilter.service.IPAdressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@Component
public class ApplicationFilter implements ApplicationRunner {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private IPFilterBean ipFilterBean;
    @Autowired
    private IPAdressService ipAdressService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IPDataLocation location = ipFilterBean.getLocation();
        IPAdressType type = ipFilterBean.getType();
        String path = ipFilterBean.getDataPath();
        switch (location) {
            case database:
                prepareIPDatabase();
                break;
            case romotefile:
                prepareIPRomoteFile();
                break;
            case locationfile:
                prepareIPLocationFile(type, path);
                break;
            default:
                break;
        }
    }

    private void prepareIPLocationFile(IPAdressType type, String path) {
        switch (type) {
            case BlackIPOnly:
                preIpData("BlackIPOnly_PERSIST", path);
                break;
            case WhiteIPOnly:
                preIpData("WhiteIPOnly", path);
                break;
            case BlackAndWhite:
                preIpData("BlackIPOnly_PERSIST", path);
                preIpData("WhiteIPOnly", path);
                break;
            default:
                break;
        }
    }

    private void preIpData(String name, String path) {
        if (path == null || "".equals(path)) throw new RuntimeException("if spring.ip.data.location=locationfile ,then 3" +
                "spring.ip.data.path,can not be null");
        if (path.lastIndexOf("/") != (path.length() - 1)) {
            path = path + "/";
        }
        File blackIpFile = new File(path);
        try {
            if (!blackIpFile.exists()) {
                blackIpFile.mkdirs();
                File f = new File(path + name + ".properties");
                f.createNewFile();
            }
            Properties properties = new Properties();
            File f = new File(path + name + ".properties");
            InputStream in = new FileInputStream(f);
            properties.load(in);
            String filedata = properties.getProperty(name);
            if (filedata != null && !"".equals(filedata)) {
                redisUtils.set(name, filedata);
            } else {
                redisUtils.set(name, null);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareIPRomoteFile() {
        String blackIps = ipAdressService.findBlackIPRemote();
        redisUtils.set("BlackIPOnly_PERSIST", blackIps);
        String whiteIps = ipAdressService.findWhiteIPRemote();
        redisUtils.set("WhiteIPOnly", whiteIps);
    }

    private void prepareIPDatabase() {
        String blackIps = ipAdressService.findBlackIPDataBase();
        redisUtils.set("BlackIPOnly_PERSIST", blackIps);
        String whiteIps = ipAdressService.findWhiteIPDataBase();
        redisUtils.set("WhiteIPOnly", whiteIps);
    }
}
