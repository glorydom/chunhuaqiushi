package com.huiyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huiyi.web.dto.BaseResult;
import com.huiyi.web.dto.Constants;
import com.huiyi.web.dto.entity.DeployedProcess;
import jdk.nashorn.internal.ir.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deploy")
public class DeploymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentController.class);

    @Autowired
    private RepositoryService repositoryService;


//    @RequestMapping("/deploy")
//    @ResponseBody
//    public BaseResult deploy(final String modelId) {
//
//            JSONObject result = new JSONObject();
//
//            Model modelData = repositoryService.getModel(modelId);
//            ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
//            byte[] bpmnBytes = null;
//            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
//            bpmnBytes = new BpmnXMLConverter().convertToXML(model);
//            String processName = modelData.getName() + ".bpmn20.xml";
//            DeploymentBuilder db = repositoryService.createDeployment().name(modelData.getName());
//            Deployment deployment = db.addString(processName, new String(bpmnBytes,"utf-8")).deploy();
//
//
//    }


    @RequestMapping(value = "listDeployedProcess", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult listAllDeployedProcess(){
        List<ProcessDefinition> list = repositoryService//与流程定义和部署对象相关的Service
                .createProcessDefinitionQuery()//创建一个流程定义查询
                        /*指定查询条件,where条件*/
                //.deploymentId(deploymentId)//使用部署对象ID查询
                //.processDefinitionId(processDefinitionId)//使用流程定义ID查询
                //.processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
                //.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

                        /*排序*/
                .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
                //.orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

                .list();//返回一个集合列表，封装流程定义
        //.singleResult();//返回唯一结果集
        //.count();//返回结果集数量
        //.listPage(firstResult, maxResults)//分页查询
        List<DeployedProcess> result = new ArrayList<DeployedProcess>();
        if(list != null && list.size()>0){
            for(ProcessDefinition processDefinition:list){
                System.out.println("流程定义ID:"+processDefinition.getId());//流程定义的key+版本+随机生成数
                System.out.println("流程定义名称:"+processDefinition.getName());//对应HelloWorld.bpmn文件中的name属性值
                System.out.println("流程定义的key:"+processDefinition.getKey());//对应HelloWorld.bpmn文件中的id属性值
                System.out.println("流程定义的版本:"+processDefinition.getVersion());//当流程定义的key值相同的情况下，版本升级，默认从1开始
                System.out.println("资源名称bpmn文件:"+processDefinition.getResourceName());
                System.out.println("资源名称png文件:"+processDefinition.getDiagramResourceName());
                System.out.println("部署对象ID:"+processDefinition.getDeploymentId());
                System.out.println("################################");
                DeployedProcess dp = new DeployedProcess();
                dp.setProcessKey(processDefinition.getKey());
                dp.setId(processDefinition.getId());
                dp.setVersion(processDefinition.getVersion());
                result.add(dp);
            }
        }


        return new BaseResult(Constants.SUCCESS, "success", result);
    }

}
