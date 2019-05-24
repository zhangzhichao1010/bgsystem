package com.managesystem.bgsystem.Controller.ipfilter.Impl;

import com.managesystem.bgsystem.Controller.ipfilter.Entity.IPAdress;
import com.managesystem.bgsystem.config.ipfilter.service.IPAdressService;
import com.managesystem.bgsystem.config.ipfilter.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class IPAdressServiceImpl implements IPAdressService {
    @Autowired
    private IPAdressRepository ipAdressRepository;

    @Override
    public void saveBlackIPDataBase() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String IP = request.getRemoteAddr();
        IPAdress adress = new IPAdress();
        adress.setRomoteIP(IpUtils.ipToLong(IP));
        adress.setType(0);
        ipAdressRepository.save(adress);
    }

    @Override
    public String findBlackIPDataBase() {
        return findIPDatabase(0);
    }

    private String findIPDatabase(int type) {
        IPAdress ipAdress = new IPAdress();
        ipAdress.setType(type);
        ExampleMatcher matcher = ExampleMatcher.matching();
        matcher = matcher.withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
        Example example = Example.of(ipAdress, matcher);
        List<IPAdress> ipAdressList = ipAdressRepository.findAll(example);
        String ipStrs = "";
        if (ipAdressList != null) {
            for (IPAdress ip : ipAdressList
            ) {
                ipStrs += "," + IpUtils.longToIp(ip.getRomoteIP());
            }
        }
        return ipStrs;
    }

    @Override
    public String findWhiteIPDataBase() {
        return findIPDatabase(1);
    }

    @Override
    public String findBlackIPRemote() {
        return null;
    }

    @Override
    public String findWhiteIPRemote() {
        return null;
    }

    @Override
    public void saveBlackIPRemote() {

    }
}
