function getcookiebyname(cookiename) {
    var cookie= document.cookie;
    var begin = cookie.indexOf(cookiename);
    var a = cookie.indexOf("=", begin) + 1;
    var b = cookie.indexOf(";", begin);
    var value = "";
    if (b != -1) {
        value = cookie.substr(a, b - a);
    } else {
        value = cookie.substr(a);
    }
    return value;
}