package com.tagtax.service.serviceImpl;

import com.tagtax.entity.*;
import com.tagtax.mapper.BadgeMapper;
import com.tagtax.mapper.GoalsMapper;
import com.tagtax.mapper.StudyRecordMapper;
import com.tagtax.mapper.StudyTimeMapper;
import com.tagtax.service.GoalsService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GoalsServiceImpl implements GoalsService {
    @Autowired
    private ChatModel zhiPuAiChatModel;

    @Autowired
    private GoalsMapper goalsMapper;

    @Autowired
    private StudyTimeMapper studyTimeMapper;

    @Autowired
    private BadgeMapper badgeMapper;

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createGoalsByAi(String userScanInput, Long userId) {
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        // 替换特殊字符
        String safeInput = userScanInput
                .replace("<", "【")
                .replace(">", "】");

        Date date=new Date();//此时date为当前的时间
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日");//设置当前时间的格式，为年-月-日
        String dateString=dateFormat.format(date);
        System.out.println(dateString);

        String aiPrompt = "请分析以下内容并返回学习目标和目标下面的任务，" +
                "并且严格填充对应的userId(" + userId +
                "),标题，目标type(1考试，2技能，3课程)，开始日期（" + dateString +
                ")，结束日期（根据提供的开始日期进行填写），" +
                "状态（0进行中，1已完成），耗费时间（小时）,sortOrder(执行顺序)等信息" + safeInput;
        List<LearningGoals> goals = chatClient.prompt(aiPrompt)
                .call()
                .entity(new ParameterizedTypeReference<>() {});


        // 把生成的目标和任务通过循环存到数据库中
        if(goalsMapper.addGoals(goals) > 0){
            for(LearningGoals goal:goals){
                Long goalId = goal.getId();
                List<GoalTasks> goalTasks = goal.getGoalTasks();
                for(GoalTasks goalTask:goalTasks){
                    goalTask.setGoalId(goalId);
                }
                if(goalsMapper.addTasks(goalTasks) > 0){
                    return Result.success(goals);
                }else {
                    return Result.error("生成失败，请重试");
                }
            }
        }else {
            return Result.error("生成失败，请重试");
        }
        return Result.error("生成失败，请重试");
    }

    @Override
    public Result cleanDataByAi(String userScanInput, Long userId) {
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        // 替换特殊字符
        String safeInput = userScanInput
                .replace("<", "【")
                .replace(">", "】");


        String aiPrompt = "请清除下面这一片数据的无用数据，并自己进行分析，" +
                "分析出你认为的几个学科（学科请用中文写出），学科数量最多5个，" +
                "分析出来的学科如果出现缺字漏字请进行补充（例如软件需补充称软件需求工程，框架程补充成框架程序）" +
                "，除了学科这一数据其他数据都不要" + safeInput;
        Map<Object, Object> res = chatClient.prompt(aiPrompt)
                .call()
                .entity(new ParameterizedTypeReference<>() {});

        assert res != null;
        return Result.success(res.toString());
    }

    @Override
    public Flux<String> talkToAi(String text) {
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);

        String aiPrompt = "你是一个语气活泼开朗的学习规划助手，会为用户提供学习规划建议，" +
                "下面是用户的学习规划，请给出100字左右的建议:" + text;
        Flux<String> res = chatClient.prompt(aiPrompt)
                .stream()
                .content();

        return res;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result learnTask(Long userId, Long goalId, Long taskId, Double hours,
                            LocalTime startTime, LocalTime endTime, LocalDate timingDate) {

        LearningGoals learningGoal = goalsMapper.getGoalByGoalId(goalId);
        GoalTasks goalTask = goalsMapper.getTaskByTaskId(taskId);
        UserStudyTime userStudyTime = studyTimeMapper.getStudyTimeByUserId(userId);

        // 得到当前目标和子任务的预计完成时长和实际时长  得到用户学习总时长和今日总时长
        Double goalEstimatedHours = learningGoal.getEstimatedHours();
        Double goalActualHours = learningGoal.getActualHours();
        Double taskEstimatedHours = goalTask.getEstimatedHours();
        Double taskActualHours = goalTask.getActualHours();
        Double userStudyTotalHours = userStudyTime.getTotalHours();
        Double userStudyDailyHours = userStudyTime.getDailyHours();

        studyTimeMapper.updateTotalHoursByUserId(userId, userStudyDailyHours + hours);
        studyTimeMapper.updateDailyHoursByUserId(userId, userStudyTotalHours + hours);

        if (goalActualHours + hours >= goalEstimatedHours) { // 如果总目标时长达成
            goalsMapper.updateGoalStatusToFinish(goalId);
            goalsMapper.updateGoalHours(goalId, goalEstimatedHours);
        }

        if(taskActualHours + hours >= taskEstimatedHours){ // 如果子任务时长已经达成
            goalsMapper.updateTaskStatusToFinish(taskId);
            goalsMapper.updateGoalHours(goalId, goalActualHours + taskEstimatedHours); // 总目标加上时长
            goalsMapper.updateTaskHours(taskId, taskEstimatedHours); // 子任务时长也填满

            // 积分更新
            Integer point = studyTimeMapper.getStudyTimeByUserId(userId).getPoints() + 20;
            studyTimeMapper.updatePointsByUserId(userId, point);

            // 检查可获得的徽章
            List<Badge> eligibleBadges = badgeMapper.findByPointsThreshold(point);

            // 授予新徽章
            eligibleBadges.forEach(badge -> {
                if (!badgeMapper.exists(userId, badge.getId())) {
                    badgeMapper.addUserBadge(userId, badge.getId());
                }
            });

            // 将用户打卡记录插入时间记录表
            StudyRecord studyRecord = new StudyRecord();
            studyRecord.setUserId(userId);
            studyRecord.setStartTime(startTime);
            studyRecord.setEndTime(endTime);
            studyRecord.setStudyDate(timingDate);
            studyRecordMapper.insert(studyRecord);

            return Result.success("打卡成功");
        }else {
            goalsMapper.updateGoalHours(goalId, goalActualHours + hours);
            goalsMapper.updateTaskHours(taskId, taskActualHours + hours);
            Integer point = studyTimeMapper.getStudyTimeByUserId(userId).getPoints() + 20;
            studyTimeMapper.updatePointsByUserId(userId, point);

            // 将用户打卡记录插入时间记录表
            StudyRecord studyRecord = new StudyRecord();
            studyRecord.setUserId(userId);
            studyRecord.setStartTime(startTime);
            studyRecord.setEndTime(endTime);
            studyRecord.setStudyDate(timingDate);
            studyRecordMapper.insert(studyRecord);
            return Result.success("打卡成功");
        }
    }


}
