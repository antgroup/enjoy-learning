package com.knowledge.springboot.repository;

import com.knowledge.springboot.domain.KnowledgeTree;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 知识树存储库接口
 */
@Repository
public interface KnowledgeTreeRepository extends MongoRepository<KnowledgeTree, String> {
    
    /**
     * 根据用户ID查询知识树列表
     * @param userId 用户ID
     * @return 知识树列表
     */
    List<KnowledgeTree> findByUserId(String userId);
    
    /**
     * 根据树ID查询知识树
     * @param treeId 树ID
     * @return 知识树
     */
    Optional<KnowledgeTree> findByTreeId(Long treeId);
    
    /**
     * 根据用户ID和树ID查询知识树
     * @param userId 用户ID
     * @param treeId 树ID
     * @return 知识树
     */
    Optional<KnowledgeTree> findByUserIdAndTreeId(String userId, Long treeId);
    
    /**
     * 根据用户ID和名称查询知识树
     * @param userId 用户ID
     * @param name 知识树名称
     * @return 知识树
     */
    Optional<KnowledgeTree> findByUserIdAndName(String userId, String name);
}
