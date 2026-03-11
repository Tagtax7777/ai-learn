package com.tagtax.mapper;

import com.tagtax.entity.KnowledgeCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgeCardMapper {

    /**
     * 根据任务ID获取知识卡片列表
     */
    List<KnowledgeCard> getCardsByTaskId(@Param("taskId") Long taskId);

    /**
     * 删除知识卡片
     */
    int deleteCard(@Param("cardId") Long cardId);

    /**
     * 批量创建知识卡片
     */
    int batchCreateCards(@Param("cards") List<KnowledgeCard> cards);
}
