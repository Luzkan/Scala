import scala.annotation.tailrec

//  4. Napisz funkcję glue: (List[String], String) => String, która na wejściu przyjmuje listę napisów
//  oraz napis będący separatorem i zwraca napis będący połączeniem napisów wejściowych
//  oddzielonych od siebie zadanym separatorem,
//  np. glue(List("To", "jest", "napis"), "-") == "To-jest-napis"
//      glue(Nil, "-") == ""

val glue: (List[String], String) => String = (xs, separator) =>
  if (xs == Nil) ""
  else if (xs.tail == Nil || xs.tail == List()) xs.head
  else if (xs.tail.tail == Nil) xs.head + separator + xs.tail.head
  else glue(xs.head + separator + xs.tail.head :: xs.tail.tail, separator)



glue(List("To", "jest", "napis"), "-") == "To-jest-napis"
// res0 = True

glue(List("To", "jest", "bardzo", "dlugi", "napis"), "-") == "To-jest-bardzo-dlugi-napis"
// res1 = True

glue(Nil, "-") == ""
// res2 = True

glue(List("Wyraz"), "-") == "Wyraz"
// res3 = True

glue(List("Wyraz1", "Wyraz2"), "-") == "Wyraz1-Wyraz2"
// res4 = True
