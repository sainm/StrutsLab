package com.strutslab.dao;

import java.util.List;
import java.util.Map;

import com.strutslab.dto.IncidentDto;
import com.strutslab.dto.TimelineDto;

public interface IncidentDao {
    List<IncidentDto> search(Map<String, Object> params);
    int count(Map<String, Object> params);
    IncidentDto findById(String incidentNo);
    void insert(IncidentDto dto);
    void update(IncidentDto dto);
    void bulkUpdateStatus(Map<String, Object> params);
    List<IncidentDto> searchSimilar(Map<String, Object> params);
    List<TimelineDto> getTimeline(String incidentNo);
    void insertTimeline(TimelineDto dto);
    String generateIncidentNo();
}
