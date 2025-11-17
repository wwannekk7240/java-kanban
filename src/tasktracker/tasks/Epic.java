package tasktracker.tasks;

import tasktracker.managers.InMemoryTaskManager;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Epic extends Task {
    private ArrayList<Integer> subtaskId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtaskId(int subtaskId) {
        this.subtaskId.add(subtaskId);
    }

    @Override
    public String toString() {
        return "Epic [ID = " + getId() + ", name = " + getName() + ", description = " + getDescription() +
                ", progress = " + getProgress() + ", subtask = " + subtaskId + ", duration" + getDuration() + ", startTime"
                + getStartTime() + ", endTime = " + endTime + "]";
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public TaskType getType() {
        return  TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
