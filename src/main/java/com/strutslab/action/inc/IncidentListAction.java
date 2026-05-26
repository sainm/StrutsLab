package com.strutslab.action.inc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.form.inc.IncidentSearchForm;
import com.strutslab.service.inc.IncidentListService;
import com.strutslab.service.inc.IncidentListService.SearchResult;

public class IncidentListAction extends Action {

    private final IncidentListService service = new IncidentListService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncidentSearchForm searchForm = (IncidentSearchForm) form;
        HttpSession session = request.getSession();

        if (request.getParameter("clear") != null) {
            searchForm.setIncDateFrom(null);
            searchForm.setIncDateTo(null);
            searchForm.setEquipmentType(null);
            searchForm.setIncidentType(null);
            searchForm.setStatus(null);
            searchForm.setSeverity(null);
            searchForm.setTeam(null);
            searchForm.setKeyword(null);
            searchForm.setPage(1);
            searchForm.setSelectedItems(null);
            searchForm.setBulkStatus(null);
        }

        if (request.getParameter("saveCondition") != null) {
            session.setAttribute("savedIncSearchCondition", cloneForm(searchForm));
            request.setAttribute("message", "検索条件を保存しました。");
        }

        if (request.getParameter("loadCondition") != null) {
            IncidentSearchForm saved = (IncidentSearchForm) session.getAttribute("savedIncSearchCondition");
            if (saved != null) {
                searchForm.setIncDateFrom(saved.getIncDateFrom());
                searchForm.setIncDateTo(saved.getIncDateTo());
                searchForm.setEquipmentType(saved.getEquipmentType());
                searchForm.setIncidentType(saved.getIncidentType());
                searchForm.setStatus(saved.getStatus());
                searchForm.setSeverity(saved.getSeverity());
                searchForm.setTeam(saved.getTeam());
                searchForm.setKeyword(saved.getKeyword());
            }
        }

        if ("true".equals(request.getParameter("csv"))) {
            service.exportCsv(response, searchForm.getIncDateFrom(), searchForm.getIncDateTo(),
                    searchForm.getEquipmentType(), searchForm.getIncidentType(),
                    searchForm.getStatus(), searchForm.getSeverity(),
                    searchForm.getTeam(), searchForm.getKeyword());
            return null;
        }

        if (request.getParameter("bulkUpdate") != null) {
            String[] selected = searchForm.getSelectedItems();
            String newStatus = searchForm.getBulkStatus();
            if (selected != null && selected.length > 0 && newStatus != null && !newStatus.isEmpty()) {
                service.bulkUpdateStatus(selected, newStatus);
            }
            searchForm.setPage(1);
        }

        SearchResult result = service.search(
                searchForm.getIncDateFrom(), searchForm.getIncDateTo(),
                searchForm.getEquipmentType(), searchForm.getIncidentType(),
                searchForm.getStatus(), searchForm.getSeverity(),
                searchForm.getTeam(), searchForm.getKeyword(), searchForm.getPage());

        request.setAttribute("incidentList", result.list);
        request.setAttribute("currentPage", result.currentPage);
        request.setAttribute("totalPages", result.totalPages);
        request.setAttribute("pagingUrl", service.buildPagingUrl(request.getContextPath(),
                searchForm.getIncDateFrom(), searchForm.getIncDateTo(),
                searchForm.getEquipmentType(), searchForm.getIncidentType(),
                searchForm.getStatus(), searchForm.getSeverity(),
                searchForm.getTeam(), searchForm.getKeyword()));
        request.setAttribute("equipmentTypeList", service.getEquipmentTypes());

        session.setAttribute("incidentSearchForm", searchForm);
        return mapping.findForward("success");
    }

    private IncidentSearchForm cloneForm(IncidentSearchForm src) {
        IncidentSearchForm dst = new IncidentSearchForm();
        dst.setIncDateFrom(src.getIncDateFrom());
        dst.setIncDateTo(src.getIncDateTo());
        dst.setEquipmentType(src.getEquipmentType());
        dst.setIncidentType(src.getIncidentType());
        dst.setStatus(src.getStatus());
        dst.setSeverity(src.getSeverity());
        dst.setTeam(src.getTeam());
        dst.setKeyword(src.getKeyword());
        return dst;
    }
}
