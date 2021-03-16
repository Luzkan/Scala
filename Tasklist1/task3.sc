import scala.annotation.tailrec

//  3. Napisz funkcję posortowana: List[Int] => Boolean sprawdzającą, czy dana lista jest
//  posortowana niemalejąco,
//  np. posortowana(List(1,3,3,5,6,7)) == true

// v1.1:
val posortowana: List[Int] => Boolean = xs =>
  if (xs == Nil || xs.tail == Nil) true
  else (xs.head <= xs.tail.head) && posortowana(xs.tail)

// v1.0
val posortowana: List[Int] => Boolean = xs =>
  if (xs == Nil || xs.tail == List()) true
  else if (xs.head > xs.tail.head) false
  else posortowana(xs.tail.head :: xs.tail.tail)

posortowana(List(1))                    // res0 = true
posortowana(List(1, 3))                 // res1 = true
posortowana(List(1, 3, 5))              // res2 = true
posortowana(List(3, 1))                 // res3 = false
posortowana(List(3, 1, 5))              // res4 = false
posortowana(List(5, 3, 1))              // res5 = false
posortowana(List(1,3,3,5,6,7))          // res6 = true
posortowana(List())                     // res7 = true