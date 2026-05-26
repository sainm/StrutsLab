package com.strutslab.action.counter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.strutslab.form.counter.CounterSearchForm;
import com.strutslab.service.counter.CounterListService;

public class CounterListAction extends Action {

    private final CounterListService service = new CounterListService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        CounterSearchForm searchForm = (CounterSearchForm) form;

        if (request.getParameter("csv") != null) {
            service.exportCsv(response, searchForm.getDateFrom(), searchForm.getDateTo(),
                    searchForm.getPerson(), searchForm.getStatus(), searchForm.getPriority());
            return null;
        }

        if (request.getParameter("bulkUpdateStatus") != null) {
            int[] selected = searchForm.getSelectedItems();
            String newStatus = request.getParameter("newStatus");
            if (selected == null || selected.length == 0) {
                ActionMessages errors = new ActionMessages();
                errors.add("selectedItems", new ActionMessage("errors.required", "更新対象"));
                saveErrors(request, errors);
            } else if (newStatus == null || newStatus.isEmpty()) {
                ActionMessages errors = new ActionMessages();
                errors.add("newStatus", new ActionMessage("errors.required", "ステータス"));
                saveErrors(request, errors);
            } else {
                service.bulkUpdateDetailStatus(selected, newStatus);
                ActionMessages messages = new ActionMessages();
                messages.add("message", new ActionMessage("label.update", selected.length + "件更新しました"));
                saveMessages(request, messages);
            }
        }

        java.util.List<com.strutslab.dto.CounterDto> orderList = service.search(
                searchForm.getDateFrom(), searchForm.getDateTo(),
                searchForm.getPerson(), searchForm.getStatus(), searchForm.getPriority());
        int totalCount = service.count(searchForm.getDateFrom(), searchForm.getDateTo(),
                searchForm.getPerson(), searchForm.getStatus(), searchForm.getPriority());

        Map<String, Integer> detailCounts = new HashMap<>();
        Map<String, Integer> completeCounts = new HashMap<>();
        service.computeCompletionStats(orderList, detailCounts, completeCounts);

        request.setAttribute("orderList", orderList);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("detailCounts", detailCounts);
        request.setAttribute("completeCounts", completeCounts);

        return mapping.findForward("success");
    }
}
