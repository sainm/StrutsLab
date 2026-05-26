package com.strutslab.action.ins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.form.ins.DailyForm;
import com.strutslab.service.ins.DailyListService;

public class DailyListAction extends Action {

    private final DailyListService service = new DailyListService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        DailyForm dailyForm = (DailyForm) form;

        String targetDate = dailyForm.getTargetDate();
        if (targetDate == null || targetDate.isEmpty()) {
            targetDate = service.getDefaultTargetDate();
            dailyForm.setTargetDate(targetDate);
        }

        String statusFilter = dailyForm.getStatusFilter();
        if (statusFilter == null || statusFilter.isEmpty()) {
            statusFilter = "全部";
        }

        request.setAttribute("dailyList",
                service.search(targetDate, statusFilter, dailyForm.getPersonCode()));
        request.setAttribute("targetDate", targetDate);
        request.setAttribute("empList", service.loadEmpList());

        return mapping.findForward("success");
    }
}
