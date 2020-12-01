package com.mqf.crm.workbench.service;

import com.mqf.crm.workbench.domain.Clue;
import com.mqf.crm.workbench.domain.Tran;

import java.util.Map;

public interface ClueService {
    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);

    boolean convert(String clueId, Tran tran, String createBy);
}
