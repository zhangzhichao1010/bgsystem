package com.managesystem.bgsystem.config.Interceptor.service;

import com.managesystem.bgsystem.config.Interceptor.Repository.IPAdressRepository;
import com.managesystem.bgsystem.config.Interceptor.Entity.IPAdress;
import com.managesystem.bgsystem.config.Interceptor.Entity.IPAdressType;
import com.managesystem.bgsystem.config.Interceptor.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IPAdressServiceImpl implements IPAdressService {
    @Autowired
    private IPAdressRepository ipAdressRepository;

    @Value("${spring.ip.filter.type}")
    private IPAdressType ipAdressType;

    @Override
    public Boolean ckeckRemoteIP(String IP) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        IPAdress blackIPAdress = new IPAdress();
        blackIPAdress.setRomoteIP(IpUtils.ipToLong(IP));
        matcher = matcher.withMatcher("romoteIP", ExampleMatcher.GenericPropertyMatchers.exact());
        Example example = Example.of(blackIPAdress, matcher);
        if (ipAdressType.equals(IPAdressType.BlackIPOnly)) {
            blackIPAdress.setType(0);
            matcher = matcher.withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
            return ipAdressRepository.findAll(example).size() == 0 ? true : false;
        } else if (ipAdressType.equals(IPAdressType.WhiteIPOnly)) {
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
    public void saveBlackRemoteIP(String IP) {
        IPAdress adress = new IPAdress();
        adress.setRomoteIP(IpUtils.ipToLong(IP));
        adress.setType(0);
        ipAdressRepository.save(adress);
    }
}
