package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.GuesserProcessor.Guesser
import scala.util.Random.nextInt

// Lista #12: Marcel Jerzyk (indeks: 244979)

// ----------------------------------------------------------------------------
// ------------------------------    Task1    ---------------------------------
// ----------------------------------------------------------------------------

// 1. Napisz klasę aktora dla serwera, który generuje losowo liczbę całkowitą
//    z przedziału [0 .. N] (N>1 ma być parametrem konstruktora serwera), wyświetla
//    odpowiednią informację i czeka na komunikaty od klientów, które próbują
//    zgadnąć tę liczbę. Po otrzymaniu komunikatu wysyła odpowiedź z informacją, czy
//    liczba klienta była mniejsza, większa czy równa liczbie wylosowanej.

// ----------------------------------------------------------------------------
// ------------------------------    Task2    ---------------------------------
// ----------------------------------------------------------------------------

// 2. Napisz klasę aktora dla klienta zgadującego liczbę, wylosowaną przez serwer.
//    Konstruktor klienta ma mieć trzy parametry: nazwę klienta, referencję do serwera
//    i górną granicę przedziału do zgadywania. Próby klienta powinny być optymalne
//    (wyszukiwanie binarne). Klient ma wyświetlać informacje, umożliwiające
//    śledzenia działania aplikacji (patrz przykład poniżej). Po odgadnięciu liczby klient
//    ma wyświetlić odpowiedni komunikat i zakończyć działanie systemu aktorów.

// ----------------------------------------------------------------------------
// ------------------------------    Task3    ---------------------------------
// ----------------------------------------------------------------------------

// 3. Napisz kompletną aplikację, która utworzy instancję serwera i co najmniej dwa
//    klienty, zgadujące liczbę wylosowaną przez serwer. W metodzie main wyślij do
//    wszystkich klientów komunikat Start, co powinno rozpocząć zgadywanie.

// ----------------------------------------------------------------------------

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

object Lista12 extends App {
  val numberOfClients = 3
  val upperLimitNumber = 100

  val guesserProcessor: ActorSystem[GuesserProcessor.Guesser] = ActorSystem(GuesserProcessor(), "Guesser")
  guesserProcessor ! Guesser(upperLimitNumber, numberOfClients)
}
