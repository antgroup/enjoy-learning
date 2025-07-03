package com.knowledge.springboot.service.impl;

import com.knowledge.springboot.domain.WisdomRecord;
import com.knowledge.springboot.repository.WisdomRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智慧值衰减服务
 */
@Service
public class WisdomDecayService {

    private static final Logger logger = LoggerFactory.getLogger(WisdomDecayService.class);

    @Autowired
    private WisdomRecordRepository wisdomRecordRepository;

    /**
     * 定时批量更新智慧值（每日凌晨2点执行）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void batchUpdateWisdomValues() {
        logger.info("开始执行智慧值衰减批量更新任务");

        try {
            // 计算30天前的时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -30);
            Date thirtyDaysAgo = calendar.getTime();

            // 查询活跃的智慧值记录
            // TODO- 需要使用分页查询，考虑后续批量处理的性能问题
            List<WisdomRecord> activeRecords = wisdomRecordRepository.findActiveRecords(thirtyDaysAgo);
            logger.info("找到 {} 条活跃的智慧值记录需要更新", activeRecords.size());

            int updatedCount = 0;
            Date now = new Date();

            for (WisdomRecord record : activeRecords) {
                try {
                    // 计算当前智慧值
                    double currentWisdom = calculateCurrentWisdom(record);

                    // 更新记录
                    record.setCurrentWisdomValue(currentWisdom);
                    record.setUpdatedAt(now);

                    wisdomRecordRepository.save(record);
                    updatedCount++;

                } catch (Exception e) {
                    logger.error("更新智慧值记录失败，记录ID: {}, 错误: {}", record.getId(), e.getMessage());
                }
            }

            logger.info("智慧值衰减批量更新任务完成，成功更新 {} 条记录", updatedCount);

        } catch (Exception e) {
            logger.error("智慧值衰减批量更新任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 计算当前智慧值
     * @param record 智慧值记录
     * @return 当前智慧值
     */
    public double calculateCurrentWisdom(WisdomRecord record) {
        if (record.getBaseWisdomValue() == null || record.getBaseWisdomValue() <= 0) {
            return 0.0;
        }

        // 计算时间差（小时）
        long timeDiffHours = calculateTimeDifference(record);

        // 计算记忆保持率
        double retentionRate = calculateRetentionRate(timeDiffHours, record.getMemoryStability());

        // 计算当前智慧值
        double currentWisdom = record.getBaseWisdomValue() * retentionRate;

        // 确保智慧值不为负数
        return Math.max(0.0, currentWisdom);
    }

    /**
     * 计算时间差（小时）
     * @param record 智慧值记录
     * @return 时间差（小时）
     */
    private long calculateTimeDifference(WisdomRecord record) {
        Date now = new Date();
        Date lastActiveTime;

        // 如果有复习记录，使用最后复习时间；否则使用最后学习时间
        if (record.getLastReviewTime() != null) {
            lastActiveTime = record.getLastReviewTime();
        } else if (record.getLastStudyTime() != null) {
            lastActiveTime = record.getLastStudyTime();
        } else {
            // 如果都没有，使用创建时间
            lastActiveTime = record.getCreatedAt();
        }

        if (lastActiveTime == null) {
            return 0;
        }

        long timeDiffMillis = now.getTime() - lastActiveTime.getTime();
        return timeDiffMillis / (1000 * 60 * 60); // 转换为小时
    }

    /**
     * 计算记忆保持率（简化版）
     * @param hours 时间差（小时）
     * @param memoryStability 记忆稳定性参数
     * @return 记忆保持率
     */
    private double calculateRetentionRate(long hours, Double memoryStability) {
        if (hours <= 0) {
            return 1.0;
        }

        // 使用默认的记忆稳定性参数
        double stability = (memoryStability != null && memoryStability > 0) ? memoryStability : 24.0;

        // 简化版公式：记忆保持率 = e^(-t/S)
        double retentionRate = Math.exp(-hours / stability);

        // 确保保持率在0-1之间
        return Math.max(0.0, Math.min(1.0, retentionRate));
    }

    /**
     * 计算记忆保持率（分阶段版）
     * @param hours 时间差（小时）
     * @return 记忆保持率
     */
    private double calculateStageBasedRetentionRate(long hours) {
        if (hours <= 0) {
            return 1.0;
        }

        if (hours <= 24) {
            // 第一阶段（0-24小时）：衰减率20%
            return 1.0 - (0.2 * hours / 24);
        } else if (hours <= 7 * 24) {
            // 第二阶段（1-7天）：衰减率30%
            return 0.8 - (0.3 * (hours - 24) / (7 * 24 - 24));
        } else if (hours <= 30 * 24) {
            // 第三阶段（7-30天）：衰减率25%
            return 0.5 - (0.25 * (hours - 7 * 24) / (30 * 24 - 7 * 24));
        } else {
            // 第四阶段（30天以上）：衰减率15%
            return Math.max(0.1, 0.25 - (0.15 * (hours - 30 * 24) / (365 * 24)));
        }
    }

    /**
     * 实时计算智慧值（用于查询时）
     * @param record 智慧值记录
     * @return 更新后的记录
     */
    public WisdomRecord calculateRealTimeWisdom(WisdomRecord record) {
        if (record == null) {
            return null;
        }

        // 检查是否需要重新计算（超过12小时）
        Date now = new Date();
        if (record.getUpdatedAt() != null) {
            long timeSinceUpdate = now.getTime() - record.getUpdatedAt().getTime();
            long hoursSinceUpdate = timeSinceUpdate / (1000 * 60 * 60);

            if (hoursSinceUpdate < 12) {
                // 不需要重新计算
                return record;
            }
        }

        // 重新计算智慧值
        double currentWisdom = calculateCurrentWisdom(record);
        record.setCurrentWisdomValue(currentWisdom);
        record.setUpdatedAt(now);

        // 保存到数据库
        return wisdomRecordRepository.save(record);
    }

    /**
     * 获取用户智慧值统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    public Map<String, Object> getUserWisdomStatistics(String userId) {
        // 查询用户的所有智慧值记录
        List<WisdomRecord> records = wisdomRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);

        Map<String, Object> statistics = new HashMap<>();

        if (records.isEmpty()) {
            statistics.put("totalRecords", 0);
            statistics.put("totalCurrentWisdom", 0.0);
            statistics.put("averageRetentionRate", 0.0);
            statistics.put("memoryHealthLevel", "无数据");
            return statistics;
        }

        double totalCurrentWisdom = 0.0;
        double totalBaseWisdom = 0.0;
        int activeCount = 0;

        for (WisdomRecord record : records) {
            // 实时计算智慧值
            record = calculateRealTimeWisdom(record);

            if (record.getCurrentWisdomValue() != null) {
                totalCurrentWisdom += record.getCurrentWisdomValue();
            }
            if (record.getBaseWisdomValue() != null) {
                totalBaseWisdom += record.getBaseWisdomValue();
                if (record.getCurrentWisdomValue() > 0) {
                    activeCount++;
                }
            }
        }

        double averageRetentionRate = totalBaseWisdom > 0 ?
            (totalCurrentWisdom / totalBaseWisdom) * 100 : 0.0;

        statistics.put("totalRecords", records.size());
        statistics.put("activeRecords", activeCount);
        statistics.put("totalCurrentWisdom", Math.round(totalCurrentWisdom * 100.0) / 100.0);
        statistics.put("totalBaseWisdom", Math.round(totalBaseWisdom * 100.0) / 100.0);
        statistics.put("averageRetentionRate", Math.round(averageRetentionRate * 10.0) / 10.0);
        statistics.put("memoryHealthLevel", calculateMemoryHealthLevel(averageRetentionRate));

        return statistics;
    }

    /**
     * 计算记忆健康度等级
     * @param averageRetentionRate 平均保持率（百分比）
     * @return 健康度等级
     */
    private String calculateMemoryHealthLevel(double averageRetentionRate) {
        if (averageRetentionRate >= 90) {
            return "优秀";
        } else if (averageRetentionRate >= 80) {
            return "良好";
        } else if (averageRetentionRate >= 70) {
            return "一般";
        } else {
            return "需要关注";
        }
    }

    /**
     * 手动触发批量更新（用于测试）
     */
    public void manualBatchUpdate() {
        logger.info("手动触发智慧值衰减批量更新");
        batchUpdateWisdomValues();
    }
}
