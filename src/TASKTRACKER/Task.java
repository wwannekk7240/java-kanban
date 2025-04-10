package TASKTRACKER;

public class Task {
    private int id;
    private String name;
    private String description;
    TaskProgress.Progress progress;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.progress = TaskProgress.Progress.NEW;
    }

    public TaskProgress.Progress getProgress() {
        return progress;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProgress(TaskProgress.Progress progress) {
        this.progress = progress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task [ID = " + id + ", name = " + name + ", description = " + description + ", progress = " + progress + "]" ;
    }
}
