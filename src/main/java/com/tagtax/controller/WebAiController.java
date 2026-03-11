package com.tagtax.controller;

import com.tagtax.entity.WebApiResponse;
import com.tagtax.service.WebAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/web")
public class WebAiController {

    @Autowired
    private WebAiService webAiService;


    @GetMapping("/getLearnPath")
    public WebApiResponse getLearnPath(@RequestParam("text") String text){
        return webAiService.getLearnPath(text);
    }


}
