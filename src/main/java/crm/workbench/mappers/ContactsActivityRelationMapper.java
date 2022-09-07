package crm.workbench.mappers;

import crm.workbench.pojo.ContactsActivityRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactsActivityRelationMapper {

    void insertContactsActivityRelationByList(@Param("contactsActivityRelationList") List<ContactsActivityRelation> contactsActivityRelationList);
}