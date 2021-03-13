//  1. Napisz funkcję suma: List[Double] => Double, zwracającą sumę liczb z podanej listy,
//  np. suma(Nil) == 0.0
//      suma(List(-1, 2, 3)) == 4.0
//      suma(List(5.6)) == 5.6

val suma: List[Double] => Double = xs =>
  if (xs == Nil) 0.0
  else xs.head + suma(xs.tail)


suma(Nil) == 0.0
suma(List(-1, 2, 3)) == 4.0
suma(List(5.6)) == 5.6
suma(List(2, 3)) == 5.0
suma(List(0.2, 0.6)) == 0.8