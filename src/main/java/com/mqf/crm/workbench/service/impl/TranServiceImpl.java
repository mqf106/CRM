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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setCreateTime(tran.getCreateTime());
        tranHistory.setCreateBy(tran.getCreateBy());
        int count2 = tranHistoryDao.save(tranHistory);
        if (count2!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran tran = tranDao.detail(id);
        return tran;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        List<TranHistory> tranHistoryList = tranHistoryDao.getHistoryListByTranId(tranId);
        return tranHistoryList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;
        //修改交易列表中的状态,修改时间，修改人
        int count1 = tranDao.changeStage(t);
        if (count1!=1){
            flag = false;
        }
        //添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setExpectedDate(t.getExpectedDate());
        tranHistory.setTranId(t.getId());
        tranHistory.setStage(t.getStage());
        tranHistory.setMoney(t.getMoney());
        tranHistory.setCreateTime(t.getEditTime());
        tranHistory.setCreateBy(t.getEditBy());
        tranHistory.setPossibility(t.getPossibility());
        int count2 = tranHistoryDao.save(tranHistory);
        if (count2!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        //取得总条数total
        int total = tranDao.getTotal();
        //取得dataList "dataList":[{value: 60, name: '访问'},{value: 40, name: '咨询'}
        List<Map<String,Object>> dataList =  tranDao.getDataList();
        //将这两项打包成map 返回
        Map<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);
        return map;
    }
}
