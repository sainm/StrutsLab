package com.strutslab.action.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.form.report.ReportForm;
import com.strutslab.service.report.ReportService;

public class SummaryReportAction extends Action {

    private static final SimpleDateFormat MONTH_FMT = new SimpleDateFormat("yyyyMM");
    private final ReportService service = new ReportService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportForm reportForm = (ReportForm) form;

        if (reportForm.getDateFrom() == null || reportForm.getDateFrom().isEmpty()) {
            reportForm.setDateFrom(MONTH_FMT.format(new Date()).substring(0, 4) + "01");
        }
        if (reportForm.getDateTo() == null || reportForm.getDateTo().isEmpty()) {
            reportForm.setDateTo(MONTH_FMT.format(new Date()));
        }

        if ("true".equals(request.getParameter("csv"))) {
            service.exportCsv(response, reportForm);
            return null;
        }

        List<Map<String, Object>> completionMatrix = service.computeCompletionRate(reportForm);
        request.setAttribute("completionMatrix", completionMatrix);

        List<Map<String, Object>> crossTab = service.computeIncidentCrossTab(reportForm);
        request.setAttribute("crossTab", crossTab);

        List<Map<String, Object>> ranking = service.computeEquipmentRanking(reportForm);
        request.setAttribute("ranking", ranking);

        request.setAttribute("reportForm", reportForm);
        return mapping.findForward("success");
    }
}
