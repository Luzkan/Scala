// 2. Napisz funkcję filter: [A](xs: List[A])(p: A => Boolean) List[A] wykorzystując funkcjonał
//    List.foldRight.
//    np. filter (List(2,7,1,3,7,8,4,1,6,9)) (_ > 3) == List(7, 7, 8, 4, 6, 9)

// Folds Functions Intuition:
//    1)
//      $ List(1, 3, 8).foldLeft(100)(_ - _)
//      > ((100 - 1) - 3) - 8 == 88
//      $ List(1, 3, 8).foldRight(100)(_ - _)
//      > 1 - (3 - (8 - 100)) == -94
//    2)
//      $ val donuts: Seq[String] = Seq("Plain", "Strawberry", "Glazed")
//      $ donuts.foldRight("")((a, b) => a + " Donut " + b)
//      > "Plain" + "Donut" + ("Strawberry" + "Donut" + ("Glazed" + "Donut") + ""))
//      > Plain Donut Strawberry Donut Glazed Donut
//    3)
//      $ val l = List(1, 2, 3, 4, 5)
//      $ l.foldRight(0)((elem: Int, acc: Int) => {println(acc+"+"+elem); acc + elem})
//      > 0+5
//      > 5+4
//      > 9+3
//      > 12+2
//      > 14+1
//      > val res0: Int = 15
//      $ l.foldRight(List[Int]())((elem: Int, acc: List[Int]) => {println(elem+"::"+acc); elem :: acc})
//      > 5::List()
//      > 4::List(5)
//      > 3::List(4, 5)
//      > 2::List(3, 4, 5)
//      > 1::List(2, 3, 4, 5)
//      > val res1: List[Int] = List(7, 7, 8, 4, 6, 9)
//
//      $ l.foldLeft(0)((acc: Int, elem: Int) => {println(acc+"+"+elem); acc + elem})
//      $ l.foldLeft(List[Int]())((acc: List[Int], elem: Int) => elem :: acc)
//
// FoldRight function as provided on the lecture:
//    def foldRight[A,B](f: A => B => B)(acc: B)(xs: List[A]): B = xs match {
//      case h::t => f (h) (foldRight (f) (acc) (t))
//      case Nil => acc
//    }

// https://stackoverflow.com/questions/52429195/trying-to-write-a-generic-filter-function-in-scala/52431718

def filter[A](xs: List[A])(p: A => Boolean): List[A] = {
  xs.foldRight[List[A]](Nil)((elem, acc) => if(p(elem)) elem :: acc else acc)
//  xs.foldRight[List[A]](Nil)((elem, acc) => if(p(elem)) {println("elem:"+elem+" | acc:"+acc); elem :: acc} else {println("elem:"+elem+" | acc:"+acc); acc})


filter(List(2,7,1,3,7,8,4,1,6,9))(_ > 3) == List(7, 7, 8, 4, 6, 9)  // true
filter(List(5, 6, 7, 8, 9))(_ < 4) == Nil                           // true
filter(List(5, 6, 7, 8, 9))(_ > 4) == List(5, 6, 7, 8, 9)           // true
filter(List(5, 6, 7, 8, 9))(_ < 7) == List(5, 6)                    // true
filter(List(-3, -2, -1, 0, 2))(_ > -2) == List(-1, 0, 2)            // true
filter(List('A', 'B', 'C', 'D'))(_ > 'B') == List('C', 'D')         // true
filter(List('a', 'b', 'A', 'B'))(_ < 'b') == List('a', 'A', 'B')    // true
filter[Char](Nil)(_ > 'b') == Nil                                   // true
