package crm.workbench.mappers;


import crm.workbench.pojo.ClueActivityRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClueActivityRelationMapper {

    int insertBund(@Param("clueActivityRelationList") List<ClueActivityRelation> clueActivityRelationList);

    int deleteClueActivityRelationByClueActivityId(ClueActivityRelation clueActivityRelation);

    List<ClueActivityRelation> selectClueActivityRelationByClueId(String clueId);

    void deleteClueActivityRelationListByClueId(String clueId);
}