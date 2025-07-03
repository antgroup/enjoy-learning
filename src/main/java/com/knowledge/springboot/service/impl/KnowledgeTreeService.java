package com.knowledge.springboot.service.impl;

import com.knowledge.springboot.domain.KnowledgeNode;
import com.knowledge.springboot.domain.KnowledgeTree;
import com.knowledge.springboot.domain.NoteKnowledgeAssociation;
import com.knowledge.springboot.dto.AssociateNotesRequest;
import com.knowledge.springboot.dto.AssociateNotesResponse;
import com.knowledge.springboot.dto.KnowledgeTreeDataResponse;
import com.knowledge.springboot.repository.KnowledgeNodeRepository;
import com.knowledge.springboot.repository.KnowledgeTreeRepository;
import com.knowledge.springboot.repository.LearningNoteRepository;
import com.knowledge.springboot.repository.NoteKnowledgeAssociationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 知识树服务实现类
 */
@Service
public class KnowledgeTreeService {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeTreeService.class);

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

    @Autowired
    private RelationService relationService;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Random random = new Random();

    /**
     * 关联笔记到知识树
     */
    public AssociateNotesResponse associateNotesToTree(AssociateNotesRequest request) {
        logger.info("开始关联笔记到知识树，用户ID：{}，树ID：{}，笔记数量：{}",
                   request.getUserId(), request.getTreeId(), request.getNoteIds().size());

        AssociateNotesResponse response = new AssociateNotesResponse();
        response.setTreeId(request.getTreeId());
        response.setProcessedNotes(new ArrayList<>());
        response.setAssociationResults(new ArrayList<>());

        KnowledgeTree tree = null;
        try {
            // 1. 验证知识树是否存在
            tree = knowledgeTreeRepository.findByUserIdAndTreeId(request.getUserId(), request.getTreeId())
                    .orElseThrow(() -> new RuntimeException("知识树不存在"));

            // 2. 更新树的状态为processing
            tree.setStatus("processing");
            tree.setUpdatedAt(new Date());
            knowledgeTreeRepository.save(tree);
            logger.info("知识树状态已更新为processing，树ID：{}", request.getTreeId());

            // 3. 创建临时的树更新摘要
            AssociateNotesResponse.TreeUpdateSummary summary = new AssociateNotesResponse.TreeUpdateSummary();
            summary.setTotalNodes(tree.getTotalNodes().intValue());
            summary.setNewNodesCount(0);
            summary.setUpdatedNodesCount(0);
            summary.setTotalAssociations(0);
            response.setTreeUpdateSummary(summary);

            // 4. 设置响应时间
            response.setProcessTime(dateFormat.format(new Date()));

            // 5. 异步处理笔记关联
            relationService.processNotesAsync(request, tree);

            logger.info("笔记关联请求已接受，树ID：{}，异步处理中", request.getTreeId());

        } catch (Exception e) {
            logger.error("启动笔记关联到知识树任务失败", e);
            // 如果发生异常，将状态设置为failed
            if (tree != null) {
                tree.setStatus("failed");
                tree.setUpdatedAt(new Date());
                knowledgeTreeRepository.save(tree);
                logger.info("知识树状态已更新为failed，树ID：{}", request.getTreeId());
            }
            throw new RuntimeException("关联笔记失败：" + e.getMessage());
        }

        return response;
    }

    /**
     * 获取更新后的知识树数据
     */
    public KnowledgeTreeDataResponse getUpdatedTreeData(String userId, Long treeId, Boolean includeNoteInfo, Integer maxDepth) {
        logger.info("获取知识树数据，用户ID：{}，树ID：{}", userId, treeId);

        try {
            // 获取知识树信息
            KnowledgeTree tree = knowledgeTreeRepository.findByUserIdAndTreeId(userId, treeId)
                    .orElseThrow(() -> new RuntimeException("知识树不存在"));

            // 获取所有节点
            List<KnowledgeNode> allNodes = knowledgeNodeRepository.findByTreeIdAndUserId(treeId, userId);

            // 构建响应
            KnowledgeTreeDataResponse response = new KnowledgeTreeDataResponse();
            response.setTreeId(treeId);
            response.setName(tree.getName());
            response.setLastUpdateTime(dateFormat.format(tree.getLastUpdateTime()));
            response.setTotalNodes(tree.getTotalNodes().intValue());
            response.setAssociatedNotesCount(tree.getAssociatedNotesCount().intValue());

            // 构建树结构
            KnowledgeTreeDataResponse.TreeNode rootNode = buildTreeStructure(allNodes, includeNoteInfo, maxDepth, tree);
            response.setTree(rootNode);

            return response;

        } catch (Exception e) {
            logger.error("获取知识树数据失败", e);
            throw new RuntimeException("获取知识树数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的知识树列表
     */
    public List<KnowledgeTree> getUserKnowledgeTrees(String userId) {
        logger.info("获取用户知识树列表，用户ID：{}", userId);
        return knowledgeTreeRepository.findByUserId(userId);
    }

    /**
     * 创建新的知识树
     */
    public KnowledgeTree createKnowledgeTree(String userId, String name, String description) {
        logger.info("创建知识树，用户ID：{}，名称：{}", userId, name);

        // 检查是否已存在同名知识树
        Optional<KnowledgeTree> existing = knowledgeTreeRepository.findByUserIdAndName(userId, name);
        if (existing.isPresent()) {
            throw new RuntimeException("已存在同名知识树");
        }

        // 创建知识树
        KnowledgeTree tree = new KnowledgeTree();
        // 使用Random生成正整数ID，确保是Long类型
        Long treeId = Math.abs(random.nextLong() % (Long.MAX_VALUE - 1000000L)) + 1000000L;
        tree.setTreeId(treeId);
        tree.setUserId(userId);
        tree.setName(name);
        tree.setDescription(description);
        tree.setTotalNodes(1L); // 包含根节点
        tree.setAssociatedNotesCount(0L);
        tree.setLastUpdateTime(new Date());
        tree.setCreatedAt(new Date());
        tree.setUpdatedAt(new Date());

        // 设置根节点ID
        String rootNodeId = "root_" + treeId;
        tree.setRootNodeId(rootNodeId);

        KnowledgeTree savedTree = knowledgeTreeRepository.save(tree);

        // 创建根节点
        createRootNode(savedTree);

        return savedTree;
    }

    /**
     * 删除知识树
     */
    public void deleteKnowledgeTree(String userId, Long treeId) {
        logger.info("删除知识树，用户ID：{}，树ID：{}", userId, treeId);

        // 验证知识树存在且属于用户
        KnowledgeTree tree = knowledgeTreeRepository.findByUserIdAndTreeId(userId, treeId)
                .orElseThrow(() -> new RuntimeException("知识树不存在或无权限"));

        // 删除所有节点
        List<KnowledgeNode> nodes = knowledgeNodeRepository.findByTreeIdAndUserId(treeId, userId);
        knowledgeNodeRepository.deleteAll(nodes);

        // 删除所有关联记录
        List<NoteKnowledgeAssociation> associations = associationRepository.findByUserIdAndTreeId(userId, treeId);
        associationRepository.deleteAll(associations);

        // 删除知识树
        knowledgeTreeRepository.delete(tree);
    }

    // 私有方法实现

    /**
     * 创建根节点
     */
    private void createRootNode(KnowledgeTree tree) {
        KnowledgeNode rootNode = new KnowledgeNode();
        rootNode.setNodeId("root_" + tree.getTreeId());
        rootNode.setTreeId(tree.getTreeId());
        rootNode.setUserId(tree.getUserId());
        rootNode.setName(tree.getName() + " - 根节点");
        rootNode.setCategory("根节点");
        rootNode.setDescription("知识树的根节点");
        rootNode.setParentNodeId(null);
        rootNode.setLevel(0);
        rootNode.setPath(Arrays.asList(rootNode.getNodeId()));
        rootNode.setIsAutoGenerated(false);

        knowledgeNodeRepository.save(rootNode);
    }

    /**
     * 构建树结构
     */
    private KnowledgeTreeDataResponse.TreeNode buildTreeStructure(List<KnowledgeNode> allNodes, Boolean includeNoteInfo, Integer maxDepth, KnowledgeTree tree) {
        // 查找根节点
        KnowledgeNode rootNode = allNodes.stream()
                .filter(node -> node.getParentNodeId() == null)
                .findFirst()
                .orElse(null);

        if (rootNode == null) {
            throw new RuntimeException("未找到根节点");
        }

        KnowledgeTreeDataResponse.TreeNode rootTreeNode = convertToTreeNode(rootNode, allNodes, includeNoteInfo, maxDepth, 0);

        // 3. 如果树的状态为processing，则在根节点下增加一个名为"？"的节点，提示用户正在构建知识网络
        if ("processing".equals(tree.getStatus())) {
            KnowledgeTreeDataResponse.TreeNode processingNode = new KnowledgeTreeDataResponse.TreeNode();
            processingNode.setNodeId("processing_indicator");
            processingNode.setName("？");
            processingNode.setLevel(1);
            processingNode.setProgress(0);
            processingNode.setDescription("正在构建知识网络，请稍候...");
            processingNode.setKeywords(Arrays.asList("处理中", "构建中"));
            processingNode.setAssociatedNotesCount(0);
            processingNode.setIsAutoGenerated(true);
            processingNode.setSourceNoteIds(new ArrayList<>());
            processingNode.setChildren(new ArrayList<>());

            // 将处理中节点添加到根节点的子节点列表中
            if (rootTreeNode.getChildren() == null) {
                rootTreeNode.setChildren(new ArrayList<>());
            }
            rootTreeNode.getChildren().add(0, processingNode); // 添加到第一个位置

            logger.info("知识树处于processing状态，已添加处理中提示节点");
            rootTreeNode.setStatus(tree.getStatus());
        }


        return rootTreeNode;
    }

    /**
     * 转换为树节点DTO
     */
    private KnowledgeTreeDataResponse.TreeNode convertToTreeNode(KnowledgeNode node, List<KnowledgeNode> allNodes,
                                                               Boolean includeNoteInfo, Integer maxDepth, int currentDepth) {
        KnowledgeTreeDataResponse.TreeNode treeNode = new KnowledgeTreeDataResponse.TreeNode();
        treeNode.setNodeId(node.getNodeId());
        treeNode.setName(node.getName());
        treeNode.setLevel(node.getLevel());
        treeNode.setProgress(node.getProgress());
        treeNode.setDescription(node.getDescription());
        treeNode.setKeywords(node.getKeywords());
        treeNode.setIsAutoGenerated(node.getIsAutoGenerated());
        treeNode.setSourceNoteIds(node.getSourceNoteIds());

        // 设置关联笔记数量
        if (node.getAssociatedNotes() != null) {
            treeNode.setAssociatedNotesCount(node.getAssociatedNotes().size());

            // 如果需要包含笔记信息
            if (includeNoteInfo != null && includeNoteInfo) {
                List<KnowledgeTreeDataResponse.AssociatedNoteInfo> noteInfos = node.getAssociatedNotes().stream()
                        .map(associatedNote -> {
                            KnowledgeTreeDataResponse.AssociatedNoteInfo noteInfo = new KnowledgeTreeDataResponse.AssociatedNoteInfo();
                            noteInfo.setNoteId(associatedNote.getNoteId());
                            noteInfo.setTitle(associatedNote.getTitle());
                            noteInfo.setRelevanceScore(associatedNote.getRelevanceScore());
                            noteInfo.setAssociatedKnowledgePoints(associatedNote.getAssociatedKnowledgePoints());
                            return noteInfo;
                        })
                        .collect(Collectors.toList());
                treeNode.setAssociatedNotes(noteInfos);
            }
        } else {
            treeNode.setAssociatedNotesCount(0);
        }

        // 递归构建子节点（如果未达到最大深度限制）
        if (maxDepth == null || currentDepth < maxDepth) {
            List<KnowledgeNode> children = allNodes.stream()
                    .filter(n -> node.getNodeId().equals(n.getParentNodeId()))
                    .collect(Collectors.toList());

            List<KnowledgeTreeDataResponse.TreeNode> childNodes = children.stream()
                    .map(child -> convertToTreeNode(child, allNodes, includeNoteInfo, maxDepth, currentDepth + 1))
                    .collect(Collectors.toList());
            treeNode.setChildren(childNodes);
        }

        return treeNode;
    }

    /**
     * 根据节点ID获取子树
     */
    public KnowledgeTreeDataResponse getSubtreeByNodeId(String userId, Long treeId, String nodeId, Boolean includeNoteInfo, Integer maxDepth) {
        logger.info("获取知识树子树，用户ID：{}，树ID：{}，节点ID：{}", userId, treeId, nodeId);

        try {
            // 获取知识树信息
            KnowledgeTree tree = knowledgeTreeRepository.findByUserIdAndTreeId(userId, treeId)
                    .orElseThrow(() -> new RuntimeException("知识树不存在"));

            // 获取所有节点
            List<KnowledgeNode> allNodes = knowledgeNodeRepository.findByTreeIdAndUserId(treeId, userId);

            // 查找指定的节点
            KnowledgeNode targetNode = allNodes.stream()
                    .filter(node -> node.getNodeId().equals(nodeId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("未找到指定节点"));

            // 构建完整的响应，保持与getUpdatedTreeData一致的结构
            KnowledgeTreeDataResponse response = new KnowledgeTreeDataResponse();
            response.setTreeId(treeId);
            response.setName(tree.getName());
            response.setLastUpdateTime(dateFormat.format(tree.getLastUpdateTime()));
            response.setTotalNodes(tree.getTotalNodes().intValue());
            response.setAssociatedNotesCount(tree.getAssociatedNotesCount().intValue());

            // 构建子树
            KnowledgeTreeDataResponse.TreeNode subtreeNode = convertToTreeNode(targetNode, allNodes, includeNoteInfo, maxDepth, 0);
            response.setTree(subtreeNode);

            return response;

        } catch (Exception e) {
            logger.error("获取知识树子树数据失败", e);
            throw new RuntimeException("获取知识树子树数据失败：" + e.getMessage());
        }
    }

}
