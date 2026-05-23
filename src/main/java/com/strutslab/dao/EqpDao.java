package com.strutslab.dao;

import com.strutslab.dto.EqpDto;
import java.util.List;
import java.util.Map;

public interface EqpDao {
    List<EqpDto> search(Map<String, Object> params);
    int count(Map<String, Object> params);
    EqpDto findById(String equipmentCode);
    void insert(EqpDto eqp);
    void update(EqpDto eqp);
    void delete(String equipmentCode);
    List<EqpDto> findAll();
}
