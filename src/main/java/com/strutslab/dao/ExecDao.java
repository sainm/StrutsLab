package com.strutslab.dao;

import com.strutslab.dto.ExecItemResultDto;
import com.strutslab.dto.ExecResultDto;
import java.util.List;
import java.util.Map;

public interface ExecDao {
    int insertResult(ExecResultDto r);
    void insertItemResult(ExecItemResultDto item);
    ExecResultDto findById(int resultId);
    List<ExecItemResultDto> findItemsByResultId(int resultId);
    List<ExecResultDto> findPendingApprovals(Map<String, Object> params);
    void updateApprovalStatus(int resultId, String status, String rejectReason);
    void bulkApprove(int[] resultIds);
    void bulkReject(int[] resultIds, String reason);
    int countPendingApprovals(Map<String, Object> params);
    void updateResult(ExecResultDto r);
    void deleteItemResults(int resultId);
}
