package crm.workbench.service.impl;


import crm.workbench.mappers.ClueActivityRelationMapper;
import crm.workbench.pojo.ClueActivityRelation;
import crm.workbench.service.ClueActivityRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("clueActivityRelationService")
public class ClueActivityRelationServiceImpl implements ClueActivityRelationService {
    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int saveBund(List<ClueActivityRelation> clueActivityRelationList) {
        return clueActivityRelationMapper.insertBund(clueActivityRelationList);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int saveunBund(ClueActivityRelation clueActivityRelation) {
        return clueActivityRelationMapper.deleteClueActivityRelationByClueActivityId(clueActivityRelation);
    }
}
