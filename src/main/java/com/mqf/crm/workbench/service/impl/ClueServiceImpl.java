package com.mqf.crm.workbench.service.impl;

import com.mqf.crm.utils.DateTimeUtil;
import com.mqf.crm.utils.SqlSessionUtil;
import com.mqf.crm.utils.UUIDUtil;
import com.mqf.crm.workbench.dao.*;
import com.mqf.crm.workbench.domain.*;
import com.mqf.crm.workbench.service.ClueService;

import java.util.List;

public class ClueServiceImpl implements ClueService {
    //线索相关的dao
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    //客户相关的dao
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    //联系人相关的dao
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    //交易相关的dao
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    @Override
    public boolean save(Clue clue) {
        boolean flag = true;
        int count = clueDao.sava(clue);
        if (count != 1){
            flag = false;
        }
        return flag;
    }


    @Override
    public Clue detail(String id) {
        Clue c = clueDao.detail(id);
        return c;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbund(id);
        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean bund(String cid, String[] aids) {
        boolean flag = true;
        for (String aid:aids) {
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(cid);
            car.setActivityId(aid);
            int count = clueActivityRelationDao.bund(car);
            if (count!=1){
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran tran, String createBy) {
        boolean flag = true;
        String createTime = DateTimeUtil.getSysTime();
        //(1)根据线索的id查线索对象
        Clue clue = clueDao.getClueById(clueId);
        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        //通过查找customer表，找customer对象，判断对象是否为空
        Customer customer = customerDao.getCustomerByName(company);
        if (customer==null){
            //如果通过公司名查询表中没有customer则新建一个customer
            customer = new Customer();
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(company);
            customer.setId(UUIDUtil.getUUID());
            customer.setDescription(clue.getDescription());
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setContactSummary(clue.getContactSummary());
            //添加customer到表中
            int count1 = customerDao.save(customer);
            if (count1!=1){
                flag = false;
            }
        }
        //(3) 通过线索对象提取联系人信息，保存联系人
        String mphone = clue.getMphone();
        Contacts contacts = contactsDao.getContactByMphpne(mphone);
        if (contacts==null){
            contacts = new Contacts();
            contacts.setSource(clue.getSource());
            contacts.setOwner(clue.getOwner());
            contacts.setNextContactTime(clue.getNextContactTime());
            contacts.setMphone(mphone);
            contacts.setJob(clue.getJob());
            contacts.setId(UUIDUtil.getUUID());
            contacts.setFullname(clue.getFullname());
            contacts.setEmail(clue.getEmail());
            contacts.setDescription(clue.getDescription());
            contacts.setCustomerId(customer.getId());
            contacts.setCreateTime(createTime);
            contacts.setCreateBy(createBy);
            contacts.setContactSummary(clue.getContactSummary());
            contacts.setAppellation(clue.getAppellation());
            contacts.setAddress(clue.getAddress());
            //添加联系人
            int count2 = contactsDao.save(contacts);
            if (count2!=1){
                flag = false;
            }
        }
        //(4) 线索备注转换到客户备注以及联系人备注
        //首先查询出跟这条线索关联的备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        for (ClueRemark clueRemark:clueRemarkList) {
            //取出每条备注的内容
            String noteContent = clueRemark.getNoteContent();
            //创建联系人备注，添加备注内容
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setEditFlag("0");
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setContactsId(contacts.getId());
            int count3 = contactsRemarkDao.save(contactsRemark);
            if (count3!=1){
                flag = false;
            }
            //创建客户备注，添加备注内容
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setNoteContent(noteContent);
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setCreateTime(createTime);
            customerRemark.setCreateBy(createBy);
            int count4 = customerRemarkDao.save(customerRemark);
            if (count4!=1){
                flag = false;
            }
        }
        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //查询出跟该线索关联的市场活动列表，（查tbl_clue_activity_relation表）
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList) {

            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();

            contactsActivityRelation.setActivityId(clueActivityRelation.getActivityId());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5 != 1){
                flag = false;
            }
        }
        //(6) 如果有创建交易需求，创建一条交易
        if (tran!=null){
            //完善一下tran的内容
            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(customer.getId());
            tran.setContactSummary(clue.getContactSummary());
            tran.setContactsId(contacts.getId());
            //添加交易
            int count6 = tranDao.save(tran);
            if (count6!=1){
                flag = false;
            }
            //(7) 如果创建了交易，则创建一条该交易下的交易历史,要放在if里面，只有创建了交易才能创建交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setTranId(tran.getId());
            tranHistory.setStage(tran.getStage());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setCreateTime(createTime);
            tranHistory.setCreateBy(createBy);
            int count7 = tranHistoryDao.save(tranHistory);
            if (count7!=1){
                flag = false;
            }
        }
        /*//(8) 删除线索备注
        for (ClueRemark clueRemark:clueRemarkList) {
            int count8 = clueRemarkDao.delete(clueRemark);
            if (count8!=1){
                flag = false;
            }
        }
        //(9) 删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList) {
            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if (count9!=1){
                flag = false;
            }
        }
        //(10) 删除线索
        int count10 = clueDao.delete(clueId);
        if (count10!=1){
            flag = false;
        }*/
        return flag;
    }

}
