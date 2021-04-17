import scala.annotation.tailrec

// 1. Napisz funkcję take[A](n: Int, xs: List[A]): List[A],
//    gdzie take(k, List(x1, ..., xn)) == List(x1, ..., xk),
//    np. take(2, List(1,2,3,5,6)) == List(1,2)
//        take(-2, List(1,2,3,5,6)) == Nil
//        take(8, List(1,2,3,5,6)) == List(1,2,3,5,6)

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

take(2, List(1,2,3,5,6)) == List(1, 2)       // true
take(-2, List(1,2,3,5,6)) == Nil             // true
take(8, List(1,2,3,5,6)) == List(1,2,3,5,6)  // true
take(1, List("a")) == List("a")              // true
take(0, List("a")) == List()                 // true
take(2, List("a")) == List("a")              // true
take(1, List()) == List()                    // true
take(3, List(1, 2, "c")) == List(1, 2, "c")  // true
take(-2, List(1, 2, 3)) == List()            // true


// -----------------------
// Update #1:

def take[A](n: Int, xs: List[A]): List[A] = {
  @tailrec
  // Accumulator will hold the final result
  def takeTailrec(n: Int, xs: List[A], accum: List[A]): List[A] =
    xs match {
      // When counter is positive - appending head to the end of accumulator
      // and creating a recursive call on tail of current "xs" list
      case head :: tail => if (n > 0) takeTailrec(n-1, tail, accum ::: List(head)) else accum
      // We repeat only on positive numbers
      case Nil => accum
  }

  takeTailrec(n, xs, Nil)
}