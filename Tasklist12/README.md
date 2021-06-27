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
  final case class ClientGuess(guessNum: Int, from: ActorRef[Client.ServerInfo])

  def apply(upper: Int): Behavior[ClientGuess] = {
    Behaviors.setup { context =>
      context.log.info(s"Guess my number from the interval [0..$upper]")
      analyseGuess(nextInt(upper))
    }
  }

  private def analyseGuess(chosenNumber: Int): Behavior[ClientGuess] =
    Behaviors.receiveMessage { message =>
      if ( message.guessNum > chosenNumber ) { message.from ! Client.NumberTooBig(message.guessNum) }
      else if ( message.guessNum < chosenNumber) { message.from ! Client.NumberTooLow(message.guessNum) }
      else { message.from ! Client.NumberGuessed(message.guessNum) }
      Behaviors.same
    }
}
```

# **Task #2**

## Create actor Client with params _name_, _server reference_, _upper guess number limit_ that makes optimal number guesses and interacts with Server.

Write an actor class for the **Client** which guesses the number chosen by the server. The **Client** constructor is meant to have three parameters: _`client name`_, _`server reference`_, and _`the upper bound of the guess range`_. The **Client's** attempts should be optimal _(binary search)_. The **Client** should have implemented a way to display information that allows tracking the operations of the application. After guessing the number, the **Client** should display an appropriate message and terminate other actors' in the system.

```scala
object Client {
  sealed trait ServerInfo

  final case class NumberGuessed(guessNumber: Int) extends ServerInfo
  final case class NumberTooBig(guessNumber: Int) extends ServerInfo
  final case class NumberTooLow(guessNumber: Int) extends ServerInfo

  def apply(upper: Int, serverRef: ActorRef[Server.ClientGuess]): Behavior[ServerInfo] = {
    Behaviors.setup { context =>
      val newGuess = nextInt(upper)
      context.log.info(s"${context.self.path.name} first random try = $newGuess")
      serverRef ! Server.ClientGuess(newGuess, context.self)
      thinkNewGuess(0, upper, serverRef)
    }
  }

  private def thinkNewGuess(lower: Int, upper: Int, serverRef: ActorRef[Server.ClientGuess]): Behavior[ServerInfo] =
    Behaviors.receive { (context, message) =>
      Thread.sleep(nextInt(1000) + 250)
      message match {
        case NumberTooBig(lastGuess) =>
          val newUpper = lastGuess - 1
          val newGuess = (lower + newUpper)/2
          context.log.info(s"Response: too big. I'm trying: $newGuess")
          serverRef ! Server.ClientGuess(newGuess, context.self)
          thinkNewGuess(lower, newUpper, serverRef)

        case NumberTooLow(lastGuess) =>
          val newLower = lastGuess + 1
          val newGuess = (newLower + upper)/2
          context.log.info(s"Response: too low. I'm trying: $newGuess")
          serverRef ! Server.ClientGuess(newGuess, context.self)
          thinkNewGuess(newLower, upper, serverRef)

        case NumberGuessed(lastGuess) =>
          context.log.info(s"${context.self.path.name}: I guessed it! ($lastGuess)")
          Behaviors.stopped
      }
    }
}

```

# **Task #3**

## Write complete application that creates server instance and at least two clients that are guessing the numbers. Make a start signal from main.

Write a complete application that will create a **Server** instance and at least two **Clients**, which are guessing the number chosen by the **Server**. In the main method, send a start message to all **Clients**, that makes them start guessing.

```scala
object GuesserMain {
  final case class Guesser(upper: Int, numberOfClients: Int)

  def apply(): Behavior[Guesser] = {
    Behaviors.receive[Guesser] { (context, message) =>
      val serverRef = context.spawn(Server(message.upper), name="Server")
      for (i <- 0 until message.numberOfClients) {
        context.watch(context.spawn(Client(message.upper, serverRef), name=s"Client${i+1}"))
      }
      manageGame(message.numberOfClients)
    }
  }

  private def manageGame(numberOfLoggedClients: Int): Behavior[Guesser] =
    Behaviors.receiveSignal { case (context, Terminated(ref)) =>
      if (numberOfLoggedClients > 1) {
        context.log.info(s"Client '${ref.path.name}' won the game. Players left: ${numberOfLoggedClients-1}")
        manageGame(numberOfLoggedClients-1)
      } else {
        context.log.info(s"Client '${ref.path.name}' won the game. All Clients won. Shutting down.")
        Behaviors.stopped
      }
    }
}

// ----------------------------------------------------------------------------

object Lista12 extends App {
  val numberOfClients = 2
  val upperLimitNumber = 100
  val guesserProcessor: ActorSystem[GuesserMain.Guesser] = ActorSystem(GuesserMain(), "Guesser")
  guesserProcessor ! Guesser(upperLimitNumber, numberOfClients)
}

```
