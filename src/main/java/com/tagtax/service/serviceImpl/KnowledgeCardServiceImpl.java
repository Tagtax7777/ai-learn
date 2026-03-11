package com.tagtax.service.serviceImpl;

import com.tagtax.entity.KnowledgeCard;
import com.tagtax.mapper.KnowledgeCardMapper;
import com.tagtax.service.KnowledgeCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KnowledgeCardServiceImpl implements KnowledgeCardService {

    @Autowired
    private KnowledgeCardMapper knowledgeCardMapper;

    @Override
    public List<KnowledgeCard> getCardsByTaskId(Long taskId) {
        return knowledgeCardMapper.getCardsByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCard(Long cardId) {
        return knowledgeCardMapper.deleteCard(cardId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateCards(List<KnowledgeCard> cards) {
        if (cards == null || cards.isEmpty()) {
            return 0;
        }
        return knowledgeCardMapper.batchCreateCards(cards);
    }
}
