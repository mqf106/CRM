package com.mqf.crm.workbench.service;

import com.mqf.crm.workbench.domain.Tran;
import com.mqf.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranService {
    boolean save(Tran tran, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String tranId);

    boolean changeStage(Tran t);
}
