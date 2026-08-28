# Altair UI Test Plan

This plan tests the interactive console behavior of `Altair`.

- Project root: repository root (`.`)
- Runtime: Java 25
- Launch command: `java -cp out/production/ip Altair`
- Output comparison: exact, including spaces and line breaks
- Test runner: `.codex/skills/test-ui/scripts/run_ui_tests.py`
- Preparation: Compile from the project root with `javac -d out/production/ip src/main/java/*.java` before running the plan.
- Persistence: Successful task-list changes rewrite `./data/duke.txt`; the file is checked separately after the UI session because the console does not display save confirmations. Event dates are stored as one combined field.
- Isolation: Test cases 1–9 finish with an empty saved task list. Test case 10 intentionally leaves one completed task for test case 11 to load.
- Missing data: Starting without `./data/duke.txt` is treated as an empty task list, and the first save creates the missing `./data/` folder.
- Corrupted data: A malformed non-empty row is rejected with a line-specific error and no Java stack trace.

## Test case 1: Start and exit

Aim: Verify that the application displays its greeting and exits with the `bye` command.

### Step 1: Exit from the greeting

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 2: Add and list a ToDo

Aim: Verify that the `todo` command creates a ToDo task and that the list displays its type and status markers.

### Step 1: Add, list, delete, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo buy milk
list
delete 1
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [T][ ] buy milk
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] buy milk
    Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 3: Mark and unmark a task

Aim: Verify that marking and unmarking update the displayed completion status of a ToDo task.

### Step 1: Toggle task status, clean up, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo read book
mark 1
list
unmark 1
list
delete 1
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Task marked as completed:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][X] read book
____________________________________________________________
____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] read book
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] read book
    Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 4: Add all three task types

Aim: Verify that ToDos, Deadlines, and Events display their type and formatted dates correctly in one list.

### Step 1: Add typed tasks, list, clean up, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-16 /to 2019-10-17
list
delete 3
delete 2
delete 1
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [T][ ] borrow book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [D][ ] return book (by: Oct 15 2019)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [D][ ] return book (by: Oct 15 2019)
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] borrow book
    Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 5: Delete a task

Aim: Verify that `delete <task number>` removes a selected middle task, updates the task count, and renumbers later tasks.

### Step 1: Delete, list, clean up, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
delete 2
list
delete 2
delete 1
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [D][ ] return book (by: Jun 06 2019)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [D][ ] return book (by: Jun 06 2019)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] read book
    Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 6: Explain incomplete and unknown commands

Aim: Verify that an incomplete ToDo command and an unrecognized command are handled without terminating the chatbot.

### Step 1: Enter invalid commands and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo
blah
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    I'm afraid the description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
    I do not understand your command. Try again, perhaps?
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 7: Reject malformed deadline and event commands

Aim: Verify that incomplete Deadline and Event commands display helpful explanations and leave the task list empty.

### Step 1: Enter malformed typed commands, list, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
deadline return book
deadline /by 2019-10-15
event project meeting /from 2019-10-16
event /from 2019-10-16 /to 2019-10-17
list
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    A deadline needs a date after /by.
____________________________________________________________
____________________________________________________________
    I'm afraid the description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
    An event needs /from and /to dates.
____________________________________________________________
____________________________________________________________
    I'm afraid the description of an event cannot be empty.
____________________________________________________________
____________________________________________________________
     The following are your tasks
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 8: Reject invalid task numbers without changing the list

Aim: Verify that malformed, non-numeric, and out-of-range task numbers are rejected while the existing task remains unchanged.

### Step 1: Enter invalid task-number commands, list, clean up, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo read book
mark
mark 0
unmark two
delete
delete second
delete 2
list
delete 1
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Please use: mark <task number>.
____________________________________________________________
____________________________________________________________
    That task number is not in your list.
____________________________________________________________
____________________________________________________________
    Please use a valid task number.
____________________________________________________________
____________________________________________________________
    Please use: delete <task number>.
____________________________________________________________
____________________________________________________________
    Please use a valid task number.
____________________________________________________________
____________________________________________________________
    That task number is not in your list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] read book
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] read book
    Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 9: Display an empty task list

Aim: Verify that `list` handles an empty list before and after the only task is deleted.

### Step 1: List an empty list, delete the only task, and list again

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
list
todo plan trip
delete 1
list
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
     The following are your tasks
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [T][ ] plan trip
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] plan trip
    Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 10: Save a changed task list

Aim: Verify that a happy-path task-list change sequence completes normally while persistence is exercised. After this UI session, confirm that `./data/duke.txt` contains the final task state.

### Step 1: Add, mark, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo write persistence test
mark 1
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [T][ ] write persistence test
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Task marked as completed:
       [T][X] write persistence test
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 11: Load a saved task list

Aim: Verify that a task saved by the previous chatbot process is restored when the chatbot starts again.

### Step 1: Load, list, delete, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
list
delete 1
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][X] write persistence test
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [T][X] write persistence test
    Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 12: Accept flexible command whitespace and case

Aim: Verify that commands remain usable with uppercase keywords, repeated spaces, and tab-separated task numbers.

### Step 1: Add, toggle, delete, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
TODO   buy milk
MARK	1
UNMARK	1
delete 1
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [T][ ] buy milk
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Task marked as completed:
       [T][X] buy milk
____________________________________________________________
____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] buy milk
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] buy milk
    Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 13: Reject unrepresentable task details

Aim: Verify that the file delimiter is rejected in user task details instead of producing an unreadable saved row.

### Step 1: Reject the task, list, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo bad | data
list
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Task details cannot contain the '|' character.
____________________________________________________________
____________________________________________________________
     The following are your tasks
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 14: Reject arguments on simple commands

Aim: Verify that `bye` and `list` reject unexpected arguments instead of silently performing a different command.

### Step 1: Reject extra arguments and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
bye now
list now
bye
```

Expected output:

```text
____________________________________________________________
   _____  .__   __         .__        
  /  _  \ |  | _/  |______ |__|______ 
 /  /_\  \|  | \   __\__  \|  \_  __ \
/    |    \  |__|  |  / __ \|  ||  | \/
\____|__  /____/|__| (____  /__||__|  
        \/                \/          
Greetings, I am Altair.
How may I help you?
____________________________________________________________
____________________________________________________________
    Please use: bye.
____________________________________________________________
____________________________________________________________
    Please use: list.
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```
