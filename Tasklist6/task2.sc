
// 2. Napisz funkcję lrepeat : [A] (k: Int) (stream: Stream[A]) (Stream[A]), który dla danej
//    dodatniej liczby całkowitej k i strumienia Stream(x0, x1, x2, x3, ... ) zwraca strumień,
//    w którym każdy element xi jest powtórzony k razy, np.
//    (lrepeat (3) (Stream.from(1)) take 12).toList == List(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4)


// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ------------------------
// -    My Extra Notes    -
// ------------------------

//  LazyList Is Preferred Over Stream
//
//  As its name suggests, a LazyList is a linked list whose elements are lazily evaluated.
//  An important semantic difference with Stream is that in LazyList both the head and the
//  tail are lazy, whereas in Stream only the tail is lazy.
//  - https://www.scala-lang.org/blog/2018/06/13/scala-213-collections.html

//  Inner Repeater taken from Task List #2, Task #4:
//      def replicateRepeater(number: Int, iter: Int): List[Int] =
//          if (iter > 0) number :: replicateRepeater(number, iter-1) else Nil


// ----------------------------
// -      My Extra Tests      -
// ----------------------------


// -------------------------------------------------------------------------

def lrepeat[A](k: Int)(xsl: LazyList[A]): LazyList[A] = {
	def lrepeatRepeater(number: A, iter: Int, tail: LazyList[A]): LazyList[A] =
		if (iter > 0) number #:: lrepeatRepeater(number, iter - 1, tail) else lrepeat(k)(tail)

	xsl match {
		case head #:: tail => if (k > 0) lrepeatRepeater(head, k, tail) else LazyList()
		case LazyList() => LazyList()
	}
}

lrepeat(3) (LazyList('a','b','c','d')).force == LazyList('a', 'a', 'a', 'b', 'b', 'b', 'c', 'c', 'c', 'd', 'd', 'd')
(lrepeat (3) (LazyList.from(1)) take 12).toList == List(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4)
(lrepeat (5) (LazyList.from(2)) take 6).toList == List(2, 2, 2, 2, 2, 3)
lrepeat(2) (LazyList(1,2,1)).force == LazyList(1, 1, 2, 2, 1, 1)
(lrepeat (1) (LazyList.from(1)) take 1).toList == List(1)
lrepeat(5) (LazyList()).force == Nil
(lrepeat (0) (LazyList.from(1)) take 2).toList == Nil
(lrepeat (-1) (LazyList.from(1)) take 2).toList == Nil
(lrepeat (3) (LazyList()) take 2).toList == Nil

// -------------------------------------------------------------------------
// 2. b) Napisz funkcję lrepeat : [A] (k: Int) (stream: Stream[A]) (Stream[A]), który dla danej
//       dodatniej liczby całkowitej k i strumienia Stream(x0, x1, x2, x3, ... ) zwraca strumień,
//       w którym każdy element xi jest powtórzony k razy.

def lrepeatStream[A](k: Int)(stream: Stream[A]): Stream[A] = {
  def lrepeatRepeater(number: A, iter: Int, tail: Stream[A]): Stream[A] =
    if (iter > 0) number #:: lrepeatRepeater(number, iter - 1, tail) else lrepeatStream(k)(tail)

  stream match {
    case head #:: tail => if (k > 0) lrepeatRepeater(head, k, tail) else Stream.Empty
    case Stream.Empty => Stream.Empty
  }
}

(lrepeatStream (3) (Stream.from(1)) take 12).toList == List(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4)
(lrepeatStream (5) (Stream.from(2)) take 6).toList == List(2, 2, 2, 2, 2, 3)
(lrepeatStream (1) (Stream.from(1)) take 1).toList == List(1)
(lrepeatStream (0) (Stream.from(1)) take 2).toList == Nil
(lrepeatStream (-1) (Stream.from(1)) take 2).toList == Nil
(lrepeatStream (3) (Stream.Empty) take 2).toList == Nil
