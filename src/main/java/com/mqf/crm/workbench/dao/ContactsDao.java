package com.mqf.crm.workbench.dao;

import com.mqf.crm.workbench.domain.Contacts;

public interface ContactsDao {

    Contacts getContactByMphpne(String mphone);

    int save(Contacts contacts);
}
