<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #5</strong></a>
    ·
    <a href="./tasklist5.pdf">TaskList5.pdf</a>
  </p>
</p>

---

# **Task #1**
## Create a class for polymorphic pair. It should have accessors, mutators and toString method.
```scala
// #1
class MyPair[A, B](var fst: A, var snd: B){ override def toString: String = { f"($fst, $snd)" } }

// #2
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
```

### *Tests*
``` scala
val p = new MyPair[Int, Double]
p.toString()	// res0: String = (null, null)
p.fst			// res1: Int = 0
p.snd			// res2: Double = 0.0
p.fst = 1		// // mutated p.fst
p.snd = 2.0		// // mutated p.snd
p.toString()	// res3: String = (1, 2.0)
p.fst			// res4: Int = 1
p.snd			// res5: Double = 2.0
```


# **Task #2 a)**
## Extend BankAccount class in which 1$ will be withdrawn for every transaction.

```scala
class CheckingAccount(initialBalance: Double) extends BankAccount(initialBalance) {

	// Transaction fee is fixed to withdraw 1$ from the account
	private val FEE = 1

	// Preventing any potential problem related to fee value - taking the abs
	// value out of the `fee` variable in case one, by mistake, sets it to negative
	def transactionFee(): Double = { super.withdraw( abs(FEE)) }

	override def deposit(amount: Double): Double = { transactionFee(); super.deposit(amount) }
	override def withdraw(amount: Double): Double = { transactionFee(); super.withdraw(amount) }
}
```

### *Tests*
``` scala
val account = new CheckingAccount(1000)
account.checkBalance   // res0: Double = 1000.0
account.deposit(100)   // res1: Double = 1099.0
account.withdraw(100)  // res2: Double = 998.0
account.checkBalance   // res3: Double = 998.0
```


# **Task #2 b)**
## Extend BankAccount class in which 1$ will be withdrawn after 3rd transaction in a given month. Implement Interest logic.

```scala
class SavingsAccount(initialBalance: Double) extends BankAccount(initialBalance) {
	private val FEE = 1
	private val FREE_TRANSACTIONS_PER_MONTH = 3
	private var transactionsDoneThisMonth = 0
	private val MONTHLY_INTEREST = 0.01  // 1%

	def manageTransaction(): Unit = {
		transactionsDoneThisMonth += 1
		if (transactionsDoneThisMonth > FREE_TRANSACTIONS_PER_MONTH){ super.withdraw( abs(FEE)) }
	}

	override def deposit(amount: Double): Double = { manageTransaction(); super.deposit(amount) }
	override def withdraw(amount: Double): Double = { manageTransaction(); super.withdraw(amount) }

	def earnMonthlyInterest(): Unit = {
		transactionsDoneThisMonth = 0
		if (this.checkBalance >= 0){ super.deposit(MONTHLY_INTEREST * this.checkBalance) }
		else { super.deposit(MONTHLY_INTEREST * this.checkBalance) }
	}
}
```

### *Tests*
``` scala
val account = new SavingsAccount(1000)
account.checkBalance           // res4: Double = 1000.0
account.earnMonthlyInterest()
account.checkBalance           // res6: Double = 1000.2
account.deposit(100)           // res7: Double = 1100.2
account.withdraw(100)          // res8: Double = 1000.2
account.withdraw(100)          // res9: Double = 900.2
account.deposit(100)           // res10: Double = 999.2

val account2 = new SavingsAccount(0)
// Testing Case: Account Balance == 0.0
// Should do nothing
account2.checkBalance					 // res13: Double = 0.0
account2.earnMonthlyInterest()
account2.checkBalance					 // res15: Double = 0.0

// Testing Case: Account Balance < 0.0
// Should increase debt by 1%
account2.withdraw(10)	 // res16: Double = -10.0
account2.earnMonthlyInterest()
account2.checkBalance					 // res18: Double = -9.9
```


# **Task #3 a)**
## Create an abstract class Animal that has methods for voice, animal type and overridden custom toString. It shall have unmodifiable name in the constructor.
```scala
abstract class Animal(val name: String){
  def voice(): String
  def species(): String = this.getClass.getSimpleName
  override def toString: String = f"${species()} $name gives a voice: ${voice()}"
}
```

# **Task #3 b)**
## Define public methods for some animals. They should allow to create new instances of animals w/ or w/o name.
```scala
class Dog(name: String = "Unnamed") extends Animal(name) {
  override def voice() = "Hau, hau!"
}

class Cat(name: String = "Unnamed") extends Animal(name) {
  override def voice() = "Meow, meow!"
}
```

### *Tests*
``` scala
val dog1 = new Dog()
dog1.toString() == "Dog Unnamed gives a voice: Hau, hau!"    // true

val dog2 = new Dog("Max")
dog2.toString() == "Dog Max gives a voice: Hau, hau!"        // true

val cat1 = new Cat()
cat1.toString() == "Cat Unnamed gives a voice: Meow, meow!"  // true

val cat2 = new Cat("Lilly")
cat2.toString() == "Cat Lilly gives a voice: Meow, meow!"    // true
```

# **Task #3 c)**
## Create Test Class for Animals (w/ Vector).
```scala
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
```
