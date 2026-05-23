package com.strutslab.action.ins;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dao.ChkItemDao;
import com.strutslab.dao.EqpDao;
import com.strutslab.dao.PlanDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ChkTmplDto;
import com.strutslab.dto.EqpDto;
import com.strutslab.dto.PlanDto;
import com.strutslab.form.ins.PlanWizardForm;

public class PlanWizardAction extends DispatchAction {

    public ActionForward step1(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PlanWizardForm wf = getWizardForm(request, form);

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao eqpDao = sqlSession.getMapper(EqpDao.class);
            List<EqpDto> eqpList = eqpDao.findAll();
            request.setAttribute("eqpList", eqpList);
        }

        wf.setStep(1);
        saveWizardForm(request, wf);
        return mapping.findForward("step1");
    }

    public ActionForward step2(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PlanWizardForm wf = getWizardForm(request, form);

        // Validate step1 selection
        if (wf.getSelectedEqpCode() == null || wf.getSelectedEqpCode().isEmpty()) {
            request.setAttribute("errorMessage", "設備を選択してください。");
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                EqpDao eqpDao = sqlSession.getMapper(EqpDao.class);
                request.setAttribute("eqpList", eqpDao.findAll());
            }
            wf.setStep(1);
            saveWizardForm(request, wf);
            return mapping.findForward("step1");
        }

        // Load equipment name and fetch applicable templates
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao eqpDao = sqlSession.getMapper(EqpDao.class);
            ChkItemDao chkDao = sqlSession.getMapper(ChkItemDao.class);
            EqpDto eqp = eqpDao.findById(wf.getSelectedEqpCode());
            if (eqp != null) {
                wf.setSelectedEqpName(eqp.getEquipmentName());
            }

            // Load templates for the equipment type
            Map<String, Object> tmplParams = new HashMap<>();
            if (eqp != null) {
                tmplParams.put("equipmentType", eqp.getEquipmentType());
            }
            List<ChkTmplDto> tmplList = chkDao.search(tmplParams);
            request.setAttribute("tmplList", tmplList);
        }

        wf.setStep(2);
        saveWizardForm(request, wf);
        return mapping.findForward("step2");
    }

    public ActionForward step3(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PlanWizardForm wf = getWizardForm(request, form);

        // Validate step2 selection
        if (wf.getSelectedTmplId() <= 0) {
            request.setAttribute("errorMessage", "点検テンプレートを選択してください。");
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                ChkItemDao chkDao = sqlSession.getMapper(ChkItemDao.class);
                Map<String, Object> tmplParams = new HashMap<>();
                String eqpType = getEquipmentTypeFromSession(request, wf);
                if (eqpType != null) {
                    tmplParams.put("equipmentType", eqpType);
                }
                request.setAttribute("tmplList", chkDao.search(tmplParams));
            }
            wf.setStep(2);
            saveWizardForm(request, wf);
            return mapping.findForward("step2");
        }

        // Load template name
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao chkDao = sqlSession.getMapper(ChkItemDao.class);
            ChkTmplDto tmpl = chkDao.findById(wf.getSelectedTmplId());
            if (tmpl != null) {
                wf.setSelectedTmplName(tmpl.getTemplateName());
            }
        }

        wf.setStep(3);
        saveWizardForm(request, wf);
        return mapping.findForward("step3");
    }

    public ActionForward confirm(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PlanWizardForm wf = getWizardForm(request, form);

        // Validate step3 fields
        StringBuilder errors = new StringBuilder();
        if (wf.getPlanDate() == null || wf.getPlanDate().isEmpty()) {
            errors.append("予定日を入力してください。");
        }
        if (wf.getTeamCode() == null || wf.getTeamCode().isEmpty()) {
            if (errors.length() > 0) errors.append("<br>");
            errors.append("担当班を選択してください。");
        }

        if (errors.length() > 0) {
            request.setAttribute("errorMessage", errors.toString());
            wf.setStep(3);
            saveWizardForm(request, wf);
            return mapping.findForward("step3");
        }

        wf.setStep(4);
        saveWizardForm(request, wf);
        return mapping.findForward("confirm");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PlanWizardForm wf = getWizardForm(request, form);

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PlanDao planDao = sqlSession.getMapper(PlanDao.class);

            PlanDto dto = new PlanDto();
            String planDate = wf.getPlanDate();

            // Determine fiscal year from plan date (YYYYMMDD -> fiscal year)
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

            planDao.insert(dto);
            sqlSession.commit();
        }

        // Clear wizard from session
        request.getSession().removeAttribute("planWizardForm");

        return mapping.findForward("success");
    }

    public ActionForward tempSave(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Form data is already in session, just redirect
        return mapping.findForward("success");
    }

    // ---- helpers ----

    private PlanWizardForm getWizardForm(HttpServletRequest request, ActionForm form) {
        HttpSession session = request.getSession();
        PlanWizardForm wf = (PlanWizardForm) session.getAttribute("planWizardForm");
        if (wf == null) {
            wf = (PlanWizardForm) form;
            session.setAttribute("planWizardForm", wf);
        } else {
            // Copy incoming form values into session-stored instance
            wf.setSelectedEqpCode(((PlanWizardForm) form).getSelectedEqpCode());
            wf.setSelectedTmplId(((PlanWizardForm) form).getSelectedTmplId());
            wf.setPlanDate(((PlanWizardForm) form).getPlanDate());
            wf.setTeamCode(((PlanWizardForm) form).getTeamCode());
            wf.setPersonCode(((PlanWizardForm) form).getPersonCode());
            wf.setNote(((PlanWizardForm) form).getNote());
        }
        return wf;
    }

    private void saveWizardForm(HttpServletRequest request, PlanWizardForm wf) {
        request.getSession().setAttribute("planWizardForm", wf);
    }

    private String getEquipmentTypeFromSession(HttpServletRequest request, PlanWizardForm wf) {
        if (wf.getSelectedEqpCode() != null && !wf.getSelectedEqpCode().isEmpty()) {
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                EqpDao eqpDao = sqlSession.getMapper(EqpDao.class);
                EqpDto eqp = eqpDao.findById(wf.getSelectedEqpCode());
                return eqp != null ? eqp.getEquipmentType() : null;
            }
        }
        return null;
    }
}
