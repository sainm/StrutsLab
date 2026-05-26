package com.strutslab.service.ins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.ExecDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ExecItemResultDto;
import com.strutslab.dto.ExecResultDto;

public class ExecDetailService {

    public ExecResultDto findResult(int resultId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            return dao.findById(resultId);
        }
    }

    public List<ExecItemResultDto> findItems(int resultId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            return dao.findItemsByResultId(resultId);
        }
    }

    public void submitModify(int resultId, String modifyReason) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            dao.updateApprovalStatus(resultId, "申請中", modifyReason);
            sqlSession.commit();
        }
    }
}
