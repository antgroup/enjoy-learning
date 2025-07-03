package com.knowledge.springboot.repository;

import com.knowledge.springboot.domain.NoteKnowledgeAssociation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 笔记知识点关联存储库接口
 */
@Repository
public interface NoteKnowledgeAssociationRepository extends MongoRepository<NoteKnowledgeAssociation, String> {
    
    /**
     * 根据关联ID查询关联记录
     * @param associationId 关联ID
     * @return 关联记录
     */
    Optional<NoteKnowledgeAssociation> findByAssociationId(String associationId);
    
    /**
     * 根据用户ID和笔记ID查询关联记录
     * @param userId 用户ID
     * @param noteId 笔记ID
     * @return 关联记录列表
     */
    List<NoteKnowledgeAssociation> findByUserIdAndNoteId(String userId, Long noteId);
    
    /**
     * 根据树ID和节点ID查询关联记录
     * @param treeId 树ID
     * @param nodeId 节点ID
     * @return 关联记录列表
     */
    List<NoteKnowledgeAssociation> findByTreeIdAndNodeId(Long treeId, String nodeId);
    
    /**
     * 根据知识点ID查询关联记录
     * @param knowledgePointId 知识点ID
     * @return 关联记录列表
     */
    List<NoteKnowledgeAssociation> findByKnowledgePointId(String knowledgePointId);
    
    /**
     * 根据笔记ID查询关联记录
     * @param noteId 笔记ID
     * @return 关联记录列表
     */
    List<NoteKnowledgeAssociation> findByNoteId(Long noteId);
    
    /**
     * 根据节点ID查询关联记录
     * @param nodeId 节点ID
     * @return 关联记录列表
     */
    List<NoteKnowledgeAssociation> findByNodeId(String nodeId);
    
    /**
     * 根据用户ID和树ID查询关联记录
     * @param userId 用户ID
     * @param treeId 树ID
     * @return 关联记录列表
     */
    List<NoteKnowledgeAssociation> findByUserIdAndTreeId(String userId, Long treeId);
    
    /**
     * 根据匹配类型查询关联记录
     * @param matchType 匹配类型
     * @return 关联记录列表
     */
    List<NoteKnowledgeAssociation> findByMatchType(String matchType);
    
    /**
     * 根据用户ID、笔记ID和知识点ID查询关联记录
     * @param userId 用户ID
     * @param noteId 笔记ID
     * @param knowledgePointId 知识点ID
     * @return 关联记录
     */
    Optional<NoteKnowledgeAssociation> findByUserIdAndNoteIdAndKnowledgePointId(String userId, Long noteId, String knowledgePointId);
    
    /**
     * 统计树的关联总数
     * @param treeId 树ID
     * @return 关联总数
     */
    long countByTreeId(Long treeId);
    
    /**
     * 统计笔记的关联总数
     * @param noteId 笔记ID
     * @return 关联总数
     */
    long countByNoteId(Long noteId);
}
