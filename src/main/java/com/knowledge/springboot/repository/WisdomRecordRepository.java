package com.knowledge.springboot.repository;

import com.knowledge.springboot.domain.WisdomRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 智慧值记录存储库接口
 */
@Repository
public interface WisdomRecordRepository extends MongoRepository<WisdomRecord, String> {
    
    /**
     * 根据用户ID查询智慧值记录
     * @param userId 用户ID
     * @return 智慧值记录列表
     */
    List<WisdomRecord> findByUserIdOrderByCreatedAtDesc(String userId);
    
    /**
     * 根据笔记ID查询智慧值记录
     * @param noteId 笔记ID
     * @return 智慧值记录
     */
    WisdomRecord findByNoteId(String noteId);
    
    /**
     * 根据用户ID和笔记ID查询智慧值记录
     * @param userId 用户ID
     * @param noteId 笔记ID
     * @return 智慧值记录
     */
    WisdomRecord findByUserIdAndNoteId(String userId, String noteId);
    
    /**
     * 查询活跃的智慧值记录（30天内有学习或复习记录）
     * @param thirtyDaysAgo 30天前的时间
     * @return 活跃的智慧值记录列表
     */
    @Query("{'$or': [{'lastStudyTime': {'$gte': ?0}}, {'lastReviewTime': {'$gte': ?0}}]}")
    List<WisdomRecord> findActiveRecords(Date thirtyDaysAgo);
    
    /**
     * 根据用户ID和知识点ID查询智慧值记录
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @return 智慧值记录
     */
    WisdomRecord findByUserIdAndKnowledgePointId(String userId, String knowledgePointId);
    
    /**
     * 根据知识点ID查询智慧值记录
     * @param knowledgePointId 知识点ID
     * @return 智慧值记录
     */
    WisdomRecord findByKnowledgePointId(String knowledgePointId);
    
    /**
     * 查询需要更新的智慧值记录（更新时间超过指定时间）
     * @param updateTime 指定的更新时间
     * @return 需要更新的智慧值记录列表
     */
    @Query("{'updatedAt': {'$lt': ?0}}")
    List<WisdomRecord> findRecordsNeedUpdate(Date updateTime);
}
