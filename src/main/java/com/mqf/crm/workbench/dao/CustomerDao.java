package com.mqf.crm.workbench.dao;

import com.mqf.crm.workbench.domain.Customer;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer customer);
}
