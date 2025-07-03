package com.knowledge.springboot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowledge.springboot.domain.KnowledgeNode;
import com.knowledge.springboot.domain.KnowledgeTree;
import com.knowledge.springboot.domain.LearningNote;
import com.knowledge.springboot.domain.NoteKnowledgeAssociation;
import com.knowledge.springboot.dto.AssociateNotesRequest;
import com.knowledge.springboot.dto.AssociateNotesResponse;
import com.knowledge.springboot.repository.KnowledgeNodeRepository;
import com.knowledge.springboot.repository.KnowledgeTreeRepository;
import com.knowledge.springboot.repository.LearningNoteRepository;
import com.knowledge.springboot.repository.NoteKnowledgeAssociationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author xiangkun.lxk
 * @version RelationService.java, v 0.1 2025/5/25 11:04 xiangkun.lxk
 */
@Service
public class RelationService {

    private static final Logger logger = LoggerFactory.getLogger(RelationService.class);

    @Autowired
    private KnowledgeTreeRepository knowledgeTreeRepository;

    @Autowired
    private KnowledgeNodeRepository knowledgeNodeRepository;

    @Autowired
    private NoteKnowledgeAssociationRepository associationRepository;

    @Autowired
    private LearningNoteRepository learningNoteRepository;

    @Autowired
    private AIAnalysisService aiAnalysisService;

    /**
     * 异步处理笔记关联任务
     * @param request 关联请求
     * @param tree 知识树
     */
    @Async("aiTaskExecutor")
    public void processNotesAsync(AssociateNotesRequest request, KnowledgeTree tree) {
        logger.info("开始异步处理笔记关联，树ID：{}", request.getTreeId());

        try {
            // 创建响应对象，用于收集处理结果
            AssociateNotesResponse asyncResponse = new AssociateNotesResponse();
            asyncResponse.setTreeId(request.getTreeId());
            asyncResponse.setProcessedNotes(new ArrayList<>());
            asyncResponse.setAssociationResults(new ArrayList<>());

            // 1. 获取知识树结构
            List<KnowledgeNode> allNodes = knowledgeNodeRepository.findByTreeIdAndUserId(request.getTreeId(), request.getUserId());

            // 2. 处理每个笔记
            for (String noteId : request.getNoteIds()) {
                processNoteAssociation(noteId, request, allNodes, asyncResponse);
            }

            // 3. 更新知识树统计信息
            updateTreeStatistics(tree, asyncResponse);

            // 4. 创建TreeUpdateSummary并记录日志
            int newNodesCount = (int) asyncResponse.getAssociationResults().stream()
                    .filter(result -> "created_new_node".equals(result.getAction()))
                    .count();

            int updatedNodesCount = (int) asyncResponse.getProcessedNotes().stream()
                    .filter(note -> "success".equals(note.getStatus()))
                    .count();

            logger.info("笔记关联异步处理完成，处理笔记数：{}，总关联数：{}，新节点数：{}",
                    asyncResponse.getProcessedNotes().size(),
                    asyncResponse.getAssociationResults().size(),
                    newNodesCount);

            // 5. 更新树的状态为success
            tree.setStatus("success");
            tree.setUpdatedAt(new Date());
            knowledgeTreeRepository.save(tree);

        } catch (Exception e) {
            logger.error("异步处理笔记关联失败", e);
            // 如果异步处理发生异常，将状态设置为failed
            tree.setStatus("failed");
            tree.setUpdatedAt(new Date());
            knowledgeTreeRepository.save(tree);
            logger.info("知识树状态已更新为failed，树ID：{}", request.getTreeId());
        }
    }

    /**
     * 处理单个笔记的关联
     */
    private void processNoteAssociation(String noteId, AssociateNotesRequest request,
                                        List<KnowledgeNode> allNodes, AssociateNotesResponse response) {
        try {
            // 获取笔记信息
            LearningNote note = learningNoteRepository.findByIdAndUserId(noteId, request.getUserId());
            if (note == null) {
                logger.warn("笔记不存在或无权限访问：{}", noteId);
                AssociateNotesResponse.ProcessedNote processedNote = new AssociateNotesResponse.ProcessedNote();
                processedNote.setNoteId(noteId);
                processedNote.setStatus("failed");
                response.getProcessedNotes().add(processedNote);
                return;
            }

            // 创建处理笔记对象
            AssociateNotesResponse.ProcessedNote processedNote = new AssociateNotesResponse.ProcessedNote();
            processedNote.setNoteId(noteId);
            processedNote.setTitle(note.getTitle());

            // 从笔记中提取知识点
            List<String> knowledgePoints = extractKnowledgePointsFromNote(note);
            if (knowledgePoints.isEmpty()) {
                logger.warn("笔记中未提取到知识点：{}", noteId);
                processedNote.setStatus("partial");
                processedNote.setAssociatedKnowledgePoints(0);
                processedNote.setNewNodesCreated(0);
                processedNote.setExistingNodesUpdated(0);
                response.getProcessedNotes().add(processedNote);
                return;
            }

            // 执行知识点匹配（通过大模型API）
            List<AssociateNotesResponse.AssociationResult> results = performKnowledgeMatching(
                    knowledgePoints, allNodes, request, note);

            // 统计关联结果
            int associatedCount = 0;
            int newNodesCreated = 0;
            int existingNodesUpdated = 0;

            for (AssociateNotesResponse.AssociationResult result : results) {
                if ("associated".equals(result.getAction())) {
                    associatedCount++;
                    existingNodesUpdated++;
                } else if ("created_new_node".equals(result.getAction())) {
                    associatedCount++;
                    newNodesCreated++;
                }
            }

            // 更新处理笔记状态
            processedNote.setStatus(associatedCount > 0 ? "success" : "partial");
            processedNote.setAssociatedKnowledgePoints(associatedCount);
            processedNote.setNewNodesCreated(newNodesCreated);
            processedNote.setExistingNodesUpdated(existingNodesUpdated);

            // 添加关联结果到响应
            response.getProcessedNotes().add(processedNote);
            response.getAssociationResults().addAll(results);

            logger.info("笔记关联处理完成：{}, 关联知识点数：{}, 新节点数：{}",
                    noteId, associatedCount, newNodesCreated);

        } catch (Exception e) {
            logger.error("处理笔记关联失败：{}", noteId, e);
            AssociateNotesResponse.ProcessedNote processedNote = new AssociateNotesResponse.ProcessedNote();
            processedNote.setNoteId(noteId);
            processedNote.setStatus("failed");
            response.getProcessedNotes().add(processedNote);
        }
    }

    /**
     * 更新知识树统计信息
     */
    private void updateTreeStatistics(KnowledgeTree tree, AssociateNotesResponse response) {
        try {
            // 统计新增节点数
            long newNodesCount = response.getAssociationResults().stream()
                    .filter(result -> "created_new_node".equals(result.getAction()))
                    .count();

            // 统计关联的笔记数
            Set<String> associatedNoteIds = response.getProcessedNotes().stream()
                    .filter(note -> "success".equals(note.getStatus()))
                    .map(AssociateNotesResponse.ProcessedNote::getNoteId)
                    .collect(Collectors.toSet());

            // 更新统计信息
            tree.setTotalNodes(tree.getTotalNodes() + newNodesCount);
            tree.setAssociatedNotesCount(tree.getAssociatedNotesCount() + (long) associatedNoteIds.size());
            tree.setLastUpdateTime(new Date());
            tree.setUpdatedAt(new Date());

            knowledgeTreeRepository.save(tree);

        } catch (Exception e) {
            logger.error("更新知识树统计信息失败", e);
        }
    }

    /**
     * 从笔记中提取知识点
     */
    private List<String> extractKnowledgePointsFromNote(LearningNote note) {
        List<String> knowledgePoints = new ArrayList<>();

        // 从AI分析结果中提取关键词作为知识点
        if (note.getAiAnalysis() != null && note.getAiAnalysis().getExtractedKeywords() != null) {
            knowledgePoints.addAll(note.getAiAnalysis().getExtractedKeywords());
        }

        // 如果AI分析提取的知识点为空，使用笔记标题作为知识点
        if (knowledgePoints.isEmpty() && note.getTitle() != null && !note.getTitle().isEmpty()) {
            knowledgePoints.add(note.getTitle());
        }

        return knowledgePoints.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 执行知识点匹配（调用AI）
     */
    private List<AssociateNotesResponse.AssociationResult> performKnowledgeMatching(
            List<String> knowledgePoints, List<KnowledgeNode> allNodes,
            AssociateNotesRequest request, LearningNote note) {

        List<AssociateNotesResponse.AssociationResult> results = new ArrayList<>();

        try {
            // 构建知识树结构信息
            String treeStructure = buildTreeStructureForAI(allNodes);

            // 构建知识点信息
            JSONArray knowledgePointsArray = new JSONArray();
            for (int i = 0; i < knowledgePoints.size(); i++) {
                JSONObject kp = new JSONObject();
                kp.put("id", "kp_" + i);
                kp.put("name", knowledgePoints.get(i));
                // 添加笔记内容的相关片段，帮助大模型更好地理解知识点
                if (note.getContent() != null) {
                    // 尝试找到与知识点相关的内容片段
                    String content = note.getContent();
                    int index = content.toLowerCase().indexOf(knowledgePoints.get(i).toLowerCase());
                    if (index >= 0) {
                        int start = Math.max(0, index - 50);
                        int end = Math.min(content.length(), index + knowledgePoints.get(i).length() + 50);
                        kp.put("context", content.substring(start, end));
                    }
                }
                knowledgePointsArray.add(kp);
            }

            // 构建完整的请求数据，包含笔记信息
            JSONObject requestData = new JSONObject();
            requestData.put("knowledge_points", knowledgePointsArray);
            requestData.put("tree_structure", JSONObject.parseObject(treeStructure));
            requestData.put("note_title", note.getTitle());
            requestData.put("note_id", note.getId());

            // 添加笔记摘要（如果有）
            if (note.getAiAnalysis() != null && note.getAiAnalysis().getSummary() != null) {
                requestData.put("note_summary", note.getAiAnalysis().getSummary());
            } else if (note.getContent() != null) {
                // 如果没有摘要，使用内容的前200个字符
                String content = note.getContent();
                requestData.put("note_summary", content.length() > 200 ?
                        content.substring(0, 200) + "..." : content);
            }

            // 调用AI匹配服务
            JSONObject matchingResult = callKnowledgeMatchingAPI(requestData.toJSONString(), false);

            // 解析匹配结果并执行关联
            if (matchingResult != null) {
                results = processMatchingResults(matchingResult, request, note, allNodes);
            }

        } catch (Exception e) {
            logger.error("知识点匹配失败", e);
            // 使用降级方案：简单的关键词匹配
//            results = performSimpleKeywordMatching(knowledgePoints, allNodes, request, note);
        }

        return results;
    }

    /**
     * 构建知识树结构信息供AI分析
     */
    private String buildTreeStructureForAI(List<KnowledgeNode> allNodes) {
        JSONObject treeStructure = new JSONObject();
        JSONArray nodes = new JSONArray();

        for (KnowledgeNode node : allNodes) {
            JSONObject nodeInfo = new JSONObject();
            nodeInfo.put("nodeId", node.getNodeId());
            nodeInfo.put("name", node.getName());
            nodeInfo.put("level", node.getLevel());
            nodeInfo.put("parentNodeId", node.getParentNodeId());
            nodeInfo.put("keywords", node.getKeywords());
            nodeInfo.put("description", node.getDescription());
            nodeInfo.put("category", node.getCategory());
            // 添加节点路径信息
            nodeInfo.put("path", node.getPath());
            nodes.add(nodeInfo);
        }

        treeStructure.put("nodes", nodes);
        return treeStructure.toJSONString();
    }

    /**
     * 调用知识匹配API
     */
    private JSONObject callKnowledgeMatchingAPI(String requestData, boolean mock) {
        try {
            // 构建提示词
            String prompt = buildKnowledgeMatchingPrompt(requestData);
            logger.info("调用AI模型API，提示词：{}", prompt);

            // 将清理后的响应内容解析为JSON
            if (!mock) {
                return aiAnalysisService.requestModelAPI(prompt);
            }

            // 这里使用Mock数据进行演示，符合简化的返回格式
            String mockResponse = "{\n" +
                "  \"knowledgePoints\": [\n" +
                "    {\n" +
                "      \"knowledgePointId\": \"kp_0\",\n" +
                "      \"knowledgePointName\": \"测试\",\n" +
                "      \"targetNodeId\": \"child_1_2261913847432728488\",\n" +
                "      \"newNodeName\": \"测试\",\n" +
                "      \"relevanceScore\": 0.85\n" +
                "    }\n" +
                "  ]\n" +
                "}";

            return JSONObject.parseObject(mockResponse);

        } catch (Exception e) {
            logger.error("调用知识匹配API失败", e);
            return null;
        }
    }

    /**
     * 构建知识匹配提示词
     */
    private String buildKnowledgeMatchingPrompt(String requestData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的知识结构分析专家，擅长将学习笔记中的知识点关联到知识树的合适位置。\n\n");
        prompt.append("**任务**：分析笔记中的知识点，为每个知识点创建新节点并确定其在知识树中的位置\n\n");
        prompt.append("**输入信息**：\n\n");
        prompt.append("**笔记知识点和知识树结构**：\n");
        prompt.append(requestData).append("\n\n");
        prompt.append("**分析要求**：\n\n");
        prompt.append("1. **节点创建原则**：\n");
        prompt.append("   - 为每个识别出的知识点创建新节点\n");
        prompt.append("   - 确定每个新节点应该挂载在哪个现有节点下\n");
        prompt.append("   - 如果知识点之间有层级关系，可以让后续节点挂载在前面创建的节点下\n\n");

        prompt.append("2. **返回格式**：\n");
        prompt.append("   - 返回一个知识点列表，按处理顺序排列\n");
        prompt.append("   - 如果知识点之间有依赖关系，请确保被依赖的节点排在前面\n\n");

        prompt.append("**请按以下JSON格式返回分析结果**：\n");
        prompt.append("```json\n");
        prompt.append("{\n");
        prompt.append("  \"knowledgePoints\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"knowledgePointId\": \"kp_001\",\n");
        prompt.append("      \"knowledgePointName\": \"Java语法\",\n");
        prompt.append("      \"targetNodeId\": \"node_001\",\n");
        prompt.append("      \"newNodeName\": \"Java语法\",\n");
        prompt.append("      \"relevanceScore\": 0.85\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        prompt.append("```\n");

        return prompt.toString();
    }

    /**
     * 处理匹配结果并执行关联
     */
    private List<AssociateNotesResponse.AssociationResult> processMatchingResults(
            JSONObject matchingResult, AssociateNotesRequest request,
            LearningNote note, List<KnowledgeNode> allNodes) {

        List<AssociateNotesResponse.AssociationResult> results = new ArrayList<>();
        Map<String, String> newNodeIdMapping = new HashMap<>();  // 存储临时ID到实际ID的映射

        // 处理知识点列表
        JSONArray knowledgePoints = matchingResult.getJSONArray("knowledgePoints");
        if (knowledgePoints != null) {
            // 按顺序处理每个知识点
            for (int i = 0; i < knowledgePoints.size(); i++) {
                JSONObject point = knowledgePoints.getJSONObject(i);
                String knowledgePointId = point.getString("knowledgePointId");
                String knowledgePointName = point.getString("knowledgePointName");
                final String originalTargetNodeId = point.getString("targetNodeId");
                String newNodeName = point.getString("newNodeName");
                Double relevanceScore = point.getDoubleValue("relevanceScore");

                // 检查targetNodeId是否为本次新创建的节点
                String targetNodeId = originalTargetNodeId;
                if (newNodeIdMapping.containsKey(targetNodeId)) {
                    targetNodeId = newNodeIdMapping.get(targetNodeId);
                }

                // 验证父节点是否存在
                KnowledgeNode parentNode = null;
                final String effectiveTargetNodeId = targetNodeId;
                if (effectiveTargetNodeId != null && !effectiveTargetNodeId.isEmpty()) {
                    parentNode = allNodes.stream()
                            .filter(node -> node.getNodeId().equals(effectiveTargetNodeId))
                            .findFirst()
                            .orElse(null);
                }

                // 如果父节点不存在，使用根节点
                if (parentNode == null) {
                    parentNode = allNodes.stream()
                            .filter(node -> node.getParentNodeId() == null)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("未找到根节点"));
                    logger.warn("未找到指定的父节点 {}，使用根节点作为父节点", targetNodeId);
                }

                // 创建新节点
                KnowledgeNode newNode = new KnowledgeNode();
                String newNodeId = UUID.randomUUID().toString().replace("-", "");
                newNode.setNodeId(newNodeId);
                newNode.setTreeId(request.getTreeId());
                newNode.setUserId(request.getUserId());
                newNode.setName(newNodeName);
                newNode.setCategory("自动创建");
                newNode.setDescription(knowledgePointName);
                newNode.setParentNodeId(parentNode.getNodeId());
                newNode.setLevel(parentNode.getLevel() + 1);

                // 设置路径
                List<String> path = new ArrayList<>(parentNode.getPath());
                path.add(newNode.getNodeId());
                newNode.setPath(path);

                // 设置关键词
                newNode.setKeywords(Collections.singletonList(knowledgePointName));
                newNode.setIsAutoGenerated(true);
                newNode.setProgress(0);

                // 关联笔记
                List<KnowledgeNode.AssociatedNote> associatedNotes = new ArrayList<>();
                KnowledgeNode.AssociatedNote associatedNote = new KnowledgeNode.AssociatedNote();
                associatedNote.setNoteId(note.getId());
                associatedNote.setTitle(note.getTitle());
                associatedNote.setRelevanceScore(relevanceScore);
                associatedNote.setAssociatedKnowledgePoints(Collections.singletonList(knowledgePointName));
                associatedNote.setAssociationTime(new Date());
                associatedNotes.add(associatedNote);
                newNode.setAssociatedNotes(associatedNotes);

                // 保存节点
                knowledgeNodeRepository.save(newNode);
                allNodes.add(newNode); // 添加到内存中的节点列表

                // 添加临时ID映射
                newNodeIdMapping.put(knowledgePointId, newNode.getNodeId());

                // 创建关联记录
                NoteKnowledgeAssociation association = new NoteKnowledgeAssociation();
                association.setId(UUID.randomUUID().toString().replace("-", ""));
                association.setUserId(request.getUserId());
                association.setTreeId(request.getTreeId());
                association.setNodeId(newNode.getNodeId());
                association.setNoteId(note.getId());
                association.setKnowledgePointName(knowledgePointName);
                association.setRelevanceScore(relevanceScore);
                association.setMatchingKeywords(Collections.singletonList(knowledgePointName));
                association.setCreatedAt(new Date());

                associationRepository.save(association);

                // 添加关联结果
                AssociateNotesResponse.AssociationResult result = new AssociateNotesResponse.AssociationResult();
                result.setKnowledgePointId(knowledgePointId);
                result.setKnowledgePointName(knowledgePointName);
                result.setAction("created_new_node");
                result.setNewNodeId(newNode.getNodeId());
                result.setParentNodeId(parentNode.getNodeId());
                result.setTargetNodeId(originalTargetNodeId);
                result.setTargetNodeName(parentNode.getName());
                result.setRelevanceScore(relevanceScore);
                results.add(result);

                logger.info("创建新节点并关联笔记：笔记ID={}，节点ID={}，节点名称={}", note.getId(), newNode.getNodeId(), newNode.getName());
            }
        }

        return results;
    }
}
