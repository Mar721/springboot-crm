package crm.workbench.service;

import crm.workbench.pojo.FunnelVO;
import crm.workbench.pojo.Tran;

import java.util.List;
import java.util.Map;

public interface TranService {
    void saveCreateTran(Map<String, Object> map);

    List<Tran> queryTranByCondition(Map<String, Object> map);

    int queryCountOfTranByCondition(Map<String, Object> map);

    Tran queryTranForDetailById(String tranId);

    List<FunnelVO> queryCountOfTranGroupByStage();
}
