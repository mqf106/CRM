package com.mqf.crm.workbench.service.impl;

import com.mqf.crm.utils.SqlSessionUtil;
import com.mqf.crm.workbench.dao.TranDao;
import com.mqf.crm.workbench.dao.TranHistoryDao;
import com.mqf.crm.workbench.service.TranService;

public class TranServiceImpl implements TranService {
    TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
}
