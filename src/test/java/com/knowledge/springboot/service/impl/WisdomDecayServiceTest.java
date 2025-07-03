package com.knowledge.springboot.service.impl;

import com.alibaba.fastjson2.JSON;
import com.knowledge.springboot.domain.WisdomRecord;
import com.knowledge.springboot.repository.WisdomRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * WisdomDecayService 测试类
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class WisdomDecayServiceTest {

    @Autowired
    private WisdomRecordRepository wisdomRecordRepository;

    @Autowired
    private WisdomDecayService wisdomDecayService;

    private WisdomRecord testRecord;
    private Date now;
    private Date oneDayAgo;
    private Date oneWeekAgo;

    @Test
    @DisplayName("测试用户智慧值统计 - 正常情况")
    void testGetUserWisdomStatistics_Normal() {
        Map<String, Object> result = wisdomDecayService.getUserWisdomStatistics("68316957aa38b6042002c733");
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    @DisplayName("测试计算当前智慧值 - 基础智慧值为null")
    void testCalculateCurrentWisdom_NullBaseWisdom() {

        now = new Date();

        // 创建一天前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        oneDayAgo = calendar.getTime();

        // 创建一周前的时间
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        oneWeekAgo = calendar.getTime();

        // 创建测试记录
        testRecord = new WisdomRecord();
        testRecord.setId(UUID.randomUUID().toString());
        testRecord.setUserId("68316957aa38b6042002c733");
        // 四位随机数
        Random random = new Random();
        int fourDigitNumber = 1000 + random.nextInt(9000);
        testRecord.setKnowledgePointId("knowledge_" + fourDigitNumber);
        testRecord.setBaseWisdomValue(100.0);
        testRecord.setCurrentWisdomValue(100.0);
        testRecord.setMemoryStability(24.0);
        testRecord.setLastStudyTime(oneDayAgo);
        testRecord.setReviewCount(0);
        testRecord.setCreatedAt(oneWeekAgo);
        testRecord.setUpdatedAt(oneDayAgo);
        testRecord.setNoteId("68319a3065e9b372c596a128");

//        wisdomRecordRepository.save(testRecord);

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);
        System.out.println(result);

        Map<String, Object> statistics = wisdomDecayService.getUserWisdomStatistics("68316957aa38b6042002c733");
        System.out.println(JSON.toJSONString(statistics));

    }

    @Test
    @DisplayName("测试计算当前智慧值 - 基础智慧值为0")
    void testCalculateCurrentWisdom_ZeroBaseWisdom() {
        testRecord.setBaseWisdomValue(0.0);

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        assertEquals(0.0, result);
    }

    @Test
    @DisplayName("测试计算当前智慧值 - 负数基础智慧值")
    void testCalculateCurrentWisdom_NegativeBaseWisdom() {
        testRecord.setBaseWisdomValue(-50.0);

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        assertEquals(0.0, result);
    }

    @Test
    @DisplayName("测试计算当前智慧值 - 正常情况")
    void testCalculateCurrentWisdom_Normal() {
        testRecord.setBaseWisdomValue(100.0);
        testRecord.setLastStudyTime(oneDayAgo); // 24小时前
        testRecord.setMemoryStability(24.0);

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        // 24小时后，使用e^(-24/24) = e^(-1) ≈ 0.368
        // 所以智慧值应该约为 100 * 0.368 = 36.8
        assertTrue(result > 30.0 && result < 40.0);
        assertTrue(result >= 0.0);
    }

    @Test
    @DisplayName("测试计算当前智慧值 - 使用复习时间")
    void testCalculateCurrentWisdom_WithReviewTime() {
        // 设置复习时间比学习时间更近
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -12); // 12小时前复习
        Date twelveHoursAgo = calendar.getTime();

        testRecord.setLastStudyTime(oneDayAgo); // 24小时前学习
        testRecord.setLastReviewTime(twelveHoursAgo); // 12小时前复习
        testRecord.setMemoryStability(24.0);

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        // 应该使用复习时间计算，12小时的衰减应该比24小时的衰减小
        // e^(-12/24) = e^(-0.5) ≈ 0.606
        assertTrue(result > 50.0 && result < 70.0);
    }

    @Test
    @DisplayName("测试计算当前智慧值 - 无学习和复习时间，使用创建时间")
    void testCalculateCurrentWisdom_UseCreatedTime() {
        testRecord.setLastStudyTime(null);
        testRecord.setLastReviewTime(null);
        testRecord.setCreatedAt(oneDayAgo);

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        assertTrue(result >= 0.0);
        assertTrue(result <= testRecord.getBaseWisdomValue());
    }

    @Test
    @DisplayName("测试计算当前智慧值 - 所有时间都为null")
    void testCalculateCurrentWisdom_AllTimesNull() {
        testRecord.setLastStudyTime(null);
        testRecord.setLastReviewTime(null);
        testRecord.setCreatedAt(null);

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        // 时间差为0，应该返回原始智慧值
        assertEquals(testRecord.getBaseWisdomValue(), result);
    }

    @Test
    @DisplayName("测试计算当前智慧值 - 自定义记忆稳定性")
    void testCalculateCurrentWisdom_CustomMemoryStability() {
        testRecord.setMemoryStability(48.0); // 48小时稳定性
        testRecord.setLastStudyTime(oneDayAgo); // 24小时前

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        // 使用48小时稳定性，衰减应该更慢
        // e^(-24/48) = e^(-0.5) ≈ 0.606
        assertTrue(result > 50.0 && result < 70.0);
    }

    @Test
    @DisplayName("测试计算当前智慧值 - 记忆稳定性为null，使用默认值")
    void testCalculateCurrentWisdom_NullMemoryStability() {
        testRecord.setMemoryStability(null);
        testRecord.setLastStudyTime(oneDayAgo);

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        // 应该使用默认的24小时稳定性
        assertTrue(result >= 0.0);
        assertTrue(result <= testRecord.getBaseWisdomValue());
    }

    @Test
    @DisplayName("测试实时计算智慧值 - 记录为null")
    void testCalculateRealTimeWisdom_NullRecord() {
        WisdomRecord result = wisdomDecayService.calculateRealTimeWisdom(null);

        assertNull(result);
    }

    @Test
    @DisplayName("测试实时计算智慧值 - 不需要重新计算")
    void testCalculateRealTimeWisdom_NoNeedToRecalculate() {
        // 设置更新时间为1小时前（小于12小时）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        testRecord.setUpdatedAt(calendar.getTime());

        WisdomRecord result = wisdomDecayService.calculateRealTimeWisdom(testRecord);

        // 应该返回原记录，不进行重新计算
        assertEquals(testRecord, result);
        verify(wisdomRecordRepository, never()).save(any(WisdomRecord.class));
    }

    @Test
    @DisplayName("测试实时计算智慧值 - 需要重新计算")
    void testCalculateRealTimeWisdom_NeedToRecalculate() {
        // 设置更新时间为13小时前（超过12小时）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -13);
        testRecord.setUpdatedAt(calendar.getTime());

        // Mock repository save方法
        when(wisdomRecordRepository.save(any(WisdomRecord.class))).thenReturn(testRecord);

        WisdomRecord result = wisdomDecayService.calculateRealTimeWisdom(testRecord);

        // 应该重新计算并保存
        assertNotNull(result);
        verify(wisdomRecordRepository, times(1)).save(any(WisdomRecord.class));

        // 验证智慧值被重新计算
        assertTrue(result.getCurrentWisdomValue() <= result.getBaseWisdomValue());
    }

    @Test
    @DisplayName("测试实时计算智慧值 - 更新时间为null")
    void testCalculateRealTimeWisdom_NullUpdatedAt() {
        testRecord.setUpdatedAt(null);

        // Mock repository save方法
        when(wisdomRecordRepository.save(any(WisdomRecord.class))).thenReturn(testRecord);

        WisdomRecord result = wisdomDecayService.calculateRealTimeWisdom(testRecord);

        // 应该重新计算并保存
        assertNotNull(result);
        verify(wisdomRecordRepository, times(1)).save(any(WisdomRecord.class));
    }

    @Test
    @DisplayName("测试批量更新智慧值 - 正常情况")
    void testBatchUpdateWisdomValues_Normal() {
        // 创建测试数据
        WisdomRecord record1 = createTestRecord("001", 100.0);
        WisdomRecord record2 = createTestRecord("002", 80.0);
        List<WisdomRecord> activeRecords = Arrays.asList(record1, record2);

        // Mock repository方法
        when(wisdomRecordRepository.findActiveRecords(any(Date.class))).thenReturn(activeRecords);
        when(wisdomRecordRepository.save(any(WisdomRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 执行批量更新
        wisdomDecayService.batchUpdateWisdomValues();

        // 验证调用
        verify(wisdomRecordRepository, times(1)).findActiveRecords(any(Date.class));
        verify(wisdomRecordRepository, times(2)).save(any(WisdomRecord.class));
    }

    @Test
    @DisplayName("测试批量更新智慧值 - 空记录列表")
    void testBatchUpdateWisdomValues_EmptyList() {
        // Mock repository返回空列表
        when(wisdomRecordRepository.findActiveRecords(any(Date.class))).thenReturn(Arrays.asList());

        // 执行批量更新
        wisdomDecayService.batchUpdateWisdomValues();

        // 验证调用
        verify(wisdomRecordRepository, times(1)).findActiveRecords(any(Date.class));
        verify(wisdomRecordRepository, never()).save(any(WisdomRecord.class));
    }

    @Test
    @DisplayName("测试批量更新智慧值 - 单个记录更新失败")
    void testBatchUpdateWisdomValues_SingleRecordFailure() {
        // 创建测试数据
        WisdomRecord record1 = createTestRecord("001", 100.0);
        WisdomRecord record2 = createTestRecord("002", 80.0);
        List<WisdomRecord> activeRecords = Arrays.asList(record1, record2);

        // Mock repository方法
        when(wisdomRecordRepository.findActiveRecords(any(Date.class))).thenReturn(activeRecords);
        when(wisdomRecordRepository.save(record1)).thenThrow(new RuntimeException("保存失败"));
        when(wisdomRecordRepository.save(record2)).thenReturn(record2);

        // 执行批量更新，不应该抛出异常
        assertDoesNotThrow(() -> wisdomDecayService.batchUpdateWisdomValues());

        // 验证调用
        verify(wisdomRecordRepository, times(1)).findActiveRecords(any(Date.class));
        verify(wisdomRecordRepository, times(2)).save(any(WisdomRecord.class));
    }

    @Test
    @DisplayName("测试批量更新智慧值 - 查询失败")
    void testBatchUpdateWisdomValues_QueryFailure() {
        // Mock repository抛出异常
        when(wisdomRecordRepository.findActiveRecords(any(Date.class)))
            .thenThrow(new RuntimeException("数据库连接失败"));

        // 执行批量更新，不应该抛出异常
        assertDoesNotThrow(() -> wisdomDecayService.batchUpdateWisdomValues());

        // 验证调用
        verify(wisdomRecordRepository, times(1)).findActiveRecords(any(Date.class));
        verify(wisdomRecordRepository, never()).save(any(WisdomRecord.class));
    }

    @Test
    @DisplayName("测试手动批量更新")
    void testManualBatchUpdate() {
        // 创建测试数据
        List<WisdomRecord> activeRecords = Arrays.asList(createTestRecord("001", 100.0));

        // Mock repository方法
        when(wisdomRecordRepository.findActiveRecords(any(Date.class))).thenReturn(activeRecords);
        when(wisdomRecordRepository.save(any(WisdomRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 执行手动批量更新
        wisdomDecayService.manualBatchUpdate();

        // 验证调用
        verify(wisdomRecordRepository, times(1)).findActiveRecords(any(Date.class));
        verify(wisdomRecordRepository, times(1)).save(any(WisdomRecord.class));
    }

    @Test
    @DisplayName("测试记忆保持率计算 - 时间为0")
    void testRetentionRateCalculation_ZeroTime() {
        testRecord.setLastStudyTime(now); // 当前时间，时间差为0

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        // 时间差为0时，保持率应该为1.0，智慧值不变
        assertEquals(testRecord.getBaseWisdomValue(), result, 0.01);
    }

    @Test
    @DisplayName("测试记忆保持率计算 - 负时间差")
    void testRetentionRateCalculation_NegativeTime() {
        // 设置未来时间（理论上不应该发生，但要处理边界情况）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        testRecord.setLastStudyTime(calendar.getTime());

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        // 负时间差应该被处理为0，返回原始智慧值
        assertEquals(testRecord.getBaseWisdomValue(), result, 0.01);
    }

    @Test
    @DisplayName("测试记忆保持率边界值")
    void testRetentionRateBoundaries() {
        // 测试极长时间的衰减
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1); // 一年前
        testRecord.setLastStudyTime(calendar.getTime());

        double result = wisdomDecayService.calculateCurrentWisdom(testRecord);

        // 即使时间很长，智慧值也不应该为负数
        assertTrue(result >= 0.0);
        assertTrue(result <= testRecord.getBaseWisdomValue());
    }



    @Test
    @DisplayName("测试用户智慧值统计 - 空记录")
    void testGetUserWisdomStatistics_EmptyRecords() {
        String userId = "test_user_001";

        // Mock repository返回空列表
        when(wisdomRecordRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Arrays.asList());

        Map<String, Object> result = wisdomDecayService.getUserWisdomStatistics(userId);

        assertNotNull(result);
        assertEquals(0, result.get("totalRecords"));
        assertEquals(0.0, result.get("totalCurrentWisdom"));
        assertEquals(0.0, result.get("averageRetentionRate"));
        assertEquals("无数据", result.get("memoryHealthLevel"));
    }

    /**
     * 创建测试记录的辅助方法
     */
    private WisdomRecord createTestRecord(String id, Double baseWisdom) {
        WisdomRecord record = new WisdomRecord();
        record.setId("test_record_" + id);
        record.setUserId("test_user_001");
        record.setKnowledgePointId("test_knowledge_" + id);
        record.setBaseWisdomValue(baseWisdom);
        record.setCurrentWisdomValue(baseWisdom);
        record.setMemoryStability(24.0);
        record.setLastStudyTime(oneDayAgo);
        record.setReviewCount(0);
        record.setCreatedAt(oneWeekAgo);
        record.setUpdatedAt(oneDayAgo);
        return record;
    }
}
