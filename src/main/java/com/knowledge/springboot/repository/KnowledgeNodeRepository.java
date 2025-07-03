package com.knowledge.springboot.repository;

import com.knowledge.springboot.domain.KnowledgeNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 知识节点存储库接口
 */
@Repository
public interface KnowledgeNodeRepository extends MongoRepository<KnowledgeNode, String> {
    
    /**
     * 根据节点ID查询知识节点
     * @param nodeId 节点ID
     * @return 知识节点
     */
    Optional<KnowledgeNode> findByNodeId(String nodeId);
    
    /**
     * 根据树ID查询所有节点
     * @param treeId 树ID
     * @return 节点列表
     */
    List<KnowledgeNode> findByTreeId(Long treeId);
    
    /**
     * 根据树ID和用户ID查询所有节点
     * @param treeId 树ID
     * @param userId 用户ID
     * @return 节点列表
     */
    List<KnowledgeNode> findByTreeIdAndUserId(Long treeId, String userId);
    
    /**
     * 根据父节点ID查询子节点
     * @param parentNodeId 父节点ID
     * @return 子节点列表
     */
    List<KnowledgeNode> findByParentNodeId(String parentNodeId);
    
    /**
     * 根据树ID和层级查询节点
     * @param treeId 树ID
     * @param level 层级
     * @return 节点列表
     */
    List<KnowledgeNode> findByTreeIdAndLevel(Long treeId, Integer level);
    
    /**
     * 根据关键词查询节点
     * @param keywords 关键词
     * @param treeId 树ID
     * @return 节点列表
     */
    @Query("{'keywords': {$in: ?0}, 'treeId': ?1}")
    List<KnowledgeNode> findByKeywordsInAndTreeId(List<String> keywords, Long treeId);
    
    /**
     * 根据笔记ID查询关联的节点
     * @param noteId 笔记ID
     * @return 节点列表
     */
    @Query("{'associatedNotes.noteId': ?0}")
    List<KnowledgeNode> findByAssociatedNotesNoteId(Long noteId);
    
    /**
     * 根据树ID查询根节点
     * @param treeId 树ID
     * @return 根节点
     */
    @Query("{'treeId': ?0, 'parentNodeId': null}")
    Optional<KnowledgeNode> findRootNodeByTreeId(Long treeId);
    
    /**
     * 根据名称和树ID查询节点（模糊匹配）
     * @param name 节点名称
     * @param treeId 树ID
     * @return 节点列表
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'treeId': ?1}")
    List<KnowledgeNode> findByNameContainingIgnoreCaseAndTreeId(String name, Long treeId);
    
    /**
     * 统计树的节点总数
     * @param treeId 树ID
     * @return 节点总数
     */
    long countByTreeId(Long treeId);
    
    /**
     * 查询树的最大层级
     * @param treeId 树ID
     * @return 最大层级
     */
    @Query(value = "{'treeId': ?0}", fields = "{'level': 1}")
    List<KnowledgeNode> findLevelsByTreeId(Long treeId);
}
