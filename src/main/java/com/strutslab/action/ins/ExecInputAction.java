package com.strutslab.action.ins;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.strutslab.dao.ChkItemDao;
import com.strutslab.dao.ExecDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ChkItemDto;
import com.strutslab.dto.ExecItemResultDto;
import com.strutslab.dto.ExecResultDto;
import com.strutslab.form.ins.ExecForm;

public class ExecInputAction extends Action {

    private static final String PHOTO_DIR = "/attachments/ins/";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ExecForm execForm = (ExecForm) form;

        if (request.getParameter("save") != null) {
            return doSave(mapping, execForm, request);
        } else {
            return doInput(mapping, execForm, request);
        }
    }

    private ActionForward doInput(ActionMapping mapping, ExecForm form,
            HttpServletRequest request) throws Exception {

        String planIdStr = request.getParameter("planId");
        String resultIdStr = request.getParameter("resultId");

        if (planIdStr == null || planIdStr.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("planId", new ActionMessage("errors.required", "計画ID"));
            saveErrors(request, errors);
            return mapping.findForward("input");
        }

        int planId = Integer.parseInt(planIdStr);
        form.setPlanId(planId);

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            // Load plan info with equipment + template details
            ExecResultDto planInfo = sqlSession.selectOne(
                    "com.strutslab.dao.ExecDao.findPlanById", planId);

            if (planInfo == null) {
                ActionMessages errors = new ActionMessages();
                errors.add("planId", new ActionMessage("errors.required", "計画が見つかりません"));
                saveErrors(request, errors);
                return mapping.findForward("input");
            }

            request.setAttribute("planInfo", planInfo);

            // Load checklist items from template
            int templateId = planInfo.getTemplateId();
            ChkItemDao chkItemDao = sqlSession.getMapper(ChkItemDao.class);
            List<ChkItemDto> items = chkItemDao.findItemsByTemplate(templateId);
            request.setAttribute("checklistItems", items);

            // Count level-3 items for maxItems
            int maxItems = 0;
            for (ChkItemDto item : items) {
                if (item.getItemLevel() == 3) {
                    maxItems++;
                }
            }
            form.setMaxItems(maxItems);

            // If result exists (re-entry), load existing data
            if (resultIdStr != null && !resultIdStr.isEmpty()) {
                int resultId = Integer.parseInt(resultIdStr);
                form.setResultId(resultId);

                ExecResultDto existing = sqlSession.selectOne(
                        "com.strutslab.dao.ExecDao.findById", resultId);
                if (existing != null) {
                    form.setExecutedDate(existing.getExecutedDate());
                    form.setSummaryJudge(existing.getSummaryJudge());
                    form.setSummaryNote(existing.getSummaryNote());
                    request.setAttribute("existingResult", existing);
                }

                // Load existing item results
                List<ExecItemResultDto> existingItems = sqlSession.selectList(
                        "com.strutslab.dao.ExecDao.findItemsByResultId", resultId);
                request.setAttribute("existingItemResults", existingItems);
            }

            // Set default executed date
            if (form.getExecutedDate() == null || form.getExecutedDate().isEmpty()) {
                form.setExecutedDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
            }
        }

        return mapping.findForward("input");
    }

    private ActionForward doSave(ActionMapping mapping, ExecForm form,
            HttpServletRequest request) throws Exception {

        ActionMessages errors = new ActionMessages();

        // Validate summary judge
        if (form.getSummaryJudge() == null || form.getSummaryJudge().isEmpty()) {
            errors.add("summaryJudge", new ActionMessage("errors.required", "総合判定"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return doInput(mapping, form, request);
        }

        String userCode = (String) request.getSession().getAttribute("userCode");
        if (userCode == null) userCode = "SYSTEM";

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);

            ExecResultDto result = new ExecResultDto();
            result.setPlanId(form.getPlanId());
            result.setExecutedDate(form.getExecutedDate());
            result.setExecutedBy(userCode);
            result.setSummaryJudge(form.getSummaryJudge());
            result.setSummaryNote(form.getSummaryNote());
            result.setApprovalStatus("完了");

            int resultId = form.getResultId();
            if (resultId > 0) {
                // Update existing result
                result.setResultId(resultId);
                dao.updateResult(result);
                dao.deleteItemResults(resultId);
            } else {
                // Insert new result header
                dao.insertResult(result);
                resultId = result.getResultId();
                form.setResultId(resultId);
            }

            // Save checklist item results
            // Try reading from items[].judge format (InspectionChecklistTag)
            // Fall back to execJudge[] format
            String[] judges = request.getParameterValues("execJudge");
            String[] values = request.getParameterValues("execValue");
            String[] notes = request.getParameterValues("execNote");

            // Check if tag format was used
            String sampleTagField = request.getParameter("items[0].judge");
            List<ChkItemDto> items = (List<ChkItemDto>) request.getAttribute("checklistItems");

            if (sampleTagField != null) {
                // Read from tag-generated format items[idx].judge/value/note
                int idx = 0;
                int itemIdx = 0;
                while (true) {
                    String judge = request.getParameter("items[" + idx + "].judge");
                    if (judge == null) break;

                    if (judge.isEmpty()) {
                        idx++;
                        continue;
                    }

                    // Find the idx-th level-3 item
                    int itemId = 0;
                    int l3count = 0;
                    if (items != null) {
                        for (ChkItemDto ci : items) {
                            if (ci.getItemLevel() == 3) {
                                if (l3count == idx) {
                                    itemId = ci.getItemId();
                                    break;
                                }
                                l3count++;
                            }
                        }
                    }

                    if (itemId > 0) {
                        String value = request.getParameter("items[" + idx + "].value");
                        String note = request.getParameter("items[" + idx + "].note");
                        ExecItemResultDto itemDto = new ExecItemResultDto();
                        itemDto.setResultId(resultId);
                        itemDto.setItemId(itemId);
                        itemDto.setJudge(judge);
                        itemDto.setMeasuredValue(value != null ? value : "");
                        itemDto.setNote(note != null ? note : "");
                        dao.insertItemResult(itemDto);
                    }
                    idx++;
                }
            } else if (judges != null) {
                // Direct array format: execJudge[], execValue[], execNote[]
                int l3idx = 0;
                for (int i = 0; i < judges.length; i++) {
                    String judge = judges[i];
                    if (judge == null || judge.isEmpty()) continue;

                    // Find the i-th level-3 item in checklistItems
                    int itemId = 0;
                    int l3count = 0;
                    if (items != null) {
                        for (ChkItemDto ci : items) {
                            if (ci.getItemLevel() == 3) {
                                if (l3count == i) {
                                    itemId = ci.getItemId();
                                    break;
                                }
                                l3count++;
                            }
                        }
                    }

                    if (itemId > 0) {
                        ExecItemResultDto itemDto = new ExecItemResultDto();
                        itemDto.setResultId(resultId);
                        itemDto.setItemId(itemId);
                        itemDto.setJudge(judge);
                        itemDto.setMeasuredValue(
                                (values != null && i < values.length && values[i] != null)
                                        ? values[i] : "");
                        itemDto.setNote(
                                (notes != null && i < notes.length && notes[i] != null)
                                        ? notes[i] : "");
                        dao.insertItemResult(itemDto);
                    }
                }
            }

            // Handle photo uploads
            FormFile[][] photos = form.getExecPhoto();
            if (photos != null) {
                String realPath = getServlet().getServletContext().getRealPath(
                        PHOTO_DIR + resultId + "/");
                File photoDir = new File(realPath);
                if (!photoDir.exists()) {
                    photoDir.mkdirs();
                }
                for (int i = 0; i < photos.length; i++) {
                    if (photos[i] != null) {
                        for (int j = 0; j < photos[i].length; j++) {
                            FormFile f = photos[i][j];
                            if (f != null && f.getFileSize() > 0) {
                                String fileName = System.currentTimeMillis() + "_"
                                        + f.getFileName();
                                File dest = new File(photoDir, fileName);
                                try (java.io.InputStream is = f.getInputStream();
                                     java.io.FileOutputStream os = new java.io.FileOutputStream(dest)) {
                                    byte[] buf = new byte[4096];
                                    int len;
                                    while ((len = is.read(buf)) > 0) {
                                        os.write(buf, 0, len);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            sqlSession.commit();
        }

        // Forward to incident creation if ABNORMAL
        if ("ABNORMAL".equals(form.getSummaryJudge())) {
            request.getSession().setAttribute("incidentFromExec", form);
            return mapping.findForward("incident");
        }

        return mapping.findForward("success");
    }
}
