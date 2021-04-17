import scala.annotation.tailrec

// 3. Napisz funkcję reverse[A](xs: List[A]): List[A], odwracającą zadaną listę w czasie liniowym
//    (bez użycia metody bibliotecznej reverse!),
//    np. reverse(List("Ala", "ma", "kota")) == List("kota", "ma", "Ala")

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


reverse(List("Ala", "ma", "kota")) == List("kota", "ma", "Ala")  // true
reverse(List(1, 2, 3, 4)) == List(4, 3, 2, 1)                    // true
reverse(List()) == List()                                        // true
reverse(List("Ala", "ma")) == List("Ala", "ma")                  // false

// Explanation:
//    Examples:
//      $ "a" :: List("b", "c", "d")
//      > res0: List[String] = List(a, b, c, d)
//
//      $ List("a") :: List("b", "c", "d")
//      > res1: List[java.io.Serializable] = List(List(a), b, c, d)
//
//      $ List("a", "b", "c") ::: List("d", "e", "f")
//      > res2: List[String] = List(a, b, c, d, e, f)
//    This is because `:::` is a method of `List` object and it prepends
//    whole list to another list (joins them together).
//
//    @Note: This is not tail-recursive, but easier to understand
//    def reverse[A](xs: List[A]): List[A] = xs match {
//      case head :: tail => reverse(tail) ::: List(head)
//      case Nil => Nil
//    }


// -----------------------
// Update #1:

def reverse[A](xs: List[A]): List[A] = {
  @tailrec
  def reverseTailrec(res: List[A], accum: List[A]): List[A] =
    accum match {
      // Taking current head from accumulator and prepending it to the result
      // Removing the head from accumulator by recursive call on the tail
      case head :: tail => reverseTailrec(head :: res, tail)
      // When accumulator is empty - the list is reversed
      case Nil => res
    }

  // Starting inner function with "xs" as the accumulator value
  reverseTailrec(Nil, xs)
}