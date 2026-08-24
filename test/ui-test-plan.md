# Altair UI Test Plan

This plan tests the interactive console behavior of `Altair`.

- Project root: repository root (`.`)
- Runtime: Java 25
- Launch command: `java -cp out/production/ip Altair`
- Output comparison: exact, including spaces and line breaks
- Test runner: `.codex/skills/test-ui/scripts/run_ui_tests.py`
- Preparation: Compile from the project root with `javac -d out/production/ip src/main/java/*.java` before running the plan.

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

### Step 1: Add, list, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo buy milk
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
    Copy. Your task has been added:
      [T][ ] buy milk
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 3: Mark and unmark a task

Aim: Verify that marking and unmarking update the displayed completion status of a ToDo task.

### Step 1: Toggle task status and exit

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
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 4: Add all three task types

Aim: Verify that ToDos, Deadlines, and Events display their type and date/time details correctly in one list.

### Step 1: Add typed tasks, list, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
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
    Copy. Your task has been added:
      [T][ ] borrow book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [D][ ] return book (by: Sunday)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [E][ ] project meeting (from: Mon 2pm to: 4pm)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 5: Delete a task

Aim: Verify that `delete <task number>` removes a selected middle task, updates the task count, and renumbers later tasks.

### Step 1: Delete, list, and exit

Command:

```text
java -cp out/production/ip Altair
```

Inputs:

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
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
    Copy. Your task has been added:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [D][ ] return book (by: June 6th)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Copy. Your task has been added:
      [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [D][ ] return book (by: June 6th)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
deadline /by Sunday
event project meeting /from Mon 2pm
event /from Mon 2pm /to 4pm
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
    A deadline needs a date or time after /by.
____________________________________________________________
____________________________________________________________
    I'm afraid the description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
    An event needs /from and /to date or time details.
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

### Step 1: Enter invalid task-number commands, list, and exit

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
