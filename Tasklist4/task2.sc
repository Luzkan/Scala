// 2. Napisz funkcjonał
//    foldBT[A, B](f: A => (B, B) => B)(acc: B)(bt: BT[A]): B
//    uogólniający funkcję sumowania wartości z węzłów drzewa binarnego tak, jak funkcjonał
//    foldRight uogólnia funkcję sumowania elementów listy. Typ (B, B) to typ pary akumulatorów dla
//    lewego i prawego poddrzewa

// Tree definition from lecture:
sealed trait BT[+A]
case object Empty extends BT[Nothing]
case class Node[+A](elem:A, left:BT[A], right:BT[A]) extends BT[A]
val t = Node(1, Node(2, Empty, Node(3, Empty, Empty)), Empty)

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ------------------------
// -    My Extra Notes    -
// ------------------------

// FoldRight definition from lecture:
//    def foldRight[A,B](f: A => B => B)(acc: B)(xs: List[A]): B = xs match {
//      case h::t => f (h) (foldRight (f) (acc) (t))
//      case Nil => acc
//    }

// -------------------------------------------------------------------------

def foldBT[A, B](f: A => (B, B) => B)(acc: B)(bt: BT[A]): B = bt match {
  case Node(node, child_left, child_right) => f(node)(foldBT(f)(acc)(child_left), foldBT(f)(acc)(child_right))
  case Empty => acc
}