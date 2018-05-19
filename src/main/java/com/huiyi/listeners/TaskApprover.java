package com.huiyi.listeners;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

import java.io.Serializable;

public class TaskApprover implements Serializable, TaskListener {
    private Expression arg;

    public Expression getArg() {
        return arg;
    }

    public void setArg(Expression arg) {
        this.arg = arg;
    }

    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("任务监听器:" );

        delegateTask.setAssignee("wangwu");
    }
}
