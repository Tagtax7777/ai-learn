# AI-Learn 项目详细分析文档

## 项目概览

| 属性 | 值 |
|------|-----|
| **项目名称** | ai-learn |
| **项目版本** | 0.0.1-SNAPSHOT |
| **技术栈** | Spring Boot 3.5.3 + Spring AI + MyBatis + MySQL + Redis |
| **Java版本** | 17 |
| **构建工具** | Maven |
| **包名** | com.tagtax |
| **服务端口** | 8080 |

**项目简介**：这是一个基于 AI 的智能学习管理平台，集成了智谱 AI（GLM-4.7 模型）为用户提供个性化的学习规划、关卡挑战、知识卡片生成等功能。

---

## 目录结构

```
ai-learn/
├── pom.xml                                    # Maven项目配置
├── src/
│   ├── main/
│   │   ├── java/com/tagtax/
│   │   │   ├── AiLearnApplication.java       # 主启动类
│   │   │   ├── config/                        # 配置类
│   │   │   ├── controller/                    # 控制器层 (11个)
│   │   │   ├── entity/                        # 实体类 (19个)
│   │   │   ├── mapper/                        # 数据访问层 (8个)
│   │   │   ├── schedule/                      # 定时任务
│   │   │   ├── service/                       # 服务接口 (9个)
│   │   │   ├── serviceImpl/                   # 服务实现 (9个)
│   │   │   ├── utils/                         # 工具类 (4个)
│   │   │   └── websocket/                     # WebSocket
│   │   └── resources/
│   │       ├── application.yml                # 应用配置
│   │       ├── mapper/                        # MyBatis映射文件 (8个)
│   │       └── sql/                           # 数据库脚本 (3个)
```

---

## 核心依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| spring-boot-starter-web | 3.5.3 | Web开发基础框架 |
| spring-ai-zhipuai-ai-spring-boot-starter | - | 智谱AI集成 |
| mybatis-spring-boot-starter | - | 数据持久化 |
| mysql-connector-java | - | MySQL数据库驱动 |
| spring-boot-starter-data-redis | - | Redis缓存 |
| jjwt-api | 0.11.5 | JWT认证 |
| cn.hutool | - | Java工具库 |
| cloopen-sdk-java | - | 容联云短信 |

---

## 功能模块详解

### 1. 用户管理模块

**核心文件**:
- `controller/UserController.java`
- `mapper/UserMapper.java`
- `service/UserService.java`
- `entity/User.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/user/sendSms` | POST | 发送短信验证码 |
| `/user/checkSms` | POST | 验证码登录/注册 |
| `/user/getUserInfo` | GET | 获取当前用户信息 |
| `/user/getUser` | GET | 获取指定用户信息 |

**用户实体字段**:
- `id` - 用户ID
- `username` - 用户名
- `phone` - 手机号
- `avatar_url` - 头像URL
- `created_at` - 创建时间

**认证机制**:
- 使用 JWT (JSON Web Token) 进行身份认证
- 令牌有效期：24小时
- 令牌生成工具：`utils/JwtUtil.java`

---

### 2. 学习目标管理模块

**核心文件**:
- `controller/GoalsController.java`
- `serviceImpl/GoalsServiceImpl.java`
- `entity/LearningGoals.java`
- `entity/GoalTasks.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/api/goals/createGoalsByAi` | POST | AI生成学习目标和任务 |
| `/api/goals/cleanDataByAi` | POST | AI数据清洗 |
| `/api/goals/talkToAi` | GET | AI对话（流式响应）|
| `/api/goals/learnTask` | POST | 学习任务打卡 |
| `/api/goals/getGoalsByUserId` | GET | 获取用户所有目标 |
| `/api/goals/getTasksByGoalId` | GET | 获取目标下的任务列表 |
| `/api/goals/deleteGoal` | DELETE | 删除学习目标 |
| `/api/goals/updateTaskEstimatedHour` | POST | 更新任务预计时间 |
| `/api/goals/updateGoalEndDate` | POST | 更新目标结束日期 |
| `/api/goals/getFinishedGoals` | GET | 获取已完成目标 |

**学习目标类型**:
1. 考试 (type=1)
2. 技能 (type=2)
3. 课程 (type=3)
4. 自定义 (type=4)

**目标状态**:
- 0 - 进行中
- 1 - 已完成
- 2 - 已暂停
- 3 - 已取消

**任务状态**:
- 0 - 待办
- 1 - 进行中
- 2 - 完成
- 3 - 跳过

---

### 3. 闯关答题模块

**核心文件**:
- `controller/LevelController.java`
- `serviceImpl/LevelServiceImpl.java`
- `entity/Level.java`
- `entity/Question.java`
- `entity/UserLevelRecord.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/api/levels/getUserLevels` | GET | 获取用户可用关卡 |
| `/api/levels/startLevel` | POST | 开始关卡（AI生成题目）|
| `/api/levels/submitAnswers` | POST | 提交答案并评分 |
| `/api/levels/getLevelHistory` | GET | 获取关卡挑战历史 |
| `/api/levels/getAllHistory` | GET | 获取所有挑战记录 |
| `/api/levels/getAllLevels` | GET | 获取所有关卡（管理员）|
| `/api/levels/getDailyStats` | GET | 获取每日挑战统计 |
| `/api/levels/clearCache` | DELETE | 清除关卡缓存 |

**关卡设计（8关难度递增）**:

| 关卡 | 等级 | 描述 |
|------|------|------|
| 第1关 | 入门级 | 基础概念测试 |
| 第2关 | 初级 | 基本应用练习 |
| 第3关 | 中级 | 知识点综合 |
| 第4关 | 中高级 | 深度理解 |
| 第5关 | 高级 | 复杂应用 |
| 第6关 | 专家级 | 综合分析 |
| 第7关 | 大师级 | 创新思维 |
| 第8关 | 终极挑战 | 全面掌握 |

**评分机制**:
- 星级评定：1-3星
- 根据正确率和答题质量自动评分
- AI 提供质量分析和学习建议

**缓存策略**:
- 使用 Redis 缓存题目
- 每日自动重置进度
- 支持手动清除缓存

---

### 4. 知识卡片模块

**核心文件**:
- `controller/KnowledgeCardController.java`
- `entity/KnowledgeCard.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/knowledgeCards/getByTaskId` | GET | 根据任务ID获取知识卡片 |
| `/knowledgeCards/batchCreate` | POST | 批量创建知识卡片 |
| `/knowledgeCards/delete` | DELETE | 删除知识卡片 |

**知识卡片字段**:
- `card_name` - 知识名称
- `explanation` - 解释说明
- `example` - 示例代码/案例
- `task_id` - 关联任务ID

---

### 5. 签到打卡模块

**核心文件**:
- `controller/CheckInController.java`
- `serviceImpl/CheckInServiceImpl.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/checkIn/userCheckIn` | POST | 用户签到 |
| `/checkIn/getOneYearCheckIn` | GET | 查询年度签到记录 |
| `/checkIn/isCheckIn` | GET | 查询今日是否签到 |
| `/checkIn/getCurrentStreak` | GET | 查询连续签到天数 |

---

### 6. 学习时间统计模块

**核心文件**:
- `controller/StudyTimeController.java`
- `mapper/StudyTimeMapper.java`
- `entity/UserStudyTime.java`
- `schedule/StudyTimeSchedule.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/studyTime/getStudyTimeByUserId` | GET | 获取用户学习时长 |
| `/studyTime/getDailyHoursTop10` | GET | 日排行榜Top10 |
| `/studyTime/getTotalHoursTop10` | GET | 总排行榜Top10 |
| `/studyTime/getPointTop10` | GET | 积分排行榜Top10 |
| `/studyTime/getStudyTimeRecord` | GET | 获取学习记录 |

**统计指标**:
- 总学习时长
- 每日学习时长
- 学习积分
- 排行榜排名

**定时任务**:
- 每日凌晨1点重置过期学习时间数据

---

### 7. 徽章成就模块

**核心文件**:
- `controller/BadgeController.java`
- `entity/Badge.java`
- `entity/UserBadge.java`
- `entity/Achievement.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/badge/getAllBadges` | GET | 获取所有徽章 |
| `/badge/getUserBadges` | GET | 获取用户徽章 |
| `/badge/getUnearnedBadges` | GET | 获取未获得徽章 |
| `/badge/countUserBadges` | GET | 统计用户徽章数量 |

**徽章系统**:
- 根据学习积分自动授予
- 不同积分对应不同徽章
- 支持徽章展示和统计

---

### 8. AI报告模块

**核心文件**:
- `controller/AIReportController.java`
- `entity/AIReport.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/api/aiReport/latest` | GET | 获取最新AI报告 |
| `/api/aiReport/clearCache` | DELETE | 清除AI报告缓存 |

**报告内容**:
- 学习分析
- 进度追踪
- AI建议
- 使用 Redis 缓存优化

---

### 9. Web端AI接口

**核心文件**:
- `controller/WebAiController.java`
- `serviceImpl/WebAiServiceImpl.java`
- `entity/WebApiResponse.java`
- `entity/WebLearningPath.java`

**功能说明**:

| API | 方法 | 描述 |
|-----|------|------|
| `/web/getLearnPath` | GET | 获取学习路径 |

**预设学习路线**:
- Java 学习路线
- Web 前端学习路线
- 其他路线由 AI 动态生成

---

### 10. 学习报告模块

**核心文件**:
- `controller/learningReportController.java`
- `entity/LearningReport.java`

**功能说明**:
- 学习报告存储与查询
- 学习数据分析
- 进度追踪

---

## 数据库设计

### 核心表结构

#### 用户表 (users)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255),
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    created_at DATETIME
);
```

#### 学习目标表 (learning_goals)
```sql
CREATE TABLE learning_goals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    title VARCHAR(255),
    goal_type INT,                    -- 1考试 2技能 3课程 4自定义
    start_date DATE,
    end_date DATE,
    status INT,                       -- 0进行中 1已完成 2已暂停 3已取消
    estimated_hours DECIMAL(10,2),
    actual_hours DECIMAL(10,2),
    completion_date DATE
);
```

#### 学习任务表 (goal_tasks)
```sql
CREATE TABLE goal_tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goal_id BIGINT,
    title VARCHAR(255),
    sort_order INT,
    estimated_hours DECIMAL(10,2),
    actual_hours DECIMAL(10,2),
    status INT                        -- 0待办 1进行中 2完成 3跳过
);
```

#### 知识卡片表 (knowledge_cards)
```sql
CREATE TABLE knowledge_cards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT,
    card_name VARCHAR(255),
    explanation TEXT,
    example TEXT,
    created_at DATETIME,
    updated_at DATETIME
);
```

#### 关卡表 (levels)
```sql
CREATE TABLE levels (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    level_name VARCHAR(100),
    level_number INT,
    description TEXT,
    required_goals JSON,
    unlock_condition VARCHAR(255),
    max_stars INT DEFAULT 3,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME
);
```

#### 题目表 (questions)
```sql
CREATE TABLE questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_text TEXT,
    option_a VARCHAR(255),
    option_b VARCHAR(255),
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_answer CHAR(1),
    difficulty_level INT,
    knowledge_point VARCHAR(255),
    explanation TEXT
);
```

#### 用户关卡记录表 (user_level_records)
```sql
CREATE TABLE user_level_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    level_id BIGINT,
    stars_earned INT,
    score DECIMAL(5,2),
    total_questions INT,
    correct_answers INT,
    completion_time INT,
    quality_analysis JSON,
    completed_at DATETIME
);
```

#### 用户学习时间表 (user_study_time)
```sql
CREATE TABLE user_study_time (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    total_hours DECIMAL(10,2),
    daily_hours DECIMAL(10,2),
    points INT DEFAULT 0
);
```

#### 签到表 (check_ins)
```sql
CREATE TABLE check_ins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    check_in_date DATE,
    streak INT DEFAULT 1
);
```

#### 徽章表 (badges)
```sql
CREATE TABLE badges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    description VARCHAR(255),
    icon_url VARCHAR(500),
    points_threshold INT
);
```

#### 用户徽章表 (user_badges)
```sql
CREATE TABLE user_badges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    badge_id BIGINT,
    earned_at DATETIME
);
```

#### 学习记录表 (study_records)
```sql
CREATE TABLE study_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    start_time DATETIME,
    end_time DATETIME,
    study_date DATE
);
```

#### AI报告表 (ai_reports)
```sql
CREATE TABLE ai_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    report_date DATE,
    learning_analysis JSON
);
```

#### 学习报告表 (learning_reports)
```sql
CREATE TABLE learning_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    report_date DATE,
    learning_analysis JSON
);
```

---

## 工具类说明

### JwtUtil.java
- JWT 令牌生成和验证
- 令牌有效期：24小时
- 用户信息提取

### Md5Util.java
- MD5 加密工具
- 密码加密存储

### SMSUtil.java
- 短信发送工具
- 集成容联云服务

### JsonSanitizer.java
- JSON 清理工具
- 处理 AI 返回的 JSON 格式

---

## 配置文件说明

### application.yml
```yaml
# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai-learn
    username: root
    password: xxx
    driver-class-name: com.mysql.cj.jdbc.Driver

# AI配置
spring.ai.zhipuai:
  api-key: xxx
  chat.options.model: glm-4.7

# Redis配置
spring.data.redis:
  host: localhost
  port: 6379

# 服务端口
server.port: 8080
```

---

## 项目架构特点

### 1. AI 集成
- **框架**: Spring AI
- **模型**: 智谱 AI GLM-4.7
- **应用场景**:
  - 生成学习目标和任务计划
  - 生成关卡测试题目
  - 分析答题质量
  - 提供学习建议
  - 生成学习路线
  - 流式对话响应

### 2. 缓存策略
- **缓存中间件**: Redis
- **缓存内容**:
  - 关卡题目（每日过期）
  - 用户关卡进度
  - AI 报告
- **序列化**: 支持 Java8 时间类型

### 3. 认证与安全
- JWT 令牌认证
- 短信验证码登录（容联云）
- 令牌有效期：24小时

### 4. 定时任务
- 每日凌晨1点重置过期学习时间数据
- 自动清理缓存

### 5. 跨域支持
- 所有 Controller 使用 `@CrossOrigin` 注解
- 支持前后端分离部署

---

## 项目统计

| 类型 | 数量 |
|------|------|
| Controller | 11 |
| Service 接口 | 9 |
| Service 实现 | 9 |
| Mapper | 8 |
| Entity | 19 |
| 工具类 | 4 |
| Mapper XML | 8 |
| SQL 脚本 | 3 |
| **Java 文件总数** | **85** |

---

## 项目亮点

1. **智能化学习**
   - 深度集成 AI，实现学习计划自动生成
   - 智能出题、学习分析

2. **游戏化设计**
   - 闯关模式、星级评定
   - 徽章成就系统

3. **完整的学习闭环**
   - 目标制定 → 任务分解 → 知识卡片 → 挑战答题 → 学习报告

4. **积分激励体系**
   - 学习时长积分
   - 徽章奖励
   - 排行榜竞争

5. **灵活的学习路径**
   - 支持自定义学习目标
   - AI 动态生成学习路线

6. **性能优化**
   - Redis 缓存
   - 定时任务清理过期数据

7. **数据持久化**
   - 完整的历史记录追踪
   - 支持学习轨迹回溯

---

## API 接口汇总

### 用户模块
- `POST /user/sendSms` - 发送验证码
- `POST /user/checkSms` - 验证登录
- `GET /user/getUserInfo` - 获取用户信息
- `GET /user/getUser` - 获取指定用户

### 目标任务模块
- `POST /api/goals/createGoalsByAi` - AI生成目标
- `POST /api/goals/cleanDataByAi` - AI数据清洗
- `GET /api/goals/talkToAi` - AI对话
- `POST /api/goals/learnTask` - 学习打卡
- `GET /api/goals/getGoalsByUserId` - 获取用户目标
- `GET /api/goals/getTasksByGoalId` - 获取任务列表
- `DELETE /api/goals/deleteGoal` - 删除目标
- `POST /api/goals/updateTaskEstimatedHour` - 更新任务时间
- `POST /api/goals/updateGoalEndDate` - 更新目标结束日期
- `GET /api/goals/getFinishedGoals` - 获取已完成目标

### 闯关答题模块
- `GET /api/levels/getUserLevels` - 获取用户关卡
- `POST /api/levels/startLevel` - 开始关卡
- `POST /api/levels/submitAnswers` - 提交答案
- `GET /api/levels/getLevelHistory` - 关卡历史
- `GET /api/levels/getAllHistory` - 所有历史
- `GET /api/levels/getAllLevels` - 所有关卡
- `GET /api/levels/getDailyStats` - 每日统计
- `DELETE /api/levels/clearCache` - 清除缓存

### 知识卡片模块
- `GET /knowledgeCards/getByTaskId` - 获取卡片
- `POST /knowledgeCards/batchCreate` - 批量创建
- `DELETE /knowledgeCards/delete` - 删除卡片

### 签到模块
- `POST /checkIn/userCheckIn` - 用户签到
- `GET /checkIn/getOneYearCheckIn` - 年度签到记录
- `GET /checkIn/isCheckIn` - 今日签到状态
- `GET /checkIn/getCurrentStreak` - 连续签到天数

### 学习时间模块
- `GET /studyTime/getStudyTimeByUserId` - 获取学习时长
- `GET /studyTime/getDailyHoursTop10` - 日榜Top10
- `GET /studyTime/getTotalHoursTop10` - 总榜Top10
- `GET /studyTime/getPointTop10` - 积分榜Top10
- `GET /studyTime/getStudyTimeRecord` - 学习记录

### 徽章模块
- `GET /badge/getAllBadges` - 所有徽章
- `GET /badge/getUserBadges` - 用户徽章
- `GET /badge/getUnearnedBadges` - 未获得徽章
- `GET /badge/countUserBadges` - 徽章统计

### AI报告模块
- `GET /api/aiReport/latest` - 最新AI报告
- `DELETE /api/aiReport/clearCache` - 清除缓存

### Web AI模块
- `GET /web/getLearnPath` - 获取学习路径

---

## 学习流程图

```
用户注册/登录
    ↓
创建学习目标 (AI辅助生成)
    ↓
生成学习任务
    ↓
关联知识卡片
    ↓
学习打卡 (记录时长)
    ↓
闯关答题 (AI生成题目)
    ↓
获取评分和星级
    ↓
AI 学习报告
    ↓
获得徽章和积分
    ↓
排行榜竞争
```

---

## 总结

**AI-Learn** 是一个功能完善的 AI 驱动学习管理平台，结合了现代 Web 开发最佳实践和人工智能技术。

**核心价值**：
1. 通过 AI 实现个性化学习路径规划
2. 游戏化学习体验提高用户粘性
3. 完整的学习数据追踪和分析
4. 灵活的任务管理和进度控制

**技术特色**：
- Spring Boot 3.5.3 最新框架
- Spring AI 智能集成
- Redis 缓存优化
- JWT 安全认证
- MyBatis 数据持久化

该平台为用户提供从目标设定、任务分解、知识学习、闯关练习到学习报告的完整学习闭环体验。
