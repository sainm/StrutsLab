package com.strutslab.dao;

import java.util.List;
import java.util.Map;

import com.strutslab.dto.CounterDetailDto;
import com.strutslab.dto.CounterDto;

public interface CounterDao {
    List<CounterDto> search(Map<String, Object> params);
    int count(Map<String, Object> params);
    CounterDto findById(String orderNo);
    void insert(CounterDto dto);
    void update(CounterDto dto);
    void insertDetail(CounterDetailDto d);
    void updateDetail(CounterDetailDto d);
    void deleteDetail(int detailId);
    List<CounterDetailDto> findDetailsByOrderNo(String orderNo);
    void updateDetailStatus(int detailId, String status);
    boolean areAllDetailsComplete(String orderNo);
}
