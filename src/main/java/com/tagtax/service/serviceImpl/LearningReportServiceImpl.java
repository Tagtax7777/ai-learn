package com.tagtax.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tagtax.entity.LearningReport;
import com.tagtax.entity.Result;
import com.tagtax.entity.StudyRecord;
import com.tagtax.mapper.StudyRecordMapper;
import com.tagtax.service.LearningReportService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class LearningReportServiceImpl implements LearningReportService {

    @Autowired
    private StudyRecordMapper studyRecordMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ChatModel zhiPuAiChatModel;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String REPORT_CACHE_KEY_PREFIX = "learning_report:";
    private static final int CACHE_DAYS = 7; // 缓存7天
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result getLatestLearningReport(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            
            // 1. 检查缓存
            String cacheKey = REPORT_CACHE_KEY_PREFIX + userId + ":latest";
            String cachedReport = redisTemplate.opsForValue().get(cacheKey);
            if (cachedReport != null) {
                LearningReport report = objectMapper.readValue(cachedReport, LearningReport.class);
                return Result.success(report);
            }
            
            // 2. 查询数据库中最新报告
            String sql = "SELECT * FROM learning_reports WHERE user_id = ? ORDER BY report_date DESC LIMIT 1";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId);
            
            // 如果有最新报告且是今天的，直接返回
            if (!results.isEmpty()) {
                Map<String, Object> reportData = results.get(0);
                LocalDate reportDate = ((java.sql.Date) reportData.get("report_date")).toLocalDate();
                
                // 如果最新报告是今天的，直接返回
                if (reportDate.equals(today)) {
                    LearningReport report = parseLearningReport(reportData);
                    
                    // 缓存结果
                    String reportJson = objectMapper.writeValueAsString(report);
                    redisTemplate.opsForValue().set(cacheKey, reportJson, CACHE_DAYS, TimeUnit.DAYS);
                    
                    return Result.success(report);
                }
            }
            
            // 3. 没有今天的报告，生成新报告
            // 获取用户学习记录
            List<StudyRecord> studyRecords = studyRecordMapper.getStudyRecord(userId);
            if (studyRecords == null || studyRecords.isEmpty()) {
                return Result.error("暂无学习记录");
            }
            
            // 分析学习数据
            LearningReport.LearningAnalysis analysis = analyzeStudyData(userId, studyRecords, today);
            
            // 保存到数据库
            LearningReport report = LearningReport.builder()
                    .userId(userId)
                    .reportDate(today)
                    .learningAnalysis(analysis)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            saveReport(report);
            
            // 缓存结果
            String reportJson = objectMapper.writeValueAsString(report);
            redisTemplate.opsForValue().set(cacheKey, reportJson, CACHE_DAYS, TimeUnit.DAYS);
            
            return Result.success(report);
        } catch (Exception e) {
            return Result.error("获取学习报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 分析学习数据
     */
    private LearningReport.LearningAnalysis analyzeStudyData(Long userId, List<StudyRecord> studyRecords, LocalDate reportDate) throws Exception {
        // 1. 按小时统计一天内的学习数据 (7:00-18:00)
        List<Integer> dayData = analyzeDayData(studyRecords);
        
        // 2. 按天统计一周内的学习数据 (周一至周日)
        List<Integer> weekData = analyzeWeekData(studyRecords);
        
        // 3. 按月统计一年内的学习数据 (1-12月)
        List<Integer> monthData = analyzeMonthData(studyRecords);
        
        // 4. 个人学习效率数据 (周一至周日)
        List<Double> personData = analyzePersonData(studyRecords);
        
        // 5. 群体学习效率数据 (周一至周日) - 模拟数据
        List<Double> crowdData = generateCrowdData();
        
        // 6. 使用AI生成学习小结
        String summary = generateAISummary(dayData, weekData, personData, crowdData);
        
        return LearningReport.LearningAnalysis.builder()
                .dayData(dayData)
                .weekData(weekData)
                .monthData(monthData)
                .personData(personData)
                .crowdData(crowdData)
                .summary(summary)
                .build();
    }
    
    /**
     * 按小时统计一天内的学习数据 (7:00-18:00)
     */
    private List<Integer> analyzeDayData(List<StudyRecord> studyRecords) {
        // 初始化12个小时的数据 (7:00-18:00)
        List<Integer> hourlyData = new ArrayList<>(Collections.nCopies(12, 0));
        
        // 统计每个小时的学习记录数量
        for (StudyRecord record : studyRecords) {
            int startHour = record.getStartTime().getHour();
            int endHour = record.getEndTime().getHour();
            
            // 只统计7:00-18:00之间的数据
            for (int hour = startHour; hour <= endHour; hour++) {
                if (hour >= 7 && hour < 19) {
                    int index = hour - 7; // 转换为数组索引
                    hourlyData.set(index, hourlyData.get(index) + 1);
                }
            }
        }
        
        // 将数据标准化到0-100范围
        return normalizeData(hourlyData, 100);
    }
    
    /**
     * 按天统计一周内的学习数据 (周一至周日)
     */
    private List<Integer> analyzeWeekData(List<StudyRecord> studyRecords) {
        // 初始化7天的数据 (周一至周日)
        List<Integer> weeklyData = new ArrayList<>(Collections.nCopies(7, 0));
        
        // 统计每天的学习记录数量
        for (StudyRecord record : studyRecords) {
            DayOfWeek dayOfWeek = record.getStudyDate().getDayOfWeek();
            int dayIndex = dayOfWeek.getValue() - 1; // 转换为数组索引 (0-6)
            weeklyData.set(dayIndex, weeklyData.get(dayIndex) + 1);
        }
        
        // 将数据标准化到0-100范围
        return normalizeData(weeklyData, 100);
    }
    
    /**
     * 按月统计一年内的学习数据 (1-12月)
     */
    private List<Integer> analyzeMonthData(List<StudyRecord> studyRecords) {
        // 初始化12个月的数据
        List<Integer> monthlyData = new ArrayList<>(Collections.nCopies(12, 0));
        
        // 统计每月的学习记录数量
        for (StudyRecord record : studyRecords) {
            Month month = record.getStudyDate().getMonth();
            int monthIndex = month.getValue() - 1; // 转换为数组索引 (0-11)
            monthlyData.set(monthIndex, monthlyData.get(monthIndex) + 1);
        }
        
        // 将数据标准化到0-100范围
        return normalizeData(monthlyData, 100);
    }
    
    /**
     * 分析个人学习效率数据 (周一至周日)
     */
    private List<Double> analyzePersonData(List<StudyRecord> studyRecords) {
        // 初始化7天的数据 (周一至周日)
        List<Double> efficiencyData = new ArrayList<>(Collections.nCopies(7, 0.0));
        List<Integer> countData = new ArrayList<>(Collections.nCopies(7, 0));
        
        // 计算每天的学习效率 (学习时长/学习次数)
        for (StudyRecord record : studyRecords) {
            DayOfWeek dayOfWeek = record.getStudyDate().getDayOfWeek();
            int dayIndex = dayOfWeek.getValue() - 1; // 转换为数组索引 (0-6)
            
            // 计算学习时长(小时)
            double hours = ChronoUnit.MINUTES.between(record.getStartTime(), record.getEndTime()) / 60.0;
            efficiencyData.set(dayIndex, efficiencyData.get(dayIndex) + hours);
            countData.set(dayIndex, countData.get(dayIndex) + 1);
        }
        
        // 计算平均效率
        for (int i = 0; i < 7; i++) {
            if (countData.get(i) > 0) {
                efficiencyData.set(i, efficiencyData.get(i) / countData.get(i));
            }
        }
        
        // 将数据标准化到0-4范围
        return normalizeData(efficiencyData, 4.0);
    }
    
    /**
     * 生成群体学习效率数据 (模拟数据)
     */
    private List<Double> generateCrowdData() {
        // 模拟群体学习效率数据 (0-4范围)
        List<Double> crowdData = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < 7; i++) {
            // 生成1.0-4.0之间的随机数
            double value = 1.0 + random.nextDouble() * 3.0;
            // 保留一位小数
            value = Math.round(value * 10) / 10.0;
            crowdData.add(value);
        }
        
        return crowdData;
    }
    
    /**
     * 使用AI生成学习小结
     */
    private String generateAISummary(List<Integer> dayData, List<Integer> weekData, 
                                    List<Double> personData, List<Double> crowdData) {
        try {
            // 构建AI提示词
            StringBuilder prompt = new StringBuilder();
            prompt.append("分析以下学习数据，生成不超过30个字的学习小结：\n");
            prompt.append("一天内各时段学习数据(7:00-18:00)：").append(dayData).append("\n");
            prompt.append("一周内各天学习数据(周一至周日)：").append(weekData).append("\n");
            prompt.append("个人学习效率数据(周一至周日)：").append(personData).append("\n");
            prompt.append("群体学习效率数据(周一至周日)：").append(crowdData).append("\n");
            prompt.append("请分析用户在哪些时间段或星期几学习效率更高，给出具体的学习建议，不超过30个字。");
            
            // 调用AI生成小结
            ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
            String summary = chatClient.prompt(prompt.toString()).call().content();
            
            // 限制字数
            if (summary.length() > 30) {
                summary = summary.substring(0, 30);
            }
            
            return summary;
        } catch (Exception e) {
            // 如果AI生成失败，返回默认小结
            return "周末学习效率高，建议合理安排时间";
        }
    }
    
    /**
     * 将数据标准化到指定范围
     */
    private <T extends Number> List<T> normalizeData(List<T> data, T maxValue) {
        if (data.isEmpty()) {
            return data;
        }
        
        // 找出最大值
        double max = 0;
        for (T value : data) {
            max = Math.max(max, value.doubleValue());
        }
        
        // 如果最大值为0，直接返回原数据
        if (max == 0) {
            return data;
        }
        
        // 标准化数据
        double maxDouble = maxValue.doubleValue();
        List<T> normalizedData = new ArrayList<>();
        
        for (T value : data) {
            double normalizedValue = (value.doubleValue() / max) * maxDouble;
            
            // 根据类型转换
            if (maxValue instanceof Integer) {
                normalizedData.add((T) Integer.valueOf((int) Math.round(normalizedValue)));
            } else if (maxValue instanceof Double) {
                // 保留一位小数
                normalizedValue = Math.round(normalizedValue * 10) / 10.0;
                normalizedData.add((T) Double.valueOf(normalizedValue));
            } else {
                normalizedData.add((T) Double.valueOf(normalizedValue));
            }
        }
        
        return normalizedData;
    }
    
    /**
     * 保存报告到数据库
     */
    private void saveReport(LearningReport report) throws Exception {
        String learningAnalysisJson = objectMapper.writeValueAsString(report.getLearningAnalysis());
        
        String sql = "INSERT INTO learning_reports (user_id, report_date, learning_analysis, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
                report.getUserId(), 
                report.getReportDate(), 
                learningAnalysisJson, 
                report.getCreatedAt());
        
        // 获取自增ID
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        report.setId(id);
    }
    
    /**
     * 解析学习报告数据
     */
    private LearningReport parseLearningReport(Map<String, Object> reportData) throws Exception {
        Long id = ((Number) reportData.get("id")).longValue();
        Long userId = ((Number) reportData.get("user_id")).longValue();
        LocalDate reportDate = ((java.sql.Date) reportData.get("report_date")).toLocalDate();
        String learningAnalysisJson = (String) reportData.get("learning_analysis");
        LocalDateTime createdAt = ((java.sql.Timestamp) reportData.get("created_at")).toLocalDateTime();
        
        // 解析学习分析数据
        LearningReport.LearningAnalysis learningAnalysis = 
                objectMapper.readValue(learningAnalysisJson, LearningReport.LearningAnalysis.class);
        
        return LearningReport.builder()
                .id(id)
                .userId(userId)
                .reportDate(reportDate)
                .learningAnalysis(learningAnalysis)
                .createdAt(createdAt)
                .build();
    }
}