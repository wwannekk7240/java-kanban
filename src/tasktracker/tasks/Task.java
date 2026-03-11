package tasktracker.tasks;

import tasktracker.user.User;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private String name;
    private String description;
    private Progress progress;
    private Duration duration;
    private LocalDateTime startTime;
    protected User user;

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.progress = Progress.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime, User user) {
        this.name = name;
        this.description = description;
        this.progress = Progress.NEW;
        this.duration = duration;
        this.startTime = startTime;
        this.user = user;
    }

    public Task(int id, String name, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.progress = Progress.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, Duration duration, LocalDateTime startTime, User user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.progress = Progress.NEW;
        this.duration = duration;
        this.startTime = startTime;
        this.user = user;
    }

    public Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.progress = task.progress;
        this.description = task.description;
        this.duration = task.duration;
        this.startTime = task.startTime;
        this.user = task.user;
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

    public TaskType getType() {
        return  TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return  null;
        }
        return startTime.plus(duration);
    }

    public User getUser() { return user; }

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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Task [ID = " + id + ", name = " + name + ", description = " + description
                + ", progress = " + progress + ", duration" + duration + ", startTime"
                + startTime + "]";
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
