package com.strutslab.service.counter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.CounterDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.CounterDetailDto;
import com.strutslab.dto.CounterDto;

public class CounterCreateService {

    public String getDefaultOrderDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public String save(CounterDto header, List<CounterDetailDto> details) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            String orderNo = generateOrderNo(dao);

            header.setOrderNo(orderNo);
            header.setStatus("未了");

            try {
                dao.insert(header);

                for (int i = 0; i < details.size(); i++) {
                    CounterDetailDto detail = details.get(i);
                    detail.setOrderNo(orderNo);
                    detail.setSeqNo(i + 1);
                    detail.setStatus("未了");
                    dao.insertDetail(detail);
                }

                sqlSession.commit();
            } catch (Exception e) {
                sqlSession.rollback();
                throw new RuntimeException("保存中にエラーが発生しました。", e);
            }

            return orderNo;
        }
    }

    private synchronized String generateOrderNo(CounterDao dao) {
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = "CTR-" + today + "-";

        HashMap<String, Object> params = new HashMap<>();
        params.put("dateFrom", today);
        params.put("dateTo", today);
        List<CounterDto> existing = dao.search(params);

        int maxSeq = 0;
        for (CounterDto d : existing) {
            String on = d.getOrderNo();
            if (on != null && on.startsWith(prefix)) {
                try {
                    int seq = Integer.parseInt(on.substring(prefix.length()));
                    if (seq > maxSeq) maxSeq = seq;
                } catch (NumberFormatException e) { }
            }
        }
        return prefix + String.format("%03d", maxSeq + 1);
    }
}
