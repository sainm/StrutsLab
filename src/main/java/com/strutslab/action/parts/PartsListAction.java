package com.strutslab.action.parts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.form.parts.PartsSearchForm;
import com.strutslab.service.parts.PartsService;
import com.strutslab.service.parts.PartsService.SearchResult;

public class PartsListAction extends Action {

    private final PartsService service = new PartsService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PartsSearchForm searchForm = (PartsSearchForm) form;
        HttpSession session = request.getSession();
        session.setAttribute("partsSearchForm", searchForm);

        if ("true".equals(request.getParameter("csv"))) {
            service.exportCsv(response, searchForm.getEquipmentType(),
                    searchForm.getPartType(), searchForm.getStockStatus(), searchForm.getKeyword());
            return null;
        }

        SearchResult result = service.search(
                searchForm.getEquipmentType(), searchForm.getPartType(),
                searchForm.getStockStatus(), searchForm.getKeyword(), searchForm.getPage());

        Map<String, String> stockBadgeMap = service.computeStockBadges(result.list);
        request.setAttribute("stockBadgeMap", stockBadgeMap);
        request.setAttribute("partsList", result.list);
        request.setAttribute("currentPage", result.currentPage);
        request.setAttribute("totalPages", result.totalPages);
        request.setAttribute("pagingUrl", service.buildPagingUrl(request.getContextPath(),
                searchForm.getEquipmentType(), searchForm.getPartType(),
                searchForm.getStockStatus(), searchForm.getKeyword()));

        if (request.getParameter("editCode") != null) {
            return mapping.findForward("edit");
        }

        return mapping.findForward("success");
    }
}
