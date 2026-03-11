package com.tagtax.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WebLearningPath {
    private String id;
    private String title;
    private String type;
    private String level;
    private String content;
    private String duration;
    private String courseUrl;
    private List<String> steps;
}
