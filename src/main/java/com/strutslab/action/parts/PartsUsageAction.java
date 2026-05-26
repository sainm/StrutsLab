package com.strutslab.action.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.dto.PartsUsageDto;
import com.strutslab.form.parts.PartsUsageSearchForm;
import com.strutslab.service.parts.PartsUsageService;
import com.strutslab.service.parts.PartsUsageService.UsageResult;

public class PartsUsageAction extends Action {

    private final PartsUsageService service = new PartsUsageService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PartsUsageSearchForm searchForm = (PartsUsageSearchForm) form;

        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null) {
            try { page = Integer.parseInt(pageStr); } catch (NumberFormatException e) { }
        }

        UsageResult result = service.search(
                searchForm.getDateFrom(), searchForm.getDateTo(),
                searchForm.getEquipmentType(), searchForm.getPartCode(), page);

        request.setAttribute("usageList", result.list);
        request.setAttribute("currentPage", result.currentPage);
        request.setAttribute("totalPages", result.totalPages);
        request.setAttribute("pagingUrl", service.buildPagingUrl(request.getContextPath(),
                searchForm.getDateFrom(), searchForm.getDateTo(),
                searchForm.getEquipmentType(), searchForm.getPartCode()));

        return mapping.findForward("success");
    }
}
