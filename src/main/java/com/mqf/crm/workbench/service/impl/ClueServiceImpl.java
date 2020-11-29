package com.mqf.crm.workbench.service.impl;

import com.mqf.crm.utils.SqlSessionUtil;
import com.mqf.crm.workbench.dao.ClueDao;
import com.mqf.crm.workbench.domain.Clue;
import com.mqf.crm.workbench.service.ClueService;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);

    @Override
    public boolean save(Clue clue) {
        boolean flag = true;
        int count = clueDao.sava(clue);
        if (count != 1){
            flag = false;
        }
        return false;
    }
}
