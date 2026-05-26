package com.strutslab.service.ins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.upload.FormFile;

import com.strutslab.dao.ChkItemDao;
import com.strutslab.dao.ExecDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ChkItemDto;
import com.strutslab.dto.ExecItemResultDto;
import com.strutslab.dto.ExecResultDto;
import com.strutslab.form.ins.ExecForm;

public class ExecInputService {

    private static final String PHOTO_DIR = "/attachments/ins/";

    public ExecResultDto loadPlanInfo(int planId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            return dao.findPlanById(planId);
        }
    }

    public List<ChkItemDto> loadChecklistItems(int templateId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);
            return dao.findItemsByTemplate(templateId);
        }
    }

    public int countLevel3Items(List<ChkItemDto> items) {
        int count = 0;
        for (ChkItemDto item : items) {
            if (item.getItemLevel() == 3) count++;
        }
        return count;
    }

    public ExecResultDto findExistingResult(int resultId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            return dao.findById(resultId);
        }
    }

    public List<ExecItemResultDto> findExistingItems(int resultId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecDao dao = sqlSession.getMapper(ExecDao.class);
            return dao.findItemsByResultId(resultId);
        }
    }

    public String getDefaultExecutedDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public int saveResult(ExecForm form, String userCode, List<ChkItemDto> checklistItems,
            String[] judges, String[] values, String[] notes, FormFile[][] photos,
            String servletRealPath) throws Exception {

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
                result.setResultId(resultId);
                dao.updateResult(result);
                dao.deleteItemResults(resultId);
            } else {
                dao.insertResult(result);
                resultId = result.getResultId();
            }

            // Save item results from flat arrays
            if (judges != null) {
                for (int i = 0; i < judges.length; i++) {
                    String judge = judges[i];
                    if (judge == null || judge.isEmpty()) continue;

                    int itemId = findLevel3ItemId(checklistItems, i);
                    if (itemId > 0) {
                        ExecItemResultDto itemDto = new ExecItemResultDto();
                        itemDto.setResultId(resultId);
                        itemDto.setItemId(itemId);
                        itemDto.setJudge(judge);
                        itemDto.setMeasuredValue(values != null && i < values.length ? values[i] : "");
                        itemDto.setNote(notes != null && i < notes.length ? notes[i] : "");
                        dao.insertItemResult(itemDto);
                    }
                }
            }

            // Save photos
            if (photos != null) {
                String photoPath = servletRealPath + PHOTO_DIR + resultId + "/";
                File photoDir = new File(photoPath);
                if (!photoDir.exists()) photoDir.mkdirs();

                for (FormFile[] photoArr : photos) {
                    if (photoArr != null) {
                        for (FormFile f : photoArr) {
                            if (f != null && f.getFileSize() > 0) {
                                String fileName = System.currentTimeMillis() + "_" + f.getFileName();
                                File dest = new File(photoDir, fileName);
                                try (InputStream is = f.getInputStream();
                                        FileOutputStream fos = new FileOutputStream(dest)) {
                                    byte[] buf = new byte[4096];
                                    int len;
                                    while ((len = is.read(buf)) > 0) {
                                        fos.write(buf, 0, len);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            sqlSession.commit();
            return resultId;
        }
    }

    private int findLevel3ItemId(List<ChkItemDto> items, int targetL3Index) {
        int l3count = 0;
        for (ChkItemDto ci : items) {
            if (ci.getItemLevel() == 3) {
                if (l3count == targetL3Index) return ci.getItemId();
                l3count++;
            }
        }
        return 0;
    }

    public List<Integer> collectLevel3ItemIds(List<ChkItemDto> items) {
        List<Integer> ids = new ArrayList<>();
        for (ChkItemDto ci : items) {
            if (ci.getItemLevel() == 3) ids.add(ci.getItemId());
        }
        return ids;
    }
}
