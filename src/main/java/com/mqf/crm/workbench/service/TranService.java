package com.mqf.crm.workbench.service;

import com.mqf.crm.workbench.domain.Tran;

public interface TranService {
    boolean save(Tran tran, String customerName);
}
