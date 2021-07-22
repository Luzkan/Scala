<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #10</strong></a>
    ·
    <a href="./tasklist10.pdf">TaskList10.pdf</a>
  </p>
</p>

---

# **Task #1 a)**

## Rewrite `Consumer`/`Producer` classes from Lecture using `ArrayBlockingQueue` instead `BoundedBuffer`.

### Lecture

```scala
class Producer(name: String, buf: BoundedBuffer) extends Thread(name) {
	override def run: Unit =
		for (i <- 1 to 10) {println(s”$getName producing $i”); buf.put(i)}
}

class Consumer(name: String, buf: BoundedBuffer) extends Thread(name) {
	override def run: Unit =
		for (i <- 1 to 10) println(s”$getName consumed ${buf.take}”)
}

object prodCons {
	def main(args: Array[String]): Unit = {
		val buf: BoundedBuffer = new BoundedBuffer(5)
		new Producer("Producer", buf).start
		new Consumer("Consumer", buf).start
	}
}
```

### Rewritten

```scala
class Producer(name: String, buf: ArrayBlockingQueue[String]) extends Thread(name) {
  val nameId = s"${name.charAt(0)}${name.charAt(name.length() - 1)}"
  override def run(): Unit = {
    for (i <- 1 to 10) { buf.put(s"m$i"); println(s"[$nameId]: $buf") }
    buf.put("Done")
    println(s"[$nameId]: $buf")
  }
}

class Consumer(name: String, buf: ArrayBlockingQueue[String]) extends Thread(name) {
  val nameId = s"${name.charAt(0)}${name.charAt(name.length() - 1)}"
  override def run(): Unit = {
    var msg = ""
    do { msg = buf.take; println(s"[$nameId]: $msg") } while (msg != "Done")
  }
}

object Task1a {
  def main(args: Array[String]): Unit = {
    val buf: ArrayBlockingQueue[String] = new ArrayBlockingQueue(3)
    new Producer("Producer0", buf).start()
    new Consumer("Consumer0", buf).start()
  }
}
```

# **Task #1 b)**

## Create several `Consumer`-s and `Producer`-s and investigate.

In the program from sub-point _a)_ create several producers and consumers. Give them unique names, such as `Producer1`, `Consumer1`, etc. In one of the tests, create **two** _producers_ and **three** \_consumers. Why doesn't the program end?

```scala
object Task1b {
  def main(args: Array[String]) {
    val buf: ArrayBlockingQueue[String] = new ArrayBlockingQueue(3)
    for (i <- 0 to 1) new Producer(s"Producer$i", buf).start()
    for (i <- 0 to 2) new Consumer(s"Consumer$i", buf).start()
  }
}
```

# **Task #1 c)**

## Analyse the program: why the counter value is not as it could be expected to be? Fixing the program.

Remove the definitions of `Producer` and `Consumer` from the program in point _b)_. Use the `ExecutionContext` to perform the corresponding tasks. Create **two** _producers_ and **three** _consumers_ in one of the tests. Why is the program ending?

```scala
object Task1c {
  def main(args: Array[String]) {
    val buf: ArrayBlockingQueue[String] = new ArrayBlockingQueue(3)
    for (i <- 0 to 1) ExecutionContext.global.execute(() => { new Producer(s"Producer$i", buf).start() })
    for (i <- 0 to 2) ExecutionContext.global.execute(() => { new Consumer(s"Consumer$i", buf).start() })
    Thread.sleep(500)
  }
}
```

# **Task #2**

## Dining Philosophers Problem

Write a program that solves the problem of dining philosophers using semaphores (`java.util.concurrent.Semaphore`). The solution should meet the following conditions:

1. A _philosopher_ eats only when he has two _chopsticks_.
2. Two _philosophers_ cannot hold the same _chopstick_ at the same time.
3. There is **no blockage (stalemate)**. It can occur, for example, when all _philosophers_ pick up the left-hand _chopsticks_ and wait for the right ones to be released.
4. Nobody can be starved. The apparently obvious strategy of waiting until both _chopsticks_ are free could starve two philosophers **(why?)**.
5. None of the philosophers have a strategy like eating 100% of time. After the meal is over, everyone puts their _chopsticks_ down and goes back to the meditation room.
6. Philosophers pick up and put down their _chopsticks_ one at a time.
7. We cannot distinguish any of the philosophers (their algorithms should be the same).

One solution is that at the beginning all philosophers meditate in a dedicated room, and eat in the dining room. A _doorman_ should be involved, guarding the door of the dining room and allowing up to four _philosophers_ to be there simultaneously. That strategy makes a scenario where at least two _philosophers_ sitting at the table lack at least one neighbour, and therefore at least one _philosopher_ can eat _(why?)_

Each _philosopher_ has to display appropriate messages informing about their status: _meditation time_, _entering the dining room_, _eating time_, _leaving the dining room_.

```scala
class Philosopher(private val id: Int,
                  private val doorkeeper: Semaphore,
                  private val leftChopstick: Chopstick,
                  private val rightChopstick: Chopstick,
                  private val actionTime: Int) {

  val NAME_TAG = s"[${this.getClass.getSimpleName.charAt(0)}$id]"

  def think(): Unit = {
    val meditateTime: Int = nextInt(actionTime)
    println(s"$NAME_TAG Meditate for: ${meditateTime/1000}s")
    Thread.sleep(meditateTime)
  }

  def enterRoom(): Unit = {
    doorkeeper.acquire()
    println(s"$NAME_TAG Enters the room!")
    leftChopstick.pick(NAME_TAG)
    rightChopstick.pick(NAME_TAG)
  }

  def eat(): Unit = {
    val eatingTime = nextInt(actionTime)
    println(s"$NAME_TAG Eating for: ${eatingTime/1000}s")
    Thread.sleep(eatingTime)
  }

  def leaveRoom(): Unit = {
    leftChopstick.put(NAME_TAG)
    rightChopstick.put(NAME_TAG)
    doorkeeper.release()
    println(s"$NAME_TAG Left the room!")
  }

  def routine(): Unit = {
    think()
    enterRoom()
    eat()
    leaveRoom()
  }

  def awake(): Unit = while (true) routine()
}

class Chopstick(id: Int) {
  val NAME_TAG = s"[${this.getClass.getSimpleName.charAt(0)}$id]"
  val s = new Semaphore(1, true)
  def pick(philosopher: String): Unit = { s.acquire(); println(s"$philosopher picks up $NAME_TAG")}
  def put(philosopher: String): Unit = { s.release(); println(s"$philosopher puts away $NAME_TAG")}
}

class FeastingPhilosophers(private val n: Int, private val actionTime: Int, private val runTime: Int) {
  val doorkeeper = new Semaphore(n - 1, true)
  val chopsticks: Array[Chopstick] = new Array[Chopstick](n)
  for (i <- 0 until n) chopsticks(i) = new Chopstick(i)

  def start(): Unit = {
    println(s"Creating $n Philosophers that will feast for ${runTime/1000}s, with 0-${actionTime}ms between actions.")
    val executionContext = ExecutionContext.global
    for (i <- 0 until n) {executionContext.execute(
      () => new Philosopher(i, doorkeeper, chopsticks(i), chopsticks((i + 1) % n), actionTime).awake()
    )}
    Thread.sleep(runTime)
  }
}

object Task2 {
  def main(args: Array[String]): Unit = {
    val PHILOSOPHERS_NUMBER: Int = 5
    val ACTION_TIME: Int = 15 * 1000
    val RUN_TIME: Int = 120 * 1000
    new FeastingPhilosophers(PHILOSOPHERS_NUMBER, ACTION_TIME, RUN_TIME).start()
  }
}
```
