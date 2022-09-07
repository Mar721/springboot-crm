package crm.commons.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static void destroyLoginCookie(HttpServletResponse response){
        Cookie cookie1 = new Cookie("loginAct","");
        Cookie cookie2 = new Cookie("loginPwd", "");
        cookie1.setMaxAge(0);
        cookie2.setMaxAge(0);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
    }
}
