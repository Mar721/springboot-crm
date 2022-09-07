package crm.workbench.mappers;

import crm.workbench.pojo.TranHistory;

import java.util.List;

public interface TranHistoryMapper {

    void insertTranHistory(TranHistory tranHistory);

    List<TranHistory> selectTranHistoryForDetailByTranId(String tranId);
}