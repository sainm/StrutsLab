package com.strutslab.service.counter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.CapaDao;
import com.strutslab.dao.IncidentDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.CapaDto;
import com.strutslab.form.counter.CapaForm;

public class CapaService {

    public CapaDto findByIncidentNo(String incidentNo) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CapaDao dao = sqlSession.getMapper(CapaDao.class);
            return dao.findByIncidentNo(incidentNo);
        }
    }

    public Map<String, Object> loadIncidentInfo(String incidentNo) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            return sqlSession.selectOne(
                    "com.strutslab.dao.IncidentDao.findByIdSimple", incidentNo);
        }
    }

    public void save(CapaForm form) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CapaDao dao = sqlSession.getMapper(CapaDao.class);

            CapaDto dto = new CapaDto();
            dto.setIncidentNo(form.getIncidentNo());
            dto.setWhy1(form.getWhy1());
            dto.setWhy2(form.getWhy2());
            dto.setWhy3(form.getWhy3());
            dto.setWhy4(form.getWhy4());
            dto.setWhy5(form.getWhy5());
            dto.setCountermeasure(form.getCountermeasure());
            dto.setVerifyMethod(form.getVerifyMethod());
            dto.setVerifyDate(form.getVerifyDate());
            dto.setStatus("申請中");

            dao.insert(dto);

            // Update incident status
            IncidentDao incDao = sqlSession.getMapper(IncidentDao.class);
            Map<String, Object> params = new HashMap<>();
            params.put("newStatus", "再発防止");
            params.put("incidentNos", Arrays.asList(form.getIncidentNo()));
            incDao.bulkUpdateStatus(params);

            sqlSession.commit();
        }
    }
}
