package com.strutslab.action.org;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.form.org.EmpSearchForm;
import com.strutslab.service.org.OrgService;
import com.strutslab.service.org.OrgService.EmpSearchResult;

public class EmpListAction extends Action {

    private final OrgService service = new OrgService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        EmpSearchForm searchForm = (EmpSearchForm) form;
        HttpSession session = request.getSession();
        session.setAttribute("empSearchForm", searchForm);

        if ("true".equals(request.getParameter("csv"))) {
            service.exportEmpCsv(response, searchForm.getDeptCode(),
                    searchForm.getPosition(), searchForm.getYearFrom(), searchForm.getYearTo());
            return null;
        }

        if ("true".equals(request.getParameter("lock")) || "true".equals(request.getParameter("unlock"))) {
            String[] selected = searchForm.getQualifications();
            if (selected != null) {
                service.lockUnlockEmps(selected, "true".equals(request.getParameter("lock")));
            }
        }

        String[] quals = request.getParameterValues("qualifications");

        EmpSearchResult result = service.searchEmps(
                searchForm.getDeptCode(), searchForm.getPosition(),
                searchForm.getYearFrom(), searchForm.getYearTo(), quals, searchForm.getPage());

        request.setAttribute("empList", result.list);
        request.setAttribute("currentPage", result.currentPage);
        request.setAttribute("totalPages", result.totalPages);
        request.setAttribute("pagingUrl", service.buildEmpPagingUrl(request.getContextPath(),
                searchForm.getDeptCode(), searchForm.getPosition(),
                searchForm.getYearFrom(), searchForm.getYearTo()));

        if (request.getParameter("editCode") != null) {
            return mapping.findForward("edit");
        }

        return mapping.findForward("success");
    }
}
