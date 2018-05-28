package com.huiyi.web.controller;

import com.huiyi.web.dto.BaseResult;
import com.huiyi.web.dto.Constants;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/history")
@Controller
public class HistoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryController.class);

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    public void findHisProcessInstance(){
        List<HistoricProcessInstance> list = historyService
                .createHistoricProcessInstanceQuery()
                .processDefinitionId("testVariables:2:1704")//流程定义ID
                .list();

        if(list != null && list.size()>0){
            for(HistoricProcessInstance hi:list){
                System.out.println(hi.getId()+"   "+hi.getStartTime()+"   "+hi.getEndTime());
            }
        }
    }


    @RequestMapping(value = "activiti/{processinstanceId}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult searchHisActivity() {
        String processInstanceId = "1801";
        List<HistoricActivityInstance> list = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if(list != null && list.size()>0){
            for(HistoricActivityInstance hai : list){
                System.out.println(hai.getId()+"  "+hai.getActivityName());
            }
        }
        return new BaseResult(Constants.SUCCESS, "", null);
    }

    @RequestMapping(value = "comment/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult searchComment(@PathVariable String taskId) {
        List<Comment> list = new ArrayList<>();
        Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

        String processInstanceId = task.getProcessInstanceId();

        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                                                                        .processInstanceId(processInstanceId)
                                                                        .list();

        if(historicTaskInstances !=null && historicTaskInstances.size()>0){
            for(HistoricTaskInstance historicTaskInstance : historicTaskInstances){
                String taskid = historicTaskInstance.getId();
                List<Comment> taskList = taskService.getTaskComments(taskid);
                list.addAll(taskList);
            }
        }

        return new BaseResult(Constants.SUCCESS, "", list);
    }


}
