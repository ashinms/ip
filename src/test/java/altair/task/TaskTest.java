package altair.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Task}, the base class of every task type.
 *
 * <p>{@code Task} is a good JUnit target for the same reason as
 * {@link altair.command.CommandType}: its interesting methods are
 * deterministic and side-effect free. {@link Task#getStatusIcon()},
 * {@link Task#toString()}, and {@link Task#toFileString()} are pure functions
 * of the object's state, and {@link Task#markAsDone()} /
 * {@link Task#markAsNotDone()} only flip an in-memory flag whose effect is
 * observable through those methods.</p>
 *
 * <p>The two protected methods {@code getTaskType()} and {@code getTypeIcon()}
 * are not tested directly; their result (the {@code T} marker for a plain
 * task) is checked through {@code toString} and {@code toFileString}.</p>
 */
public class TaskTest {

    // ----- getDescription -----

    @Test
    public void getDescription_returnsTextGivenToConstructor() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    // ----- getStatusIcon -----

    @Test
    public void getStatusIcon_newTask_returnsBlankSpace() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_afterMarkAsDone_returnsX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    // ----- descriptionContains -----

    @Test
    public void descriptionContains_keywordIsASubstring_returnsTrue() {
        Task task = new Task("read book");
        assertTrue(task.descriptionContains("book"));
    }

    @Test
    public void descriptionContains_keywordDiffersInCase_returnsTrue() {
        // The search ignores case, so "BOOK" still matches "read book".
        Task task = new Task("read book");
        assertTrue(task.descriptionContains("BOOK"));
    }

    @Test
    public void descriptionContains_keywordAbsent_returnsFalse() {
        Task task = new Task("read book");
        assertFalse(task.descriptionContains("milk"));
    }

    // ----- markAsDone / markAsNotDone -----

    @Test
    public void markAsNotDone_afterMarkAsDone_returnsToNotDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsDone_calledTwice_staysDone() {
        // Marking a task done is idempotent: a second call must not "toggle" it back.
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_onNewTask_staysNotDone() {
        Task task = new Task("read book");
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    // ----- toString -----

    @Test
    public void toString_newTask_showsTypeMarkerAndBlankStatus() {
        Task task = new Task("read book");
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void toString_doneTask_showsXStatus() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
    }

    // ----- toFileString -----

    @Test
    public void toFileString_newTask_usesPipeSeparatedFieldsWithZeroStatus() {
        Task task = new Task("read book");
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    public void toFileString_doneTask_usesOneStatus() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("T | 1 | read book", task.toFileString());
    }
}
