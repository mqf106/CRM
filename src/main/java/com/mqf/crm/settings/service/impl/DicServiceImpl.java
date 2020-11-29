package com.mqf.crm.settings.service.impl;

import com.mqf.crm.settings.dao.DicTypeDao;
import com.mqf.crm.settings.dao.DicValueDao;
import com.mqf.crm.settings.domain.DicType;
import com.mqf.crm.settings.domain.DicValue;
import com.mqf.crm.settings.service.DicService;
import com.mqf.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, List<DicValue>> getAll() {
        //取出所有的DicType数据封装成对象
        List<DicType> dicTypeList = dicTypeDao.getTypeList();
        Map<String,List<DicValue>> map = new HashMap<>();
        //循环取出DicType的值
        for (DicType dt:dicTypeList) {
            //获得主键
            String code = dt.getCode();
            List<DicValue> dicValueList = dicValueDao.getValueListByCode(code);
            map.put(code,dicValueList);
        }
        return map;
    }
}
