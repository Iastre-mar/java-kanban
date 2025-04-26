package ru.yandex.practicum.cva.task.tracker;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String backupFileName = "autosave.csv";
    private final String backupDirectory = "out/";

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager resBackedTaskManager = new FileBackedTaskManager();

        resBackedTaskManager.loadTasksFromFile(file);

        return resBackedTaskManager;
    }

    @Override
    public Task createTask(Task task) {
        Task task1 = super.createTask(task);
        save();
        return task1;
    }

    @Override
    public EpicTask createEpic(EpicTask task) {
        EpicTask epic = super.createEpic(task);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask task) {
        SubTask subTask = super.createSubTask(task);
        save();
        return subTask;
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task task1 = super.updateTask(task);
        save();
        return task1;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        SubTask subTask = super.updateSubTask(task);
        save();
        return subTask;
    }

    @Override
    public EpicTask updateEpic(EpicTask task) {
        EpicTask epicTask = super.updateEpic(task);
        save();
        return epicTask;
    }

    @Override
    public Task deleteTaskById(int id) {
        Task task = super.deleteTaskById(id);
        save();
        return task;
    }

    @Override
    public EpicTask deleteEpicById(int id) {
        EpicTask epicTask = super.deleteEpicById(id);
        save();
        return epicTask;
    }

    @Override
    public SubTask deleteSubtaskById(int id) {
        SubTask subTask = super.deleteSubtaskById(id);
        save();
        return subTask;
    }

    private void save() {
        String autoSaveFilePath = Paths.get(backupDirectory, backupFileName)
                                       .toString();

        try (OutputStream fos = new FileOutputStream(autoSaveFilePath);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter bw = new BufferedWriter(osw);
        ) {

            bw.write(String.join(",", ColumnsInFile.getTableHeaderList()));
            bw.newLine();

            for (EpicTask epicTask : epicMap.values()) {
                bw.write(toString(epicTask));
                bw.newLine();
            }

            for (Task task : taskMap.values()) {
                bw.write(toString(task));
                bw.newLine();
            }

            for (SubTask subTask : subTaskMap.values()) {
                bw.write(toString(subTask));
                bw.newLine();
            }

        } catch (IOException ioe) {
            throw new ManagerSaveException(ioe.getMessage(), ioe.getCause());
        }
    }

    private void loadTasksFromFile(File file) {

        try (InputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr);
        ) {

            String headerOfTable = br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (line.trim()
                        .isEmpty()) {
                    continue;
                }

                Task task = fromString(line);
                dispatchTaskToMap(task);
                checkIdLoad(task);
            }

            checkAllEpics();
        } catch (IOException ioe) {
            throw new ManagerSaveException(ioe.getMessage(), ioe.getCause());
        }
    }

    private void dispatchTaskToMap(Task task) {
        switch (task.getTaskType()) {
            case EPIC -> epicMap.put(task.getId(), (EpicTask) task);
            case TASK -> {
                taskMap.put(task.getId(), task);
                addTaskToPrioritizeTasks(task);
            }
            case SUBTASK -> {
                subTaskMap.put(task.getId(), (SubTask) task);
                addTaskToPrioritizeTasks(task);
            }

        }
    }
    private void checkAllEpics(){
        this.subTaskMap.values().stream().forEach(subTask -> epicMap.get(subTask.getParentId()).addNewSubtask(subTask.getId()));
        this.epicMap.values().stream().map(Task::getId).forEach(this::checkEpic);
    }

    private void checkIdLoad(Task task) {
        if (lastID < task.getId()) {
            lastID = task.getId() + 1;
        }
    }

    private String toString(Task task) {

        String epicOfTask = null;
        if (task instanceof SubTask subTask) {
            epicOfTask = String.valueOf(subTask.getParentId());
        }


        List<String> taskProperties = new ArrayList<>();
        taskProperties.add(String.valueOf(task.getId()));
        taskProperties.add(String.valueOf(task.getTaskType()));
        taskProperties.add(task.getName());
        taskProperties.add(String.valueOf(task.getStatus()));
        taskProperties.add(task.getDescription());
        taskProperties.add(epicOfTask);
        taskProperties.add(task.getStartTimeFormatted());
        taskProperties.add(String.valueOf(task.getDuration()));

        return String.join(",", taskProperties);
    }

    private Task fromString(String value) {

        List<String> taskProperties = List.of(value.split(","));

        Task resTask = TaskType.TASK.createSampleTask(
                taskProperties.get(ColumnsInFile.NAME.ordinal()),
                TaskType.valueOf(
                        taskProperties.get(ColumnsInFile.TYPE.ordinal())));

        resTask.setId(Integer.parseInt(
                taskProperties.get(ColumnsInFile.ID.ordinal())));

        resTask.setStatus(Statuses.valueOf(
                taskProperties.get(ColumnsInFile.STATUS.ordinal())));

        resTask.setDescription(
                taskProperties.get(ColumnsInFile.DESCRIPTION.ordinal()));

        resTask.setStartTime(
                taskProperties.get(ColumnsInFile.STARTTIME.ordinal()));

        resTask.setDuration(Duration.parse(
                taskProperties.get(ColumnsInFile.DURATION.ordinal())));

        if (resTask instanceof SubTask) {
            ((SubTask) resTask).setParentId(Integer.parseInt(
                    taskProperties.get(ColumnsInFile.EPIC.ordinal())));
        }

        return resTask;
    }


}
