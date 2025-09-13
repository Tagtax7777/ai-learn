package com.tagtax.mapper;

import com.tagtax.entity.Level;
import com.tagtax.entity.UserLevelRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LevelMapper {
    
    /**
     * 获取所有启用的关卡
     * @return 关卡列表
     */
    List<Level> getAllActiveLevels();
    
    /**
     * 根据ID获取关卡信息
     * @param levelId 关卡ID
     * @return 关卡信息
     */
    Level getLevelById(@Param("levelId") Long levelId);
    
    /**
     * 获取所有关卡
     * @return 所有关卡
     */
    List<Level> getAllLevels();
    
    /**
     * 保存用户关卡通关记录
     * @param record 通关记录
     * @return 影响行数
     */
    int saveUserLevelRecord(UserLevelRecord record);
    
    /**
     * 获取用户关卡历史记录
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 历史记录列表
     */
    List<UserLevelRecord> getUserLevelRecords(@Param("userId") Long userId, @Param("levelId") Long levelId);

    /**
     * 获取用户关卡历史记录
     * @param userId 用户ID
     * @return 历史记录列表
     */
    List<UserLevelRecord> getAllUserLevelRecords(@Param("userId") Long userId);
    
    /**
     * 获取用户最佳成绩
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 最佳记录
     */
    UserLevelRecord getUserBestRecord(@Param("userId") Long userId, @Param("levelId") Long levelId);
    
    /**
     * 添加新关卡
     * @param level 关卡信息
     * @return 影响行数
     */
    int addLevel(Level level);
    
    /**
     * 更新关卡信息
     * @param level 关卡信息
     * @return 影响行数
     */
    int updateLevel(Level level);
    
    /**
     * 删除关卡
     * @param levelId 关卡ID
     * @return 影响行数
     */
    int deleteLevel(@Param("levelId") Long levelId);
}