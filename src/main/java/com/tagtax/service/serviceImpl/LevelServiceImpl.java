package com.tagtax.service.serviceImpl;

import com.tagtax.entity.*;
import com.tagtax.mapper.LevelMapper;
import com.tagtax.mapper.GoalsMapper;
import com.tagtax.service.LevelService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LevelServiceImpl implements LevelService {
    
    @Autowired
    private ChatModel zhiPuAiChatModel;
    
    @Autowired
    private LevelMapper levelMapper;
    
    @Autowired
    private GoalsMapper goalsMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis key前缀
    private static final String DAILY_QUESTIONS_KEY = "daily:questions:";
    private static final String USER_DAILY_PROGRESS_KEY = "daily:progress:";
    private static final int MAX_LEVELS = 8; // 最多8关
    
    @Override
    public Result getUserAvailableLevels(Long userId) {
        try {
            // 获取今日用户进度
            String today = LocalDate.now().toString();
            String progressKey = USER_DAILY_PROGRESS_KEY + userId + ":" + today;
            Integer currentLevel = (Integer) redisTemplate.opsForValue().get(progressKey);
            if (currentLevel == null) {
                currentLevel = 1; // 每天从第一关开始
            }
            
            // 生成8关的关卡信息
            List<Level> levels = new ArrayList<>();
            for (int i = 1; i <= MAX_LEVELS; i++) {
                Level level = Level.builder()
                        .id((long) i)
                        .levelName("第" + i + "关")
                        .levelNumber(i)
                        .description(getDifficultyDescription(i))
                        .maxStars(3)
                        .isActive(i <= currentLevel ? 1 : 0) // 只有当前关卡及之前的关卡可用
                        .build();
                levels.add(level);
            }
            
            return Result.success(levels);
        } catch (Exception e) {
            return Result.error("获取关卡列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据关卡数获取难度描述
     */
    private String getDifficultyDescription(int levelNumber) {
        switch (levelNumber) {
            case 1: return "入门级 - 基础概念测试";
            case 2: return "初级 - 基本应用练习";
            case 3: return "中级 - 知识点综合";
            case 4: return "中高级 - 深度理解";
            case 5: return "高级 - 复杂应用";
            case 6: return "专家级 - 综合分析";
            case 7: return "大师级 - 创新思维";
            case 8: return "终极挑战 - 全面掌握";
            default: return "未知难度";
        }
    }
    
    @Override
    public Result generateLevelQuestions(Long userId, Long levelId, List<Long> goalIds) {
        try {
            // 验证关卡序号
            if (levelId < 1 || levelId > MAX_LEVELS) {
                return Result.error("无效的关卡序号");
            }
            
            // 检查用户是否有权限挑战该关卡
            String today = LocalDate.now().toString();
            String progressKey = USER_DAILY_PROGRESS_KEY + userId + ":" + today;
            Integer currentLevel = (Integer) redisTemplate.opsForValue().get(progressKey);
            if (currentLevel == null) {
                currentLevel = 1;
            }
            
            if (levelId > currentLevel) {
                return Result.error("请先通过第" + currentLevel + "关");
            }
            
            // 获取学习目标信息
            List<LearningGoals> goals = new ArrayList<>();
            for (Long goalId : goalIds) {
                LearningGoals goal = goalsMapper.getGoalByGoalId(goalId);
                if (goal != null) {
                    goals.add(goal);
                }
            }
            
            if (goals.isEmpty()) {
                return Result.error("未找到有效的学习目标");
            }
            
            // 构建AI提示词，根据关卡难度调整
            StringBuilder goalTitles = new StringBuilder();
            for (LearningGoals goal : goals) {
                goalTitles.append(goal.getTitle()).append(", ");
            }
            
            String difficultyPrompt = getDifficultyPrompt(levelId.intValue());
            String aiPrompt = "请根据以下学习目标生成5道选择题：" + goalTitles.toString() + 
                            "\n要求：\n1. 每题4个选项(A、B、C、D)\n2. " + difficultyPrompt + "\n3. 涵盖相关知识点\n" +
                            "4. 必须包含以下字段：questionText(题目内容), optionA, optionB, optionC, optionD(选项), " +
                            "correctAnswer(正确答案，只能是A、B、C、D中的一个), knowledgePoint(知识点), " +
                            "explanation(答案解析), difficultyLevel(难度等级1-5的整数)\n" +
                            "5. 返回标准JSON数组格式，确保所有字段都有值，不能为null或空字符串\n" +
                            "6. 示例格式：[{\"questionText\":\"题目内容\",\"optionA\":\"选项A\",\"optionB\":\"选项B\",\"optionC\":\"选项C\",\"optionD\":\"选项D\",\"correctAnswer\":\"A\",\"knowledgePoint\":\"知识点\",\"explanation\":\"解析内容\",\"difficultyLevel\":2}]";
            
            ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
            List<Question> questions = chatClient.prompt(aiPrompt)
                    .call()
                    .entity(new ParameterizedTypeReference<>() {});
            
            if (questions == null || questions.isEmpty()) {
                return Result.error("AI生成题目失败");
            }
            
            // 设置题目ID和创建时间，并确保必要字段有默认值
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                question.setId((long) (i + 1));
                question.setCreatedAt(LocalDateTime.now());
                
                // 确保必要字段有默认值
                if (question.getDifficultyLevel() == null) {
                    question.setDifficultyLevel(levelId.intValue()); // 根据关卡设置默认难度
                }
                if (question.getCorrectAnswer() == null || question.getCorrectAnswer().isEmpty()) {
                    question.setCorrectAnswer("A"); // 默认正确答案为A（这种情况不应该发生，但作为保险）
                }
                if (question.getExplanation() == null || question.getExplanation().isEmpty()) {
                    question.setExplanation("请参考相关学习资料"); // 默认解析
                }
            }
            
            // 存储题目到Redis，设置过期时间为当天结束
            String questionsKey = DAILY_QUESTIONS_KEY + userId + ":" + today + ":" + levelId;
            redisTemplate.opsForValue().set(questionsKey, questions, getSecondsUntilEndOfDay(), TimeUnit.SECONDS);
            
            // 返回题目给前端(不包含正确答案)
            List<Question> questionsForFrontend = new ArrayList<>();
            for (Question q : questions) {
                Question frontendQuestion = Question.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .knowledgePoint(q.getKnowledgePoint())
                        .difficultyLevel(q.getDifficultyLevel())
                        .explanation(q.getExplanation())
                        .createdAt(q.getCreatedAt())
                        // 注意：不包含correctAnswer字段，保持答题的公平性
                        .build();
                questionsForFrontend.add(frontendQuestion);
            }
            
            return Result.success(questionsForFrontend);
            
        } catch (Exception e) {
            return Result.error("生成题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据关卡获取难度提示词
     */
    private String getDifficultyPrompt(int levelNumber) {
        switch (levelNumber) {
            case 1: case 2: return "难度简单，基础概念题";
            case 3: case 4: return "难度中等，需要一定理解";
            case 5: case 6: return "难度较高，需要深入思考";
            case 7: case 8: return "难度很高，综合应用题";
            default: return "难度适中";
        }
    }
    
    /**
     * 计算到当天结束的秒数
     */
    private long getSecondsUntilEndOfDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        return java.time.Duration.between(now, endOfDay).getSeconds();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result evaluateUserAnswers(Long userId, Long levelId, List<String> userAnswers) {
        try {
            // 从Redis获取题目
            String today = LocalDate.now().toString();
            String questionsKey = DAILY_QUESTIONS_KEY + userId + ":" + today + ":" + levelId;
            Object questionsObj = redisTemplate.opsForValue().get(questionsKey);
            
            List<Question> questions = null;
            if (questionsObj != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                    objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    
                    String jsonString = objectMapper.writeValueAsString(questionsObj);
                    questions = objectMapper.readValue(jsonString, new TypeReference<List<Question>>() {});
                } catch (Exception e) {
                    return Result.error("题目数据格式错误: " + e.getMessage());
                }
            }
            
            if (questions == null || questions.isEmpty()) {
                return Result.error("未找到题目信息，请重新开始关卡");
            }
            
            if (userAnswers.size() != questions.size()) {
                return Result.error("答案数量不匹配");
            }
            
            // 计算得分
            int correctCount = 0;
            List<String> wrongTopics = new ArrayList<>();
            
            for (int i = 0; i < userAnswers.size(); i++) {
                String userAnswer = userAnswers.get(i);
                String correctAnswer = questions.get(i).getCorrectAnswer();
                
                if (userAnswer.equals(correctAnswer)) {
                    correctCount++;
                } else {
                    wrongTopics.add(questions.get(i).getKnowledgePoint());
                }
            }
            
            int totalQuestions = questions.size();
            double accuracy = (double) correctCount / totalQuestions;
            int score = (int) (accuracy * 100);
            
            // 计算星级
            int stars = 1;
            if (accuracy >= 0.9) {
                stars = 3;
            } else if (accuracy >= 0.7) {
                stars = 2;
            }
            
            // AI分析用户表现
            String analysisPrompt = "用户在第" + levelId + "关中答对了" + correctCount + "/" + totalQuestions + 
                                  "道题，正确率为" + String.format("%.1f", accuracy * 100) + "%。" +
                                  (wrongTopics.isEmpty() ? "全部答对！" : "错误涉及知识点: " + String.join(", ", wrongTopics)) +
                                  "请分析用户的学习质量并给出具体的学习建议。";
            
            ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
            String analysis = chatClient.prompt(analysisPrompt).call().content();
            
            // 保存通关记录
            UserLevelRecord record = UserLevelRecord.builder()
                    .userId(userId)
                    .levelId(levelId)
                    .starsEarned(stars)
                    .score(score)
                    .totalQuestions(totalQuestions)
                    .correctAnswers(correctCount)
                    .qualityAnalysis(analysis)
                    .completedAt(LocalDateTime.now())
                    .build();
            
            if (levelMapper.saveUserLevelRecord(record) > 0) {
                // 如果通过当前关卡，解锁下一关
                if (stars >= 1 && levelId < MAX_LEVELS) {
                    String progressKey = USER_DAILY_PROGRESS_KEY + userId + ":" + today;
                    Integer currentProgress = (Integer) redisTemplate.opsForValue().get(progressKey);
                    if (currentProgress == null || currentProgress <= levelId) {
                        redisTemplate.opsForValue().set(progressKey, levelId + 1, getSecondsUntilEndOfDay(), TimeUnit.SECONDS);
                    }
                }
                
                // 删除已使用的题目
                redisTemplate.delete(questionsKey);
                
                // 返回结果
                return Result.success(record);
            } else {
                return Result.error("保存记录失败");
            }
            
        } catch (Exception e) {
            return Result.error("评估答案失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result getUserLevelHistory(Long userId, Long levelId) {
        try {
            // 获取今日进度
            String today = LocalDate.now().toString();
            String progressKey = USER_DAILY_PROGRESS_KEY + userId + ":" + today;
            Integer currentLevel = (Integer) redisTemplate.opsForValue().get(progressKey);
            
            // 获取历史记录
            List<UserLevelRecord> records = levelMapper.getUserLevelRecords(userId, levelId);
            
            // 构建返回数据
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("currentLevel", currentLevel != null ? currentLevel : 1);
            result.put("todayDate", today);
            result.put("records", records);
            result.put("maxLevels", MAX_LEVELS);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取历史记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result getAllLevels() {
        try {
            List<Level> levels = levelMapper.getAllLevels();
            return Result.success(levels);
        } catch (Exception e) {
            return Result.error("获取关卡信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result getUserDailyStats(Long userId) {
        try {
            String today = LocalDate.now().toString();
            String progressKey = USER_DAILY_PROGRESS_KEY + userId + ":" + today;
            Integer currentLevel = (Integer) redisTemplate.opsForValue().get(progressKey);
            
            // 获取今日所有关卡的通关记录
            List<UserLevelRecord> todayRecords = new ArrayList<>();
            for (int i = 1; i <= MAX_LEVELS; i++) {
                List<UserLevelRecord> levelRecords = levelMapper.getUserLevelRecords(userId, (long) i);
                for (UserLevelRecord record : levelRecords) {
                    if (record.getCompletedAt().toLocalDate().equals(LocalDate.now())) {
                        todayRecords.add(record);
                    }
                }
            }
            
            // 计算统计信息
            int totalAttempts = todayRecords.size();
            int totalScore = todayRecords.stream().mapToInt(UserLevelRecord::getScore).sum();
            int maxLevel = currentLevel != null ? currentLevel - 1 : 0; // 当前可挑战关卡-1就是已通过的最高关卡
            
            // 构建返回数据
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("todayDate", today);
            stats.put("currentLevel", currentLevel != null ? currentLevel : 1);
            stats.put("maxLevelReached", maxLevel);
            stats.put("totalAttempts", totalAttempts);
            stats.put("totalScore", totalScore);
            stats.put("todayRecords", todayRecords);
            stats.put("maxLevels", MAX_LEVELS);
            
            // 计算表现等级
            String performanceLevel;
            if (maxLevel == 8) {
                performanceLevel = "全部通关";
            } else if (maxLevel >= 6) {
                performanceLevel = "优秀";
            } else if (maxLevel >= 4) {
                performanceLevel = "良好";
            } else if (maxLevel >= 2) {
                performanceLevel = "及格";
            } else {
                performanceLevel = "需努力";
            }
            stats.put("performanceLevel", performanceLevel);
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取每日统计失败: " + e.getMessage());
        }
    }
}