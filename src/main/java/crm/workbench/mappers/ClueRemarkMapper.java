package crm.workbench.mappers;

import crm.workbench.pojo.ClueRemark;

import java.util.List;

public interface ClueRemarkMapper {
    /**
     * 为线索明细页面查询备注，表连接
     * @param id
     * @return
     */
    List<ClueRemark> selectClueRemarkListByClueId(String id);

    /**
     * 为转化线索
     * @param clueId
     * @return
     */
    List<ClueRemark> selectClueRemarkByClueId(String clueId);

    void deleteClueRemarkByClueId(String clueId);
}
