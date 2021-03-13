//  2. Napisz funkcję ends[A](xs: List[A]): (A, A), zwracającą parę, zawierającą pierwszy
//  i ostatni element zadanej listy,
//  np. ends(List(1, 3, 5, 6, 9)) == (1,9)
//      ends(List("Ala", "ma", "kota")) == ("Ala", "kota")
//      ends(List(1)) == (1,1)
//      ends(Nil) =>> wyjątek NoSuchElementException: empty list

def ends[A](xs: List[A]): (A, A) =
  if (xs == Nil) throw new NoSuchElementException("empty list")
  else if (xs.tail == List() || xs.tail == Nil)  (xs.head, xs.head)
  else if (xs.tail.tail == Nil)  (xs.head, xs.tail.head)
  else ends(xs.head :: xs.tail.tail)

ends(List(1, 3, 5, 6, 9)) == (1,9)                    // res0 = true
ends(List(1)) == (1,1)                                // res1 = true
ends(List("Ala", "ma", "kota")) == ("Ala", "kota")    // res2 = true
ends(Nil)                                             // empty list
