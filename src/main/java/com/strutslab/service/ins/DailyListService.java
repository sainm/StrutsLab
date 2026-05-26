package com.strutslab.service.ins;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.EmpDao;
import com.strutslab.dao.ExecDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EmpDto;
import com.strutslab.dto.ExecResultDto;

public class DailyListService {

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyyMMdd");

    public String getDefaultTargetDate() {
        return DATE_FMT.format(new Date());
    }

    public List<ExecResultDto> search(String targetDate, String statusFilter, String personCode) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            Map<String, Object> params = new HashMap<>();
            params.put("targetDate", targetDate);

            if (statusFilter != null && !"全部".equals(statusFilter)) {
                params.put("statusFilter", statusFilter);
            }
            if (personCode != null && !personCode.isEmpty()) {
                params.put("personCode", personCode);
            }

            return dao.findByDate(params);
        }
    }

    public List<EmpDto> loadEmpList() {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            return dao.findAll();
        }
    }
}
