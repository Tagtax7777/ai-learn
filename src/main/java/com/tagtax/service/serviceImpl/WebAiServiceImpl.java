package com.tagtax.service.serviceImpl;

import com.tagtax.entity.*;
import com.tagtax.service.WebAiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class WebAiServiceImpl implements WebAiService {

    @Autowired
    private ChatModel zhiPuAiChatModel;


    @Override
    public WebApiResponse getLearnPath(String text) {
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);

        // 创建WebApiResponse对象
        WebApiResponse response = new WebApiResponse();

        if(text.equals("我想要学好Java，你给我一些学习路线")){
            // 创建学习路线列表
            WebLearningPath path1 = new WebLearningPath();
            path1.setId("1");
            path1.setTitle("Java基础入门");
            path1.setType("基础路线");
            path1.setLevel("初级");
            path1.setContent("学习Java语法、面向对象编程");
            path1.setDuration("3个月");
            path1.setCourseUrl("https://www.bilibili.com/video/av48144058");
            path1.setSteps(Arrays.asList("环境搭建", "基础语法", "面向对象", "集合框架"));

            WebLearningPath path2 = new WebLearningPath();
            path2.setId("2");
            path2.setTitle("Java进阶提升");
            path2.setType("进阶路线");
            path2.setLevel("中级");
            path2.setContent("深入学习JVM、多线程、网络编程");
            path2.setDuration("6个月");
            path2.setCourseUrl("https://www.bilibili.com/video/av83622425");
            path2.setSteps(Arrays.asList("JVM原理", "并发编程", "网络编程", "框架学习"));

            WebLearningPath path3 = new WebLearningPath();
            path3.setId("3");
            path3.setTitle("Java架构师之路");
            path3.setType("高级路线");
            path3.setLevel("高级");
            path3.setContent("微服务架构、分布式系统设计");
            path3.setDuration("12个月");
            path3.setCourseUrl("https://www.bilibili.com/video/BV19RWdzxEF7");
            path3.setSteps(Arrays.asList("微服务", "分布式", "性能优化", "系统设计"));

            WebLearningPath path4 = new WebLearningPath();
            path4.setId("4");
            path4.setTitle("Spring全家桶");
            path4.setType("进阶路线");
            path4.setLevel("中级");
            path4.setContent("掌握Spring Boot、Spring Cloud等框架");
            path4.setDuration("4个月");
            path4.setCourseUrl("https://www.bilibili.com/video/BV1UJc2ezEFU");
            path4.setSteps(Arrays.asList("Spring Core", "Spring Boot", "Spring Cloud", "实战项目"));

            WebLearningPath path5 = new WebLearningPath();
            path5.setId("5");
            path5.setTitle("数据库与缓存");
            path5.setType("基础路线");
            path5.setLevel("初级");
            path5.setContent("MySQL、Redis等数据存储技术");
            path5.setDuration("2个月");
            path5.setCourseUrl("https://www.bilibili.com/video/BV1Kr4y1i7ru");
            path5.setSteps(Arrays.asList("SQL基础", "MySQL优化", "Redis应用", "实战练习"));

            // 设置响应对象属性
            response.setSuccess(true);
            response.setData(Arrays.asList(path1, path2, path3, path4, path5));
            response.setTotal(5);
            response.setTimestamp(LocalDate.now());
            return response;
        }else if(text.equals("我想要学好web前端，你给我一些学习路线")){
            // 创建WebApiResponse对象
            WebApiResponse response1 = new WebApiResponse();

            // 创建Web前端学习路线列表
            WebLearningPath path1 = new WebLearningPath();
            path1.setId("1");
            path1.setTitle("Web前端基础入门");
            path1.setType("基础路线");
            path1.setLevel("初级");
            path1.setContent("学习HTML5、CSS3、JavaScript基础语法");
            path1.setDuration("2个月");
            path1.setCourseUrl("https://www.bilibili.com/video/BV1Kg411T7t9");
            path1.setSteps(Arrays.asList("HTML5标签与语义化", "CSS3选择器与盒模型", "Flex布局与Grid布局", "JavaScript基础语法", "DOM操作与事件处理"));

            WebLearningPath path2 = new WebLearningPath();
            path2.setId("2");
            path2.setTitle("JavaScript进阶与工程化");
            path2.setType("进阶路线");
            path2.setLevel("中级");
            path2.setContent("深入学习ES6+、异步编程、前端工程化");
            path2.setDuration("3个月");
            path2.setCourseUrl("https://www.bilibili.com/video/BV1Y84y1L7Nn");
            path2.setSteps(Arrays.asList("ES6+新特性", "异步编程Promise/async/await", "模块化开发", "Git版本控制", "Webpack构建工具"));

            WebLearningPath path3 = new WebLearningPath();
            path3.setId("3");
            path3.setTitle("Vue.js全家桶");
            path3.setType("框架路线");
            path3.setLevel("中级");
            path3.setContent("掌握Vue3、Vue Router、Vuex、Pinia等");
            path3.setDuration("3个月");
            path3.setCourseUrl("https://www.bilibili.com/video/BV1QA4y1d7xf");
            path3.setSteps(Arrays.asList("Vue3组合式API", "Vue Router路由管理", "状态管理Pinia", "Vue组件化开发", "Vite工程化配置"));

            WebLearningPath path4 = new WebLearningPath();
            path4.setId("4");
            path4.setTitle("React生态体系");
            path4.setType("框架路线");
            path4.setLevel("中级");
            path4.setContent("学习React、Hooks、Redux、React Router");
            path4.setDuration("4个月");
            path4.setCourseUrl("https://www.bilibili.com/video/BV1Z44y1K7Fj");
            path4.setSteps(Arrays.asList("React基础与JSX", "Hooks状态管理", "Redux状态管理", "React Router", "组件性能优化"));

            WebLearningPath path5 = new WebLearningPath();
            path5.setId("5");
            path5.setTitle("前端工程化与性能优化");
            path5.setType("高级路线");
            path5.setLevel("高级");
            path5.setContent("前端工程化、性能优化、TypeScript");
            path5.setDuration("3个月");
            path5.setCourseUrl("https://www.bilibili.com/video/BV14Z4y1u7pi");
            path5.setSteps(Arrays.asList("TypeScript类型系统", "Webpack高级配置", "性能监控与优化", "自动化部署", "微前端架构"));

            // 设置响应对象属性
            response1.setSuccess(true);
            response1.setData(Arrays.asList(path1, path2, path3, path4, path5));
            response1.setTotal(5);
            response1.setTimestamp(LocalDate.now());
            return response1;
        }else {
            String aiPrompt = "{\n" +
                    "  \"success\": true,\n" +
                    "  \"data\": [\n" +
                    "    {\n" +
                    "      \"id\": \"1\",\n" +
                    "      \"title\": \"Java基础入门\",\n" +
                    "      \"type\": \"基础路线\",\n" +
                    "      \"level\": \"初级\",\n" +
                    "      \"content\": \"学习Java语法、面向对象编程\",\n" +
                    "      \"duration\": \"3个月\",\n" +
                    "      \"courseUrl\": \"https://www.bilibili.com/video/av48144058\",\n" +
                    "      \"steps\": [\n" +
                    "        \"环境搭建\",\n" +
                    "        \"基础语法\",\n" +
                    "        \"面向对象\",\n" +
                    "        \"集合框架\"\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\": \"2\",\n" +
                    "      \"title\": \"Java进阶提升\",\n" +
                    "      \"type\": \"进阶路线\",\n" +
                    "      \"level\": \"中级\",\n" +
                    "      \"content\": \"深入学习JVM、多线程、网络编程\",\n" +
                    "      \"duration\": \"6个月\",\n" +
                    "      \"courseUrl\": \"https://www.bilibili.com/video/av83622425\",\n" +
                    "      \"steps\": [\n" +
                    "        \"JVM原理\",\n" +
                    "        \"并发编程\",\n" +
                    "        \"网络编程\",\n" +
                    "        \"框架学习\"\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\": \"3\",\n" +
                    "      \"title\": \"Java架构师之路\",\n" +
                    "      \"type\": \"高级路线\",\n" +
                    "      \"level\": \"高级\",\n" +
                    "      \"content\": \"微服务架构、分布式系统设计\",\n" +
                    "      \"duration\": \"12个月\",\n" +
                    "      \"courseUrl\": \"https://www.bilibili.com/video/BV18E411x7eT\",\n" +
                    "      \"steps\": [\n" +
                    "        \"微服务\",\n" +
                    "        \"分布式\",\n" +
                    "        \"性能优化\",\n" +
                    "        \"系统设计\"\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" + "请根据上文的json示例来对下文用户提出的需求进行分析，" +
                    "里面的id应该按照1，2，3的形式递增。" +
                    " 而且List<WebLearningPath>里面的数据应该在3到4条之间" + text;
            return chatClient.prompt(aiPrompt)
                    .call()
                    .entity(new ParameterizedTypeReference<>() {});
        }
    }
}
