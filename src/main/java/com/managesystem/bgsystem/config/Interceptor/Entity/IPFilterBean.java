package com.managesystem.bgsystem.config.Interceptor.Entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "spring.ip.filter", ignoreUnknownFields = false)
public class IPFilterBean {
    /*
     * location ip filte data remind location
     * */
    private IPDataLocation location;
    /*
     * @IPDataLocation locationfile path
     * */
    private String dataPath;
    /*
     * filter type
     * */
    private IPAdressType type;
    /*
     * if ip is refused redirect errorpageURL
     * */
    private String errorpageURL;
    /*
     * remind number of ip viewing
     * */
    private String store;

    public IPDataLocation getLocation() {
        return location;
    }

    public void setLocation(IPDataLocation location) {
        this.location = location;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public IPAdressType getType() {
        return type;
    }

    public void setType(IPAdressType type) {
        this.type = type;
    }

    public String getErrorpageURL() {
        return errorpageURL;
    }

    public void setErrorpageURL(String errorpageURL) {
        this.errorpageURL = errorpageURL;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
