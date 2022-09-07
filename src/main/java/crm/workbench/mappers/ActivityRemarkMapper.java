package crm.workbench.mappers;

import crm.workbench.pojo.ActivityRemark;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityRemarkMapper {
    List<ActivityRemark> selectRemarkByActivityId(String id);

    int insertRemark(ActivityRemark activityRemark);

    int deleteRemark(String id);

    int changeRemark(ActivityRemark activityRemark);

    int deleteRemarkByActivityIds(@Param("ids") String[] ids);
}
