package com.managesystem.bgsystem.config.Interceptor.utils;


import com.managesystem.bgsystem.config.Interceptor.Entity.IPAdressType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "spring.ip.filter"
)
public class IPFilterProperties {
    /*
     * @Params type:IPAdressType
     * */
    private IPAdressType type;
    /*
     * @Params filterURL:the path to beintercepted
     * */
    private String filterURL;
    /*
     * @Params errorpageURL:error page url
     * */
    private String errorpageURL;

    public IPAdressType getType() {
        return type;
    }

    public String getFilterURL() {
        return filterURL;
    }

    public String getErrorpageURL() {
        return errorpageURL;
    }

    public void setType(IPAdressType type) {
        this.type = type;
    }

    public void setFilterURL(String filterURL) {
        this.filterURL = filterURL;
    }

    public void setErrorpageURL(String errorpageURL) {
        this.errorpageURL = errorpageURL;
    }
}
