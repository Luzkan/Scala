import scala.annotation.tailrec

// Disclaimer: I'm using Scala first time in my life.

//  1 .Napisz funkcję rekurencyjną last[A](xs: List[A]): A zwracającą ostatni element zadanej listy,
//  np. last (List(1, 9, 5, 6, 3)) == 3
//      last (List(5)) == 5
//      last(Nil) =>> java.util.NoSuchElementException: last of empty list

@tailrec
def last[A](xs: List[A]) : A =
  if (xs == Nil) throw new NoSuchElementException("last of empty list")
  else if (xs.tail == Nil) xs.head
  else last(xs.tail)

last(List(1, 9, 5, 6, 3))
last(List(5))
last(List("Hej", "3", "Ok"))
last(Nil)
last(List())
last(List(Nil))


// Bonus:
//    This solution was not allowed (yet)
@tailrec
def last_match[A](xs: List[A]) : A = xs match {
  case h :: Nil => h
  case _ :: tail => last_match(tail)
  case _ => throw new NoSuchElementException
}
