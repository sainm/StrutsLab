package com.strutslab.action.org;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dao.DeptDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.DeptDto;
import com.strutslab.form.org.DeptForm;

public class DeptSaveAction extends DispatchAction {

    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, String name)
            throws Exception {
        if ("new".equals(name)) {
            return newMethod(mapping, form, request, response);
        }
        return super.dispatchMethod(mapping, form, request, response, name);
    }

    /**
     * Load dept for edit (method=unspecified).
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        DeptForm deptForm = (DeptForm) form;
        String deptCode = request.getParameter("deptCode");

        if (deptCode != null && !deptCode.isEmpty()) {
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                DeptDao dao = sqlSession.getMapper(DeptDao.class);
                DeptDto dto = dao.findById(deptCode);
                if (dto != null) {
                    deptForm.setDeptCode(dto.getDeptCode());
                    deptForm.setDeptName(dto.getDeptName());
                    deptForm.setParentDeptCode(dto.getParentDeptCode());
                    deptForm.setParentDeptName(dto.getParentDeptName());
                    deptForm.setDeptLevel(dto.getDeptLevel());
                    deptForm.setDeptType(dto.getDeptType());
                    deptForm.setStartDate(dto.getStartDate());
                    deptForm.setEndDate(dto.getEndDate());
                    deptForm.setAddress(dto.getAddress());
                    deptForm.setTel(dto.getTel());
                }
            }
        }
        return mapping.findForward("success");
    }

    /**
     * New department blank form (method=new).
     */
    public ActionForward newMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        DeptForm deptForm = (DeptForm) form;
        deptForm.setDeptCode(null);
        deptForm.setDeptName(null);
        deptForm.setParentDeptCode(null);
        deptForm.setParentDeptName(null);
        deptForm.setDeptLevel(1);
        deptForm.setDeptType(null);
        deptForm.setStartDate(null);
        deptForm.setEndDate(null);
        deptForm.setAddress(null);
        deptForm.setTel(null);
        return mapping.findForward("success");
    }

    /**
     * Save department (insert or update).
     */
    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        DeptForm deptForm = (DeptForm) form;

        // Validation
        String errorMsg = validate(deptForm);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
            return mapping.getInputForward();
        }

        // Past-date movement warning (if end_date is set and is past)
        if (deptForm.getEndDate() != null && !deptForm.getEndDate().isEmpty()) {
            String today = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
            if (deptForm.getEndDate().compareTo(today) < 0) {
                request.setAttribute("warningMessage", "終了日が過去日付です。異動・廃止履歴として記録されます。");
            }
        }

        // Convert to DTO
        DeptDto dto = new DeptDto();
        dto.setDeptCode(deptForm.getDeptCode());
        dto.setDeptName(deptForm.getDeptName());
        dto.setParentDeptCode(deptForm.getParentDeptCode());
        dto.setDeptLevel(deptForm.getDeptLevel());
        dto.setDeptType(deptForm.getDeptType());
        dto.setStartDate(deptForm.getStartDate());
        dto.setEndDate(deptForm.getEndDate());
        dto.setAddress(deptForm.getAddress());
        dto.setTel(deptForm.getTel());

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            DeptDao dao = sqlSession.getMapper(DeptDao.class);

            boolean isUpdate = dto.getDeptCode() != null
                    && !dto.getDeptCode().isEmpty()
                    && dao.findById(dto.getDeptCode()) != null;

            if (isUpdate) {
                dao.update(dto);
            } else {
                if (dto.getDeptCode() == null || dto.getDeptCode().isEmpty()) {
                    dto.setDeptCode(generateNewCode(dao));
                }
                dao.insert(dto);
            }

            sqlSession.commit();
        }

        return mapping.findForward("success");
    }

    private String generateNewCode(DeptDao dao) {
        java.util.List<DeptDto> all = dao.findAll();
        int maxNum = 0;
        for (DeptDto d : all) {
            String code = d.getDeptCode();
            if (code != null) {
                try {
                    int num = Integer.parseInt(code.replaceAll("\\D", ""));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException e) {
                    // skip
                }
            }
        }
        return String.format("DEPT-%04d", maxNum + 1);
    }

    private String validate(DeptForm form) {
        if (form.getDeptName() == null || form.getDeptName().trim().isEmpty()) {
            return "部署名は必須です。";
        }
        if (form.getDeptName().length() > 100) {
            return "部署名は100文字以内で入力してください。";
        }
        if (form.getStartDate() == null || form.getStartDate().isEmpty()) {
            return "開始日は必須です。";
        }
        if (form.getDeptType() == null || form.getDeptType().isEmpty()) {
            return "部署種別は必須です。";
        }
        return null;
    }
}
