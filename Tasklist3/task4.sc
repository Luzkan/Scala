import scala.annotation.tailrec
// 4. Napisz funkcję splitAt: [A](xs: List[A])(n: Int) (List[A], List[A]), zwracającą parę równą
//    (xs take n, xs drop n), ale bez dwukrotnego przechodzenia listy xs.
//    np. splitAt(List('a','b','c','d','e')) (2) == (List('a', 'b'), List('c', 'd', 'e'))

def splitAt[A](xs: List[A])(n: Int): (List[A], List[A]) = {
  @tailrec
  def splitAtTailrec(xs: List[A], accum: List[A], n: Int): (List[A], List[A]) = xs match {
    // When the input xs is empty - the accumulator holds reversed result and
    //    because the xs is already empty can we can pass Nil as second list
    case Nil => (accum.reverse, Nil)
    // We are recursively iterating down and at each step "xs" head element is prepended
    //    to the accumulator. When iterator hits 0 or was given negative in the first place
    //    then we can return accumulator (reversed) and the rest of "xs" as second List.
    case head :: tail => if(n > 0) splitAtTailrec(tail, head :: accum, n-1) else (accum.reverse, xs)
  }

  splitAtTailrec(xs, Nil, n)
}

splitAt(List('a','b','c','d','e'))(2) == (List('a', 'b'), List('c', 'd', 'e'))   // true
splitAt(List('a','b','c','d','e'))(0) == ((List(), List('a','b','c','d','e')))   // true
splitAt(List('a','b','c','d','e'))(6) == ((List('a','b','c','d','e'), List()))   // true
splitAt(List('a','b','c','d','e'))(-2) == ((List(), List('a','b','c','d','e')))  // true
splitAt(List(1, 2, 3))(2) == (List(1, 2), List(3))                               // true
splitAt[Any](Nil)(0) == (Nil, Nil)                                               // true
