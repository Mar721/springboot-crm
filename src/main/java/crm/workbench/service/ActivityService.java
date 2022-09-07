package crm.workbench.service;

import crm.workbench.pojo.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    int saveActivity(Activity activity);

    List<Activity> queryActivityByConditionForPage(Map<String, Object> map);

    int queryCountActivityCondition(Map<String, Object> map);

    int deleteActivityIds(String[] ids);

    int changeActivity(Activity activity);

    Activity queryActivityById(String id);

    List<Activity> getAllActivity();

    List<Activity> getActivityByIds(String[] ids);

    int saveCreateActivityByList(List<Activity> activityList);

    Activity getActivityForDetailById(String id);

    List<Activity> queryConnectActivityByClueId(String id);

    List<Activity> queryActivityForDetailByNameClueId(Map<String, Object> map);

    List<Activity> queryActivityForConvertByNameClueId(Map<String, Object> map);

    List<Activity> queryActivityByActivityName(String activityName);
}
