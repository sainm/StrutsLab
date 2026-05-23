package com.strutslab.dao;

import com.strutslab.dto.ChkTmplDto;
import java.util.List;
import java.util.Map;

public interface ChkItemDao {
    List<ChkTmplDto> search(Map<String,Object> params);
    int count(Map<String,Object> params);
    ChkTmplDto findById(int templateId);
    void insert(ChkTmplDto tmpl);
    void update(ChkTmplDto tmpl);
    void copyTemplate(int templateId);
    void swapOrder(int id1, int id2);
    void delete(int templateId);
}
