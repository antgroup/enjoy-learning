package com.knowledge.springboot.controller;

import com.knowledge.springboot.common.BaseResponse;
import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.ResultUtils;
import com.knowledge.springboot.domain.LearningNote;
import com.knowledge.springboot.dto.CreateNoteRequest;
import com.knowledge.springboot.service.impl.LearningNoteService;
import com.knowledge.springboot.utils.RequestContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 学习笔记控制器
 * 处理笔记上传和分析
 */
@RestController
@RequestMapping("/api/notes")
public class LearningNoteController {

    private static final Logger logger = LoggerFactory.getLogger(LearningNoteController.class);

    @Autowired
    private LearningNoteService learningNoteService;

    @Autowired
    private RequestContextUtil requestContextUtil;

    /**
     * 上传笔记接口
     * @param request 笔记创建请求
     * @return 笔记ID和分析状态
     */
    @PostMapping
    public BaseResponse<Map<String, Object>> uploadNote(@RequestBody @Validated CreateNoteRequest request) {
        logger.info("接收到笔记上传请求");

        try {
            // 从请求上下文中获取用户ID
            String userId = requestContextUtil.getCurrentUserId();
            if (userId == null) {
                return ResultUtils.error(ErrorCode.USER_LOGIN_ERROR, "用户未登录");
            }

            // 调用服务创建笔记
            LearningNote note = learningNoteService.createNote(request, userId);

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("noteId", note.getId());
            responseData.put("title", note.getTitle());
            responseData.put("analysisStatus", "pending");
            responseData.put("estimatedTime", 15); // 预计15秒完成分析

            return ResultUtils.success(responseData);
        } catch (Exception e) {
            logger.error("笔记上传失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "笔记上传失败：" + e.getMessage());
        }
    }

    /**
     * 查询笔记分析状态接口
     * @param noteId 笔记ID
     * @return 分析状态和结果
     */
    @GetMapping("/{noteId}/analysis")
    public BaseResponse<Map<String, Object>> getNoteAnalysisStatus(@PathVariable String noteId) {
        logger.info("查询笔记分析状态：{}", noteId);

        try {
            // 调用服务获取分析状态
            Map<String, Object> analysisStatus = learningNoteService.getNoteAnalysisStatus(noteId);

            if (analysisStatus == null) {
                return ResultUtils.error(ErrorCode.USER_INPUT_PARAM_ERROR, "笔记不存在");
            }

            return ResultUtils.success(analysisStatus);
        } catch (Exception e) {
            logger.error("查询笔记分析状态失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "查询笔记分析状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取笔记详情接口
     * @param noteId 笔记ID
     * @return 笔记详情
     */
    @GetMapping("/{noteId}")
    public BaseResponse<LearningNote> getNoteDetail(@PathVariable String noteId) {
        logger.info("获取笔记详情：{}", noteId);

        try {
            // 调用服务获取笔记详情
            LearningNote note = learningNoteService.getNoteById(noteId);

            if (note == null) {
                return ResultUtils.error(ErrorCode.USER_INPUT_PARAM_ERROR, "笔记不存在");
            }

            return ResultUtils.success(note);
        } catch (Exception e) {
            logger.error("获取笔记详情失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取笔记详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户笔记列表接口
     * @return 笔记列表
     */
    @GetMapping("/list")
    public BaseResponse<Object> getUserNotes() {
        logger.info("获取用户笔记列表");

        try {
            // 从请求上下文中获取用户ID
            String userId = requestContextUtil.getCurrentUserId();
            if (userId == null) {
                return ResultUtils.error(ErrorCode.USER_LOGIN_ERROR, "用户未登录");
            }

            // 调用服务获取用户笔记列表
            return ResultUtils.success(learningNoteService.getNotesByUserId(userId));
        } catch (Exception e) {
            logger.error("获取用户笔记列表失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取用户笔记列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除笔记接口
     * @param noteId 笔记ID
     * @return 删除结果
     */
    @DeleteMapping("/{noteId}")
    public BaseResponse<Boolean> deleteNote(@PathVariable String noteId) {
        logger.info("删除笔记：{}", noteId);

        try {
            // 从请求上下文中获取用户ID
            String userId = requestContextUtil.getCurrentUserId();
            if (userId == null) {
                return ResultUtils.error(ErrorCode.USER_LOGIN_ERROR, "用户未登录");
            }

            // 调用服务删除笔记
            boolean result = learningNoteService.deleteNote(noteId, userId);

            if (!result) {
                return ResultUtils.error(ErrorCode.USER_INPUT_PARAM_ERROR, "笔记不存在或无权删除");
            }

            return ResultUtils.success(true);
        } catch (Exception e) {
            logger.error("删除笔记失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "删除笔记失败：" + e.getMessage());
        }
    }
}
