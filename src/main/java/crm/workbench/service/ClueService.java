package crm.workbench.service;

import crm.workbench.pojo.Clue;

import java.util.List;
import java.util.Map;

public interface ClueService {

    int saveClue(Clue clue);

    List<Clue> queryClueByCondition(Map<String, Object> map);

    Clue queryClueById(String id);

    int queryCountOfClueByCondition(Map<String, Object> map);

    void convertClue(Map<String, Object> map);
}
