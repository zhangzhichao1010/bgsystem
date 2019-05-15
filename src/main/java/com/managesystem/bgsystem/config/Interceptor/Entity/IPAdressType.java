package com.managesystem.bgsystem.config.Interceptor.Entity;

public enum IPAdressType {
    /**
     * maked by zzz
     *
     * @Params BlackIPOnly:only Black IP ckecked,if find one then deny else pass
     * @Params WhiteIPOnly:only White IP ckecked，if find one then pass else deny
     * @Params BlackAndWhite:first do checking  in WhiteIP if finded then pass else do checking BlackIP if find one then deny else pass
     */
    BlackIPOnly,
    WhiteIPOnly,
    BlackAndWhite;

    private IPAdressType() {
    }
}
