# Altair UI Test Plan

This plan tests the interactive console behavior of `Altair`.

- Project root: repository root (`.`)
- Runtime: Java 25
- Launch command: `java -cp out/production/ip Altair`
- Output comparison: exact, including spaces and line breaks
- Test runner: `.codex/skills/test-ui/scripts/run_ui_tests.py`

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
    Got it. I've added this task:
      [T][ ] buy milk
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 3: Mark and unmark a task

Aim: Verify that marking and unmarking work through the base `Task` reference for a ToDo task.

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
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
____________________________________________________________
____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```

## Test case 4: Add all three task types

Aim: Verify that ToDos, Deadlines, and Events are stored together in a polymorphic `Task[]` and retain their string date/time details.

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
    Got it. I've added this task:
      [T][ ] borrow book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Sunday)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Mon 2pm to: 4pm)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
    Goodbye. Let me know when you need me again.
____________________________________________________________
```
