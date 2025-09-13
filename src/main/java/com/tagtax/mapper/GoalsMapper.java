package com.tagtax.mapper;

import com.tagtax.entity.GoalTasks;
import com.tagtax.entity.LearningGoals;
import com.tagtax.entity.Result;
import com.tagtax.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;


@Mapper
public interface GoalsMapper {
    Integer addGoals(List<LearningGoals> goals);

    Integer addTasks(List<GoalTasks> tasks);

    List<LearningGoals> getGoalsByUserId(Long userId);

    List<GoalTasks> getTasksByGoalId(Long goalId);

    LearningGoals getGoalByGoalId(Long goalId);

    GoalTasks getTaskByTaskId(Long taskId);

    Integer updateGoalHours(@Param("goalId") Long goalId, @Param("hours")Double hours);

    Integer updateTaskHours(@Param("taskId")Long taskId, @Param("hours")Double hours);

    Integer updateTaskEstimatedHours(@Param("taskId") Long taskId, @Param("hours")Double hours);

    Integer updateGoalEndDate(@Param("goalId") Long goalId, @Param("endDate") LocalDate endDate);

    Integer updateGoalStatusToFinish(Long goalId);

    Integer updateTaskStatusToFinish(Long taskId);

    Integer deleteGoalsByUserId(Long goalId);

    Integer getFinishedGoals(Long userId);
}
