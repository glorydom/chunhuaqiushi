package com.huiyi.web.controller;


import com.huiyi.web.dto.BaseResult;
import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/runtime")
@Controller
public class RuntimeController {

    @Autowired
    private RuntimeService runtimeService;

    @RequestMapping(value = "stopProcess/{processId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult StopProcess(@PathVariable String processId){
        runtimeService.suspendProcessInstanceById(processId);

        return new BaseResult(1, "success", null);
    }

}
