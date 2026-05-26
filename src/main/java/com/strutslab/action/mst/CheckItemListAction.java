package com.strutslab.action.mst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.form.mst.CheckItemSearchForm;
import com.strutslab.service.mst.CheckItemListService;

public class CheckItemListAction extends Action {

    private final CheckItemListService service = new CheckItemListService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        CheckItemSearchForm searchForm = (CheckItemSearchForm) form;
        HttpSession session = request.getSession();
        session.setAttribute("checkItemSearchForm", searchForm);

        String paramCopy = request.getParameter("copy");
        String paramMoveUp = request.getParameter("moveUp");
        String paramMoveDown = request.getParameter("moveDown");

        if (paramCopy != null && !paramCopy.isEmpty()) {
            try {
                service.copyTemplate(Integer.parseInt(paramCopy));
            } catch (NumberFormatException e) {
                // ignore invalid param
            }
        }

        if (paramMoveUp != null && !paramMoveUp.isEmpty()) {
            try {
                service.moveUp(Integer.parseInt(paramMoveUp), searchForm);
            } catch (NumberFormatException e) {
                return mapping.findForward("success");
            }
        }

        if (paramMoveDown != null && !paramMoveDown.isEmpty()) {
            try {
                service.moveDown(Integer.parseInt(paramMoveDown), searchForm);
            } catch (NumberFormatException e) {
                return mapping.findForward("success");
            }
        }

        CheckItemListService.SearchResult result = service.search(searchForm);

        request.setAttribute("templateList", result.list);
        request.setAttribute("currentPage", result.currentPage);
        request.setAttribute("totalPages", result.totalPages);
        request.setAttribute("pagingUrl", service.buildPagingUrl(request.getContextPath(), searchForm));

        return mapping.findForward("success");
    }
}
