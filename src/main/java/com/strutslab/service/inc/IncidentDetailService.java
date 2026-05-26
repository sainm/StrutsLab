package com.strutslab.service.inc;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.IncidentDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.IncidentDto;
import com.strutslab.dto.TimelineDto;

public class IncidentDetailService {

    public IncidentDto findById(String incidentNo) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            return dao.findById(incidentNo);
        }
    }

    public List<TimelineDto> getTimeline(String incidentNo) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            return dao.getTimeline(incidentNo);
        }
    }

    public void transition(String incidentNo, String newStatus, String fieldToUpdate,
            String fieldValue, String actionUser, String actionContent) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);

            IncidentDto dto = dao.findById(incidentNo);
            if (dto == null) return;

            String oldStatus = dto.getStatus();
            dto.setStatus(newStatus);

            if ("cause".equals(fieldToUpdate)) {
                dto.setCause(fieldValue);
            } else if ("counterDetail".equals(fieldToUpdate)) {
                dto.setCounterDetail(fieldValue);
            }

            dao.update(dto);

            TimelineDto tl = new TimelineDto();
            tl.setIncidentNo(incidentNo);
            tl.setActionDatetime(new Timestamp(System.currentTimeMillis()));
            tl.setActionUser(actionUser);
            tl.setActionContent(actionContent);
            tl.setStatusFrom(oldStatus);
            tl.setStatusTo(newStatus);
            dao.insertTimeline(tl);

            sqlSession.commit();
        }
    }
}
