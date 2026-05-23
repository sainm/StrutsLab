package com.strutslab.action.cal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.dao.CalendarDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.form.cal.CalendarForm;

public class CalendarListAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        CalendarForm calForm = (CalendarForm) form;

        String year = calForm.getYear();
        if (year == null || year.isEmpty()) {
            year = new SimpleDateFormat("yyyy").format(new Date());
            calForm.setYear(year);
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);
            List<Map<String, Object>> holidays = dao.findByYear(year);

            // Build monthly calendar data
            List<Map<String, Object>> months = new ArrayList<>();

            // Map holiday dates for quick lookup
            Map<String, Map<String, Object>> holidayMap = new HashMap<>();
            for (Map<String, Object> h : holidays) {
                String date = (String) h.get("holidayDate");
                if (date != null) {
                    holidayMap.put(date, h);
                }
            }

            for (int m = 1; m <= 12; m++) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", m);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(year));
                cal.set(Calendar.MONTH, m - 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);

                int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=Sun
                int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                List<List<Map<String, Object>>> weeks = new ArrayList<>();
                List<Map<String, Object>> week = new ArrayList<>();

                // Empty cells before first day
                for (int i = 0; i < firstDayOfWeek; i++) {
                    Map<String, Object> cell = new HashMap<>();
                    cell.put("day", null);
                    week.add(cell);
                }

                for (int d = 1; d <= daysInMonth; d++) {
                    String dateStr = String.format("%s%02d%02d", year, m, d);

                    Map<String, Object> cell = new HashMap<>();
                    cell.put("day", d);
                    cell.put("dateStr", dateStr);

                    Map<String, Object> holiday = holidayMap.get(dateStr);
                    if (holiday != null) {
                        cell.put("isHoliday", true);
                        cell.put("holidayType", holiday.get("holidayType"));
                        cell.put("holidayName", holiday.get("holidayName"));
                        cell.put("holidayId", holiday.get("holidayId"));
                    }

                    week.add(cell);

                    // Check if week is complete (Saturday) or end of month
                    Calendar tmp = Calendar.getInstance();
                    tmp.set(Integer.parseInt(year), m - 1, d);
                    int dayOfWeek = tmp.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SATURDAY || d == daysInMonth) {
                        // Pad remaining days
                        while (week.size() < 7) {
                            Map<String, Object> empty = new HashMap<>();
                            empty.put("day", null);
                            week.add(empty);
                        }
                        weeks.add(week);
                        week = new ArrayList<>();
                    }
                }

                monthData.put("weeks", weeks);
                months.add(monthData);
            }

            request.setAttribute("months", months);
            request.setAttribute("holidays", holidays);
        }

        return mapping.findForward("success");
    }
}
