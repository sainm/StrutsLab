package com.strutslab.dao;

import com.strutslab.dto.DeptDto;
import java.util.List;
import java.util.Map;

public interface DeptDao {
    List<DeptDto> findAll();
    DeptDto findById(String deptCode);
    void insert(DeptDto dept);
    void update(DeptDto dept);
    List<DeptDto> search(Map<String, Object> params);
}
