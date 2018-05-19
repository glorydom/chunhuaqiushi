package com.huiyi.web.controller;


import com.github.pagehelper.util.StringUtil;
import com.huiyi.web.dto.BaseResult;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/task")
@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @RequestMapping(value = "listByCandidateUsers/{candidateUser}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listTasks(@PathVariable String candidateUser) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(candidateUser).list();
        for (Task task : tasks) {
            System.out.println(task.getName());
        }

        return new BaseResult(1, "success", tasks);
    }

    @RequestMapping(value = "listByCandidateGroup/{candidateGroup}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listCandidateGroupTasks(@PathVariable String candidateGroup) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(candidateGroup).list();
        for (Task task : tasks) {
            System.out.println(task.getName());
        }

        return new BaseResult(1, "success", tasks);
    }

    @RequestMapping(value = "listBySingleUser/{singleUser}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listSingleUserTask(@PathVariable String singleUser) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(singleUser).list();
        for (Task task : tasks) {
            System.out.println(task.getName());
        }

        return new BaseResult(1, "success", tasks);
    }

    @RequestMapping(value = "completeTask/{taskid}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult completeTask(@PathVariable String taskid){

        taskService.complete(taskid);

        return new BaseResult(1, "success", null);
    }


    @RequestMapping(value = "listCurrentTaskAllOutgoing/{taskid}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listAllOutgoing(@PathVariable String taskid){

        Task task = taskService.createTaskQuery().taskId(taskid)
                .singleResult();

        String processDefinitionId = task.getProcessDefinitionId();

        ProcessDefinitionEntity def = (ProcessDefinitionEntity) (repositoryService.getProcessDefinition(processDefinitionId));

        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();

        String activityId = instance.getActivityId();

        ActivityImpl activity = def.findActivity(activityId);

        List<PvmTransition> pvmlist = activity.getOutgoingTransitions();

        List<String> result = new ArrayList<String>();
        if(pvmlist !=null && pvmlist.size()>0){
            for(PvmTransition pvm:pvmlist){
                String name = (String) pvm.getProperty("name");
                if(StringUtil.isNotEmpty(name)){
                    result.add(name);
                }else {
                    result.add("默认提交");
                }
            }
        }

        return new BaseResult(1, "success", result);
    }


}
