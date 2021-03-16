import scala.annotation.tailrec

// 2. Napisz funkcję drop[A](n: Int, xs: List[A]): List[A],
//    gdzie drop(k, List(x1 , ..., xn)) == List(xk+1, ..., xn),
//    np. drop(2, List(1,2,3,5,6)) == List(3,5,6)
//        drop(-2, List(1,2,3,5,6)) == List(1,2,3,5,6)
//        drop(8, List(1,2,3,5,6)) == Nil

@tailrec
def drop[A](n: Int, xs: List[A]): List[A] = xs match {
  // Abandoning the head element and decreasing counter "n"
  case head :: tail if n > 0 => drop(n-1, tail)
  case _ => xs
}

drop(2, List(1,2,3,5,6)) == List(3,5,6)       // true
drop(-2, List(1,2,3,5,6)) == List(1,2,3,5,6)  // true
drop(8, List(1,2,3,5,6)) == Nil               // true
drop(1, List("a")) == List()                  // true
drop(0, List("a")) == List("a")               // true
drop(2, List("a")) == List()                  // true
drop(1, List()) == List()                     // true
drop(3, List(1, 2, "c", "d")) == List("d")    // true
drop(-2, List(1, 2, 3)) == List(1, 2, 3)      // true
