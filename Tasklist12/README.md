<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #12</strong></a>
    ·
    <a href="./tasklist12.pdf">TaskList12.pdf</a>
  </p>
</p>

---

# **Task #1**

## Create actor Server that generated random number and awaits for signals from clients with guess messages.

Write an actor class for a **Server** that randomly generates an integer between `[0 ... N]` _(`N` `> 1` is supposed to be a server constructor parameter)_, displays the appropriate information, and waits for messages from **Clients** trying to guess the number. After receiving the message, it sends a response with information whether the number from **Client** was smaller, greater or equal to the chosen number.

```scala
object Server {
  def apply(upperLimitNumberGuess: Int): Behavior[Client.MakeGuess] =
    analyseGuess(nextInt(upperLimitNumberGuess))

  private def analyseGuess(chosenNumber: Int): Behavior[Client.MakeGuess] =
    Behaviors.receive { (context, message) =>
      context.log.debug(s"chosenNumber=$chosenNumber")
      if (message.guessNum == chosenNumber) {
        context.log.info(s"Guess ${message.guessNum} is correct!")
        Behaviors.stopped
      } else {
        val infoMsg = if (message.guessNum < chosenNumber) "too low" else "too high"
        context.log.info(s"Received Guess: ${message.guessNum}, which is $infoMsg!")
        message.from ! Client.KeepGuessing(message.guessNum < chosenNumber)
        analyseGuess(chosenNumber)
      }
    }
}
```

# **Task #2**

## Create actor Client with params _name_, _server reference_, _upper guess number limit_ that makes optimal number guesses and interacts with Server.

Write an actor class for the **Client** which guesses the number chosen by the server. The **Client** constructor is meant to have three parameters: _`client name`_, _`server reference`_, and _`the upper bound of the guess range`_. The **Client's** attempts should be optimal _(binary search)_. The **Client** should have implemented a way to display information that allows tracking the operations of the application. After guessing the number, the **Client** should display an appropriate message and terminate other actors' in the system.

```scala
object Client {
  sealed trait Command
  final case class StartGuessing() extends Command
  final case class KeepGuessing(guessedToLow: Boolean) extends Command
  final case class MakeGuess(guessNum: Int, from: ActorRef[KeepGuessing])

  def apply(upperLimitNumberGuess: Int, server: ActorRef[MakeGuess]): Behavior[Command] =
    thinkNewGuess(0, upperLimitNumberGuess/2, upperLimitNumberGuess, server)

  private def thinkNewGuess(lowerLimit: Int, lastGuess: Int, upperLimit: Int, server: ActorRef[MakeGuess]): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      Thread.sleep(nextInt(2000) + 500)
      message match {
        case KeepGuessing(guessedToLow) =>
          val newLower = if (guessedToLow) lastGuess + 1 else lowerLimit
          val newUpper = if (!guessedToLow) lastGuess - 1 else upperLimit
          val newGuess = (newLower + newUpper) / 2
          context.log.debug(s"lower=($lowerLimit -> $newLower), upper=($upperLimit -> $newUpper), lastGuess=$lastGuess")
          context.log.info(s"Sending new guess: ($newLower + $newUpper)/2 = $newGuess.")
          server ! MakeGuess (newGuess, context.self)
          thinkNewGuess(newLower, newGuess, newUpper, server)
        case StartGuessing() =>
          context.log.info(s"Sending initial guess: $lastGuess.")
          server ! MakeGuess (lastGuess, context.self)
          thinkNewGuess(lowerLimit, lastGuess, upperLimit, server)
      }
    }
}
```

# **Task #3**

## Write complete application that creates server instance and at least two clients that are guessing the numbers. Make a start signal from main.

Write a complete application that will create a **Server** instance and at least two **Clients**, which are guessing the number chosen by the **Server**. In the main method, send a start message to all **Clients**, that makes them start guessing.

```scala
object GuesserProcessor {
  final case class Guesser(upperLimitNumber: Int, numberOfClients: Int)

  def apply(): Behavior[Guesser] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage { message =>
        val serverRef = context.spawn(Server(message.upperLimitNumber), name="server")
        for(i <- 0 until message.numberOfClients) {
          val clientRef = context.spawn(Client(message.upperLimitNumber, serverRef), name=s"client${i+1}")
          clientRef ! Client.StartGuessing()
        }
        Behaviors.same
      }
    }
}

object Tasklist12 extends App {
  val numberOfClients = 3
  val upperLimitNumber = 100

  val guesserProcessor: ActorSystem[GuesserProcessor.Guesser] = ActorSystem(GuesserProcessor(), "Guesser")
  guesserProcessor ! Guesser(upperLimitNumber, numberOfClients)
}
```
