import scala.annotation.tailrec
// 3. Napisz funkcję remove1: [A](xs: List[A])(p: A => Boolean) List[A] zwracający listę z tymi
//    samymi wartościami, co lista xs, z której usunięto pierwszy element spełniający predykat p.
//    np. remove1(List(1,2,3,2,5)) (_ == 2) == List(1, 3, 2, 5)
//
//    Należy napisać dwie wersje tej funkcji:
//      a) ze zwykłą rekursją,
//      b) z możliwie efektywną rekursją ogonową (użyj List.reverse_:::).


// This is very close to what I would like to achieve:
//
// def remove1[A](xs: List[A])(p: A => Boolean): List[A] =
//   xs.foldRight[List[A]](Nil)((elem, acc) => if(p(elem)) acc else elem :: acc)
//
// All I need to do is to return just the tail after condition is met, not
// recursively call repeated process on the tail -> just the tail.

def remove1[A](xs: List[A])(p: A => Boolean): List[A] =
  xs match {
    case Nil => Nil
    case head :: tail => if(p(head)) tail else head :: remove1(tail)(p)
  }

remove1(List(1, 2, 3, 2, 5))(_ == 2) == List(1, 3, 2, 5)            // true
remove1(List(1, 2, 3, 2, 5))(_ == 4) == List(1, 2, 3, 2, 5)         // true
remove1(List(1, -1, 1, -1))(_ == -1) == List(1, 1, -1)              // true
remove1(List('A', 'B', 'C', 'D'))(_ == 'C') == List('A', 'B', 'D')  // true
remove1(Nil)(_ == 0) == Nil                                         // true
remove1(List(4))(_ == 4) == Nil                                     // true

def remove2[A](xs: List[A])(p: A => Boolean): List[A] = {
  @tailrec
  def removeTailrec(xs: List[A], accum: List[A]): List[A] = xs match {
    // When the input xs is empty - the accumulator holds reversed result
    case Nil => accum.reverse
    // Taking current head from input xs and if it meets condition it quits
    //    with reversed result from accumulator. Else it prepends the head
    //    to the current accumulator and continues with recursive call

    // Important Information:
    //   Q: Why do we reverse the list for instead of just appending to the end of list?
    //   A: Because append operation has time complexity of O(n)
    //      List(1, 2, 3) :+ 4     ->    List(1, 2, 3, 4)
    case head :: tail => if(p(head)) tail.reverse_:::(accum) else removeTailrec(tail, head :: accum)
  }

  removeTailrec(xs, Nil)
}

remove2(List(1, 2, 3, 2, 5))(_ == 2) == List(1, 3, 2, 5)            // true
remove2(List(1, 2, 3, 2, 5))(_ == 4) == List(1, 2, 3, 2, 5)         // true
remove2(List(1, -1, 1, -1))(_ == -1) == List(1, 1, -1)              // true
remove2(List('A', 'B', 'C', 'D'))(_ == 'C') == List('A', 'B', 'D')  // true
remove2(Nil)(_ == 0) == Nil                                         // true
remove2(List(4))(_ == 4) == Nil                                     // true


