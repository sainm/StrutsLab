package com.strutslab.dao;

import java.util.List;
import java.util.Map;

public interface ReportDao {
    List<Map<String, Object>> computeCompletionRate(Map<String, Object> params);
    List<Map<String, Object>> computeIncidentCrossTab(Map<String, Object> params);
    List<Map<String, Object>> computeEquipmentRanking(Map<String, Object> params);
}
