package tasktracker;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int id) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask [ID = " + getId() + ", name = " + getName() + ", description = " + getDescription() +
                ", progress = " + getProgress() + ", Epic ID = " + epicId + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;  // + сравнение epicId
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
