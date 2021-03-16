<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #2</strong></a>
    ·
    <a href="./tasklist2.pdf">TaskList2.pdf</a>
  </p>
</p>

---

### Disclaimer: Functions in Task #1, #2 and #3 has to be linear (linear complexity compared to the length of input list).

### Task #1
##### Write a function that takes N first elements of a list.
```scala
def take[A](n: Int, xs: List[A]): List[A] = {
  @tailrec
  // Accumulator will hold the final result
  def takeTailrec(n: Int, xs: List[A], accum: List[A]): List[A] = xs match {
    // When counter is positive - appending head to the end of accumulator
    // and creating a recursive call on tail of current "xs" list
    case head :: tail if n > 0 => takeTailrec(n-1, tail, accum ::: List(head))
    // We only repeat positive numbers
    case _ => accum
  }

  takeTailrec(n, xs, Nil)
}
```

##### Tests
``` scala
take(2, List(1,2,3,5,6)) == List(1, 2)       // true
take(-2, List(1,2,3,5,6)) == Nil             // true
take(8, List(1,2,3,5,6)) == List(1,2,3,5,6)  // true
take(1, List("a")) == List("a")              // true
take(0, List("a")) == List()                 // true
take(2, List("a")) == List("a")              // true
take(1, List()) == List()                    // true
take(3, List(1, 2, "c")) == List(1, 2, "c")  // true
take(-2, List(1, 2, 3)) == List()            // true
```

### Task #2
##### Write a function that drops N first elements of a list.
```scala
@tailrec
def drop[A](n: Int, xs: List[A]): List[A] = xs match {
  // Abandoning the head element and decreasing counter "n"
  case head :: tail if n > 0 => drop(n-1, tail)
  case _ => xs
}
```

##### Tests
```scala
drop(2, List(1,2,3,5,6)) == List(3,5,6)       // true
drop(-2, List(1,2,3,5,6)) == List(1,2,3,5,6)  // true
drop(8, List(1,2,3,5,6)) == Nil               // true
drop(1, List("a")) == List()                  // true
drop(0, List("a")) == List("a")               // true
drop(2, List("a")) == List()                  // true
drop(1, List()) == List()                     // true
drop(3, List(1, 2, "c", "d")) == List("d")    // true
drop(-2, List(1, 2, 3)) == List(1, 2, 3)      // true
```

### Task #3
##### Write a function that reverses a list.
```scala
def reverse[A](xs: List[A]): List[A] = {
  @tailrec
  def reverseTailrec(res: List[A], accum: List[A]): List[A] = accum match {
    // When accumulator is empty - the list is reversed
    case Nil => res
    // Taking current head from accumulator and prepending it to the result
    // Removing the head from accumulator by recursive call on the tail
    case head :: tail => reverseTailrec(head :: res, tail)
  }

  // Starting inner function "xs" as the accumulator value
  reverseTailrec(Nil, xs)
}
```

##### Tests:
```scala
reverse(List("Ala", "ma", "kota")) == List("kota", "ma", "Ala")  // true
reverse(List(1, 2, 3, 4)) == List(4, 3, 2, 1)                    // true
reverse(List()) == List()                                        // true
reverse(List("Ala", "ma")) == List("Ala", "ma")                  // false
```

##### Explanation:
```scala
// Examples:
$ "a" :: List("b", "c", "d")
> res0: List[String] = List(a, b, c, d)

$ List("a") :: List("b", "c", "d")
> res1: List[java.io.Serializable] = List(List(a), b, c, d)

$ List("a", "b", "c") ::: List("d", "e", "f")
> res2: List[String] = List(a, b, c, d, e, f)

// This is because `:::` is a method of `List` object and it prepends
// whole list to another list (joins them together).

// @Note: This is not tail-recursive, but easier to understand
def reverse[A](xs: List[A]): List[A] = xs match {
  case head :: tail => reverse(tail) ::: List(head)
  case Nil => Nil
}
```

### Task #4
##### Write a function that repeats N times the element where N is an integer in a list.
```scala
val replicate: List[Int] => List[Int] = xs => {

  // Number repeater (without for loop to keep things functional)
  def replicateRepeater(number: Int, iter: Int): List[Int] = iter match {
    // We only repeat positive numbers
    case x if x > 0 => number :: replicateRepeater(number, iter-1)
    case _ => Nil
  }

  @tailrec
  def replicateTailrec(xs: List[Int], accum: List[Int]): List[Int] = {
    xs match {
      // Return our result when finished (accumulator holds our result)
      case Nil => accum
      // Recursive call on tail and appending repeated number to accumulator
      case head :: tail => replicateTailrec(tail, accum ::: replicateRepeater(head, head))
    }
  }

  replicateTailrec(xs, Nil)
}
```

##### Tests:
```scala
replicate(List(1,0,4,-2,3)) == List(1, 4, 4, 4, 4, 3, 3, 3)  // true
replicate(List(-3)) == List()                                // true
replicate(List(0)) == List()                                 // true
replicate(List(1, 1)) == List(1, 1)                          // true
replicate(List()) == List()                                  // true
```

##### Explanation:
```scala
// @Note: This is not tail-recursive, but easier to understand
val replicate:List[Int] => List[Int] = xs => {
  def replicateRepeater(number: Int, i: Int): List[Int] = i match {
    case x if x > 0 => number :: replicateRepeater(number, i-1)
    case _ => Nil
  }

  xs match {
    case Nil => Nil
    case head :: tail => replicateRepeater(head, head) ::: replicate(tail)
  }
```

### Task #5
##### Calculate 3rd root of a double with epsilon approximation.
```scala
val root3: Double => Double = a => {
  @tailrec
  def root3_Tailrec(a: Double, xi: Double): Double = {
    // if x_{i}^3 - a| ≤  * |a|:
    //    then relative accuracy is achieved -> returning approximation x_{i}
    if ((xi*xi*xi - a).abs <= 10e-15 * a.abs) xi
    // else calculating next approximation:
    //    x_{i+1} = x_{i} + (a/x_{i}^2 - x_{i})/3
    else root3_Tailrec(a, xi + (a/(xi*xi) - xi)/3)
  }
  
  // x_{0} = a/3 for a > 1
  if (a > 1) root3_Tailrec(a, a/3)
  // x_{0} = a for a ≤ 1
  else root3_Tailrec(a, a)
}
```

##### Tests:
```scala
root3(125.0) == 5.0   // true
root3(64.0) == 4.0    // true
root3(8.0) == 2.0     // true
root3(0.0) == 0.0     // true
root3(-64.0) == -4.0  // true
root3(2.0) != 1.0     // true
```
