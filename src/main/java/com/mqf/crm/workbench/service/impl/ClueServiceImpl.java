package com.mqf.crm.workbench.service.impl;

import com.mqf.crm.utils.SqlSessionUtil;
import com.mqf.crm.workbench.dao.ClueActivityRelationDao;
import com.mqf.crm.workbench.dao.ClueDao;
import com.mqf.crm.workbench.domain.Clue;
import com.mqf.crm.workbench.service.ClueService;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

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
}
