package com.tagtax.controller;

import com.tagtax.entity.KnowledgeCard;
import com.tagtax.entity.Result;
import com.tagtax.service.KnowledgeCardService;
import com.tagtax.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/knowledgeCards")
public class KnowledgeCardController {

    @Autowired
    private KnowledgeCardService knowledgeCardService;

    /**
     * 根据任务ID获取知识卡片列表
     */
    @GetMapping("/getByTaskId")
    public Result getByTaskId(@RequestParam Long taskId, @RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        List<KnowledgeCard> cards = knowledgeCardService.getCardsByTaskId(taskId);
        return Result.success(cards);
    }

    /**
     * 批量创建知识卡片
     */
    @PostMapping("/batchCreate")
    public Result batchCreate(@RequestBody List<KnowledgeCard> cards, @RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        int count = knowledgeCardService.batchCreateCards(cards);
        return Result.success("成功创建" + count + "张知识卡片");
    }

    /**
     * 删除知识卡片
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestParam Long cardId, @RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        boolean success = knowledgeCardService.deleteCard(cardId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
