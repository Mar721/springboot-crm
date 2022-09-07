package crm.settings.mappers;

import crm.settings.pojo.DicValue;

import java.util.List;

public interface DicValueMapper {
    List<DicValue> selectDicValueByTypeCode(String typeCode);
}
