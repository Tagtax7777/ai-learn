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
        String aiPrompt = "你是一个专业的教育出题专家。请从以下学习目标中随机选择一个：[" + goalNames + "]，生成一道内容简洁的测试题。\n\n" +
                "出题条件：\n" +
                "- 难度等级：" + difficultyLevel + " (1-9级，数字越大越难。8-9级必须是非常刁钻的底层原理或复杂应用)\n" +
                "- 题型：随机选择一种（1-单选, 2-判断, 3-填空）\n\n" +
                "绝对要求（非常重要）：\n" +
                "1. 必须且只能输出一个纯合法的 JSON 对象，绝对不要包含任何 Markdown 标记（如 ```json），也不要有任何前言后语！\n" +
                "2. questionText 字段：如果是填空题，填空处必须使用 HTML 标签，例如：\"It is a red <input type='text' class='wheel-input' /> (苹果) .\"；如果是其他题型，正常输出纯文本。\n" +
                "3. options 字段：单选题输出字符串数组，如 [\"A. xx\", \"B. xx\", \"C. xx\", \"D. xx\"]；判断题严格输出 [\"正确\", \"错误\"]；填空题严格输出 null。\n" +
                "4. correctAnswer 字段：单选题输出字母（如 \"A\"），判断题输出 \"正确\" 或 \"错误\"，填空题输出确切的填空词。\n\n" +
                "5. 除了自然语言类（英语，日语）相关的目标生成的题目，其他知识类题目用中文题干" +
                "请严格参考以下 JSON 结构模板进行输出（只需替换值为你生成的内容）：\n" +
                "{\n" +
                "  \"difficultyLevel\": " + difficultyLevel + ",\n" +
                "  \"questionType\": 1,\n" +
                "  \"questionText\": \"这里是题目正文\",\n" +
                "  \"options\": [\"A. 选项1\", \"B. 选项2\", \"C. 选项3\", \"D. 选项4\"],\n" +
                "  \"correctAnswer\": \"A\"\n" +
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
