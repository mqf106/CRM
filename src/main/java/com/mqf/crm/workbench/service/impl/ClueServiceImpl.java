package com.mqf.crm.workbench.service.impl;

import com.mqf.crm.utils.SqlSessionUtil;
import com.mqf.crm.workbench.dao.ClueDao;
import com.mqf.crm.workbench.service.ClueService;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
}
