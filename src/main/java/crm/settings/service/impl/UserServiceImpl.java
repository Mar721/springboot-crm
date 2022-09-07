package crm.settings.service.impl;

import crm.settings.mappers.UserMapper;
import crm.settings.pojo.User;
import crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public User login(Map<String,Object> map) {
        return userMapper.selectUserByLoginActAndPwd(map);

    }

    @Override
    @Transactional(readOnly = true)
    public List<User> queryAllUser() {
        return userMapper.selectAllUser();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUname(String uName) {
        return userMapper.queryUserByUname(uName);
    }

    @Override
    public int register(User user) {
        return userMapper.addUser(user);
    }
}
