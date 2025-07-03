package com.knowledge.springboot.service.impl;

import com.alibaba.fastjson2.JSON;
import com.knowledge.springboot.domain.KnowledgeNode;
import com.knowledge.springboot.domain.KnowledgeTree;
import com.knowledge.springboot.domain.LearningNote;
import com.knowledge.springboot.domain.NoteKnowledgeAssociation;
import com.knowledge.springboot.dto.AssociateNotesRequest;
import com.knowledge.springboot.dto.AssociateNotesResponse;
import com.knowledge.springboot.dto.KnowledgeTreeDataResponse;
import com.knowledge.springboot.repository.KnowledgeNodeRepository;
import com.knowledge.springboot.repository.KnowledgeTreeRepository;
import com.knowledge.springboot.repository.LearningNoteRepository;
import com.knowledge.springboot.repository.NoteKnowledgeAssociationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * KnowledgeTreeService 测试类
 * 除了AI模型请求外，其他都使用真实数据库操作
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KnowledgeTreeServiceTest {

    @Autowired
    private KnowledgeTreeService knowledgeTreeService;

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

    // 测试数据
    private static final String TEST_USER_ID = "test_user_001";
    private static final String TEST_TREE_NAME = "测试知识树";
    private static final String TEST_TREE_DESCRIPTION = "这是一个用于测试的知识树";

    private static Long testTreeId;
    private static String testNoteId;
    private static KnowledgeTree testTree;
    private static List<KnowledgeNode> testNodes;

    // getSubtreeByNodeId
    @Test
    @DisplayName("测试获取子树")
    void testGetSubtreeByNodeId() {
//        KnowledgeTreeDataResponse.TreeNode child = knowledgeTreeService.getSubtreeByNodeId(
//            "68316957aa38b6042002c733", 8859564068760021104L, "child_1_8859564068760021104", false, null);
//        System.out.println("查询知识树：" + JSON.toJSONString(child));
    }

    /**
     * 测试创建知识树
     */
    @Test
    @Order(1)
    @DisplayName("测试创建知识树")
    void testCreateKnowledgeTree() {
        // 执行创建操作
        KnowledgeTree createdTree = knowledgeTreeService.createKnowledgeTree(
            "68316957aa38b6042002c733", TEST_TREE_NAME, TEST_TREE_DESCRIPTION);

        System.out.println("testCreateKnowledgeTree: " + JSON.toJSONString(createdTree));

        // 验证根节点是否创建
        List<KnowledgeNode> nodes = knowledgeNodeRepository.findByTreeIdAndUserId(
            createdTree.getTreeId(), TEST_USER_ID);

        System.out.println("testCreateKnowledgeTree: " + JSON.toJSONString(nodes));

        // 保存测试数据供后续测试使用
        testTreeId = createdTree.getTreeId();
        testTree = createdTree;
    }

    /**
     * 测试创建重名知识树（应该失败）
     */
    @Test
    @Order(2)
    @DisplayName("测试创建重名知识树")
    void testCreateDuplicateKnowledgeTree() {
        // 先创建一个知识树
        knowledgeTreeService.createKnowledgeTree(TEST_USER_ID, TEST_TREE_NAME, TEST_TREE_DESCRIPTION);

        // 尝试创建同名知识树，应该抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            knowledgeTreeService.createKnowledgeTree(TEST_USER_ID, TEST_TREE_NAME, TEST_TREE_DESCRIPTION);
        });

        assertTrue(exception.getMessage().contains("已存在同名知识树"));
    }

    /**
     * 测试获取用户知识树列表
     */
    @Test
    @Order(3)
    @DisplayName("测试获取用户知识树列表")
    void testGetUserKnowledgeTrees() {
//        KnowledgeTree tree1 = knowledgeTreeService.createKnowledgeTree(
//            TEST_USER_ID, "知识树1", "描述1");

        // 获取知识树列表
        List<KnowledgeTree> trees = knowledgeTreeService.getUserKnowledgeTrees("68316957aa38b6042002c733");
        System.out.println(JSON.toJSONString(trees));
    }

    /**
     * 测试获取空的知识树列表
     */
    @Test
    @Order(4)
    @DisplayName("测试获取空的知识树列表")
    void testGetEmptyKnowledgeTreeList() {
        List<KnowledgeTree> trees = knowledgeTreeService.getUserKnowledgeTrees("non_existent_user");

        assertNotNull(trees);
        assertTrue(trees.isEmpty());
    }

    /**
     * 测试获取知识树详细数据
     */
    @Test
    @Order(5)
    @DisplayName("测试获取知识树详细数据")
    void testGetUpdatedTreeData() {
        // 先创建知识树和一些节点
        KnowledgeTree tree = createTestTreeWithNodes("68328f6ffbac353969ca3b7c");
//        System.out.println("创建多节点知识树：" + JSON.toJSONString(tree));

        // 获取知识树数据
        KnowledgeTreeDataResponse response = knowledgeTreeService.getUpdatedTreeData(
            "68328f6ffbac353969ca3b7c", tree.getTreeId(), false, null);
        System.out.println("查询知识树：" + JSON.toJSONString(response));
    }

    /**
     * 测试获取知识树数据（包含笔记信息）
     */
    @Test
    @Order(6)
    @DisplayName("测试获取知识树数据（包含笔记信息）")
    void testGetTreeDataWithNoteInfo() {

        // 创建测试笔记和关联
        createTestNoteAndAssociation(5967969725434484586L);

        // 获取包含笔记信息的知识树数据
        KnowledgeTreeDataResponse response = knowledgeTreeService.getUpdatedTreeData(
            "68316957aa38b6042002c733", 5967969725434484586L, true, null);
        System.out.println("包含笔记的知识树：" + JSON.toJSONString(response));
    }

    /**
     * 测试获取知识树数据（限制深度）
     */
    @Test
    @Order(7)
    @DisplayName("测试获取知识树数据（限制深度）")
    void testGetTreeDataWithMaxDepth() {
        // 创建多层级的知识树
        KnowledgeTree tree = createTestTreeWithMultipleLevels();

        // 获取限制深度为1的数据
        KnowledgeTreeDataResponse response = knowledgeTreeService.getUpdatedTreeData(
            TEST_USER_ID, tree.getTreeId(), false, 1);

        // 验证深度限制
        assertNotNull(response);
        assertNotNull(response.getTree());

        // 验证根节点的子节点存在，但子节点的子节点应该为空或不存在
        if (response.getTree().getChildren() != null && !response.getTree().getChildren().isEmpty()) {
            for (KnowledgeTreeDataResponse.TreeNode child : response.getTree().getChildren()) {
                assertTrue(child.getLevel() <= 1);
                // 如果有子节点，验证深度限制
                if (child.getChildren() != null) {
                    assertTrue(child.getChildren().isEmpty() ||
                              child.getChildren().stream().allMatch(grandChild -> grandChild.getLevel() <= 1));
                }
            }
        }
    }

    /**
     *
     */
    @Test
    @Order(8)
    void testGetNonExistentTreeData() {
        KnowledgeTreeDataResponse updatedTreeData = knowledgeTreeService.getUpdatedTreeData("68316957aa38b6042002c733", 8484679910452901394L, false, null);
        System.out.println("查询知识树：" + JSON.toJSONString(updatedTreeData));
    }

    /**
     * 测试关联笔记到知识树
     */
    @Test
    @Order(9)
    @DisplayName("测试关联笔记到知识树")
    void testAssociateNotesToTree() {
        // 创建知识树和节点
//        KnowledgeTree tree = createTestTreeWithNodes();

        // 创建测试笔记
        LearningNote note = createTestNote();

        // 创建关联请求 - 使用String类型的笔记ID
        AssociateNotesRequest request = new AssociateNotesRequest();
        request.setUserId(TEST_USER_ID);
        request.setTreeId(2261913847432728488L);
        // 使用笔记的原始ID，不需要转换
        request.setNoteIds(Arrays.asList("f84e2f90-6151-424f-8ffc-bd1f44526c7e"));
        request.setAssociationMode("auto");
        request.setOverrideExisting(false);

        // 执行关联操作
        AssociateNotesResponse response = knowledgeTreeService.associateNotesToTree(request);
        System.out.println("关联笔记结果：" + JSON.toJSONString(response));

    }

    /**
     * 测试关联不存在的笔记
     */
    @Test
    @Order(10)
    @DisplayName("测试关联不存在的笔记")
    void testAssociateNonExistentNote() {
        // 创建知识树
//        KnowledgeTree tree = knowledgeTreeService.createKnowledgeTree(
//            TEST_USER_ID, TEST_TREE_NAME, TEST_TREE_DESCRIPTION);
//
//        // 创建关联请求（使用不存在的笔记ID）
//        AssociateNotesRequest request = new AssociateNotesRequest();
//        request.setUserId(TEST_USER_ID);
//        request.setTreeId(tree.getTreeId());
//        request.setNoteIds(Arrays.asList("999999"));
//        request.setAssociationMode("auto");
//
//        // 执行关联操作
//        AssociateNotesResponse response = knowledgeTreeService.associateNotesToTree(request);
//
//        // 验证响应 - 应该处理失败但不抛出异常
//        assertNotNull(response);
//        assertEquals(tree.getTreeId(), response.getTreeId());
//
//        // 验证处理结果为空或失败
//        assertTrue(response.getProcessedNotes().isEmpty() ||
//                  response.getProcessedNotes().stream().allMatch(note -> !"success".equals(note.getStatus())));
    }

    /**
     * 测试关联到不存在的知识树
     */
    @Test
    @Order(11)
    @DisplayName("测试关联到不存在的知识树")
    void testAssociateToNonExistentTree() {
        // 创建关联请求（使用不存在的知识树ID和笔记ID）
//        AssociateNotesRequest request = new AssociateNotesRequest();
//        request.setUserId(TEST_USER_ID);
//        request.setTreeId(999999L);
//        request.setNoteIds(Arrays.asList(123456L));
//
//        // 执行关联操作，应该抛出异常
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            knowledgeTreeService.associateNotesToTree(request);
//        });
//
//        assertTrue(exception.getMessage().contains("知识树不存在"));
    }

    /**
     * 测试删除知识树
     */
    @Test
    @Order(12)
    @DisplayName("测试删除知识树")
    void testDeleteKnowledgeTree() {
        // 创建知识树和相关数据
        KnowledgeTree tree = createTestTreeWithNodes("");
        createTestNoteAndAssociation(tree.getTreeId());

        // 验证数据存在
        assertTrue(knowledgeTreeRepository.findByUserIdAndTreeId(TEST_USER_ID, tree.getTreeId()).isPresent());
        assertFalse(knowledgeNodeRepository.findByTreeIdAndUserId(tree.getTreeId(), TEST_USER_ID).isEmpty());

        // 执行删除操作
        knowledgeTreeService.deleteKnowledgeTree(TEST_USER_ID, tree.getTreeId());

        // 验证知识树已删除
        assertFalse(knowledgeTreeRepository.findByUserIdAndTreeId(TEST_USER_ID, tree.getTreeId()).isPresent());

        // 验证相关节点已删除
        assertTrue(knowledgeNodeRepository.findByTreeIdAndUserId(tree.getTreeId(), TEST_USER_ID).isEmpty());

        // 验证关联记录已删除
        assertTrue(associationRepository.findByUserIdAndTreeId(TEST_USER_ID, tree.getTreeId()).isEmpty());
    }

    /**
     * 测试删除不存在的知识树
     */
    @Test
    @Order(13)
    @DisplayName("测试删除不存在的知识树")
    void testDeleteNonExistentTree() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            knowledgeTreeService.deleteKnowledgeTree(TEST_USER_ID, 999999L);
        });

        assertTrue(exception.getMessage().contains("知识树不存在或无权限"));
    }

    /**
     * 测试删除其他用户的知识树（权限测试）
     */
    @Test
    @Order(14)
    @DisplayName("测试删除其他用户的知识树")
    void testDeleteOtherUserTree() {
        // 创建知识树
        KnowledgeTree tree = knowledgeTreeService.createKnowledgeTree(
            TEST_USER_ID, TEST_TREE_NAME, TEST_TREE_DESCRIPTION);

        // 尝试用其他用户ID删除，应该失败
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            knowledgeTreeService.deleteKnowledgeTree("other_user", tree.getTreeId());
        });

        assertTrue(exception.getMessage().contains("知识树不存在或无权限"));

        // 验证知识树仍然存在
        assertTrue(knowledgeTreeRepository.findByUserIdAndTreeId(TEST_USER_ID, tree.getTreeId()).isPresent());
    }

    // 辅助方法

    /**
     * 清理测试数据
     */
    private void cleanupTestData() {
        try {
            // 删除测试用户的所有关联记录
            List<NoteKnowledgeAssociation> userAssociations = associationRepository.findAll().stream()
                .filter(assoc -> TEST_USER_ID.equals(assoc.getUserId()))
                .collect(java.util.stream.Collectors.toList());
            associationRepository.deleteAll(userAssociations);

            // 删除测试用户的所有节点
            List<KnowledgeNode> userNodes = knowledgeNodeRepository.findAll().stream()
                .filter(node -> TEST_USER_ID.equals(node.getUserId()))
                .collect(java.util.stream.Collectors.toList());
            knowledgeNodeRepository.deleteAll(userNodes);

            // 删除测试用户的所有知识树
            List<KnowledgeTree> userTrees = knowledgeTreeRepository.findByUserId(TEST_USER_ID);
            knowledgeTreeRepository.deleteAll(userTrees);

            // 删除测试笔记
            if (testNoteId != null) {
                learningNoteRepository.deleteById(testNoteId);
                testNoteId = null;
            }
        } catch (Exception e) {
            // 忽略清理错误
        }
    }

    /**
     * 创建带有节点的测试知识树
     */
    private KnowledgeTree createTestTreeWithNodes(String userId) {
        // 创建知识树
        KnowledgeTree tree = knowledgeTreeService.createKnowledgeTree(
            userId, "AI", TEST_TREE_DESCRIPTION);

        // 创建子节点
        KnowledgeNode childNode1 = new KnowledgeNode();
        childNode1.setNodeId("child_1_" + tree.getTreeId());
        childNode1.setTreeId(tree.getTreeId());
        childNode1.setUserId(userId);
        childNode1.setName("CodeFuse");
        childNode1.setCategory("测试分类");
        childNode1.setDescription("这是Codefuse的学习");
        childNode1.setParentNodeId("root_" + tree.getTreeId());
        childNode1.setLevel(1);
        childNode1.setPath(Arrays.asList("root_" + tree.getTreeId(), childNode1.getNodeId()));
        childNode1.setKeywords(Arrays.asList("编程工具", "笔记"));
        childNode1.setIsAutoGenerated(true);
        childNode1.setProgress(50);
        childNode1.setSourceNoteIds(Arrays.asList("test_note_1", "test_note_2"));

        KnowledgeNode childNode2 = new KnowledgeNode();
        childNode2.setNodeId("child_2_" + tree.getTreeId());
        childNode2.setTreeId(tree.getTreeId());
        childNode2.setUserId(userId);
        childNode2.setName("cursor");
        childNode2.setCategory("测试分类");
        childNode2.setDescription("cursor使用");
        childNode2.setParentNodeId("root_" + tree.getTreeId());
        childNode2.setLevel(1);
        childNode2.setPath(Arrays.asList("root_" + tree.getTreeId(), childNode2.getNodeId()));
        childNode2.setKeywords(Arrays.asList("测试", "cursor"));
        childNode2.setIsAutoGenerated(true);
        childNode2.setProgress(30);

        // 给 child_1_ 添加子节点
        KnowledgeNode childNode3 = new KnowledgeNode();
        childNode3.setNodeId("child_3_" + tree.getTreeId());
        childNode3.setTreeId(tree.getTreeId());
        childNode3.setUserId(userId);
        childNode3.setName("黑客松");
        childNode3.setCategory("测试分类");
        childNode3.setDescription("黑客松挑战作品");
        childNode3.setParentNodeId("child_1_" + tree.getTreeId());
        childNode3.setLevel(2);
        childNode3.setPath(Arrays.asList("root_" + tree.getTreeId(), "child_1_" + tree.getTreeId(), childNode3.getNodeId()));
        childNode3.setKeywords(Arrays.asList("测试", "节点"));
        childNode3.setIsAutoGenerated(true);
        childNode3.setProgress(40);

        knowledgeNodeRepository.save(childNode1);
        knowledgeNodeRepository.save(childNode2);
        knowledgeNodeRepository.save(childNode3);

        // 更新知识树统计
        tree.setTotalNodes(4L); // 根节点 + 2个子节点
        return knowledgeTreeRepository.save(tree);
    }

    /**
     * 创建多层级的测试知识树
     */
    private KnowledgeTree createTestTreeWithMultipleLevels() {
        KnowledgeTree tree = createTestTreeWithNodes(TEST_USER_ID);

        // 创建第三层节点
        KnowledgeNode grandChildNode = new KnowledgeNode();
        grandChildNode.setNodeId("grandchild_1_" + tree.getTreeId());
        grandChildNode.setTreeId(tree.getTreeId());
        grandChildNode.setUserId(TEST_USER_ID);
        grandChildNode.setName("孙子节点1");
        grandChildNode.setCategory("测试分类");
        grandChildNode.setDescription("测试孙子节点1");
        grandChildNode.setParentNodeId("child_1_" + tree.getTreeId());
        grandChildNode.setLevel(2);
        grandChildNode.setPath(Arrays.asList("root_" + tree.getTreeId(), "child_1_" + tree.getTreeId(), grandChildNode.getNodeId()));
        grandChildNode.setKeywords(Arrays.asList("测试", "孙子节点"));
        grandChildNode.setIsAutoGenerated(true);
        grandChildNode.setProgress(20);

        knowledgeNodeRepository.save(grandChildNode);

        // 更新知识树统计
        tree.setTotalNodes(4L);
        tree.setMaxDepth(2L);
        knowledgeTreeRepository.save(tree);

        return tree;
    }

    /**
     * 创建测试笔记
     */
    private LearningNote createTestNote() {
        LearningNote note = new LearningNote();
        note.setId(UUID.randomUUID().toString());
        note.setUserId(TEST_USER_ID);
        note.setTitle("测试笔记");
        note.setContent("这是一个测试笔记的内容，包含一些测试关键词。");
        note.setStatus("active");
        note.setCreatedAt(new Date());
        note.setUpdatedAt(new Date());

        // 设置AI分析结果
        LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
        aiAnalysis.setStatus("completed");
        aiAnalysis.setExtractedKeywords(Arrays.asList("测试", "关键词", "笔记"));
        aiAnalysis.setAnalyzedAt(new Date());
        note.setAiAnalysis(aiAnalysis);

        LearningNote savedNote = learningNoteRepository.save(note);
        testNoteId = savedNote.getId();
        return savedNote;
    }

    /**
     * 创建测试笔记和关联
     */
    private void createTestNoteAndAssociation(Long treeId) {
        LearningNote note = createTestNote();

        // 创建关联记录
        NoteKnowledgeAssociation association = new NoteKnowledgeAssociation();
        association.setAssociationId(UUID.randomUUID().toString());
        association.setUserId(TEST_USER_ID);
        // 直接使用笔记的原始ID，不需要转换
        association.setNoteId(note.getId());
        association.setTreeId(treeId);
        association.setNodeId("child_1_" + treeId);
        association.setKnowledgePointId("kp_test_001");
        association.setKnowledgePointName("测试知识点");
        association.setMatchType("keyword");
        association.setRelevanceScore(0.8);
        association.setAssociationReason("测试关联");
        association.setCreatedAt(new Date());

        associationRepository.save(association);
    }
}
