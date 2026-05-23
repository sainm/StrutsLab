package com.strutslab.dao;

import com.strutslab.dto.PartsDto;
import com.strutslab.dto.PartsUsageDto;
import java.util.List;
import java.util.Map;

public interface PartsDao {
    List<PartsDto> search(Map<String, Object> params);
    int count(Map<String, Object> params);
    PartsDto findById(String partCode);
    void insert(PartsDto parts);
    void update(PartsDto parts);
    void delete(String partCode);
    List<PartsDto> findAll();
    List<Map<String, Object>> findEquipmentRelations(String partCode);
    void insertEquipmentRelation(Map<String, Object> relation);
    void deleteEquipmentRelations(String partCode);

    // Usage
    List<PartsUsageDto> searchUsage(Map<String, Object> params);
    int countUsage(Map<String, Object> params);
    void insertUsage(PartsUsageDto usage);
    PartsUsageDto findUsageById(Integer usageId);
}
