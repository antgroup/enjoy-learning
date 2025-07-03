package com.knowledge.springboot.repository;

import com.knowledge.springboot.domain.LearningNote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学习笔记存储库接口
 */
@Repository
public interface LearningNoteRepository extends MongoRepository<LearningNote, String> {
    
    /**
     * 根据用户ID查询笔记列表
     * @param userId 用户ID
     * @return 笔记列表
     */
    List<LearningNote> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
    
    /**
     * 根据ID和用户ID查询笔记
     * @param id 笔记ID
     * @param userId 用户ID
     * @return 笔记对象
     */
    LearningNote findByIdAndUserId(String id, String userId);
} 