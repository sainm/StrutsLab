package com.strutslab.action.org;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dao.DeptDao;
import com.strutslab.dao.EmpDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.DeptDto;
import com.strutslab.dto.EmpDto;
import com.strutslab.form.org.EmpForm;

public class EmpSaveAction extends DispatchAction {

    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, String name)
            throws Exception {
        if ("new".equals(name)) {
            return newMethod(mapping, form, request, response);
        }
        return super.dispatchMethod(mapping, form, request, response, name);
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EmpForm empForm = (EmpForm) form;
        String empNo = request.getParameter("empNo");

        if (empNo != null && !empNo.isEmpty()) {
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                EmpDao dao = sqlSession.getMapper(EmpDao.class);
                EmpDto dto = dao.findById(empNo);
                if (dto != null) {
                    empForm.setEmpNo(dto.getEmpNo());
                    empForm.setName(dto.getName());
                    empForm.setNameKana(dto.getNameKana());
                    empForm.setBirthDate(dto.getBirthDate());
                    empForm.setJoinDate(dto.getJoinDate());
                    empForm.setDeptCode(dto.getDeptCode());
                    empForm.setPosition(dto.getPosition());
                    empForm.setAssignDate(dto.getAssignDate());
                    empForm.setInspectionRank(dto.getInspectionRank());
                    empForm.setInspectionCertDate(dto.getInspectionCertDate());
                    empForm.setInspectionCertExpire(dto.getInspectionCertExpire());
                    empForm.setLoginId(dto.getLoginId());
                }
            }
        }
        return mapping.findForward("success");
    }

    public ActionForward newMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EmpForm empForm = (EmpForm) form;
        empForm.setEmpNo(null);
        empForm.setName(null);
        empForm.setNameKana(null);
        empForm.setBirthDate(null);
        empForm.setJoinDate(null);
        empForm.setDeptCode(null);
        empForm.setPosition(null);
        empForm.setAssignDate(null);
        empForm.setQualifications(null);
        empForm.setInspectionRank(null);
        empForm.setInspectionCertDate(null);
        empForm.setInspectionCertExpire(null);
        empForm.setLoginId(null);
        empForm.setPassword(null);
        empForm.setPasswordConfirm(null);
        return mapping.findForward("success");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EmpForm empForm = (EmpForm) form;

        // --- Validate ---
        String errorMsg = validate(empForm, request);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
            return mapping.getInputForward();
        }

        // Check cert expire is future
        if (empForm.getInspectionCertExpire() != null && !empForm.getInspectionCertExpire().isEmpty()) {
            String today = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
            if (empForm.getInspectionCertExpire().compareTo(today) < 0) {
                request.setAttribute("errorMessage", "点検員認定の有効期限は過去日付にできません。");
                return mapping.getInputForward();
            }
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao empDao = sqlSession.getMapper(EmpDao.class);

            // Check loginId uniqueness (excluding current user)
            if (empForm.getLoginId() != null && !empForm.getLoginId().isEmpty()) {
                EmpDto existing = empDao.findByLoginId(empForm.getLoginId());
                if (existing != null && !existing.getEmpNo().equals(empForm.getEmpNo())) {
                    request.setAttribute("errorMessage", "ログインID「" + empForm.getLoginId() + "」は既に使用されています。");
                    return mapping.getInputForward();
                }
            }

            boolean isUpdate = empForm.getEmpNo() != null && !empForm.getEmpNo().isEmpty()
                    && empDao.findById(empForm.getEmpNo()) != null;

            // Convert to DTO
            EmpDto dto = new EmpDto();
            if (isUpdate) {
                dto.setEmpNo(empForm.getEmpNo());
            } else {
                String newNo = generateEmpNo(empDao);
                dto.setEmpNo(newNo);
            }
            dto.setName(empForm.getName());
            dto.setNameKana(empForm.getNameKana());
            dto.setBirthDate(empForm.getBirthDate());
            dto.setJoinDate(empForm.getJoinDate());
            dto.setDeptCode(empForm.getDeptCode());
            dto.setPosition(empForm.getPosition());
            dto.setAssignDate(empForm.getAssignDate());
            dto.setInspectionRank(empForm.getInspectionRank());
            dto.setInspectionCertDate(empForm.getInspectionCertDate());
            dto.setInspectionCertExpire(empForm.getInspectionCertExpire());
            dto.setLoginId(empForm.getLoginId());

            if (empForm.getPassword() != null && !empForm.getPassword().isEmpty()) {
                dto.setPasswordHash(hashPassword(empForm.getPassword()));
            } else if (isUpdate) {
                EmpDto existing = empDao.findById(empForm.getEmpNo());
                if (existing != null) {
                    dto.setPasswordHash(existing.getPasswordHash());
                }
            }

            if (isUpdate) {
                // Preserve locked status
                EmpDto existing = empDao.findById(empForm.getEmpNo());
                if (existing != null) {
                    dto.setIsLocked(existing.getIsLocked());
                }
                empDao.update(dto);
            } else {
                dto.setIsLocked(false);
                empDao.insert(dto);
            }

            sqlSession.commit();
        }

        return mapping.findForward("success");
    }

    private String generateEmpNo(EmpDao dao) {
        List<EmpDto> all = dao.findAll();
        int maxNum = 0;
        for (EmpDto e : all) {
            String no = e.getEmpNo();
            if (no != null && no.startsWith("EMP-")) {
                try {
                    int num = Integer.parseInt(no.substring(4));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException ex) { }
            }
        }
        return String.format("EMP-%04d", maxNum + 1);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    private String validate(EmpForm form, HttpServletRequest request) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            return "氏名は必須です。";
        }

        // Validate kana is katakana
        if (form.getNameKana() != null && !form.getNameKana().isEmpty()) {
            if (!form.getNameKana().matches("[\\u30A0-\\u30FF\\u3000\\s]+")) {
                return "氏名カナはカタカナで入力してください。";
            }
        }

        if (form.getLoginId() == null || form.getLoginId().isEmpty()) {
            return "ログインIDは必須です。";
        }

        boolean isNew = form.getEmpNo() == null || form.getEmpNo().isEmpty();
        if (isNew && (form.getPassword() == null || form.getPassword().isEmpty())) {
            return "パスワードは必須です。";
        }

        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            if (form.getPassword().length() < 6) {
                return "パスワードは6文字以上で入力してください。";
            }
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                return "パスワードと確認用パスワードが一致しません。";
            }
        }

        if (form.getInspectionCertExpire() != null && !form.getInspectionCertExpire().isEmpty()) {
            if (form.getInspectionCertDate() == null || form.getInspectionCertDate().isEmpty()) {
                return "認定日を入力してください。";
            }
        }

        return null;
    }
}
