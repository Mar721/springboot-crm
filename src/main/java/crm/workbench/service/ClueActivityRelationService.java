package crm.workbench.service;

import crm.workbench.pojo.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationService {
    int saveBund(List<ClueActivityRelation> clueActivityRelationList);

    int saveunBund(ClueActivityRelation clueActivityRelation);
}
