package com.example

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import com.example.GuesserMain.Guesser
import scala.util.Random.nextInt

// Lista #12: Marcel Jerzyk (indeks: 244979)

// Update #1:
//    Renamed:
//      - "MakeGuess" -> "ClientGuess"
//      - "Command" -> "ServerInfo"
//      - "GuessProcessor" -> "GuesserMain"
//    - Log messages changed to match specification
//    - Clients now start through self "apply" method instead of via "StartGame" signal from Server
//    - Changed "keepGuessing(tooLow: bool)" into two separate messages "NumberTooBig" & "NumberTooLow"
//    - Now actor system termination is executed via GuesserMain which counts the number of "Behaviors.stopped"
//      signals instead of previous implementation which counted the number of players who won in Server and
//      then send termination signal to the GuesserMain

// ----------------------------------------------------------------------------
// ------------------------------    Task1    ---------------------------------
// ----------------------------------------------------------------------------

// 1. Napisz aktora dla serwera (server: ActorRef[Server.ClientGuess]), który generuje
//    losowo liczbę całkowitą z przedziału [0 .. N] (N>1 ma być parametrem metody
//    apply serwera), wyświetla odpowiednią informację i czeka na komunikaty od
//    klientów, które próbują zgadnąć tę liczbę. Po otrzymaniu komunikatu serwer
//    wysyła odpowiedź z informacją, czy liczba klienta była mniejsza, większa czy
//    równa liczbie wylosowanej.
//
//    def apply(upper: Int): Behavior[ClientGuess] = ???

// ----------------------------------------------------------------------------

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

// ----------------------------------------------------------------------------
// ------------------------------    Task2    ---------------------------------
// ----------------------------------------------------------------------------

// 2. Napisz aktora dla klienta zgadującego liczbę, wylosowaną przez serwer. Próby
//    klienta powinny być optymalne (wyszukiwanie binarne). Klient ma wyświetlać
//    informacje, umożliwiające śledzenia działania aplikacji (patrz przykład poniżej).
//    Po odgadnięciu liczby klient ma wyświetlić odpowiedni komunikat i zakończyć
//    swoje działanie.
//
//    def apply(upper: Int, server: ActorRef[Server.ClientGuess]): Behavior[ServerInfo] = ???

// ----------------------------------------------------------------------------

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

// ----------------------------------------------------------------------------
// ------------------------------    Task3    ---------------------------------
// ----------------------------------------------------------------------------

// 3. Napisz kompletną aplikację (w stylu funkcyjnym). Strażnik użytkownika ma
//    utworzyć instancję serwera i co najmniej dwa klienty, zgadujące liczbę
//    wylosowaną przez serwer. Wzoruj się na przykładzie „klient/serwer” z wykładu.

// ----------------------------------------------------------------------------

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
