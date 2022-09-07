package crm.workbench.mappers;

import crm.workbench.pojo.FunnelVO;
import crm.workbench.pojo.Tran;

import java.util.List;
import java.util.Map;

public interface TranMapper {

    void insertTran(Tran tran);

    List<Tran> selectTranByCondition(Map<String, Object> map);

    int selectCountOfTranByCondition(Map<String, Object> map);

    Tran selectTranForDetailById(String tranId);

    List<FunnelVO> selectCountOfTranGroupByStage();
}