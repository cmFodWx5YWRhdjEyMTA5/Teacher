package com.easyschools.teacher;

import java.util.List;

public class MaterialData
{
    private String   title , id , description , date , questions , week_number;

    private List<String> filesUrl, files_extensions  , type;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getWeek_number() {
        return week_number;
    }

    public void setWeek_number(String week_number) {
        this.week_number = week_number;
    }

    public List<String> getFilesUrl() {
        return filesUrl;
    }

    public void setFilesUrl(List<String> filesUrl) {
        this.filesUrl = filesUrl;
    }

    public List<String> getFiles_extensions() {
        return files_extensions;
    }

    public void setFiles_extensions(List<String> files_extensions) {
        this.files_extensions = files_extensions;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }
}
