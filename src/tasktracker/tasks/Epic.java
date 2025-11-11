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

    public void updateEpicTiming(InMemoryTaskManager taskManager) {
        if (subtaskId.isEmpty()) {
            super.setStartTime(null);
            super.setDuration(Duration.ZERO);
            this.endTime = null;
            return;
        }

        List<Subtask> validSubtasks = subtaskId.stream()
                .map(taskManager::getSubtask)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Optional<LocalDateTime> earliestStart = validSubtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> latestEnd = validSubtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        Duration totalDuration = validSubtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        setStartTime(earliestStart.orElse(null));
        setDuration(totalDuration);
        endTime = latestEnd.orElse(null);
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
    public LocalDateTime getEndTime() { return endTime; }
}
