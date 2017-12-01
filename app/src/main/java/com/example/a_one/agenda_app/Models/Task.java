package com.example.a_one.agenda_app.models;

import java.util.HashMap;

/**
 * Created by a-one on 2017-11-30.
 */



public class Task {

    private String task_id;
    private String message;
    private String date;
    private String priority;

    public Task() {

    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }


    public HashMap<String,String> toFirebaseObject() {
        HashMap<String,String> task =  new HashMap<String,String>();
        task.put("task_id", task_id);
        task.put("message", message);
        task.put("date", date);
        task.put("priority",priority);

        return task;
    }

}
