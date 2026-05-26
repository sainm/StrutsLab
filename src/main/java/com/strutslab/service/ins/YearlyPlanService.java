package com.strutslab.service.ins;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.EqpDao;
import com.strutslab.dao.PlanDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EqpDto;
import com.strutslab.dto.PlanCellDto;
import com.strutslab.dto.PlanDto;

public class YearlyPlanService {

    private static final int[] MONTH_NUMS = {4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3};

    public String getDefaultFiscalYear() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        return String.valueOf(month >= 4 ? year : year - 1);
    }

    public List<EqpDto> loadFilteredEquipment(String equipmentType) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            List<EqpDto> all = dao.findAll();
            List<EqpDto> filtered = new ArrayList<>();
            for (EqpDto eqp : all) {
                if (equipmentType == null || equipmentType.isEmpty()
                        || equipmentType.equals(eqp.getEquipmentType())) {
                    filtered.add(eqp);
                }
            }
            return filtered;
        }
    }

    public Map<String, Map<Integer, PlanCellDto>> buildYearlyMatrix(
            List<EqpDto> eqpList, String fiscalYear) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PlanDao dao = sqlSession.getMapper(PlanDao.class);
            Map<String, Map<Integer, PlanCellDto>> matrix = new LinkedHashMap<>();

            for (EqpDto eqp : eqpList) {
                Map<Integer, PlanCellDto> monthMap = new HashMap<>();
                for (int m : MONTH_NUMS) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("fiscalYear", fiscalYear);
                    params.put("equipmentCode", eqp.getEquipmentCode());
                    params.put("month", String.format("%02d", m));

                    int plannedCount = dao.countMonthly(params);
                    PlanCellDto cell = new PlanCellDto();
                    cell.setPlannedCount(plannedCount);
                    cell.setActualCount(0);
                    monthMap.put(m, cell);
                }
                matrix.put(eqp.getEquipmentCode(), monthMap);
            }
            return matrix;
        }
    }

    public void lockYear(String fiscalYear) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PlanDao dao = sqlSession.getMapper(PlanDao.class);
            dao.lockYear(fiscalYear);
            sqlSession.commit();
        }
    }

    public void unlockYear(String fiscalYear) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PlanDao dao = sqlSession.getMapper(PlanDao.class);
            dao.unlockYear(fiscalYear);
            sqlSession.commit();
        }
    }

    public void exportCsv(HttpServletResponse response, String fiscalYear) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PlanDao dao = sqlSession.getMapper(PlanDao.class);
            List<PlanDto> list = dao.findByYear(fiscalYear);

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"yearly_plan_" + fiscalYear + ".csv\"");

            try (PrintWriter pw = response.getWriter()) {
                pw.write('﻿');
                pw.println("計画ID,年度,設備コード,設備名,テンプレートID,予定日,担当班,担当者,状態,ロック,備考");
                for (PlanDto p : list) {
                    StringBuilder line = new StringBuilder();
                    appendCsvField(line, String.valueOf(p.getPlanId()));
                    appendCsvField(line, p.getFiscalYear());
                    appendCsvField(line, p.getEquipmentCode());
                    appendCsvField(line, p.getEquipmentName());
                    appendCsvField(line, String.valueOf(p.getTemplateId()));
                    appendCsvField(line, p.getPlannedDate());
                    appendCsvField(line, p.getTeamCode());
                    appendCsvField(line, p.getPersonCode());
                    appendCsvField(line, p.getStatus());
                    appendCsvField(line, p.getIsLocked() ? "ロック" : "");
                    appendCsvField(line, p.getNote());
                    pw.println(line.toString());
                }
            }
        }
    }

    private void appendCsvField(StringBuilder sb, String value) {
        if (sb.length() > 0) sb.append(',');
        if (value == null) return;
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            sb.append('"').append(value.replace("\"", "\"\"")).append('"');
        } else {
            sb.append(value);
        }
    }
}
