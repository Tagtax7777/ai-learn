package com.tagtax.service;


import com.tagtax.entity.WebApiResponse;

public interface WebAiService {
    WebApiResponse getLearnPath(String text);
}
