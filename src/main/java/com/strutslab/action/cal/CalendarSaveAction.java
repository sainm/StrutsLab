package com.strutslab.action.cal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dao.CalendarDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.form.cal.CalendarRegForm;

public class CalendarSaveAction extends DispatchAction {

    /**
     * Bulk register holidays (method=bulkRegister).
     * Generate dates from dateFrom to dateTo, check duplicates, insert all.
     */
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

        // Validate past dates
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (dateFrom.compareTo(today) < 0) {
            request.setAttribute("errorMessage", "過去の日付は一括登録できません。");
            return mapping.getInputForward();
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);

            // Generate date list
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
            Date from = sdf.parse(dateFrom);
            Date to = sdf.parse(dateTo);

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(from);

            int registered = 0;
            int skipped = 0;
            StringBuilder errors = new StringBuilder();

            while (!cal.getTime().after(to)) {
                String dateStr = sdf.format(cal.getTime());

                // Check duplicate
                Map<String, Object> existing = dao.findByDate(dateStr);
                if (existing != null) {
                    skipped++;
                    if (errors.length() > 0) errors.append(", ");
                    errors.append(dateStr).append("(重複)");
                } else {
                    Map<String, Object> holiday = new HashMap<>();
                    holiday.put("holidayDate", dateStr);
                    holiday.put("holidayType", holidayType);
                    holiday.put("holidayName", holidayName);
                    holiday.put("isTransfer", false);
                    holiday.put("transferDate", null);
                    dao.insert(holiday);
                    registered++;
                }

                cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            }

            sqlSession.commit();

            String msg = registered + "件登録しました。";
            if (skipped > 0) {
                msg += " " + skipped + "件は重複のためスキップしました。";
            }
            request.setAttribute("successMessage", msg);
        }

        return mapping.findForward("success");
    }

    /**
     * Transfer setting (method=transferSetting).
     */
    public ActionForward transferSetting(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CalendarRegForm regForm = (CalendarRegForm) form;

        String transferFrom = regForm.getTransferFrom();
        String transferTo = regForm.getTransferTo();

        if (transferFrom == null || transferTo == null) {
            request.setAttribute("errorMessage", "振替元・振替先の日付は必須です。");
            return mapping.getInputForward();
        }

        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (transferFrom.compareTo(today) < 0) {
            request.setAttribute("errorMessage", "過去の日付は振替設定できません。");
            return mapping.getInputForward();
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);

            // Check both dates exist or create
            Map<String, Object> fromHoliday = dao.findByDate(transferFrom);
            if (fromHoliday == null) {
                request.setAttribute("errorMessage", "振替元の日付が休日として登録されていません。");
                return mapping.getInputForward();
            }

            Map<String, Object> toHoliday = dao.findByDate(transferTo);
            if (toHoliday == null) {
                // Create the target holiday
                Map<String, Object> newHoliday = new HashMap<>();
                newHoliday.put("holidayDate", transferTo);
                newHoliday.put("holidayType", "振替休日");
                newHoliday.put("holidayName", "振替休日(" + transferFrom + ")");
                newHoliday.put("isTransfer", true);
                newHoliday.put("transferDate", transferFrom);
                dao.insert(newHoliday);
            }

            // Update from date as transfer
            fromHoliday.put("isTransfer", true);
            fromHoliday.put("transferDate", transferTo);
            dao.update(fromHoliday);

            sqlSession.commit();
            request.setAttribute("successMessage", "振替設定しました。");
        }

        return mapping.findForward("success");
    }

    /**
     * Delete holiday (method=delete).
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String holidayId = request.getParameter("holidayId");
        if (holidayId != null && !holidayId.isEmpty()) {
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                CalendarDao dao = sqlSession.getMapper(CalendarDao.class);
                dao.delete(Integer.parseInt(holidayId));
                sqlSession.commit();
            }
        }
        return mapping.findForward("success");
    }

    /**
     * Save single holiday (method=save).
     */
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

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);

            if (holidayId != null && !holidayId.isEmpty()) {
                // Update
                Map<String, Object> holiday = dao.findById(Integer.parseInt(holidayId));
                if (holiday != null) {
                    holiday.put("holidayType", holidayType);
                    holiday.put("holidayName", holidayName);
                    dao.update(holiday);
                }
            } else {
                // Insert
                Map<String, Object> existing = dao.findByDate(dateFrom);
                if (existing != null) {
                    request.setAttribute("errorMessage", dateFrom + " は既に登録済みです。");
                    return mapping.getInputForward();
                }
                Map<String, Object> holiday = new HashMap<>();
                holiday.put("holidayDate", dateFrom);
                holiday.put("holidayType", holidayType);
                holiday.put("holidayName", holidayName);
                holiday.put("isTransfer", false);
                holiday.put("transferDate", null);
                dao.insert(holiday);
            }

            sqlSession.commit();
        }

        return mapping.findForward("success");
    }
}
