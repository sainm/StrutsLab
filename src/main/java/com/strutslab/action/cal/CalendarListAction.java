package com.strutslab.action.cal;

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

import com.strutslab.form.cal.CalendarForm;
import com.strutslab.service.cal.CalendarService;

public class CalendarListAction extends Action {

    private static final SimpleDateFormat YEAR_FMT = new SimpleDateFormat("yyyy");
    private final CalendarService service = new CalendarService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        CalendarForm calForm = (CalendarForm) form;

        String year = calForm.getYear();
        if (year == null || year.isEmpty()) {
            year = YEAR_FMT.format(new Date());
            calForm.setYear(year);
        }

        try {
            Integer.parseInt(year);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "年度の形式が正しくありません。");
            return mapping.findForward("success");
        }

        List<Map<String, Object>> holidays = service.findHolidaysByYear(year);
        List<Map<String, Object>> months = service.buildMonthlyCalendar(year, holidays);

        request.setAttribute("months", months);
        request.setAttribute("holidays", holidays);

        return mapping.findForward("success");
    }
}
