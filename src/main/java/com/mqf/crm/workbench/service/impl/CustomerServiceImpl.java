package com.mqf.crm.workbench.service.impl;

import com.mqf.crm.utils.SqlSessionUtil;
import com.mqf.crm.workbench.dao.CustomerDao;
import com.mqf.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerName(String name) {
        List<String> customerNameList = customerDao.getCustomerName(name);
        return customerNameList;
    }
}
