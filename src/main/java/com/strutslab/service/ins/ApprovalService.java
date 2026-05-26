package com.strutslab.service.ins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.EmpDao;
import com.strutslab.dao.ExecDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EmpDto;
import com.strutslab.dto.ExecResultDto;

public class ApprovalService {

    public List<ExecResultDto> search(String dateFrom, String dateTo, String team, String status) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            Map<String, Object> params = new HashMap<>();
            if (dateFrom != null && !dateFrom.isEmpty()) params.put("dateFrom", dateFrom);
            if (dateTo != null && !dateTo.isEmpty()) params.put("dateTo", dateTo);
            if (team != null && !team.isEmpty()) params.put("team", team);
            if (status != null && !status.isEmpty()) params.put("status", status);

            return dao.findPendingApprovals(params);
        }
    }

    public int countPending(String dateFrom, String dateTo, String team, String status) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            Map<String, Object> params = new HashMap<>();
            if (dateFrom != null && !dateFrom.isEmpty()) params.put("dateFrom", dateFrom);
            if (dateTo != null && !dateTo.isEmpty()) params.put("dateTo", dateTo);
            if (team != null && !team.isEmpty()) params.put("team", team);
            if (status != null && !status.isEmpty()) params.put("status", status);

            return dao.countPendingApprovals(params);
        }
    }

    public void bulkApprove(int[] resultIds) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            dao.bulkApprove(resultIds);
            sqlSession.commit();
        }
    }

    public void bulkReject(int[] resultIds, String reason) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            dao.bulkReject(resultIds, reason);
            sqlSession.commit();
        }
    }

    public List<EmpDto> loadTeamList() {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            return dao.findAll();
        }
    }
}
