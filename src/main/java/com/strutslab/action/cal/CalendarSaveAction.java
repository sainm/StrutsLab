package com.strutslab.action.cal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.form.cal.CalendarRegForm;
import com.strutslab.service.cal.CalendarService;
import com.strutslab.service.cal.CalendarService.BulkResult;

public class CalendarSaveAction extends DispatchAction {

    private final CalendarService service = new CalendarService();

    public ActionForward bulkRegister(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CalendarRegForm regForm = (CalendarRegForm) form;

        String dateFrom = regForm.getDateFrom();
        String dateTo = regForm.getDateTo();
        String holidayType = regForm.getHolidayType();
        String holidayName = regForm.getHolidayName();

        if (dateFrom == null || dateTo == null || holidayType == null) {
            request.setAttribute("errorMessage", "日付範囲と休日種別は必須です。");
            return mapping.getInputForward();
        }

        BulkResult result = service.bulkRegister(dateFrom, dateTo, holidayType, holidayName);

        if (result.errorMessage != null) {
            request.setAttribute("errorMessage", result.errorMessage);
        } else {
            String msg = result.registered + "件登録しました。";
            if (result.skipped > 0) {
                msg += " " + result.skipped + "件は重複のためスキップしました。";
            }
            request.setAttribute("successMessage", msg);
        }

        return mapping.findForward("success");
    }

    public ActionForward transferSetting(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CalendarRegForm regForm = (CalendarRegForm) form;

        String transferFrom = regForm.getTransferFrom();
        String transferTo = regForm.getTransferTo();

        if (transferFrom == null || transferTo == null) {
            request.setAttribute("errorMessage", "振替元・振替先の日付は必須です。");
            return mapping.getInputForward();
        }

        String error = service.transferSetting(transferFrom, transferTo);
        if (error != null) {
            request.setAttribute("errorMessage", error);
        } else {
            request.setAttribute("successMessage", "振替設定しました。");
        }

        return mapping.findForward("success");
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String holidayId = request.getParameter("holidayId");
        if (holidayId != null && !holidayId.isEmpty()) {
            try {
                service.deleteHoliday(Integer.parseInt(holidayId));
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "休日IDの形式が正しくありません。");
            }
        }
        return mapping.findForward("success");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CalendarRegForm regForm = (CalendarRegForm) form;

        String dateFrom = regForm.getDateFrom();
        String holidayType = regForm.getHolidayType();
        String holidayName = regForm.getHolidayName();
        String holidayId = request.getParameter("holidayId");

        if (dateFrom == null || holidayType == null) {
            request.setAttribute("errorMessage", "日付と休日種別は必須です。");
            return mapping.getInputForward();
        }

        String error = service.saveHoliday(dateFrom, holidayType, holidayName, holidayId);
        if (error != null) {
            request.setAttribute("errorMessage", error);
            return mapping.getInputForward();
        }

        return mapping.findForward("success");
    }
}
