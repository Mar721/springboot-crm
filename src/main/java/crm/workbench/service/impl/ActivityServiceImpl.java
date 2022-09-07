package crm.workbench.service.impl;

import crm.workbench.mappers.ActivityMapper;
import crm.workbench.pojo.Activity;
import crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service(value = "activityService")
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int saveActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map) {
        return activityMapper.selectActivityByConditionForPage(map);
    }

    @Override
    @Transactional(readOnly = true)
    public int queryCountActivityCondition(Map<String, Object> map) {
        return activityMapper.selectCountActivityCondition(map);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteActivityIds(String[] ids) {
        return activityMapper.deleteActivityIds(ids);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int changeActivity(Activity activity) {
        return activityMapper.changeActivity(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public Activity queryActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> getAllActivity() {
        return activityMapper.selectAllActivity();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> getActivityByIds(String[] ids) {
        return activityMapper.selectActivityByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int saveCreateActivityByList(List<Activity> activityList) {
        return activityMapper.insertActivityList(activityList);
    }

    @Override
    @Transactional(readOnly = true)
    public Activity getActivityForDetailById(String id) {
        return activityMapper.selectActivityForDetailById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> queryConnectActivityByClueId(String id) {
        return activityMapper.selectConnectActivityByClueId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> queryActivityForDetailByNameClueId(Map<String, Object> map) {
        return activityMapper.selectActivityForDetailByNameClueId(map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> queryActivityForConvertByNameClueId(Map<String, Object> map) {
        return activityMapper.selectActivityForConvertByNameClueId(map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> queryActivityByActivityName(String activityName) {
        return activityMapper.selectActivityByActivityName(activityName);
    }
}
