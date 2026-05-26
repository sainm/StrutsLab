package com.strutslab.action.mst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.form.mst.EqpSearchForm;
import com.strutslab.service.mst.EqpListService;
import com.strutslab.service.mst.EqpListService.SearchResult;

public class EqpListAction extends Action {

    private final EqpListService service = new EqpListService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        EqpSearchForm searchForm = (EqpSearchForm) form;
        HttpSession session = request.getSession();
        session.setAttribute("eqpSearchForm", searchForm);

        String paramCsv = request.getParameter("csv");
        String paramDelete = request.getParameter("delete");

        if ("true".equals(paramCsv)) {
            service.exportCsv(response, searchForm);
            return null;
        }

        if ("true".equals(paramDelete)) {
            service.deleteSelected(searchForm.getSelectedItems());
            searchForm.setPage(1);
        }

        SearchResult result = service.search(searchForm);

        request.setAttribute("eqpList", result.list);
        request.setAttribute("currentPage", result.currentPage);
        request.setAttribute("totalPages", result.totalPages);
        request.setAttribute("pagingUrl", service.buildPagingUrl(request.getContextPath(), searchForm));

        String editParam = request.getParameter("edit");
        if ("new".equals(editParam)) {
            return mapping.findForward("new");
        }
        if (request.getParameter("editCode") != null) {
            return mapping.findForward("edit");
        }

        return mapping.findForward("success");
    }
}
