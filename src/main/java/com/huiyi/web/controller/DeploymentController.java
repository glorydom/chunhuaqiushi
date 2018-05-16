package com.huiyi.web.controller;

import com.huiyi.web.dto.BaseResult;
import org.activiti.engine.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DeploymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentController.class);

    @Autowired
    private RepositoryService repositoryService;


    @RequestMapping(value = "/deploy", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult deployProcess(){

        return new BaseResult(1, "好的", null);
    }


}
