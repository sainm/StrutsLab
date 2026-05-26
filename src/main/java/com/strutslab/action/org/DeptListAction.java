package com.strutslab.action.org;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.dto.DeptDto;
import com.strutslab.form.org.DeptSearchForm;
import com.strutslab.service.org.OrgService;

public class DeptListAction extends Action {

    private final OrgService service = new OrgService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        DeptSearchForm searchForm = (DeptSearchForm) form;
        HttpSession session = request.getSession();
        session.setAttribute("deptSearchForm", searchForm);

        if ("true".equals(request.getParameter("csv"))) {
            service.exportDeptCsv(response, searchForm.getDeptCode(), searchForm.getDeptName());
            return null;
        }

        List<DeptDto> allDepts = service.searchDepts(searchForm.getDeptCode(), searchForm.getDeptName());
        List<DeptDto> tree = service.buildDeptTree(allDepts);

        request.setAttribute("deptTree", tree);
        request.setAttribute("allDepts", allDepts);

        return mapping.findForward("success");
    }
}
