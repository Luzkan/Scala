import scala.math.abs
// 2. Klasa BankAccount jest zdefiniowana następująco:

class BankAccount(initialBalance : Double) {
	private[this] var balance = initialBalance
	def checkBalance = balance
	def deposit(amount : Double) = { balance += amount; balance}
	def withdraw(amount : Double) = { balance -= amount; balance}
	override def toString = "%.2f".format(balance)
}

// 	W podpunktach a) i b) należy utworzyć odpowiednie konto i przeprowadzić na nim testy.
//
//		a) Zdefiniuj klasę CheckingAccount, rozszerzającą klasę BankAccount, w której pobierana jest opłata w
//			 wysokości 1$ za każdą wpłatę i wypłatę z konta. Zmodyfikuj odpowiednio metody deposit i withdraw.
//
// 		b) Zdefiniuj klasę SavingsAccount, rozszerzającą klasę BankAccount, w której co miesiąc do konta
// 			 dodawane jest oprocentowanie 1%, a w przypadku debetu pobierana jest opłata w wysokości 1% . Nie
// 			 chodzi tu o korzystanie z kalendarza. Dodaj metodę earnMonthlyInterest: Unit; jej użycie oznacza, że
// 			 upłynął miesiąc.
// 			 Trzy transakcje miesięcznie są bezpłatne, za pozostałe pobierana jest opłata w wysokości 1$.
// 			 Zmodyfikuj odpowiednio metody deposit i withdraw.


// -------------------------------------------------------------------------
// a)

class CheckingAccount(initialBalance: Double) extends BankAccount(initialBalance) {
	override def deposit(amount: Double): Double = { super.withdraw( 1 ); super.deposit(amount) }
	override def withdraw(amount: Double): Double = { super.withdraw( 1 ); super.withdraw(amount) }
}


val account = new CheckingAccount(1000)
account.checkBalance 					 // res0: Double = 1000.0
account.toString()						 // res1: String = 1000,00
account.deposit(100)	 // res2: Double = 1099.0
account.withdraw(100)	 // res3: Double = 998.0
account.toString()						 // res4: String = 998,00

// -------------------------------------------------------------------------
// b)

class SavingsAccount(initialBalance: Double) extends BankAccount(initialBalance) {

	private var transactionsDoneThisMonth = 0

	override def deposit(amount: Double): Double = {
		transactionsDoneThisMonth += 1
		if (transactionsDoneThisMonth > 3){ super.withdraw( 1 ); super.deposit(amount) }
		else { super.deposit(amount) }
	}
	override def withdraw(amount: Double): Double = {
		transactionsDoneThisMonth += 1
		if (transactionsDoneThisMonth > 3){ super.withdraw( amount+1.0 ) }
		else  { super.withdraw(amount)  }
	}

	def earnMonthlyInterest(): Unit = {
		transactionsDoneThisMonth = 0
		if (this.checkBalance > 0 ) { super.deposit(0.01 * this.checkBalance) }
		else { super.withdraw(0.01 * this.checkBalance) }
	}
}


val account = new SavingsAccount(1000)
account.checkBalance 					 // res5: Double = 1000.0
account.earnMonthlyInterest()
account.toString()						 // res6: String = 1010,00
account.deposit(100)	 // res7: Double = 1110.0
account.withdraw(100)	 // res8: Double = 1010.0
account.withdraw(100)	 // res9: Double = 910.0
account.deposit(100)	 // res10: Double = 1009.0
account.toString()						 // res12: String = 1009,00


// -------------------------------------------------------------------------
// -------------------------------------------------------------------------
// My implementation of these classes (using new methods / more variables in a class)
// 		before I read the final notice-comment in the task list that classes should
// 		use only variables & methods given in the task.
// 	  EDIT: This constraint was only for Task 3. I'm leaving this 'legacy code' here anyway though.

class MyCheckingAccount(initialBalance: Double) extends BankAccount(initialBalance) {

	// Transaction fee is fixed to withdraw 1$ from the account
	private val FEE = 1

	// Preventing any potential problem related to fee value - taking the abs
	// value out of the `fee` variable in case one, by mistake, sets it to negative
	def transactionFee(): Double = { super.withdraw( abs(FEE)) }

	override def deposit(amount: Double): Double = { transactionFee(); super.deposit(amount) }
	override def withdraw(amount: Double): Double = { transactionFee(); super.withdraw(amount) }

}

class MySavingsAccount(initialBalance: Double) extends BankAccount(initialBalance) {

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
		if (this.checkBalance > 0 ) { super.deposit(MONTHLY_INTEREST * this.checkBalance) }
		else { super.withdraw(MONTHLY_INTEREST * this.checkBalance) }
	}

}
