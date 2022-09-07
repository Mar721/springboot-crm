package crm.settings.mappers;

import crm.settings.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    User selectUserByLoginActAndPwd(Map<String,Object> map);
    List<User> selectAllUser();

    User queryUserByUname(String uName);

    int addUser(User user);
}
