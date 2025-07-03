package com.knowledge.springboot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.knowledge.springboot.domain.LearningNote;
import com.knowledge.springboot.domain.WisdomRecord;
import com.knowledge.springboot.repository.LearningNoteRepository;
import com.knowledge.springboot.repository.WisdomRecordRepository;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * AI分析服务实现类
 * 负责调用大语言模型API进行笔记分析和智慧值计算
 */
@Service
public class AIAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AIAnalysisService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LearningNoteRepository learningNoteRepository;

    @Autowired
    private WisdomRecordRepository wisdomRecordRepository;

    @Value("${ai.model.api.url:}")
    private String aiModelApiUrl;

    @Value("${ai.model.api.key:}")
    private String aiModelApiKey;

    @Value("${ai.model.name:DeepSeek-R1}")
    private String aiModelName;

    // 缓存过期时间（24小时）
    private static final long CACHE_EXPIRE_TIME = 24;

    // 用于清理think标签的正则表达式
    private static final Pattern THINK_PATTERN = Pattern.compile("<think>.*?</think>", Pattern.DOTALL);

    /**
     * 异步分析笔记内容
     * @param note 笔记对象
     * @return 分析结果
     */
    @Async("aiTaskExecutor")
    public void analyzeNote(LearningNote note) {
        logger.info("开始分析笔记：{}", note.getId());

        try {
            // 检查缓存
            String cacheKey = generateCacheKey(note);
            JSONObject cachedResult = (JSONObject) redisTemplate.opsForValue().get(cacheKey);

            if (cachedResult != null) {
                logger.info("使用缓存的分析结果：{}", note.getId());
                processAnalysisResult(note, cachedResult);
                return;
            }

            // 准备请求参数
            Map<String, Object> requestParams = prepareRequestParams(note);

            // 调用AI模型API
            JSONObject result = callAIModelApi(requestParams, false);

            // 处理分析结果
            if (result != null) {
                // 缓存结果
                redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_TIME, TimeUnit.HOURS);

                // 处理结果
                processAnalysisResult(note, result);
            } else {
                handleAnalysisFailure(note, "AI模型返回空结果");
            }
        } catch (Exception e) {
            logger.error("分析笔记失败：{}", note.getId(), e);
            handleAnalysisFailure(note, e.getMessage());
        }
    }

    /**
     * 生成缓存键
     * @param note 笔记对象
     * @return 缓存键
     */
    private String generateCacheKey(LearningNote note) {
        // 使用内容的哈希值作为缓存键的一部分
        int contentHash = note.getContent().hashCode();
        return String.format("note:content_hash:%d", contentHash);
    }

    /**
     * 准备请求参数
     * @param note 笔记对象
     * @return 请求参数
     */
    private Map<String, Object> prepareRequestParams(LearningNote note) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", note.getContent());

        // 如果用户提供了标题，也发送给模型
        if (note.getTitle() != null && !note.getTitle().isEmpty()) {
            params.put("title", note.getTitle());
        }

        // 设置学习方式，默认为reading
        String learningMethod = note.getLearningMethod();
        if (learningMethod == null || learningMethod.isEmpty()) {
            learningMethod = "reading";
        }
        params.put("learning_method", learningMethod);

        return params;
    }

    /**
     * 调用AI模型API (临时使用Mock数据进行测试)
     * @param params 请求参数
     * @return API返回结果
     */
    private JSONObject callAIModelApi(Map<String, Object> params, boolean mock) {
        try {
            logger.info("使用Mock数据进行测试");
            String response = "";
            if (mock) {
                response = "{\n" +
                    "  \"extracted_keywords\": [\"Elasticsearch\", \"mapping\", \"enabled\", \"index\", \"index_options\", \"docs\", \"freqs\", \"positions\", \"offsets\", \"norms\", \"doc_values\"],\n" +
                    "  \"summary\": \"笔记详细阐述了Elasticsearch中mapping的配置属性，包括enabled、index、index_options、norms和doc_values等参数的设置选项及作用。重点解释了各参数对索引构建、搜索、聚合分析、分词行为及内存消耗的影响，为优化Elasticsearch索引配置提供了技术参考。\",\n" +
                    "  \"raw_content\": \"# ES学习笔记\\n## Mapping属性配置\\n### enabled\\n- 控制字段是否参与搜索和聚合分析\\n- 缺省值：true（启用）\\n\\n### index\\n- 决定是否创建倒排索引（分词与否）\\n- true（缺省）或false\\n\\n### index_options\\n- 索引存储细节配置\\n\\n\\\"index_options\\\": \\\"docs\\\"  # 可选值：docs/freqs/positions/offsets\\\"\\n```\\n- 默认分词字段：positions，其他字段默认docs\\n\\n### norms\\n- 归一化参数开关\\n\\n\\\"norms\\\": {\\\"enable\\\": true, \\\"loading\\\": \\\"lazy\\\"}\\n```\\n- 影响评分计算，关闭可减少内存占用\\n\\n### doc_values\\n- 开启后支持聚合和排序分析\\n\\n学习方式：reading\",\n" +
                    "  \"suggested_title\": \"Elasticsearch Mapping配置参数详解\",\n" +
                    "  \"subject_area\": \"计算机科学（搜索引擎/数据库系统配置）\",\n" +
                    "  \"difficulty_level\": \"中级\",\n" +
                    "  \"difficulty_coefficient\": 0.7,\n" +
                    "  \"importance_level\": \"高\",\n" +
                    "  \"importance_coefficient\": 0.85,\n" +
                    "  \"quality_level\": \"中等\",\n" +
                    "  \"quality_coefficient\": 0.65,\n" +
                    "  \"learning_method_coefficient\": 0.55,\n" +
                    "  \"knowledge_type\": {\n" +
                    "    \"knowledge_type\": \"技术配置知识\",\n" +
                    "    \"knowledge_type_coefficient\": 0.75\n" +
                    "  },\n" +
                    "  \"base_wisdom_value\": 12.375,\n" +
                    "  \"calculation_breakdown\": {\n" +
                    "    \"difficulty\": 0.7 * 1.5,\n" +
                    "    \"importance\": 0.85 * 2.0,\n" +
                    "    \"quality\": 0.65 * 1.0,\n" +
                    "    \"knowledge_type\": 0.75 * 1.2,\n" +
                    "    \"learning_method\": 0.55 * 0.8\n" +
                    "  },\n" +
                    "  \"evaluation_summary\": \"该笔记聚焦Elasticsearch核心配置技术，对优化索引性能有关键作用。内容结构清晰但缺乏实践案例，建议结合配置实例学习。技术难度适中偏高，适合有基础的开发者参考。\"\n" +
                    "}";
                System.out.println("Mock响应内容：" + response);
                logger.info("AI模型API返回成功(Mock)");

                // 将Mock响应内容解析为JSON
                return JSONObject.parseObject(response);
            }


            // 构建提示词
            String prompt = buildPrompt(params);
            logger.info("调用AI模型API，提示词：{}", prompt);

            // 创建OpenAI客户端
            return requestModelAPI(prompt);
        } catch (Exception e) {
            logger.error("AI模型API调用异常", e);
            return null;
        }
    }

    @Nullable
    public JSONObject requestModelAPI(String prompt) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey("")
            .baseUrl(aiModelApiUrl)
            .build();

        // 构建请求参数
        ChatCompletionCreateParams chatParams = ChatCompletionCreateParams.builder()
            .addUserMessage(prompt)
            .model(aiModelName)
            .build();

        // 发送请求
        ChatCompletion chatCompletion = client.chat().completions().create(chatParams);
        // 解析响应
        String responseContent = chatCompletion.choices().get(0).message().content().get();
        System.out.println("原始响应内容：" + responseContent);

        // 清理响应内容，去掉think标签
        String cleanedContent = cleanResponseContent(responseContent);
        System.out.println("清理后的响应内容：" + cleanedContent);
        logger.info("AI模型API返回成功");

        // 将清理后的响应内容解析为JSON
        return JSONObject.parseObject(cleanedContent);
    }

    /**
     * 清理响应内容，去掉think标签和其他非JSON内容
     * @param responseContent 原始响应内容
     * @return 清理后的JSON内容
     */
    private String cleanResponseContent(String responseContent) {
        if (responseContent == null || responseContent.trim().isEmpty()) {
            return responseContent;
        }

        // 去掉think标签及其内容
        String cleaned = THINK_PATTERN.matcher(responseContent).replaceAll("");

        // 去掉可能的markdown代码块标记
        cleaned = cleaned.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "");

        // 查找JSON内容的开始和结束位置
        int jsonStart = cleaned.indexOf('{');
        int jsonEnd = cleaned.lastIndexOf('}');

        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            cleaned = cleaned.substring(jsonStart, jsonEnd + 1);
        }

        return cleaned.trim();
    }

    /**
     * 构建提示词
     * @param params 请求参数
     * @return 提示词
     */
    private String buildPrompt(Map<String, Object> params) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下学习笔记，并返回JSON格式的分析结果，包括以下内容：\n");
        prompt.append("1. 提取关键词(extracted_keywords)\n");
        prompt.append("2. 生成摘要(summary)\n");
        prompt.append("3. 将笔记转换为Markdown格式(raw_content)\n");
        prompt.append("4. 推荐标题(suggested_title)\n");
        prompt.append("5. 识别学科领域(subject_area)\n");
        prompt.append("6. 难度评估(difficulty_level和difficulty_coefficient)\n");
        prompt.append("7. 重要性评估(importance_level和importance_coefficient)\n");
        prompt.append("8. 质量评估(quality_level和quality_coefficient)\n");
        prompt.append("9. 学习方法系数(learning_method_coefficient)\n");
        prompt.append("10. 知识类型(knowledge_type和knowledge_type_coefficient)\n");
        prompt.append("11. 计算基础智慧值(base_wisdom_value)\n");
        prompt.append("12. 计算明细(calculation_breakdown)\n");
        prompt.append("13. 评估摘要(evaluation_summary)\n\n");

        // 添加笔记内容
        prompt.append("笔记内容：\n").append(params.get("content")).append("\n\n");

        // 添加标题（如果有）
        if (params.containsKey("title")) {
            prompt.append("笔记标题：").append(params.get("title")).append("\n\n");
        }

        // 添加学习方式
        prompt.append("学习方式：").append(params.get("learning_method")).append("\n\n");

        // 要求返回JSON格式
        prompt.append("请直接返回JSON格式的结果，不要包含任何其他文本、解释或思考过程。");

        return prompt.toString();
    }

    /**
     * 处理分析结果
     * @param note 笔记对象
     * @param result API返回结果
     */
    private void processAnalysisResult(LearningNote note, JSONObject result) {
        try {
            // 如果用户未提供标题，使用AI生成的标题
            if (note.getTitle() == null || note.getTitle().isEmpty()) {
                String suggestedTitle = result.getString("suggested_title");
                if (suggestedTitle != null && !suggestedTitle.isEmpty()) {
                    note.setTitle(suggestedTitle);
                } else {
                    // 如果AI也没有提供标题，使用内容的前20个字符
                    String content = note.getContent();
                    String defaultTitle = content.length() > 20 ? content.substring(0, 20) + "..." : content;
                    note.setTitle(defaultTitle);
                }
            }

            // 保存Markdown格式的笔记内容
            String rawContent = result.getString("raw_content");
            if (rawContent != null && !rawContent.isEmpty()) {
                note.setRawContent(rawContent);
            }

            // 更新笔记分析状态
            updateNoteAnalysisStatus(note, result);

            // 保存更新后的笔记到数据库
            LearningNote savedNote = learningNoteRepository.save(note);
            logger.info("笔记更新成功：{}", savedNote.getId());

            // 创建智慧值记录
            WisdomRecord wisdomRecord = createWisdomRecord(note, result);
            logger.info("创建智慧值记录：{}", JSON.toJSONString(wisdomRecord));

            // 保存智慧值记录到数据库
            WisdomRecord savedWisdomRecord = wisdomRecordRepository.save(wisdomRecord);
            logger.info("智慧值记录保存成功：{}", savedWisdomRecord.getId());

            logger.info("笔记分析完成：{}, 智慧值：{}", note.getId(), wisdomRecord.getBaseWisdomValue());
        } catch (Exception e) {
            logger.error("处理分析结果失败", e);
            handleAnalysisFailure(note, "处理分析结果失败：" + e.getMessage());
        }
    }

    /**
     * 创建智慧值记录
     * @param note 笔记对象
     * @param result API返回结果
     * @return 智慧值记录
     */
    private WisdomRecord createWisdomRecord(LearningNote note, JSONObject result) {
        WisdomRecord record = new WisdomRecord();
        record.setUserId(note.getUserId());
        record.setNoteId(note.getId());

        // 设置基础智慧值
        Double baseWisdomValue = result.getDouble("base_wisdom_value");
        if (baseWisdomValue == null || baseWisdomValue < 10) {
            baseWisdomValue = 100.0; // 默认值
        }
        record.setBaseWisdomValue(baseWisdomValue);
        record.setCurrentWisdomValue(baseWisdomValue); // 初始时当前值等于基础值

        // 设置难度系数
        record.setDifficultyLevel(result.getString("difficulty_level"));
        record.setDifficultyCoefficient(result.getDouble("difficulty_coefficient"));

        // 设置重要性系数
        record.setImportanceLevel(result.getString("importance_level"));
        record.setImportanceCoefficient(result.getDouble("importance_coefficient"));

        // 设置质量系数
        record.setQualityLevel(result.getString("quality_level"));
        record.setQualityCoefficient(result.getDouble("quality_coefficient"));

        // 设置学习方式
        record.setLearningMethod(note.getLearningMethod());
        record.setLearningMethodCoefficient(result.getDouble("learning_method_coefficient"));

        // 设置知识类型
        record.setKnowledgeType(result.getString("knowledge_type"));
        record.setKnowledgeTypeCoefficient(result.getDouble("knowledge_type_coefficient"));

        // 设置计算明细
        JSONObject breakdown = result.getJSONObject("calculation_breakdown");
        if (breakdown != null) {
            Map<String, String> calculationBreakdown = new HashMap<>();
            for (String key : breakdown.keySet()) {
                calculationBreakdown.put(key, breakdown.getString(key));
            }
            record.setCalculationBreakdown(calculationBreakdown);
        }

        // 设置评估摘要
        record.setEvaluationSummary(result.getString("evaluation_summary"));

        // 设置时间
        Date now = new Date();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        return record;
    }

    /**
     * 更新笔记分析状态
     * @param note 笔记对象
     * @param result API返回结果
     */
    private void updateNoteAnalysisStatus(LearningNote note, JSONObject result) {
        LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
        aiAnalysis.setStatus("completed");
        aiAnalysis.setAnalyzedAt(new Date());

        // 设置提取的关键词
        aiAnalysis.setExtractedKeywords(result.getJSONArray("extracted_keywords").toJavaList(String.class));

        // 设置学科领域
        aiAnalysis.setSubjectArea(result.getString("subject_area"));

        // 设置摘要
        aiAnalysis.setSummary(result.getString("summary"));

        // TODO: 设置知识点，需要根据API返回格式进行处理

        note.setAiAnalysis(aiAnalysis);
    }

    /**
     * 处理分析失败
     * @param note 笔记对象
     * @param errorMessage 错误信息
     */
    private void handleAnalysisFailure(LearningNote note, String errorMessage) {
        logger.error("笔记分析失败：{}, 错误：{}", note.getId(), errorMessage);

        // 如果用户未提供标题，使用内容的前20个字符
        if (note.getTitle() == null || note.getTitle().isEmpty()) {
            String content = note.getContent();
            String defaultTitle = content.length() > 20 ? content.substring(0, 20) + "..." : content;
            note.setTitle(defaultTitle);
        }

        // 更新笔记分析状态为失败
        LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
        aiAnalysis.setStatus("failed");
        aiAnalysis.setErrorMessage(errorMessage);
        aiAnalysis.setAnalyzedAt(new Date());
        note.setAiAnalysis(aiAnalysis);

        // 保存失败状态的笔记
        try {
            learningNoteRepository.save(note);
            logger.info("笔记失败状态保存成功：{}", note.getId());
        } catch (Exception e) {
            logger.error("保存笔记失败状态时出错：{}", e.getMessage());
        }

        // 使用降级方案计算智慧值
        WisdomRecord fallbackRecord = createFallbackWisdomRecord(note);

        // 保存降级方案的智慧值记录
        try {
            wisdomRecordRepository.save(fallbackRecord);
            logger.info("降级方案智慧值记录保存成功：{}", fallbackRecord.getId());
        } catch (Exception e) {
            logger.error("保存降级方案智慧值记录时出错：{}", e.getMessage());
        }

        logger.info("使用降级方案计算智慧值：{}", fallbackRecord.getBaseWisdomValue());
    }

    /**
     * 创建降级方案的智慧值记录
     * @param note 笔记对象
     * @return 智慧值记录
     */
    private WisdomRecord createFallbackWisdomRecord(LearningNote note) {
        WisdomRecord record = new WisdomRecord();
        record.setUserId(note.getUserId());
        record.setNoteId(note.getId());

        // 根据学习方式设置系数
        double methodCoefficient = 0.8; // 默认为reading
        String learningMethod = note.getLearningMethod();
        if (learningMethod != null) {
            switch (learningMethod) {
                case "practice":
                    methodCoefficient = 1.0;
                    break;
                case "hands-on":
                    methodCoefficient = 1.2;
                    break;
                case "creation":
                    methodCoefficient = 1.5;
                    break;
            }
        }

        // 计算基础智慧值：100 * 学习方式系数
        double baseWisdomValue = 100 * methodCoefficient;
        record.setBaseWisdomValue(baseWisdomValue);
        record.setCurrentWisdomValue(baseWisdomValue);

        // 设置默认系数
        record.setDifficultyLevel("intermediate");
        record.setDifficultyCoefficient(1.0);
        record.setImportanceLevel("general");
        record.setImportanceCoefficient(1.0);
        record.setQualityLevel("good");
        record.setQualityCoefficient(1.0);
        record.setLearningMethod(learningMethod != null ? learningMethod : "reading");
        record.setLearningMethodCoefficient(methodCoefficient);
        record.setKnowledgeType("understanding");
        record.setKnowledgeTypeCoefficient(1.0);

        // 设置评估摘要
        record.setEvaluationSummary("由于AI分析失败，使用默认智慧值计算方法");

        // 设置时间
        Date now = new Date();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        return record;
    }
}
