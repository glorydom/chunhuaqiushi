package com.huiyi.web.controller;

import com.alibaba.fastjson.JSON;
import com.huiyi.web.dto.BaseResult;
import com.huiyi.web.dto.Constants;
import com.huiyi.web.dto.entity.ProcessInstanceDto;
import com.huiyi.web.dto.entity.ProcessStartParameter;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RequestMapping("/process")
@Controller
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;


    @RequestMapping(value = "start", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult StartProcess(@RequestBody String parameters){
        ProcessStartParameter parameter = JSON.parseObject(parameters, ProcessStartParameter.class);
        if(null != parameters){
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(parameter.getProcessId(),
                    parameter.getBussinessId(), parameter.getParameters());
            return new BaseResult(Constants.SUCCESS, "success", null);
        }else {
            return new BaseResult(Constants.ERROR, "no parameters", parameters);
        }
    }

    @RequestMapping(value = "listActiveProcess", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listActiveProcess(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                        .active()
                        .list();
        List<ProcessInstanceDto> result = new ArrayList<ProcessInstanceDto>();
        if(list != null ){
            for(ProcessInstance ps : list){
                result.add(new ProcessInstanceDto(ps.getProcessInstanceId(), ps.getBusinessKey()));
            }
        }
        return new BaseResult(Constants.SUCCESS, "success", result);
    }

    @RequestMapping(value = "listSuspendProcess", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listSuspendProcess(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .suspended()
                .list();
        List<ProcessInstanceDto> result = new ArrayList<ProcessInstanceDto>();
        if(list != null ){
            for(ProcessInstance ps : list){
                result.add(new ProcessInstanceDto(ps.getProcessInstanceId(), ps.getBusinessKey()));
            }
        }
        return new BaseResult(Constants.SUCCESS, "success", result);
    }


    @RequestMapping(value = "stop/{processId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult StopProcess(@PathVariable String processId){
        runtimeService.deleteProcessInstance(processId, "取消");
        return new BaseResult(Constants.SUCCESS, "success", null);
    }

    @RequestMapping(value = "stopByBussinesskey/{bussiness_key}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult StopByBussinessKeyProcess(@PathVariable String bussiness_key){
        ProcessInstance ps = runtimeService.createProcessInstanceQuery()
                        .processInstanceBusinessKey(bussiness_key)
                        .singleResult();
        String processId = ps.getProcessInstanceId();
        runtimeService.deleteProcessInstance(processId, "取消");
        return new BaseResult(Constants.SUCCESS, "success", null);
    }
}
