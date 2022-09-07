package crm.workbench.service;

import crm.workbench.pojo.Contacts;

import java.util.List;

public interface ContactsService {
    List<Contacts> queryContactsByName(String contactsName);
}
