package com.tagtax.service;

import com.tagtax.entity.KnowledgeCard;

import java.util.List;

public interface KnowledgeCardService {

    /**
     * 根据任务ID获取知识卡片列表
     */
    List<KnowledgeCard> getCardsByTaskId(Long taskId);

    /**
     * 删除知识卡片
     */
    boolean deleteCard(Long cardId);

    /**
     * 批量创建知识卡片
     */
    int batchCreateCards(List<KnowledgeCard> cards);
}
