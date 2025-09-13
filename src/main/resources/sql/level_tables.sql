-- 闯关答题功能相关数据库表

-- 1. 题目表
CREATE TABLE IF NOT EXISTS `questions` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `question_text` TEXT NOT NULL COMMENT '题目内容',
    `option_a` VARCHAR(500) NOT NULL COMMENT '选项A',
    `option_b` VARCHAR(500) NOT NULL COMMENT '选项B',
    `option_c` VARCHAR(500) NOT NULL COMMENT '选项C',
    `option_d` VARCHAR(500) NOT NULL COMMENT '选项D',
    `correct_answer` CHAR(1) NOT NULL COMMENT '正确答案(A/B/C/D)',
    `difficulty_level` INT NOT NULL DEFAULT 1 COMMENT '难度等级(1-5)',
    `knowledge_point` VARCHAR(200) COMMENT '知识点标签',
    `explanation` TEXT COMMENT '答案解析',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_difficulty` (`difficulty_level`),
    INDEX `idx_knowledge` (`knowledge_point`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- 2. 关卡表
CREATE TABLE IF NOT EXISTS `levels` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `level_name` VARCHAR(100) NOT NULL COMMENT '关卡名称',
    `level_number` INT NOT NULL COMMENT '关卡序号',
    `description` TEXT COMMENT '关卡描述',
    `required_goals` TEXT COMMENT '关联的学习目标ID(JSON格式)',
    `unlock_condition` VARCHAR(200) COMMENT '解锁条件',
    `max_stars` INT DEFAULT 3 COMMENT '最高星级',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否启用(1启用 0禁用)',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_level_number` (`level_number`),
    INDEX `idx_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关卡表';

-- 3. 用户关卡通关记录表
CREATE TABLE IF NOT EXISTS `user_level_records` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `level_id` BIGINT NOT NULL COMMENT '关卡ID',
    `stars_earned` INT NOT NULL COMMENT '获得星级(1-3)',
    `score` INT NOT NULL COMMENT '得分',
    `total_questions` INT NOT NULL COMMENT '总题数',
    `correct_answers` INT NOT NULL COMMENT '正确答案数',
    `completion_time` INT COMMENT '完成时间(秒)',
    `quality_analysis` TEXT COMMENT 'AI分析结果(JSON格式)',
    `completed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '完成时间',
    INDEX `idx_user_level` (`user_id`, `level_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_level_id` (`level_id`),
    INDEX `idx_completed_at` (`completed_at`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`level_id`) REFERENCES `levels`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关卡通关记录表';

-- 插入8关每日挑战数据
INSERT INTO `levels` (`level_name`, `level_number`, `description`, `required_goals`, `unlock_condition`, `max_stars`, `is_active`) VALUES
('第1关', 1, '入门级 - 基础概念测试', '[]', '每日开始', 3, 1),
('第2关', 2, '初级 - 基本应用练习', '[]', '通过第1关', 3, 1),
('第3关', 3, '中级 - 知识点综合', '[]', '通过第2关', 3, 1),
('第4关', 4, '中高级 - 深度理解', '[]', '通过第3关', 3, 1),
('第5关', 5, '高级 - 复杂应用', '[]', '通过第4关', 3, 1),
('第6关', 6, '专家级 - 综合分析', '[]', '通过第5关', 3, 1),
('第7关', 7, '大师级 - 创新思维', '[]', '通过第6关', 3, 1),
('第8关', 8, '终极挑战 - 全面掌握', '[]', '通过第7关', 3, 1);

-- 4. 每日挑战统计表
CREATE TABLE IF NOT EXISTS `daily_challenge_stats` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `challenge_date` DATE NOT NULL COMMENT '挑战日期',
    `max_level_reached` INT NOT NULL DEFAULT 1 COMMENT '当日最高通过关卡',
    `total_attempts` INT NOT NULL DEFAULT 0 COMMENT '当日总尝试次数',
    `total_score` INT NOT NULL DEFAULT 0 COMMENT '当日总得分',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_date` (`user_id`, `challenge_date`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_challenge_date` (`challenge_date`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日挑战统计表';

-- 创建视图：用户每日挑战统计
CREATE OR REPLACE VIEW `user_daily_challenge_stats` AS
SELECT 
    u.id as user_id,
    u.username,
    dcs.challenge_date,
    dcs.max_level_reached,
    dcs.total_attempts,
    dcs.total_score,
    CASE 
        WHEN dcs.max_level_reached = 8 THEN '全部通关'
        WHEN dcs.max_level_reached >= 6 THEN '优秀'
        WHEN dcs.max_level_reached >= 4 THEN '良好'
        WHEN dcs.max_level_reached >= 2 THEN '及格'
        ELSE '需努力'
    END as performance_level
FROM `users` u
LEFT JOIN `daily_challenge_stats` dcs ON u.id = dcs.user_id
ORDER BY u.id, dcs.challenge_date DESC;

-- 创建存储过程：获取用户今日挑战进度
DELIMITER //
CREATE PROCEDURE GetUserDailyProgress(IN p_user_id BIGINT, IN p_date DATE)
BEGIN
    DECLARE current_level INT DEFAULT 1;
    
    -- 获取今日最高通过关卡
    SELECT COALESCE(max_level_reached, 1) INTO current_level
    FROM daily_challenge_stats 
    WHERE user_id = p_user_id AND challenge_date = p_date;
    
    -- 返回8关信息及解锁状态
    SELECT 
        level_number as id,
        CONCAT('第', level_number, '关') as level_name,
        level_number,
        description,
        3 as max_stars,
        CASE WHEN level_number <= current_level THEN 1 ELSE 0 END as is_active,
        COALESCE((
            SELECT MAX(stars_earned) 
            FROM user_level_records ulr 
            WHERE ulr.user_id = p_user_id 
            AND ulr.level_id = level_number 
            AND DATE(ulr.completed_at) = p_date
        ), 0) as today_best_stars,
        COALESCE((
            SELECT MAX(score) 
            FROM user_level_records ulr 
            WHERE ulr.user_id = p_user_id 
            AND ulr.level_id = level_number 
            AND DATE(ulr.completed_at) = p_date
        ), 0) as today_best_score
    FROM levels 
    WHERE level_number <= 8
    ORDER BY level_number;
END //
DELIMITER ;

-- 创建存储过程：更新每日挑战统计
DELIMITER //
CREATE PROCEDURE UpdateDailyChallengeStats(IN p_user_id BIGINT, IN p_date DATE, IN p_level INT, IN p_score INT)
BEGIN
    INSERT INTO daily_challenge_stats (user_id, challenge_date, max_level_reached, total_attempts, total_score)
    VALUES (p_user_id, p_date, p_level, 1, p_score)
    ON DUPLICATE KEY UPDATE 
        max_level_reached = GREATEST(max_level_reached, p_level),
        total_attempts = total_attempts + 1,
        total_score = total_score + p_score,
        updated_at = CURRENT_TIMESTAMP;
END //
DELIMITER ;