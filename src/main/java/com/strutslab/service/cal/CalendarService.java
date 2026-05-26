package com.strutslab.service.cal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.CalendarDao;
import com.strutslab.db.MyBatisUtil;

public class CalendarService {

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyyMMdd");

    public List<Map<String, Object>> findHolidaysByYear(String year) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);
            return dao.findByYear(year);
        }
    }

    public List<Map<String, Object>> buildMonthlyCalendar(String year, List<Map<String, Object>> holidays) {
        Map<String, Map<String, Object>> holidayMap = new HashMap<>();
        for (Map<String, Object> h : holidays) {
            String date = (String) h.get("holidayDate");
            if (date != null) {
                holidayMap.put(date, h);
            }
        }

        int yearInt = Integer.parseInt(year);
        List<Map<String, Object>> months = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", m);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, yearInt);
            cal.set(Calendar.MONTH, m - 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);

            int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            List<List<Map<String, Object>>> weeks = new ArrayList<>();
            List<Map<String, Object>> week = new ArrayList<>();

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

                Calendar tmp = Calendar.getInstance();
                tmp.set(yearInt, m - 1, d);
                int dayOfWeek = tmp.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || d == daysInMonth) {
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

        return months;
    }

    public static class BulkResult {
        public final int registered;
        public final int skipped;
        public final String errorDates;
        public final String errorMessage;

        BulkResult(int registered, int skipped, String errorDates, String errorMessage) {
            this.registered = registered;
            this.skipped = skipped;
            this.errorDates = errorDates;
            this.errorMessage = errorMessage;
        }
    }

    public BulkResult bulkRegister(String dateFrom, String dateTo,
            String holidayType, String holidayName) throws Exception {
        String today = DATE_FMT.format(new Date());
        if (dateFrom.compareTo(today) < 0) {
            return new BulkResult(0, 0, null, "過去の日付は一括登録できません。");
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);

            Date from = DATE_FMT.parse(dateFrom);
            Date to = DATE_FMT.parse(dateTo);

            Calendar cal = Calendar.getInstance();
            cal.setTime(from);

            int registered = 0;
            int skipped = 0;
            StringBuilder errors = new StringBuilder();

            while (!cal.getTime().after(to)) {
                String dateStr = DATE_FMT.format(cal.getTime());

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

                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            sqlSession.commit();
            return new BulkResult(registered, skipped, errors.toString(), null);
        }
    }

    public String transferSetting(String transferFrom, String transferTo) {
        String today = DATE_FMT.format(new Date());
        if (transferFrom.compareTo(today) < 0) {
            return "過去の日付は振替設定できません。";
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);

            Map<String, Object> fromHoliday = dao.findByDate(transferFrom);
            if (fromHoliday == null) {
                return "振替元の日付が休日として登録されていません。";
            }

            Map<String, Object> toHoliday = dao.findByDate(transferTo);
            if (toHoliday == null) {
                Map<String, Object> newHoliday = new HashMap<>();
                newHoliday.put("holidayDate", transferTo);
                newHoliday.put("holidayType", "振替休日");
                newHoliday.put("holidayName", "振替休日(" + transferFrom + ")");
                newHoliday.put("isTransfer", true);
                newHoliday.put("transferDate", transferFrom);
                dao.insert(newHoliday);
            }

            fromHoliday.put("isTransfer", true);
            fromHoliday.put("transferDate", transferTo);
            dao.update(fromHoliday);

            sqlSession.commit();
            return null;
        }
    }

    public void deleteHoliday(int holidayId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);
            dao.delete(holidayId);
            sqlSession.commit();
        }
    }

    public String saveHoliday(String dateFrom, String holidayType, String holidayName, String holidayId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CalendarDao dao = sqlSession.getMapper(CalendarDao.class);

            if (holidayId != null && !holidayId.isEmpty()) {
                int hId = Integer.parseInt(holidayId);
                Map<String, Object> holiday = dao.findById(hId);
                if (holiday != null) {
                    holiday.put("holidayType", holidayType);
                    holiday.put("holidayName", holidayName);
                    dao.update(holiday);
                }
            } else {
                Map<String, Object> existing = dao.findByDate(dateFrom);
                if (existing != null) {
                    return dateFrom + " は既に登録済みです。";
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
            return null;
        }
    }
}
