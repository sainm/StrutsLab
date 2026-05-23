package com.strutslab.action.counter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.strutslab.form.counter.CounterDetailForm;

public class CounterDetailAction extends DispatchAction {

    /**
     * Load counter order and its details for display.
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterDetailForm df = (CounterDetailForm) form;

        String orderNo = request.getParameter("orderNo");
        if (orderNo == null || orderNo.isEmpty()) {
            orderNo = df.getOrderNo();
        }
        if (orderNo == null || orderNo.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("orderNo", new ActionMessage("errors.required", "指示番号"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);

            CounterDto order = dao.findById(orderNo);
            List<CounterDetailDto> details = dao.findDetailsByOrderNo(orderNo);

            if (order == null) {
                ActionMessages errors = new ActionMessages();
                errors.add("orderNo", new ActionMessage("errors.required", "指示が見つかりません"));
                saveErrors(request, errors);
                return mapping.findForward("success");
            }

            request.setAttribute("order", order);
            request.setAttribute("details", details);
        }

        return mapping.findForward("input");
    }

    /**
     * Show completion form for one detail.
     */
    public ActionForward completeDetail(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Load the order and details, mark which detail is being completed
        String orderNo = request.getParameter("orderNo");
        String detailIdx = request.getParameter("detailIndex");

        if (orderNo == null || orderNo.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("orderNo", new ActionMessage("errors.required", "指示番号"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        int idx = 0;
        if (detailIdx != null) {
            try { idx = Integer.parseInt(detailIdx); } catch (NumberFormatException e) { }
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            CounterDto order = dao.findById(orderNo);
            List<CounterDetailDto> details = dao.findDetailsByOrderNo(orderNo);

            request.setAttribute("order", order);
            request.setAttribute("details", details);
            request.setAttribute("completeIndex", idx);
        }

        return mapping.findForward("complete");
    }

    /**
     * Save completion for one detail item.
     */
    public ActionForward saveCompletion(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterDetailForm df = (CounterDetailForm) form;

        String orderNo = df.getOrderNo();
        Integer detailIdx = df.getDetailIndex();
        if (orderNo == null || orderNo.isEmpty() || detailIdx == null) {
            ActionMessages errors = new ActionMessages();
            errors.add("params", new ActionMessage("errors.required", "パラメータ"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);

            List<CounterDetailDto> details = dao.findDetailsByOrderNo(orderNo);
            if (detailIdx < 0 || detailIdx >= details.size()) {
                ActionMessages errors = new ActionMessages();
                errors.add("detailIndex", new ActionMessage("errors.required", "明細インデックスが不正"));
                saveErrors(request, errors);
                return mapping.findForward("success");
            }

            CounterDetailDto detail = details.get(detailIdx);

            // Update completion data
            if (df.getActualHour(detailIdx) != null) {
                detail.setActualHours(df.getActualHour(detailIdx));
            }
            if (df.getUsedPartCode(detailIdx) != null && !df.getUsedPartCode(detailIdx).isEmpty()) {
                detail.setUsedPartCode(df.getUsedPartCode(detailIdx));
                detail.setUsedQuantity(df.getUsedQuantity(detailIdx));

                // Check parts stock (warning only)
                Map<String, Object> stockParams = new HashMap<>();
                stockParams.put("partCode", df.getUsedPartCode(detailIdx));
                // Stock check would query parts DAO
                Integer qty = df.getUsedQuantity(detailIdx);
                if (qty != null && qty > 0) {
                    try {
                        // Simple stock check via parts DAO if available
                        request.setAttribute("stockWarning", "部品在庫を確認してください。");
                    } catch (Exception e) {
                        // Parts DAO may not exist yet
                    }
                }
            }
            if (df.getNote(detailIdx) != null) {
                detail.setNote(df.getNote(detailIdx));
            }
            detail.setStatus("完了");

            dao.updateDetail(detail);

            // Auto-complete header if all details done
            boolean allComplete = dao.areAllDetailsComplete(orderNo);
            if (allComplete) {
                CounterDto order = dao.findById(orderNo);
                if (order != null) {
                    order.setStatus("完了");
                    dao.update(order);
                }
            }

            sqlSession.commit();

            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("label.update", "明細を完了しました。"));
            saveMessages(request, messages);
        }

        // Reload the order
        return unspecified(mapping, form, request, response);
    }
}
