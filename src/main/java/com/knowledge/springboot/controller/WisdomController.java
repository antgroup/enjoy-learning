package com.knowledge.springboot.controller;

import com.knowledge.springboot.common.BaseResponse;
import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.ResultUtils;
import com.knowledge.springboot.domain.WisdomRecord;
import com.knowledge.springboot.repository.WisdomRecordRepository;
import com.knowledge.springboot.service.impl.WisdomDecayService;
import com.knowledge.springboot.utils.RequestContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 智慧值相关接口控制器
 */
@RestController
@RequestMapping("/api/wisdom")
@CrossOrigin(origins = "*")
public class WisdomController {

    private static final Logger logger = LoggerFactory.getLogger(WisdomController.class);

    @Autowired
    private WisdomDecayService wisdomDecayService;

    @Autowired
    private WisdomRecordRepository wisdomRecordRepository;

    @Autowired
    private RequestContextUtil requestContextUtil;

    /**
     * 获取知识点当前智慧值
     * @param knowledgePointId 知识点ID
     * @return 智慧值信息
     */
    @GetMapping("/knowledge-point/{knowledgePointId}/current")
    public BaseResponse<Map<String, Object>> getCurrentWisdom(@PathVariable String knowledgePointId) {
        try {
            // 这里暂时使用第一个找到的记录，实际应该根据用户ID和知识点ID查询
            WisdomRecord record = wisdomRecordRepository.findByKnowledgePointId(knowledgePointId);

            if (record == null) {
                return ResultUtils.error(ErrorCode.USER_INPUT_PARAM_ERROR, "未找到该知识点的智慧值记录");
            }

            // 实时计算智慧值
            record = wisdomDecayService.calculateRealTimeWisdom(record);

            Map<String, Object> result = new HashMap<>();
            result.put("knowledgePointId", knowledgePointId);
            result.put("knowledgePointName", "知识点名称"); // 这里需要从知识点表获取
            result.put("baseWisdomValue", record.getBaseWisdomValue());
            result.put("currentWisdomValue", record.getCurrentWisdomValue());

            // 计算记忆保持率
            double retentionRate = record.getBaseWisdomValue() > 0 ?
                record.getCurrentWisdomValue() / record.getBaseWisdomValue() : 0.0;
            result.put("memoryRetentionRate", Math.round(retentionRate * 1000.0) / 10.0); // 保留一位小数

            result.put("lastStudyTime", record.getLastStudyTime());
            result.put("lastReviewTime", record.getLastReviewTime());
            result.put("memoryStability", record.getMemoryStability());
            result.put("reviewCount", record.getReviewCount());

            // 计算紧急度等级
            String urgencyLevel = calculateUrgencyLevel(retentionRate);
            result.put("urgencyLevel", urgencyLevel);

            return ResultUtils.success(result);

        } catch (Exception e) {
            logger.error("获取智慧值失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取智慧值失败");
        }
    }

    /**
     * 根据笔记ID获取智慧值
     * @param noteId 笔记ID
     * @return 智慧值信息
     */
    @GetMapping("/note/{noteId}/wisdom")
    public BaseResponse<Map<String, Object>> getWisdomByNoteId(@PathVariable String noteId) {
        try {
            // 从请求上下文中获取用户ID
            String userId = requestContextUtil.getCurrentUserId();
            if (userId == null) {
                return ResultUtils.error(ErrorCode.USER_LOGIN_ERROR, "用户未登录");
            }

            // 根据用户ID和笔记ID查询智慧值记录
            WisdomRecord record = wisdomRecordRepository.findByUserIdAndNoteId(userId, noteId);

            if (record == null) {
                return ResultUtils.error(ErrorCode.USER_INPUT_PARAM_ERROR, "未找到该笔记的智慧值记录");
            }

            // 实时计算智慧值
            record = wisdomDecayService.calculateRealTimeWisdom(record);

            Map<String, Object> result = new HashMap<>();
            result.put("noteId", noteId);
            result.put("wisdomRecordId", record.getId());
            result.put("knowledgePointId", record.getKnowledgePointId());
            result.put("baseWisdomValue", record.getBaseWisdomValue());
            result.put("currentWisdomValue", record.getCurrentWisdomValue());

            // 计算记忆保持率
            double retentionRate = record.getBaseWisdomValue() > 0 ?
                record.getCurrentWisdomValue() / record.getBaseWisdomValue() : 0.0;
            result.put("memoryRetentionRate", Math.round(retentionRate * 1000.0) / 10.0);

            // 智慧值评估信息
            result.put("difficultyLevel", record.getDifficultyLevel());
            result.put("importanceLevel", record.getImportanceLevel());
            result.put("qualityLevel", record.getQualityLevel());
            result.put("learningMethod", record.getLearningMethod());
            result.put("knowledgeType", record.getKnowledgeType());
            result.put("evaluationSummary", record.getEvaluationSummary());

            // 衰减相关信息
            result.put("lastStudyTime", record.getLastStudyTime());
            result.put("lastReviewTime", record.getLastReviewTime());
            result.put("memoryStability", record.getMemoryStability());
            result.put("reviewCount", record.getReviewCount());

            // 计算紧急度等级
            String urgencyLevel = calculateUrgencyLevel(retentionRate);
            result.put("urgencyLevel", urgencyLevel);

            // 时间信息
            result.put("createdAt", record.getCreatedAt());
            result.put("updatedAt", record.getUpdatedAt());

            return ResultUtils.success(result);

        } catch (Exception e) {
            logger.error("根据笔记ID获取智慧值失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取智慧值失败");
        }
    }

    /**
     * 手动触发智慧值衰减批量更新（用于测试）
     * @return 更新结果
     */
    @PostMapping("/batch-update")
    public BaseResponse<String> manualBatchUpdate() {
        try {
            wisdomDecayService.manualBatchUpdate();
            return ResultUtils.success("批量更新任务已触发");
        } catch (Exception e) {
            logger.error("手动触发批量更新失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "批量更新失败");
        }
    }

    /**
     * 获取用户的智慧值统计信息
     * @return 统计信息
     */
    @GetMapping("/user/statistics")
    public BaseResponse<Map<String, Object>> getUserWisdomStatistics() {
        try {
            // 从请求上下文中获取用户ID
            String userId = requestContextUtil.getCurrentUserId();
            if (userId == null) {
                return ResultUtils.error(ErrorCode.USER_LOGIN_ERROR, "用户未登录");
            }

            // 调用Service层获取统计信息
            Map<String, Object> statistics = wisdomDecayService.getUserWisdomStatistics(userId);
            
            return ResultUtils.success(statistics);
        } catch (Exception e) {
            logger.error("获取用户智慧值统计失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取统计信息失败");
        }
    }

    /**
     * 计算紧急度等级
     * @param retentionRate 记忆保持率
     * @return 紧急度等级
     */
    private String calculateUrgencyLevel(double retentionRate) {
        if (retentionRate < 0.3) {
            return "high"; // 红色警报
        } else if (retentionRate < 0.6) {
            return "medium"; // 黄色提醒
        } else {
            return "low"; // 绿色状态
        }
    }
}
