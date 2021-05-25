import java.util.concurrent.Semaphore
import scala.concurrent.ExecutionContext
import scala.util.Random.nextInt


// ----------------------------------------------------------------------------
// ------------------------------    Task2    ---------------------------------
// ----------------------------------------------------------------------------

// 2. Napisz program, rozwiązujący problem ucztujących filozofów (wykład 9, str. 38) dla N
//    filozofów za pomocą semaforów (java.util.concurrent.Semaphore). Rozwiązanie powinno
//    spełniać następujące warunki:
//
//    1. Każdy filozof ma stałe miejsce przy stole. Filozof je tylko wtedy, gdy ma dwie pałeczki.
//    2. Dwóch filozofów nie może jednocześnie trzymać tej samej pałeczki.
//    3. Nie występuje blokada (sytuacja patowa). Może ona wystąpić np. wtedy, gdy wszyscy filozofowie
//       podniosą lewe pałeczki i będą czekać na zwolnienie prawych.
//    4. Nikt nie może być zagłodzony. Oczywista z pozoru strategia, polegająca na poczekaniu, aż obie
//       pałeczki będą wolne, może spowodować zagłodzenie dwóch filozofów (dlaczego?).
//    5. Żaden z filozofów nie zajmuje się tylko jedzeniem. Po zakończeniu posiłku każdy odkłada
//       pałeczki i wraca do sali medytacji.
//    6. Filozofowie podnoszą i odkładają pałeczki po jednej naraz.
//    7. Nie można wyróżniać żadnego z filozofów (algorytmy ich działania powinny być takie same).
//
//    Jedno z rozwiązań zakłada, że na początku wszyscy filozofowie medytują w przeznaczonej do
//    tego sali, natomiast posiłki spożywają w jadalni. Należy zaangażować odźwiernego,
//    pilnującego drzwi jadalni i pozwalającego przebywać w niej jednocześnie co najwyżej N-1
//    filozofom. Dzięki temu co najmniej dwom filozofom, siedzącym przy stole, brakuje co
//    najmniej jednego sąsiada, a zatem co najmniej jeden filozof może jeść (dlaczego?).
//    Każdy filozof ma wyświetlać odpowiednie komunikaty, informujące o: czasie medytacji,
//    wejściu do jadalni, czasie jedzenia, wyjściu z jadalni.
//
//    Wskazówka. Przedstaw filozofów jako wątki (każdy filozof w pętli naprzemiennie medytuje i posila
//    się), sekcją krytyczną jest jedzenie, a zasobami dzielonymi są pałeczki do ryżu. Wątki są
//    ponumerowane od 0 do N-1, co odpowiada stałym miejscom filozofów przy stole i wykonują się
//    współbieżnie. Użycie każdej pałeczki jest kontrolowane przez semafor binarny, a odźwierny jest
//    reprezentowany przez semafor ogólny z wartością początkową N-1.

// -------------------------------------------------------------------------

// ----------------------------
// -      My Extra Notes      -
// ----------------------------

//    Problem jedzących filozofów polega na zsynchronizowaniu działań pięciu (ogólnie N) filozofów, którzy
//    zajmują się wyłącznie myśleniem i jedzeniem, nie komunikują się w żaden sposób. Filozofowie jedzą
//    przy okrągłym stole, na którym stoi pięć talerzy, między kolejnymi talerzami leży jedna pałeczka. Na
//    środku stołu stoi duży talerz z nieograniczoną ilością ryżu. Do jedzenia potrzebne są oczywiście dwie
//    pałeczki. Rozwiązanie nie powinno wyróżniać żadnego z filozofów.

//    Q: Oczywista z pozoru strategia, polegająca na poczekaniu, aż obie pałeczki będą wolne, może spowodować
//       zagłodzenie dwóch filozofów. Dlaczego?
//    A: Każdy filozof może zająć jedną pałęczke co prowadzi do deadlocku.

//    Q: (...) dzięki temu co najmniej dwom filozofom, siedzącym przy stole, brakuje co najmniej jednego sąsiada, a
//       zatem co najmniej jeden filozof może jeść. Dlaczego?
//    A: Zbiór [n] filozofów nie może podnieść zbioru [n+1] pałeczek, tak aby się zablokować.
//       Z Zasady Szufladkowej Dirichleta, z całą pewnością będzie filozof, który otrzyma dwie pałeczki.

// -------------------------------------------------------------------------

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
