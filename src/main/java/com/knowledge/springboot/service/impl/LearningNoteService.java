package com.knowledge.springboot.service.impl;

import com.knowledge.springboot.domain.LearningNote;
import com.knowledge.springboot.domain.WisdomRecord;
import com.knowledge.springboot.dto.CreateNoteRequest;
import com.knowledge.springboot.repository.LearningNoteRepository;
import com.knowledge.springboot.repository.WisdomRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习笔记服务类
 * 处理笔记的创建、分析和查询
 */
@Service
public class LearningNoteService {

    private static final Logger logger = LoggerFactory.getLogger(LearningNoteService.class);

    @Autowired
    private LearningNoteRepository learningNoteRepository;

    @Autowired
    private WisdomRecordRepository wisdomRecordRepository;

    @Autowired
    private AIAnalysisService aiAnalysisService;

    /**
     * 创建笔记并触发分析
     * @param request 笔记创建请求
     * @param userId 用户ID
     * @return 创建的笔记对象
     */
    public LearningNote createNote(CreateNoteRequest request, String userId) {
        logger.info("创建笔记，用户ID：{}", userId);

        // 创建笔记对象
        LearningNote note = new LearningNote();

        // 设置笔记内容
        note.setContent(request.getContent());

        // 设置标题（如果用户提供）
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            note.setTitle(request.getTitle());
        } else {
            // 设置临时标题
            String tempTitle = "笔记分析中...";
            note.setTitle(tempTitle);
        }

        // 设置标签（如果用户提供）
        note.setTags(request.getTags());

        // 设置关联知识点（如果用户提供）
        note.setRelatedKnowledgePoints(request.getRelatedKnowledgePoints());

        // 设置学习方式，默认为reading
        if (request.getLearningMethod() != null && !request.getLearningMethod().isEmpty()) {
            note.setLearningMethod(request.getLearningMethod());
        } else {
            note.setLearningMethod("reading");
        }

        // 设置用户ID
        note.setUserId(userId);

        // 设置笔记状态
        note.setStatus("active");

        // 设置AI分析状态为pending
        LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
        aiAnalysis.setStatus("pending");
        note.setAiAnalysis(aiAnalysis);

        // 设置创建和更新时间
        Date now = new Date();
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        // 保存笔记
        note = learningNoteRepository.save(note);
        logger.info("笔记初始化创建成功，ID：{}", note.getId());

        // 异步调用AI分析服务
        aiAnalysisService.analyzeNote(note);

        return note;
    }

    /**
     * 根据ID获取笔记
     * @param noteId 笔记ID
     * @return 笔记对象
     */
    public LearningNote getNoteById(String noteId) {
        logger.info("获取笔记，ID：{}", noteId);
        return learningNoteRepository.findById(noteId).orElse(null);
    }

    /**
     * 根据用户ID获取笔记列表
     * @param userId 用户ID
     * @return 笔记列表
     */
    public List<LearningNote> getNotesByUserId(String userId) {
        logger.info("获取用户笔记列表，用户ID：{}", userId);
        List<LearningNote> active = learningNoteRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "active");
        // 遍历active，根据笔记id查询当前智慧值
        for (LearningNote note : active) {
            WisdomRecord wisdomRecord = wisdomRecordRepository.findByNoteId(note.getId());
            if (wisdomRecord != null) {
                note.setCurrentWisdom(String.valueOf(wisdomRecord.getBaseWisdomValue()));
            }
        }
        return active;
    }

    /**
     * 获取笔记分析状态和结果
     * @param noteId 笔记ID
     * @return 分析状态和结果
     */
    public Map<String, Object> getNoteAnalysisStatus(String noteId) {
        logger.info("获取笔记分析状态，笔记ID：{}", noteId);

        // 查询笔记
        LearningNote note = learningNoteRepository.findById(noteId).orElse(null);
        if (note == null) {
            return null;
        }

        // 获取分析状态
        String analysisStatus = note.getAiAnalysis().getStatus();

        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("analysisStatus", analysisStatus);

        // 根据分析状态返回不同数据
        if ("completed".equals(analysisStatus)) {
            // 分析完成，返回分析结果
            Map<String, Object> aiAnalysis = new HashMap<>();
            aiAnalysis.put("extractedKeywords", note.getAiAnalysis().getExtractedKeywords());
            aiAnalysis.put("summary", note.getAiAnalysis().getSummary());
            aiAnalysis.put("knowledgePoints", note.getAiAnalysis().getKnowledgePoints());
            aiAnalysis.put("subjectArea", note.getAiAnalysis().getSubjectArea());

            responseData.put("aiAnalysis", aiAnalysis);

            // 获取智慧值记录
            WisdomRecord wisdomRecord = wisdomRecordRepository.findByNoteId(noteId);
            if (wisdomRecord != null) {
                Map<String, Object> wisdomValue = new HashMap<>();
                wisdomValue.put("baseWisdomValue", wisdomRecord.getBaseWisdomValue());
                wisdomValue.put("difficultyLevel", wisdomRecord.getDifficultyLevel());
                wisdomValue.put("importanceLevel", wisdomRecord.getImportanceLevel());
                wisdomValue.put("evaluationSummary", wisdomRecord.getEvaluationSummary());

                responseData.put("wisdomValue", wisdomValue);
            }
        } else if ("failed".equals(analysisStatus)) {
            // 分析失败，返回错误信息
            responseData.put("errorMessage", note.getAiAnalysis().getErrorMessage());
        }

        return responseData;
    }

    /**
     * 更新笔记
     * @param note 笔记对象
     * @return 更新后的笔记对象
     */
    public LearningNote updateNote(LearningNote note) {
        logger.info("更新笔记，ID：{}", note.getId());

        // 设置更新时间
        note.setUpdatedAt(new Date());

        return learningNoteRepository.save(note);
    }

    /**
     * 删除笔记（逻辑删除）
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    public boolean deleteNote(String noteId, String userId) {
        logger.info("删除笔记，ID：{}，用户ID：{}", noteId, userId);

        // 查询笔记
        LearningNote note = learningNoteRepository.findByIdAndUserId(noteId, userId);
        if (note == null) {
            return false;
        }

        // 设置状态为deleted
        note.setStatus("deleted");
        note.setUpdatedAt(new Date());

        // 保存更新
        learningNoteRepository.save(note);

        return true;
    }
}
