import com.tuneit.courses.Task;
import com.tuneit.courses.db.DBTaskGeneratorService;
import com.tuneit.courses.db.LabTaskQA;
import com.tuneit.courses.db.schema.Schema;
import com.tuneit.courses.db.schema.SchemaLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskGeneratorTest {

    private DBTaskGeneratorService taskGeneratorService = new DBTaskGeneratorService();

    @Test
    void tasksValidAnswerTest() {
        for (int i = 0; i < 5; i++) {
            Task[] tasks = taskGeneratorService.getTasks("student", "lab02", "0" + i, 0);

            for (Task task : tasks) {
                Schema schema = SchemaLoader.getSchema(task.getYearOfStudy(), task.getStudentId());
                LabTaskQA labTaskQA = taskGeneratorService.findLabTask(task).generate(schema, task);

                task.setAnswer(labTaskQA.getCorrectAnswer()).setComplete(true);
            }

            taskGeneratorService.checkTasks(tasks);

            for (Task task : tasks) {
                if (task.getRating() != 1) {
                    System.out.println(i + ": " + task.getQuestion());
                    System.out.println(task.getTaskId() + ": " + task.getAnswer());
                }
                assertEquals(task.getRating(), 1);
            }
        }
    }

    @Test
    void task1QueryTest() {
        Task[] tasks = taskGeneratorService.getTasks("student", "lab02", "1", 0);

        //true
        tasks[0].setAnswer("select * from seats").setComplete(true);
        taskGeneratorService.checkTasks(tasks);
        assertTrue(tasks[0].getRating() > 0.5);

        tasks[0].setAnswer("select aircraft_code, seat_no, fare_conditions from seats");
        taskGeneratorService.checkTasks(tasks);
        assertTrue(tasks[0].getRating() > 0.5);

        tasks[0].setAnswer("select seat_no, aircraft_code, fare_conditions from seats");
        taskGeneratorService.checkTasks(tasks);
        assertTrue(tasks[0].getRating() > 0.5);

        tasks[0].setAnswer("select seat_no seat, aircraft_code air, fare_conditions fare from seats sea");
        taskGeneratorService.checkTasks(tasks);
        assertTrue(tasks[0].getRating() > 0.5);


        //false
        tasks[0].setAnswer("select seat_no, aircraft_code from seats");
        taskGeneratorService.checkTasks(tasks);
        assertTrue(tasks[0].getRating() < 0.5);
    }

    @Test
    void randomTest() {
        String variant = "-1";
        LabTaskQA[] labTaskQAS = getLabTaskQAS(variant);


        for (int i = 0; i < 10000; i++) {
            Task[] tasks = taskGeneratorService.getTasks("student", "lab02", variant, 0);
            for (int j = 0; j < tasks.length; j++) {
                Task task = tasks[j];

                Schema schema = SchemaLoader.getSchema(task.getYearOfStudy(), task.getStudentId());
                LabTaskQA labTaskQA = taskGeneratorService.findLabTask(task).generate(schema, task);

                assertEquals(labTaskQAS[j].getCorrectAnswer(), labTaskQA.getCorrectAnswer());
                assertEquals(labTaskQAS[j].getQuestion(), labTaskQA.getQuestion());
            }
        }
    }

    private LabTaskQA[] getLabTaskQAS(String variant) {
        LabTaskQA[] labTaskQAS = new LabTaskQA[12];
        Task[] tasks = taskGeneratorService.getTasks("student", "lab02", variant, 0);

        for (int i = 0; i < tasks.length; i++) {
            Task task = tasks[i];
            Schema schema = SchemaLoader.getSchema(task.getYearOfStudy(), task.getStudentId());
            LabTaskQA labTaskQA = taskGeneratorService.findLabTask(task).generate(schema, task);
            labTaskQAS[i] = labTaskQA;
        }

        return labTaskQAS;
    }
}