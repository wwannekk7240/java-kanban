package tasktracker;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Progress progress;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.progress = Progress.NEW;
    }

    public Progress getProgress() {
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

    public void setProgress(Progress progress) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
