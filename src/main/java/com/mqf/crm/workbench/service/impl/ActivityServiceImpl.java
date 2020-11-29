package com.mqf.crm.workbench.service.impl;

import com.mqf.crm.settings.dao.UserDao;
import com.mqf.crm.settings.domain.User;
import com.mqf.crm.utils.SqlSessionUtil;
import com.mqf.crm.vo.PaginationVO;
import com.mqf.crm.workbench.dao.ActivityDao;
import com.mqf.crm.workbench.dao.ActivityRemarkDao;
import com.mqf.crm.workbench.domain.Activity;
import com.mqf.crm.workbench.domain.ActivityRemark;
import com.mqf.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    @Override
    public boolean save(Activity activity) {
        boolean flag = true;
        int count = activityDao.save(activity);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取得activityList
        List<Activity> activityList = activityDao.getActivityListByCondition(map);
        //封装成vo
        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(activityList);
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        //查询此aid外键中有几条备注信息
        int count1 = activityRemarkDao.getCountByAids(ids);
        //删除备注返回实际收到影响的条数
        int count2 = activityRemarkDao.deleteByAids(ids);
        if (count1!=count2){
            flag = false;
        }
        //删除市场活动条数
        int count3 = activityDao.delete(ids);
        if (count3!= ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //前端需要userList和一个activity
        //得到userList
        List<User> userList = userDao.selectUserList();
        Activity activity = activityDao.getById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("activity",activity);
        return map;
    }

    @Override
    public boolean update(Activity a) {
        boolean flag = true;
        int count = activityDao.update(a);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity activity = activityDao.detail(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> remarkList = activityRemarkDao.getRemarkListByAid(activityId);
        return remarkList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activityRemarkDao.deleteById(id);
        if (count !=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean savaRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.savaRemark(ar);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(ar);
        if (count != 1){
            flag = false;
        }
        return flag;
    }
}
