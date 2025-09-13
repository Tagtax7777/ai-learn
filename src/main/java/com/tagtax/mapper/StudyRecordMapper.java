package com.tagtax.mapper;

import com.tagtax.entity.StudyRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudyRecordMapper {

    // 插入一条用户打卡记录
    Integer insert(StudyRecord record);

    // 获取用户全部打卡记录
    List<StudyRecord> getStudyRecord(Long userId);

}
