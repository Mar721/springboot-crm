package crm.workbench.mappers;

import crm.workbench.pojo.Activity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ActivityMapper {
    int insertActivity(Activity activity);

    List<Activity> selectActivityByConditionForPage(Map<String, Object> map);

    int selectCountActivityCondition(Map<String, Object> map);

    int deleteActivityIds(@Param("ids") String[] ids);

    int changeActivity(Activity activity);

    Activity selectActivityById(String id);

    List<Activity> selectAllActivity();

    List<Activity> selectActivityByIds(@Param("ids") String[] ids);

    int insertActivityList(@Param("activityList") List<Activity> activityList);

    Activity selectActivityForDetailById(String id);

    List<Activity> selectConnectActivityByClueId(String id);

    List<Activity> selectActivityForDetailByNameClueId(Map<String, Object> map);

    List<Activity> selectActivityForConvertByNameClueId(Map<String, Object> map);

    List<Activity> selectActivityByActivityName(String activityName);
}
