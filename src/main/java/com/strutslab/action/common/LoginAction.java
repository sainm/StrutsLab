package com.strutslab.action.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.strutslab.form.common.LoginForm;
import com.strutslab.service.common.AuthService;
import com.strutslab.service.common.AuthService.AuthResult;

public class LoginAction extends Action {

    private final AuthService authService = new AuthService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        LoginForm loginForm = (LoginForm) form;
        String loginId = loginForm.getLoginId();
        String password = loginForm.getPassword();

        if (loginId == null || loginId.isEmpty()) {
            return mapping.findForward("input");
        }

        AuthResult result = authService.authenticate(loginId, password);
        MessageResources resources = getResources(request);

        if (result.errorKey != null) {
            request.setAttribute("errorMessage", resources.getMessage(result.errorKey));
            return mapping.findForward("input");
        }

        request.getSession().invalidate();
        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", result.emp.getName());
        session.setAttribute("empNo", result.emp.getEmpNo());
        session.setAttribute("deptCode", result.emp.getDeptCode());

        return mapping.findForward("success");
    }
}
