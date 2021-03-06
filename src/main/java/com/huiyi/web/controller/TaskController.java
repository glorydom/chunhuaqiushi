package com.huiyi.web.controller;


import com.github.pagehelper.util.StringUtil;
import com.huiyi.web.dto.BaseResult;
import com.huiyi.web.dto.Constants;
import com.huiyi.web.dto.entity.TaskCompleteDto;
import com.huiyi.web.dto.entity.TaskDto;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.identity.Authentication;
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
    private HistoryService historyService;

    @Autowired
    private IdentityService identityService;

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
        List<TaskDto> tds = new ArrayList<>();
        for (Task task : tasks) {
            tds.add(convertToTaskDto(task));
        }

        return new BaseResult(Constants.SUCCESS, "success", tds);
    }

    /**
     * 根据用户id，将该用户所在的组的所有任务读取出来
     * @param userId
     * @return
     */
    @RequestMapping(value = "list/group/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listMyGroupTasks(@PathVariable String userId) {
        System.out.println("the userId is:  " + userId);
        String sql = "select g.* FROM\n" +
                "ACT_ID_GROUP g join ACT_ID_MEMBERSHIP m\n" +
                "on g.ID_ = m.GROUP_ID_\n" +
                "join ACT_ID_USER u\n" +
                "on m.USER_ID_ = u.ID_\n" +
                "where u.ID_= '" + userId + "'";
        List<Group> groups  = identityService.createNativeGroupQuery().sql(sql).list();
        List<Task> tasks = new ArrayList<>();
        for(Group g:groups){
            List<Task> groupedTasks = taskService.createTaskQuery().taskCandidateGroup(g.getId()).list();
            tasks.addAll(groupedTasks);
        }

        List<TaskDto> tds = new ArrayList<>();
        for (Task task : tasks) {
            tds.add(convertToTaskDto(task));
        }

        return new BaseResult(Constants.SUCCESS, "success", tds);
    }

    /**
     * 获取该用户拥有的任务
     * @param userId
     * @return
     */
    @RequestMapping(value = "list/owner/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listMyOwnerTasks(@PathVariable String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskOwner(userId).list();
        List<TaskDto> tds = new ArrayList<>();
        for (Task task : tasks) {
            tds.add(convertToTaskDto(task));
        }

        return new BaseResult(Constants.SUCCESS, "success", tds);
    }


    /**
     * 根据用户id获取分配给该用户的任务
     * @param userId
     * @return
     */
    @RequestMapping(value = "list/user/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listMyTasks(@PathVariable String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).list();
        List<TaskDto> tds = new ArrayList<>();
        for (Task task : tasks) {
            tds.add(convertToTaskDto(task));
        }
        return new BaseResult(Constants.SUCCESS, "success", tds);
    }

    @RequestMapping(value = "listByCandidateGroup/{candidateGroup}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listCandidateGroupTasks(@PathVariable String candidateGroup) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(candidateGroup).list();
        List<TaskDto> tds = new ArrayList<>();
        for (Task task : tasks) {
            tds.add(convertToTaskDto(task));
        }

        return new BaseResult(Constants.SUCCESS, "success", tds);
    }

    @RequestMapping(value = "listBySingleUser/{singleUser}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listSingleUserTask(@PathVariable String singleUser) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(singleUser).list();
        List<TaskDto> tds = new ArrayList<>();
        for (Task task : tasks) {
            tds.add(convertToTaskDto(task));
        }

        return new BaseResult(Constants.SUCCESS, "success", tds);
    }

    @RequestMapping(value = "complete", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult completeTask(@RequestBody  TaskCompleteDto dto){
        if(null == dto){
            return new BaseResult(Constants.ERROR, "no argument to complete this task", null);
        }
        Authentication.setAuthenticatedUserId(dto.getUserId());//批注人的名称  一定要写，不然查看的时候不知道人物信息
        // 添加批注信息
        taskService.addComment(dto.getTaskId(), null, dto.getComment());//comment为批注内容
        // 完成任务
        taskService.complete(dto.getTaskId(),dto.getCompletionArguments());//vars是一些变量

        return new BaseResult(Constants.SUCCESS, "success", null);
    }

    @RequestMapping(value = "claim/{userid}/{taskid}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult claimTask(@PathVariable String userid, @PathVariable String taskid){

        taskService.claim(taskid, userid);

        return new BaseResult(Constants.SUCCESS, "success", null);
    }

    /**
     * 拥有任务，转发给其他人
     * @param userid
     * @param taskid
     * @return
     */
    @RequestMapping(value = "own/{userid}/{taskid}/{assigneeId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult ownTask(@PathVariable String userid, @PathVariable String taskid, String asignee){
        taskService.setOwner(taskid, userid);
        if(asignee != null)
            taskService.setAssignee(taskid, asignee);
        return new BaseResult(Constants.SUCCESS, "success", null);
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

        return new BaseResult(Constants.SUCCESS, "success", result);
    }

    private String getBusinessObjId(String taskId) {
        //1  获取任务对象
        Task task  =  taskService.createTaskQuery().taskId(taskId).singleResult();

        //2  通过任务对象获取流程实例
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        //3 通过流程实例获取“业务键”
        String businessKey = pi.getBusinessKey();
        //4 拆分业务键，拆分成“业务对象名称”和“业务对象ID”的数组
        // a=b  LeaveBill.1
//        String objId = null;
//        if(StringUtils.isNotBlank(businessKey)){
//            objId = businessKey.split("\\.")[1];
//        }
        return businessKey;
    }


    private TaskDto convertToTaskDto(Task task){
        if(task == null)
            return null;

        String processInstanceId = task.getProcessInstanceId();
//使用流程实例ID，查询历史任务，获取历史任务对应的每个任务ID
        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()//历史任务表查询
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .list();

        TaskDto taskDto = new TaskDto();
        taskDto.setAssigne(task.getAssignee());
        taskDto.setDescription(task.getDescription());
        taskDto.setDueDate(task.getDueDate());
        taskDto.setName(task.getName());
        taskDto.setFormKey(task.getFormKey());
        taskDto.setOwner(task.getOwner());
        taskDto.setTaskId(task.getId());
        taskDto.setBussinessKey(getBusinessObjId(task.getId()));

        return taskDto;
    }


}
