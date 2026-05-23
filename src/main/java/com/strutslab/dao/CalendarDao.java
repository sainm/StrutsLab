package com.strutslab.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface CalendarDao {
    List<Map<String, Object>> findByYear(@Param("year") String year);
    Map<String, Object> findById(@Param("holidayId") Integer holidayId);
    Map<String, Object> findByDate(@Param("holidayDate") String holidayDate);
    void insert(Map<String, Object> holiday);
    void update(Map<String, Object> holiday);
    void delete(@Param("holidayId") Integer holidayId);
    List<Map<String, Object>> findBetween(@Param("dateFrom") String dateFrom, @Param("dateTo") String dateTo);
}
