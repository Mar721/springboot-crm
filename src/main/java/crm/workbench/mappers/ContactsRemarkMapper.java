package crm.workbench.mappers;

import crm.workbench.pojo.ContactsRemark;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactsRemarkMapper {

    void insertContactsRemarkByList(@Param("contactsRemarkList") List<ContactsRemark> contactsRemarkList);
}