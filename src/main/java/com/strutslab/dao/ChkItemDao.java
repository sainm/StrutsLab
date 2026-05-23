package com.strutslab.dao;

import com.strutslab.dto.ChkItemDto;
import com.strutslab.dto.ChkTmplDto;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface ChkItemDao {
    // Template list/search (used by CheckItemListAction)
    List<ChkTmplDto> search(Map<String, Object> params);
    int count(Map<String, Object> params);

    // Template CRUD
    ChkTmplDto findById(int templateId);
    void insert(ChkTmplDto dto);
    void update(ChkTmplDto dto);
    void delete(int templateId);

    // Template utilities
    void copyTemplate(int templateId);
    void swapOrder(@Param("id1") int id1, @Param("id2") int id2);

    // Items
    List<ChkItemDto> findItemsByTemplate(@Param("templateId") int templateId);
    void insertItem(ChkItemDto dto);
    void deleteItemsByLevel(@Param("templateId") int templateId, @Param("itemLevel") int itemLevel);
}
