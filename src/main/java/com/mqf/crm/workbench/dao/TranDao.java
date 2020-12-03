package com.mqf.crm.workbench.dao;

import com.mqf.crm.workbench.domain.Tran;

public interface TranDao {

    int save(Tran tran);

    Tran detail(String id);

    int changeStage(Tran t);
}
