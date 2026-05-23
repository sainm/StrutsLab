package com.strutslab.dao;

import com.strutslab.dto.CapaDto;

public interface CapaDao {
    void insert(CapaDto dto);
    CapaDto findById(int capaId);
    CapaDto findByIncidentNo(String incidentNo);
}
