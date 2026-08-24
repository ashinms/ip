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

Aim: Verify that ToDos, Deadlines, and Events are stored together in a polymorphic collection and retain their string date/time details.

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

Aim: Verify that `delete <task number>` removes the selected task and updates the task count.

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
delete 3
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
      [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     The following are your tasks
     1.[T][ ] read book
     2.[D][ ] return book (by: June 6th)
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
