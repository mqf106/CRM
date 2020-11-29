package com.mqf.crm.settings.dao;

import com.mqf.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getValueListByCode(String code);
}
