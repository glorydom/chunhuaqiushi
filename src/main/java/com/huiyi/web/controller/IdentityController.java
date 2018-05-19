package com.huiyi.web.controller;


import com.huiyi.web.dto.BaseResult;
import org.activiti.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/identity")
public class IdentityController {

    @Autowired
    private IdentityService identityService;

//    @RequestMapping(value = "addUser", method = RequestMethod.POST)
//    @ResponseBody
//    public BaseResult addUser(){
//
//
//    }
}
