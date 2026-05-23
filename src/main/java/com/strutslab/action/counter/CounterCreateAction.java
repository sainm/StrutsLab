package com.strutslab.action.counter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dao.CounterDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.CounterDetailDto;
import com.strutslab.dto.CounterDto;
import com.strutslab.form.counter.CounterForm;

public class CounterCreateAction extends DispatchAction {

    /**
     * Show the create form. If incidentNo param is present, pre-populate from incident.
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterForm cf = (CounterForm) form;
        String incidentNo = request.getParameter("incidentNo");

        if (incidentNo != null && !incidentNo.isEmpty()) {
            cf.setIncidentNo(incidentNo);
            // Load incident info to prefill date, etc.
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                // Could load incident details here for display
                request.setAttribute("incidentNo", incidentNo);
            }
        }

        // Set default order date
        if (cf.getOrderDate() == null || cf.getOrderDate().isEmpty()) {
            cf.setOrderDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        }

        // Ensure at least one empty detail row
        if (cf.getDetailCount() == 0) {
            cf.setDetailWorkContents(new String[1]);
            cf.setDetailPersons(new String[1]);
            cf.setDetailDeadlines(new String[1]);
            cf.setDetailPriorities(new String[1]);
        }

        return mapping.findForward("input");
    }

    /**
     * Add a row to the detail table.
     */
    public ActionForward addRow(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterForm cf = (CounterForm) form;

        int oldLen = cf.getDetailCount();
        int newLen = oldLen + 1;

        String[] newWork = new String[newLen];
        String[] newPersons = new String[newLen];
        String[] newDeadlines = new String[newLen];
        String[] newPriorities = new String[newLen];

        for (int i = 0; i < oldLen; i++) {
            newWork[i] = cf.getDetailWorkContent(i);
            newPersons[i] = cf.getDetailPerson(i);
            newDeadlines[i] = cf.getDetailDeadline(i);
            newPriorities[i] = cf.getDetailPriority(i);
        }

        cf.setDetailWorkContents(newWork);
        cf.setDetailPersons(newPersons);
        cf.setDetailDeadlines(newDeadlines);
        cf.setDetailPriorities(newPriorities);

        return mapping.findForward("addRow");
    }

    /**
     * Delete a row from the detail table by index.
     */
    public ActionForward delRow(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterForm cf = (CounterForm) form;

        String idxStr = request.getParameter("index");
        int removeIdx = 0;
        if (idxStr != null) {
            try { removeIdx = Integer.parseInt(idxStr); } catch (NumberFormatException e) { }
        }

        int oldLen = cf.getDetailCount();
        if (oldLen <= 1) {
            // Keep at least one empty row
            ActionMessages errors = new ActionMessages();
            errors.add("detail", new ActionMessage("errors.required", "明細は最低1行必要です。"));
            saveErrors(request, errors);
            return mapping.findForward("delRow");
        }

        int newLen = oldLen - 1;
        String[] newWork = new String[newLen];
        String[] newPersons = new String[newLen];
        String[] newDeadlines = new String[newLen];
        String[] newPriorities = new String[newLen];

        int j = 0;
        for (int i = 0; i < oldLen; i++) {
            if (i == removeIdx) continue;
            newWork[j] = cf.getDetailWorkContent(i);
            newPersons[j] = cf.getDetailPerson(i);
            newDeadlines[j] = cf.getDetailDeadline(i);
            newPriorities[j] = cf.getDetailPriority(i);
            j++;
        }

        cf.setDetailWorkContents(newWork);
        cf.setDetailPersons(newPersons);
        cf.setDetailDeadlines(newDeadlines);
        cf.setDetailPriorities(newPriorities);

        return mapping.findForward("delRow");
    }

    /**
     * Save the counter order.
     */
    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterForm cf = (CounterForm) form;

        // --- Validation ---
        ActionMessages errors = new ActionMessages();

        if (cf.getDetailCount() == 0
                || cf.getDetailWorkContents() == null
                || cf.getDetailWorkContents().length == 0) {
            errors.add("detail", new ActionMessage("errors.required", "明細は最低1行必要です。"));
        } else {
            for (int i = 0; i < cf.getDetailCount(); i++) {
                String wc = cf.getDetailWorkContent(i);
                if (wc == null || wc.trim().isEmpty()) {
                    errors.add("detailWorkContents",
                            new ActionMessage("errors.required", (i + 1) + "行目の指示内容"));
                }
                String person = cf.getDetailPerson(i);
                if (person == null || person.trim().isEmpty()) {
                    errors.add("detailPersons",
                            new ActionMessage("errors.required", (i + 1) + "行目の担当者"));
                }
                String deadline = cf.getDetailDeadline(i);
                if (deadline != null && !deadline.isEmpty()) {
                    // Check deadline is not past
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Date dl = sdf.parse(deadline);
                        if (dl.before(new Date())) {
                            errors.add("detailDeadlines",
                                    new ActionMessage("errors.required", (i + 1) + "行目の期限が過去日付"));
                        }
                    } catch (Exception e) {
                        errors.add("detailDeadlines",
                                new ActionMessage("errors.required", (i + 1) + "行目の期限が不正"));
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.getInputForward();
        }

        // --- Generate order number ---
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String orderNoPrefix = "CTR-" + today + "-";

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);

            // Find max sequence for today
            List<CounterDto> existing = dao.search(new java.util.HashMap<String, Object>() {{
                put("dateFrom", today);
                put("dateTo", today);
            }});

            int maxSeq = 0;
            for (CounterDto d : existing) {
                String on = d.getOrderNo();
                if (on != null && on.startsWith(orderNoPrefix)) {
                    try {
                        int seq = Integer.parseInt(on.substring(orderNoPrefix.length()));
                        if (seq > maxSeq) maxSeq = seq;
                    } catch (NumberFormatException e) { }
                }
            }
            String orderNo = orderNoPrefix + String.format("%03d", maxSeq + 1);

            // --- Insert header ---
            CounterDto header = new CounterDto();
            header.setOrderNo(orderNo);
            header.setIncidentNo(cf.getIncidentNo());
            header.setOrderDate(cf.getOrderDate());
            header.setIssuer(cf.getIssuer());
            header.setOverallDeadline(cf.getOverallDeadline());
            header.setOverallPriority(cf.getOverallPriority());
            header.setStatus("未了");
            dao.insert(header);

            // --- Insert details ---
            for (int i = 0; i < cf.getDetailCount(); i++) {
                CounterDetailDto detail = new CounterDetailDto();
                detail.setOrderNo(orderNo);
                detail.setSeqNo(i + 1);
                detail.setWorkContent(cf.getDetailWorkContent(i));
                detail.setPersonName(cf.getDetailPerson(i));
                detail.setDeadline(cf.getDetailDeadline(i));
                detail.setPriority(cf.getDetailPriority(i));
                detail.setStatus("未了");
                dao.insertDetail(detail);
            }

            sqlSession.commit();

            // Need to re-query with person_code. If person is a name, set personName and leave personCode.
            // For simplicity, we set personCode from a lookup or leave blank
            // Update details with person codes if needed

            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("label.update", "対応指示を登録しました。番号:" + orderNo));
            saveMessages(request, messages);
        }

        return mapping.findForward("success");
    }
}
