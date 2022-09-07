package crm.workbench.service;

import crm.workbench.pojo.TranRemark;

import java.util.List;

public interface TranRemarkService {
    List<TranRemark> queryTranRemarkForDetailByTranId(String tranId);
}
