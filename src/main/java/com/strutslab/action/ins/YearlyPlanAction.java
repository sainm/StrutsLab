package com.strutslab.action.ins;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.dao.EqpDao;
import com.strutslab.dao.PlanDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EqpDto;
import com.strutslab.dto.PlanCellDto;
import com.strutslab.dto.PlanDto;
import com.strutslab.form.ins.YearlyPlanForm;

public class YearlyPlanAction extends Action {

    private static final List<String> MONTHS = Arrays.asList(
        "4月","5月","6月","7月","8月","9月","10月","11月","12月","1月","2月","3月"
    );

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        YearlyPlanForm ypf = (YearlyPlanForm) form;
        String paramCsv = request.getParameter("csv");
        String paramLock = request.getParameter("lock");
        String paramUnlock = request.getParameter("unlock");

        // Set default fiscal year if empty
        if (ypf.getFiscalYear() == null || ypf.getFiscalYear().isEmpty()) {
            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            ypf.setFiscalYear(String.valueOf(month >= 4 ? year : year - 1));
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao eqpDao = sqlSession.getMapper(EqpDao.class);
            PlanDao planDao = sqlSession.getMapper(PlanDao.class);

            // Handle CSV export
            if ("true".equals(paramCsv)) {
                exportCsv(response, planDao, ypf.getFiscalYear());
                return null;
            }

            // Handle lock/unlock
            if ("true".equals(paramLock)) {
                planDao.lockYear(ypf.getFiscalYear());
                sqlSession.commit();
            }
            if ("true".equals(paramUnlock)) {
                planDao.unlockYear(ypf.getFiscalYear());
                sqlSession.commit();
            }

            // Load equipment list filtered by type
            Map<String, Object> eqpParams = new HashMap<>();
            if (ypf.getEquipmentType() != null && !ypf.getEquipmentType().isEmpty()) {
                eqpParams.put("equipmentType", ypf.getEquipmentType());
            }
            List<EqpDto> eqpList = eqpDao.findAll();

            // (Optional further filtering by equipmentType in-memory since findAll returns all)
            List<EqpDto> filteredEqp = new ArrayList<>();
            for (EqpDto eqp : eqpList) {
                if (ypf.getEquipmentType() == null || ypf.getEquipmentType().isEmpty()
                        || ypf.getEquipmentType().equals(eqp.getEquipmentType())) {
                    filteredEqp.add(eqp);
                }
            }

            // Build the yearly matrix
            // Map<equipmentCode, Map<monthNumber(1-12), PlanCellDto>>
            Map<String, Map<Integer, PlanCellDto>> yearlyMatrix = new LinkedHashMap<>();

            // Month numbers in Japanese fiscal year order: 4,5,6,7,8,9,10,11,12,1,2,3
            int[] monthNums = {4,5,6,7,8,9,10,11,12,1,2,3};

            for (EqpDto eqp : filteredEqp) {
                Map<Integer, PlanCellDto> monthMap = new HashMap<>();
                for (int m : monthNums) {
                    Map<String, Object> countParams = new HashMap<>();
                    countParams.put("fiscalYear", ypf.getFiscalYear());
                    countParams.put("equipmentCode", eqp.getEquipmentCode());
                    String monthStr = String.format("%02d", m);
                    countParams.put("month", monthStr);

                    int plannedCount = planDao.countMonthly(countParams);

                    PlanCellDto cell = new PlanCellDto();
                    cell.setPlannedCount(plannedCount);
                    cell.setActualCount(0); // actual counts would come from inspection_results join
                    monthMap.put(m, cell);
                }
                yearlyMatrix.put(eqp.getEquipmentCode(), monthMap);
            }

            request.setAttribute("yearlyMatrix", yearlyMatrix);
            request.setAttribute("months", MONTHS);
            request.setAttribute("eqpList", filteredEqp);
        }

        return mapping.findForward("success");
    }

    private void exportCsv(HttpServletResponse response, PlanDao planDao, String fiscalYear) throws Exception {
        List<PlanDto> list = planDao.findByYear(fiscalYear);

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

    private void appendCsvField(StringBuilder sb, String value) {
        if (sb.length() > 0) sb.append(',');
        if (value == null) {
            sb.append("");
            return;
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            sb.append('"').append(value.replace("\"", "\"\"")).append('"');
        } else {
            sb.append(value);
        }
    }
}
