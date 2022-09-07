package crm.workbench.service.impl;

import crm.workbench.mappers.ActivityRemarkMapper;
import crm.workbench.pojo.ActivityRemark;
import crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("activityRemarkService")
public class ActivityRemarkServiceImpl implements ActivityRemarkService {
    @Autowired
    ActivityRemarkMapper activityRemarkMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ActivityRemark> getRemarkByActivityId(String id) {
        return activityRemarkMapper.selectRemarkByActivityId(id);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int addRemark(ActivityRemark activityRemark) {
        return activityRemarkMapper.insertRemark(activityRemark);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteRemark(String id) {
        return activityRemarkMapper.deleteRemark(id);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int changeRemark(ActivityRemark activityRemark) {
        return activityRemarkMapper.changeRemark(activityRemark);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteRemarkByActivityIds(String[] ids) {
        return activityRemarkMapper.deleteRemarkByActivityIds(ids);
    }
}
