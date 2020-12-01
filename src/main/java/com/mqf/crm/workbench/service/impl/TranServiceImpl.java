package com.mqf.crm.workbench.service.impl;

import com.mqf.crm.utils.SqlSessionUtil;
import com.mqf.crm.utils.UUIDUtil;
import com.mqf.crm.workbench.dao.CustomerDao;
import com.mqf.crm.workbench.dao.TranDao;
import com.mqf.crm.workbench.dao.TranHistoryDao;
import com.mqf.crm.workbench.domain.Customer;
import com.mqf.crm.workbench.domain.Tran;
import com.mqf.crm.workbench.domain.TranHistory;
import com.mqf.crm.workbench.service.TranService;

public class TranServiceImpl implements TranService {
    TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    @Override
    public boolean save(Tran tran, String customerName) {
        /*
        （3）查询是否有此客户，如果没有则添加客户记录
		（4）添加交易记录（注意先后顺序，添加完客户之后，才知道客户id）
		（5）添加交易历史
        */
        boolean flag = true;
        Customer customer = customerDao.getCustomerByName(customerName);
        if (customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(tran.getName());
            customer.setContactSummary(tran.getContactSummary());
            customer.setCreateTime(tran.getCreateTime());
            customer.setCreateBy(tran.getCreateBy());
            customer.setDescription(tran.getDescription());
            customer.setOwner(tran.getOwner());
            int count1 = customerDao.save(customer);
            if (count1!=1){
                flag = false;
            }
        }
        tran.setCustomerId(customer.getId());
        tranDao.save(tran);
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistoryDao.save(tranHistory);
        return flag;
    }
}
