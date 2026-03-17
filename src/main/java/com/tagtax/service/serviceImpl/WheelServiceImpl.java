package com.tagtax.service.serviceImpl;

import com.tagtax.entity.LearningGoals;
import com.tagtax.entity.Result;
import com.tagtax.entity.dto.RewardRequest;
import com.tagtax.entity.dto.WheelQuestionRequest;
import com.tagtax.mapper.GoalsMapper;
import com.tagtax.mapper.StudyRecordMapper;
import com.tagtax.mapper.StudyTimeMapper;
import com.tagtax.mapper.WheelMapper;
import com.tagtax.service.WheelService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WheelServiceImpl implements WheelService {

    @Autowired
    private ChatModel zhiPuAiChatModel;

    @Autowired
    private WheelMapper wheelMapper;

    @Autowired
    private GoalsMapper goalsMapper;

    @Autowired
    private StudyTimeMapper studyTimeMapper;

    @Override
    public Result getQuestion(Long id, Integer difficultyLevel) {
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        List<LearningGoals> goals = goalsMapper.getGoalsByUserId(id);

        // 把所有目标的名称拼接到字符串中，供后面的prompt使用
        String goalNames = "";
        for (LearningGoals goal : goals) {
            goalNames = goalNames + goal.getTitle() + ", ";
        }
        System.out.println(goalNames);
        String aiPrompt = "请根据这些学习目标:" + goalNames + "生成题目，你是一个专业的教育出题专家。请根据以下条件，生成一道内容简洁的单道测试题。\n" +
                "\n" +
                "出题条件：\n" +
                "- 知识领域：{前面学习目标中随机选一个}\n" +
                "- 难度等级：{ " + difficultyLevel + "} (1-9级，数字越大越难。8-9级必须是非常刁钻的底层原理或复杂应用)\n" +
                "- 题型：随机选择一种（1-单选, 2-判断, 3-填空）\n" +
                "\n" +
                "绝对要求：\n" +
                "不要包含任何 Markdown 标记（如 ```json），不要有任何前言后语和解释。结构必须严格如下：\n" +
                "{\n" +
                "  \"id\": 1001, \n" +
                "  \"difficultyLevel\": 8,\n" +
                "  \"questionType\": 1, \n" +
                "  \"questionText\": \"题目正文\",\n" +
                "  \"options\": 选择题[\"A. xx\", \"B. xx\", \"C. xx\", \"D. xx\"], // 如果是判断题则为 [\"正确\", \"错误\"]，如果是填空题则严格输出 null\n" +
                "  \"correctAnswer\": \"选择题则是前面的字母，判断题则是正确或错误，填空题则是填空词\"\n" +
                "}";

        WheelQuestionRequest wheelQuestionRequest = chatClient.prompt(aiPrompt)
                .call()
                .entity(new ParameterizedTypeReference<>() {
                });
        return Result.success(wheelQuestionRequest);
    }

    @Override
    public Result claimReward(Integer difficultyLevel, Long id) {
        RewardRequest rewardRequest = new RewardRequest();
        if (difficultyLevel < 4) {
            rewardRequest.setRewardType(1);
            rewardRequest.setRewardName("10学习积分");
            // 积分更新
            Integer point = studyTimeMapper.getStudyTimeByUserId(id).getPoints() + 10;
            studyTimeMapper.updatePointsByUserId(id, point);

        } else if (difficultyLevel < 6) {
            rewardRequest.setRewardType(2);
            rewardRequest.setRewardName("商城随机立减劵");
            wheelMapper.addRewards(id, 2, "商城随机立减劵");
        } else if (difficultyLevel < 8) {
            rewardRequest.setRewardType(3);
            rewardRequest.setRewardName("智学轨迹专属定制马克杯");
            wheelMapper.addRewards(id, 3, "智学轨迹专属定制马克杯");
        } else {
            rewardRequest.setRewardType(4);
            rewardRequest.setRewardName("现金奖励");
            wheelMapper.addRewards(id, 4, "现金奖励");
        }
        return Result.success(rewardRequest);
    }
}
