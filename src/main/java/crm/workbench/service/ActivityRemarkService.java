package crm.workbench.service;

import crm.workbench.pojo.ActivityRemark;

import java.util.List;

public interface ActivityRemarkService {
    List<ActivityRemark> getRemarkByActivityId(String id);

    int addRemark(ActivityRemark activityRemark);

    int deleteRemark(String id);

    int changeRemark(ActivityRemark activityRemark);

    int deleteRemarkByActivityIds(String[] ids);
}
