package com.strutslab.service.ins;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.ChkItemDao;
import com.strutslab.dao.EqpDao;
import com.strutslab.dao.PlanDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ChkTmplDto;
import com.strutslab.dto.EqpDto;
import com.strutslab.dto.PlanDto;
import com.strutslab.form.ins.PlanWizardForm;

public class PlanWizardService {

    public List<EqpDto> loadEquipmentList() {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            return dao.findAll();
        }
    }

    public EqpDto loadEquipment(String eqpCode) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            return dao.findById(eqpCode);
        }
    }

    public List<ChkTmplDto> loadTemplates(String equipmentType) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);
            Map<String, Object> params = new HashMap<>();
            if (equipmentType != null) {
                params.put("equipmentType", equipmentType);
            }
            return dao.search(params);
        }
    }

    public ChkTmplDto loadTemplate(int templateId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);
            return dao.findById(templateId);
        }
    }

    public void savePlan(PlanWizardForm wf) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PlanDao dao = sqlSession.getMapper(PlanDao.class);

            PlanDto dto = new PlanDto();
            String planDate = wf.getPlanDate();

            String fiscalYear;
            if (planDate != null && planDate.length() >= 8) {
                int yyyy = Integer.parseInt(planDate.substring(0, 4));
                int mm = Integer.parseInt(planDate.substring(4, 6));
                fiscalYear = String.valueOf(mm >= 4 ? yyyy : yyyy - 1);
            } else {
                int year = LocalDate.now().getYear();
                int month = LocalDate.now().getMonthValue();
                fiscalYear = String.valueOf(month >= 4 ? year : year - 1);
            }

            dto.setFiscalYear(fiscalYear);
            dto.setEquipmentCode(wf.getSelectedEqpCode());
            dto.setTemplateId(wf.getSelectedTmplId());
            dto.setPlannedDate(planDate);
            dto.setTeamCode(wf.getTeamCode());
            dto.setPersonCode(wf.getPersonCode());
            dto.setNote(wf.getNote());
            dto.setStatus("予定");
            dto.setIsLocked(false);

            dao.insert(dto);
            sqlSession.commit();
        }
    }

    public String getEquipmentType(String eqpCode) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            EqpDto eqp = dao.findById(eqpCode);
            return eqp != null ? eqp.getEquipmentType() : null;
        }
    }
}
