package com.strutslab.action.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.strutslab.form.common.LoginForm;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dao.EmpDao;
import com.strutslab.dto.EmpDto;
import org.apache.ibatis.session.SqlSession;
import java.security.MessageDigest;

public class LoginAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        LoginForm loginForm = (LoginForm) form;
        String loginId = loginForm.getLoginId();
        String password = loginForm.getPassword();

        if (loginId == null || loginId.isEmpty()) {
            return mapping.findForward("input");
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            EmpDto emp = dao.findByLoginId(loginId);

            if (emp == null) {
                request.setAttribute("errorMessage", "ログインIDまたはパスワードが正しくありません。");
                return mapping.findForward("input");
            }

            String hash = sha256(password);
            if (!hash.equals(emp.getPasswordHash())) {
                request.setAttribute("errorMessage", "ログインIDまたはパスワードが正しくありません。");
                return mapping.findForward("input");
            }

            if (emp.getIsLocked() != null && emp.getIsLocked()) {
                request.setAttribute("errorMessage", "アカウントがロックされています。");
                return mapping.findForward("input");
            }

            HttpSession session = request.getSession();
            session.setAttribute("loginUser", emp.getName());
            session.setAttribute("empNo", emp.getEmpNo());
            session.setAttribute("deptCode", emp.getDeptCode());

            return mapping.findForward("success");
        }
    }

    private String sha256(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(s.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
