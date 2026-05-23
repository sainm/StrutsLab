package com.strutslab.dao;

import com.strutslab.dto.EmpDto;
import java.util.List;
import java.util.Map;

public interface EmpDao {
    EmpDto findByLoginId(String loginId);
    EmpDto findById(String empNo);
    List<EmpDto> findAll();
    List<EmpDto> search(Map<String, Object> params);
    int count(Map<String, Object> params);
    void insert(EmpDto emp);
    void update(EmpDto emp);
}
