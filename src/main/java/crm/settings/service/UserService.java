package crm.settings.service;

import crm.settings.pojo.User;

import java.util.List;
import java.util.Map;


public interface UserService {
    User login(Map<String,Object> map);
    List<User> queryAllUser();

    User getUserByUname(String uName);

    int register(User user);
}
