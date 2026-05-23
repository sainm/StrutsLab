package com.strutslab.dao;

import com.strutslab.dto.PlanDto;
import java.util.List;
import java.util.Map;

public interface PlanDao {
    List<PlanDto> search(Map<String,Object> params);
    int countMonthly(Map<String,Object> params);
    PlanDto findById(int planId);
    void insert(PlanDto plan);
    void update(PlanDto plan);
    void lockYear(String fiscalYear);
    void unlockYear(String fiscalYear);
    List<PlanDto> findByYear(String fiscalYear);
}
