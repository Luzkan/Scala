// 1. Zdefiniuj swoją klasę dla modyfikowalnej pary polimorficznej MyPair[A, B]. Ma ona mieć dwa pola
// 		modyfikowalne fst i snd z odpowiednimi akcesorami i mutatorami, oraz metodę toString, zwracającą
//		napis w formacie (fst, snd).


// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ------------------------
// -    My Extra Notes    -
// ------------------------

// Accessors are `get` methods
// Mutators are `set` methods

// Convention:
// 		- Accessors: the name of the method should be the name of the property
//								 sometimes methods for booleans could follow "isEmpty" pattern
// 		- Mutators:  name of the property with `_=` appended, ex:
//								 def foo _= (foo: Foo) { }

// In Scala that's how it is by default. To prevent that, one should add "private"
// before the variable in parameter

// -------------------------------------------------------------------------

class MyPair[A, B] {
	// Accessors
	private[this] var f: A = _
	private[this] var s: B = _

	// Variables
	def fst: A = f
	def snd: B = s

	// Mutators
	def fst_=(x: A) { f = x }
	def snd_=(x: B) { s = x }

	// Method
	override def toString: String = { f"($fst, $snd)" }
}

val p = new MyPair[Int, Double]
p.toString()	// res0: String = (null, null)
p.fst			// res1: Int = 0
p.snd			// res2: Double = 0.0
p.fst = 1		// // mutated p.fst
p.snd = 2.0		// // mutated p.snd
p.toString()	// res3: String = (1, 2.0)
p.fst			// res4: Int = 1
p.snd			// res5: Double = 2.0

// -------------------------------------------------------------------------

class MyPair[A, B](var fst: A, var snd: B){ override def toString: String = { f"($fst, $snd)" } }

val p = new MyPair(1, 2.0)
p.toString()	// res0: String = (null, null)
p.fst			// res1: Int = 0
p.snd			// res2: Double = 0.0
p.fst = 3		// // mutated p.fst
p.snd = 2.5		// // mutated p.snd
p.toString()	// res3: String = (3, 2.5)
p.fst			// res4: Int = 3
p.snd			// res5: Double = 2.5