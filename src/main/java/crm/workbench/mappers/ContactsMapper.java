package crm.workbench.mappers;


import crm.workbench.pojo.Contacts;

import java.util.List;

public interface ContactsMapper {

    void insertContacts(Contacts contacts);

    List<Contacts> selectContactsByName(String contactsName);
}