//  3.
//
//  a) Zdefiniuj abstrakcyjną klasę Zwierz, z dokładnie jednym niemodyfikowalnym polem imie. Jej
//	   konstruktor(y) ma(ją) umożliwiać tworzenie zwierzęcia z domyślnym imieniem (np. „bez imienia”)
//		 lub z imieniem, podanym jako argument.
//		 Klasa ma mieć cztery publiczne bezargumentowe metody: imie, rodzaj, dajGlos i toString, które
//		 odpowiednio zwracają: imię, rodzaj i charakterystyczny głos zwierzęcia.
//		 Metoda toString ma zwracać informację o zwierzęciu w poniższym formacie:
//		 <rodzaj zwierzęcia> <imię zwierzęcia> daje głos <charakterystyczny głos zwierzęcia>!
//		 Na przykład (dla psa):
//		 Pies Kruczek daje głos Hau, hau!
//		 Zdecyduj, które metody muszą być abstrakcyjne.
//
//	b) Zdefiniuj klasy publiczne dla kilku rodzajów zwierząt (co najmniej dwóch), dziedziczące
//		 z klasy Zwierz. Podobnie jak klasa Zwierz mają one umożliwiać tworzenie zwierzęcia na dwa sposoby:
//		 bez podanego imienia i z imieniem. Nie wolno dodawać żadnych nowych pól ani metod! Które
//		 metody trzeba zdefiniować, które zastąpić, a które odziedziczyć bez zmian?
//
//	c) Napisz program testujący TestZwierza (jako obiekt singletonowy), w którym należy utworzyć
//		 kolekcję kilku zwierząt (użyj kolekcji Vector) i wypisać informacje o tych zwierzętach (wykorzystaj
//		 wyrażenie for). Zaobserwuj efekt działania polimorfizmu inkluzyjnego i wiązania dynamicznego.
//		 W arkuszu elektronicznym (worksheet) program uruchamia się tak: TestZwierza.main(Array())
//
//	UWAGA. Klasy mają posiadać wyłącznie pola i metody opisane w specyfikacji. Proszę ściśle
//			 	 przestrzegać powyższej specyfikacji. Jest ona celowo bardzo restrykcyjna i dość sztuczna
//			   z powodów dydaktycznych. Chodzi o wymuszenie użycia pewnych konstrukcji języka Scala.


// -------------------------------------------------------------------------
// a)

abstract class Animal(val name: String = "Unnamed"){
	def voice(): String
	def species(): String = this.getClass.getSimpleName
	override def toString: String = s"${species()} $name gives a voice: ${voice()}"
}

// -------------------------------------------------------------------------
// b)

class Dog(name: String = "Unnamed") extends Animal(name) { override def voice(): String = "Hau, hau!" }
class Cat(name: String = "Unnamed") extends Animal(name) { override def voice(): String = "Meow, meow!" }

val dog1 = new Dog()
dog1.toString() == "Dog Unnamed gives a voice: Hau, hau!"	   // true
val dog2 = new Dog("Max")
dog1.toString() == "Dog Max gives a voice: Hau, hau!"  		 	 // true

val cat1 = new Cat()
cat1.toString() == "Cat Unnamed gives a voice: Meow, meow!"  // true
val cat2 = new Cat("Lilly")
cat2.toString() == "Cat Lilly gives a voice: Meow, meow!"		 // true

// -------------------------------------------------------------------------
// c)

object TestAnimals {
	val Animals: Vector[Animal] = Vector(
		new Dog(),
		new Dog("Max"),
		new Cat(),
		new Cat("Lilly")
	)

	def main(args: Array[String]): Unit = {
		for (animal <- Animals) { println(animal.toString())}
	}
}

TestAnimals.main(Array())
