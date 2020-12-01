package com.mqf.crm.workbench.dao;

import com.mqf.crm.workbench.domain.Clue;

public interface ClueDao {

    int sava(Clue clue);

    Clue detail(String id);

    Clue getClueById(String clueId);

    int delete(String clueId);
}
