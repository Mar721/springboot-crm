package crm.workbench.mappers;

import crm.workbench.pojo.TranRemark;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TranRemarkMapper {

    void insertTranRemarkByList(@Param("tranRemarkList") List<TranRemark> tranRemarkList);

    List<TranRemark> selectTranRemarkForDetailByTranId(String tranId);
}