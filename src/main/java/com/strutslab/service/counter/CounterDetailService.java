package com.strutslab.service.counter;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.CounterDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.CounterDetailDto;
import com.strutslab.dto.CounterDto;

public class CounterDetailService {

    public CounterDto findOrder(String orderNo) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            return dao.findById(orderNo);
        }
    }

    public List<CounterDetailDto> findDetails(String orderNo) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            return dao.findDetailsByOrderNo(orderNo);
        }
    }

    public void completeDetail(String orderNo, CounterDetailDto detail,
            Double actualHours, String usedPartCode, Integer usedQuantity, String note) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);

            if (actualHours != null) detail.setActualHours(actualHours);
            if (usedPartCode != null && !usedPartCode.isEmpty()) {
                detail.setUsedPartCode(usedPartCode);
                detail.setUsedQuantity(usedQuantity);
            }
            if (note != null) detail.setNote(note);
            detail.setStatus("完了");

            dao.updateDetail(detail);

            if (dao.areAllDetailsComplete(orderNo)) {
                CounterDto order = dao.findById(orderNo);
                if (order != null) {
                    order.setStatus("完了");
                    dao.update(order);
                }
            }

            sqlSession.commit();
        }
    }
}
