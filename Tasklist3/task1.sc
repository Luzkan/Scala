import scala.annotation.tailrec
// 1. Napisz funkcję exists: [A] (xs: List[A]) (p: A => Boolean) Boolean.
//    exists (xs) (p) ma wartość logiczną zdania „Exist "x" in xs.p(x)”
//    np. exists (List(5,1,2,3)) (_ == 2) == true
//    Należy napisać trzy wersje tej funkcji:
//      a) z wykorzystaniem dopasowania do wzorca i rekursji,
//      b) z wykorzystaniem metody List.foldLeft,
//      c) z wykorzystaniem metody List.foldRight.

@tailrec
def existsA[A](xs: List[A])(p: A => Boolean): Boolean = xs match {
  case Nil => false
  case head :: tail => if(p(head)) true else existsA(tail)(p)
}

existsA(List(5, 1, 2, 3))(_ == 2)        // true
existsA(List(5, 1, 2, 3, 6, 7))(_ == 2)  // true
existsA(List("A", "B", "C"))(_ == "B")   // true
existsA(List(5, 1, 2, 3))(_ == 4)        // false
existsA(List("A", "B", "C"))(_ == 2)     // false

def existsB[A](xs: List[A])(p: A => Boolean): Boolean =
  xs.foldLeft(false)((acc, elem) => {println("acc:"+acc+" | elem:"+elem); acc || p(elem)})

existsB(List(5, 1, 2, 3))(_ == 2)          // true
// acc: false | elem: 5  -> p(5) is false
// acc: false | elem: 1  -> p(1) is false
// acc: false | elem: 2  -> p(2) is true
// acc: true  | elem: 3  -> p(3) is false
existsB(List(5, 1, 2, 3, 6, 7))(_ == 2)    // true
existsB(List("A", "B", "C"))(_ == "B")     // true
existsB(List(5, 1, 2, 3))(_ == 4)          // false
existsB(List("A", "B", "C"))(_ == 2)       // false

def existsC[A](xs: List[A])(p: A => Boolean): Boolean =
  xs.foldRight(false)((elem, acc) => {println("elem:"+elem+" | acc:"+acc); acc || p(elem)})

existsC(List(5, 1, 2, 3))(_ == 2)          // true
// acc: false | elem: 3  -> p(3) is false
// acc: false | elem: 2  -> p(2) is true
// acc: true  | elem: 1  -> p(1) is false
// acc: true  | elem: 5  -> p(5) is false
existsC(List(5, 1, 2, 3, 6, 7))(_ == 2)    // true
existsC(List("A", "B", "C"))(_ == "B")     // true
existsC(List(5, 1, 2, 3))(_ == 4)          // false
existsC(List("A", "B", "C"))(_ == 2)       // false