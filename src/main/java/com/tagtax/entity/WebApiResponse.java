package com.tagtax.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WebApiResponse {
    private Boolean success;
    private List<WebLearningPath> data;
    private Integer total;
    private LocalDate timestamp;
}