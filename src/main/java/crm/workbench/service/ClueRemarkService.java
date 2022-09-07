package crm.workbench.service;

import crm.workbench.pojo.ClueRemark;

import java.util.List;

public interface ClueRemarkService {
    List<ClueRemark> queryClueRemarkListByClueId(String id);
}
