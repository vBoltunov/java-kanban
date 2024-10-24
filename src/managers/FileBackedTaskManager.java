package managers;

import exceptions.FileLoadException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.Status;
import model.enums.TaskType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    static final String HEADER = "id,type,name,status,description,epic,startTime,duration,endTime\n";

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(HEADER);
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.write(entry.getValue().toString() + "\n");
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.write(entry.getValue().toString() + "\n");
            }
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.write(entry.getValue().toString() + "\n");
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Произошла ошибка записи в файл", exp);
        }
    }

    protected static Task parseFromString(String value) {
        Task parsedTask;
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = LocalDateTime.parse(parts[6]);
        Duration duration = Duration.ofMinutes(Integer.parseInt(parts[7]));
        switch (taskType) {
            case TASK:
                parsedTask = new Task(id, name, description);
                parsedTask.setStatus(status);
                parsedTask.setStartTime(startTime);
                parsedTask.setDuration(duration);
                prioritizedTasks.add(parsedTask);
                break;

            case EPIC:
                parsedTask = new Epic(id, name, description);
                parsedTask.setStatus(status);
                parsedTask.setStartTime(startTime);
                parsedTask.setDuration(duration);
                break;

            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                parsedTask = new Subtask(id, name, description, epicId);
                parsedTask.setStatus(status);
                parsedTask.setStartTime(startTime);
                parsedTask.setDuration(duration);
                prioritizedTasks.add(parsedTask);
                break;
            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + taskType);
        }
        return parsedTask;
    }

    protected static FileBackedTaskManager loadFromFile(File file) throws FileLoadException {
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isEmpty() || line.contains("id")) {
                    continue;
                }
                Task loadedTask = parseFromString(line);
                int id = loadedTask.getId();
                switch (loadedTask.getType()) {
                    case TASK:
                        tasks.put(id, loadedTask);
                        break;
                    case EPIC:
                        epics.put(id, (Epic) loadedTask);
                        break;
                    case SUBTASK:
                        subtasks.put(id, (Subtask) loadedTask);
                        Epic epic = epics.get(subtasks.get(id).getEpicId());
                        epics.put(id, epic);
                        break;
                }
            }
        } catch (FileNotFoundException exp) {
            throw new FileLoadException("Файл не найден", exp);
        } catch (IOException exp) {
            throw new FileLoadException("Произошла ошибка чтения из файла", exp);
        }
        return manager;
    }
}
