package crm.settings.web.controllers;

import crm.commons.contants.Contants;
import crm.commons.pojo.ReturnObject;
import crm.commons.utils.CookieUtil;
import crm.commons.utils.DateUtil;
import crm.commons.utils.PasswordEncoder;
import crm.commons.utils.UUIDUtil;
import crm.settings.pojo.User;
import crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(){
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/toRegister.do")
    public String toRegister(){
        return "settings/qx/user/register";
    }

    @RequestMapping("/settings/qx/user/registerCheckUname.do")
    @ResponseBody
    public ReturnObject checkUname(String uName){
        User user = userService.getUserByUname(uName);
        if(user!=null){
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,"用户名已存在");
        }else{
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS);
        }
    }

    @RequestMapping("/settings/qx/user/register.do")
    @ResponseBody
    public ReturnObject register(String loginAct, String name,String loginPwd,HttpServletRequest request){
        loginPwd = PasswordEncoder.encode(loginPwd);
        User user = new User();
        user.setId(UUIDUtil.getUUID());
        user.setLoginAct(loginAct);
        user.setName(name);
        user.setLoginPwd(loginPwd);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 30);
        user.setExpireTime(DateUtil.formatDateTime(cal.getTime()));
        user.setLockState(Contants.LOCK_STATE_NO);
        user.setAllowIps(request.getRemoteAddr());
        user.setCreatetime(DateUtil.formatDateTime(new Date()));
        user.setCreateBy(name);

        int i = userService.register(user);

        if (i > 0) {
            HttpSession session = request.getSession();
            session.setAttribute(Contants.SESSION_USER,user);
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS);
        } else {
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                    "系统忙，请稍后重试...");
        }
    }

    @RequestMapping(value = "/settings/qx/user/login.do")
    @ResponseBody
    public Object login(String loginAct, String loginPwd, String isrememberpwd,String haveCookie,
                        HttpServletRequest request, HttpServletResponse response){
        String encodedPassword = userService.getUserByUname(loginAct).getLoginPwd();
        //密码不允许有@符号
        if(loginPwd.contains("@")){
            //这个肯定是使用cookie记住密码登录的
            if(!loginPwd.equals(encodedPassword)){
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL, "用户或密码错误", null);
            }
        }else{
            //普通登录
            boolean pwdIsTrue = PasswordEncoder.matches(encodedPassword,loginPwd);
            if(!pwdIsTrue){
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL, "用户或密码错误", null);
            }
        }
        Map<String,Object> map=new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",encodedPassword);
        User user = userService.login(map);
        if (user == null) {
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL, "用户或密码错误", null);
        } else {
            if (DateUtil.formatDateTime(new Date()).compareTo(user.getExpireTime())>0){
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,"用户已过期",null);
            }else if ("0".equals(user.getLockState())){
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,"用户已被锁定",null);
            }else if (!user.getAllowIps().contains(request.getRemoteAddr())){
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,"用户ip受限",null);
            }else{
                HttpSession session = request.getSession();
                session.setAttribute(Contants.SESSION_USER,user);
                if ("true".equals(isrememberpwd)&&!"true".equals(haveCookie)){
                    Cookie cookie1 = new Cookie("loginAct",loginAct);
                    Cookie cookie2 = new Cookie("loginPwd", encodedPassword);
                    cookie1.setMaxAge(10*24*60*60);
                    cookie2.setMaxAge(10*24*60*60);
                    response.addCookie(cookie1);
                    response.addCookie(cookie2);
                }else if(!"true".equals(isrememberpwd)){
                    CookieUtil.destroyLoginCookie(response);
                }
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS,null,null);
            }
        }
    }

    @RequestMapping("settings/qx/user/loginOut.do")
    public String loginOut(HttpServletResponse response, HttpSession session){
        CookieUtil.destroyLoginCookie(response);
        session.invalidate();
        return "redirect:/";
    }
}
