<p align="center">
  <h2 align="center">Concurrent and Functional Programming</h2>
  <p align="center">
    <b>Task Lists Answers:</b>
    <p align="center">
    <a href="./Tasklist0/README.md">#0</a>
    ·
    <a href="./Tasklist1/README.md">#1</a>
    ·
    <a href="./Tasklist2/README.md">#2</a>
    ·
    <a href="./Tasklist3/README.md">#3</a>
    ·
    <a href="./Tasklist4/README.md">#4</a>
    ·
    <a href="./Tasklist5/README.md">#5</a>
    ·
    <a href="./Tasklist6/README.md">#6</a>
    ·
    <a href="./Tasklist7/README.md">#7</a>
    ·
    <a href="./Tasklist8/README.md">#8</a>
    ·
    <a href="./Tasklist9/README.md">#9</a>
    ·
    <a href="./Tasklist10/README.md">#10</a>
    ·
    <a href="./Tasklist11/README.md">#11</a>
    ·
    <a href="./Tasklist12/README.md">#12</a>
    ·
    <a href="./Tasklist12/README.md">#13</a>
    </p>
  </p>
</p>

# Hey! 👋

So the funny thing about this lecture was that all of these tasks were given at the start of laboratories class and we had **1 hour** to write these answers. Obviously, this could lead to increased cortisol levels, accelerated sweating, or the feeling of impending doom, but besides that, that was quite a refreshing experience. Hopefully, these answers to various tasks that I have written could help you relieve some stress and give you mental clarity in any situation that you are currently facing and somehow ended up here.

Regarding the tasks: keep in mind that I had only **1 hour** starting from reading what brand-new problem I have to solve, through thinking of a decent solution up to spitting it through keys on keyboards into a `.scala` or `.sc` file and then sending the solutions. There are some simple but exhaustive tests for all the tasks and sometimes even comments with explanations. Maybe it could be better, but for your convenience... maybe it is good enough? I hope so!

Cheers and have fun! 🤗

# _Details of Tasks in Task Lists in a tl;dr_

## [Task List #0](./Tasklist0/README.md) - Introduction

_Everything is written in a functional style; recursively using only `head` & `tail`._

### Tasks:

- **#1**: Return last element of a list.

---

## [Task List #1](./Tasklist1/README.md) - Functional Programming

_Everything is written in a functional style; recursively using only `head` & `tail`._

### Tasks:

- **#1**: Return the sum of the whole list.
- **#2**: Return a pair - first & last element of a list.
- **#3**: Return a `boolean` whether the list is sorted or not
- **#4**: Return merged string based on a given list with a given separator

---

## [Task List #2](./Tasklist2/README.md) - Pattern Matching

_Tasks from `1` to `4` are using a pattern matching mechanism & tasks from `1` to `3` have linear complexity with respect to the length of the input list._

### Tasks:

- **#1**: Write a function that takes `N` first elements of a list.
- **#2**: Write a function that drops `N` first elements of a list.
- **#3**: Write a function that reverses a list.
- **#4**: Write a function that repeats `N` times the element where `N` is an integer in a list.
- **#5**: Calculate 3rd root of a double with epsilon approximation.

---

## [Task List #3](./Tasklist3/README.md) - List Folding

### Tasks:

- **#1**: Write a function that returns a Boolean whether an element is in a list
  - **a)** with pattern matching and recursion
  - **b)** with `foldLeft`
  - **c)** with `foldRight`
- **#2**: Write a filter function with `foldRight`.
- **#3**: Write a function which removes the first found element given in as functional
- **#4**: Write a function that splits a list in two at given index without double list traversing.

---

## [Task List #4](./Tasklist4/README.md) - Tree & Graph

### Tasks:

- **#1**: Write a function that sums up vertices from Binary Tree
- **#2**: Write a fold function for Binary Trees
- **#3**: Write a function that uses `foldBT` _(from Task #2)_ to sum up the vertices from Binary Tree and another one that creates a list out of vertices from Binary Tree:
  - **a)** infix
  - **b)** prefix
  - **c)** postfix
- **#4**: Using `foldBT` create a functional map function
- **#4**: Write a function to check whether a path between two vertices in a Graph exist

---

## [Task List #5](./Tasklist5/README.md) - Tree & Graph

### Tasks:

- **#1**: Create a class for polymorphic pair. It should have accessors, mutators and toString method.
- **#2 a)**: Extend `BankAccount` class in which 1$ will be withdrawn for every transaction.
- **#2 b)**: Extend `BankAccount` class in which 1$ will be withdrawn after 3rd transaction in a given month. Implement Interest logic.
- **#3 a)**: Create an abstract class Animal that has methods for voice, animal type and overridden custom toString. It shall have an unmodifiable name in the constructor
- **#3 b)**: Define public methods for some animals. They should allow creating new instances of animals w/ or w/o name.
- **#3 c)**: Create Test Class for Animals (w/ Vector).

---

## [Task List #6](./Tasklist6/README.md) - Side Effects & Lazy Binary Tree

### Tasks:

- **#1**: Write a `whileLoop` function (without using computational effects) that takes two arguments: a condition and an expression, and accurately simulates the performance of a while loop (also syntactically). What type (and why) must the arguments and the result of the function be?
- **#2**: Write the function `lrepeat`, which for given positive integer ki of the stream Stream `(x0, x1, x2, x3, ...)` returns a stream where each `xi` element is repeated `k` times
- **#3 a)**: Write the function `lBreadth`, which creates a stream containing all the node values of the lazy binary tree
- **#3 b)**: Write the function `lTree`: which for a given natural number `n` constructs an infinite lazy binary tree with root `n` and two subtrees `lTree (2 * n)` and `lTree (2 * n + 1)`.

---

## [Task List #7](./Tasklist7/README.md) - Covariant Immutable Queue & Tree Traversing

### Tasks:

- **#1**: Define a general class for a covariant unmodifiable queue represented by two lists.
- **#2**: Write a `breadthBT [A] function: BT [A] => List [A]` traversing binary tree in breadth and returning a list of values stored in tree nodes.

---

## [Task List #8](./Tasklist8/README.md) - Cyclic Mutable Queue

### Tasks:

- **#1 a)**: Write the class extending the queue as a circular array (all operations are performed by modulo the size of the array)
- **#1 b)**: It's basically the same task, but I did a different implementation as requested on labs.

---

## [Task List #9](./Tasklist9/README.md) - Concurrent Programming

### Tasks:

- **#1**:
  - **a)**: Analyse the program: why the counter value is not as it could be expected to be?
  - **b)**: Fix it with synchronised code
  - **c)**: Fix it using `Semaphores`.
- **#2**: Implement method `parallel` that takes two blocks of arguments as a parameter and executes them simultaneously.
- **#2**: Implement method `periodically` that takes the number of repetitions and pause-timer as arguments. The method should make use of daemons.

---

## [Task List #10](./Tasklist10/README.md) - Dining Philosophers & Execution Contexts / Buffers

### Tasks:

- **#1**:
  - **a)**: Rewrite `Consumer`/`Producer` classes from Lecture using `ArrayBlockingQueue` instead `BoundedBuffer`.
  - **b)**: Create several `Consumer`-s and `Producer`-s and investigate.
  - **c)**: Analyse the program: why the counter value is not as it could be expected to be? Fixing the program.
- **#2**: Dining Philosophers Problem

---

## [Task List #11](./Tasklist11/README.md) - Futures & Promises

### Tasks:

- **#1**: Define a function `pairFut[A, B] (fut1: Future[A], fut2: Future[B]): Future[(A, B)] = ???`
  - **a)** Using `zip` method
  - **b)** Using `for`
- **#2**: Add `exists` method to `Future[T]` type
  - **a)** Using `promise`
  - **b)** Without `promise`
- **#3**: Count the amount of words in all files in a given directory

---

## [Task List #12](./Tasklist12/README.md) - Client-Server Game

### Tasks:

- **#1**: Create an actor Server that generates a random number and awaits signals from clients with guess messages.
- **#2**: Create actor Client with params _name_, _server reference_, _upper guess number limit_ that makes optimal number guesses and interacts with Server.
- **#3**: Write a complete application that creates a server instance and at least two clients that are guessing the numbers. Make a start signal from the main.

---

## [Task List #13](./Tasklist13/README.md) - Emitting Events

### Tasks:

- **#1**: Define **two** `observable` objects, one of which emits an `event` every `5` and the other one emits an `event` every `12` second but both of them do not emit anything when time is multiple of `30` seconds. One of them should use `merge` method.
- **#2**: Define **one** `observable` object that emits an `event` every `5` and `12` second but not when time is multiple of `30` seconds. Use `filter` method.
