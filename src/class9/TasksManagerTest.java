package class9;


import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

interface ITask { //Component
    int getPriority();

    LocalDateTime getDeadline();

    String getCategory();
}

class Task implements ITask {
    String category;
    String name;
    String description;

    public Task(String category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public LocalDateTime getDeadline() {
        return LocalDateTime.MAX;
    }

    @Override
    public String getCategory() {
        return category;
    }

    //Task{name='NP', description='prepare for June exam :)'}

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

abstract class TaskDecorator implements ITask {
    ITask wrapped;

    public TaskDecorator(ITask task) {
        this.wrapped = task;
    }

    @Override
    public String getCategory() {
        return wrapped.getCategory();
    }


}

class PriorityTaskDecorator extends TaskDecorator {
    int priority;

    public PriorityTaskDecorator(ITask task, int priority) {
        super(task);
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public LocalDateTime getDeadline() {
        return wrapped.getDeadline();
    }

    @Override
    public String toString() {
        return wrapped.toString().replace("}","") + ", priority=" + priority + "}";
    }
}

class DeadlineTaskDecorator extends TaskDecorator {
    LocalDateTime deadline;

    public DeadlineTaskDecorator(ITask task, LocalDateTime deadline) {
        super(task);
        this.deadline = deadline;

    }

    @Override
    public int getPriority() {
        return wrapped.getPriority();
    }

    @Override
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return wrapped.toString().replace("}","") + ", deadline=" + deadline + "}";

    }
}


class TaskFactory {
    public static ITask create(String line) {
        //School,NP,prepare for June exam :) - basic (3)
        //School,NP,solve all exercises,3 - priority (4)
        //School,NP,lab 4 po NP,2020-07-11T23:59:59.000 - deadline (4)
        //School,NP,lab 1 po NP,2020-06-23T23:59:59.000,1 - deadline & priority (5)

        String[] parts = line.split(",");
        String category = parts[0];
        String name = parts[1];
        String description = parts[2];
        ITask task = new Task(category, name, description);
        if (parts.length == 3) {
            return task;
        } else if (parts.length == 4) {
            try {
                Integer priority = Integer.parseInt(parts[3]);
                return new PriorityTaskDecorator(task, priority);
            } catch (Exception e) {
                LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                return new DeadlineTaskDecorator(task, deadline);
            }
        } else { //parts.length==5
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);
            int priority = Integer.parseInt(parts[4]);
            task = new DeadlineTaskDecorator(task, deadline);
            task = new PriorityTaskDecorator(task, priority);
            return task;
        }
    }
}

class TaskManager {

    Map<String, List<ITask>> tasks;

    TaskManager() {
        tasks = new HashMap<>();
    }

    public void readTasks(InputStream in) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        tasks = reader.lines()
                .map(line -> TaskFactory.create(line))
                .collect(Collectors.groupingBy(
                        ITask::getCategory,
                        Collectors.toList()
                ));

    }

    void printTasks(OutputStream os, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(os);

        if (includeCategory) {
            tasks.forEach((category, tasks) -> {
                pw.println(category.toUpperCase());
                for (ITask task : tasks) {
                    pw.println(task.toString());
                }
            });
        }

        pw.flush();
    }
}

public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}
