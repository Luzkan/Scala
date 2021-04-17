// 4. Wykorzystując foldBT zdefiniuj funkcjonał
//    mapBT[A, B](f: A => B)(bt: BT[A]): BT[B]
//    aplikujący daną funkcję do wartości we wszystkich węzłach drzewa.
//    np. mapBT[Int, Int](v => 2 * v)(t) == Node(2,Node(4,Empty,Node(6,Empty,Empty)),Empty)

// Tree definition from lecture:
sealed trait BT[+A]
case object Empty extends BT[Nothing]
case class Node[+A](elem:A, left:BT[A], right:BT[A]) extends BT[A]
val t = Node(1, Node(2, Empty, Node(3, Empty, Empty)), Empty)

// FoldBT from Task #2
def foldBT[A, B](f: A => (B, B) => B)(acc: B)(bt: BT[A]): B = bt match {
  case Empty => acc
  case Node(node, child_left, child_right) => f(node)(foldBT(f)(acc)(child_left), foldBT(f)(acc)(child_right))
}

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ------------------------
// -    My Extra Notes    -
// ------------------------

// Definition from lecture:

//  def map[A,B](f: A=>B)(xs: List[A]): List[B] =
//    xs match {
//      case x :: xs => f(x) :: map(f)(xs)
//      case Nil => Nil
//    }

// ----------------------------
// -      My Extra Tests      -
// ----------------------------

// Extra Test Trees:
val tree_empty = Empty
val tree_root = Node(5, Empty, Empty)
val tree_zero = Node(4, Node(-2, Empty, Empty), Node(-2, Empty, Empty))

// -------------------------------------------------------------------------

def mapBT[A, B](f: A => B)(tree: BT[A]): BT[B] =
  foldBT[A, BT[B]]((node: A) => (child_left: BT[B], child_right: BT[B]) => Node(f(node), child_left, child_right))(Empty)(tree)

mapBT[Int, Int](v => 2 * v)(t) == Node(2, Node(4, Empty, Node(6, Empty, Empty)), Empty)            // true
mapBT[Int, Int](v => 2 * v)(tree_empty) == Empty                                                   // true
mapBT[Int, Int](v => 3 * v)(tree_root)  == Node(15, Empty, Empty)                                  // true
mapBT[Int, Int](v => v * v)(tree_zero)  == Node(16, Node(4, Empty, Empty), Node(4, Empty, Empty))  // true
mapBT[Int, Int](v => 0 * v)(tree_zero)  == Node(0,  Node(0, Empty, Empty), Node(0, Empty, Empty))  // true
mapBT[Int, Int](_ => 1)(tree_zero)      == Node(1,  Node(1, Empty, Empty), Node(1, Empty, Empty))  // true
mapBT[Int, Double](_ => 3.5)(t) == Node(3.5, Node(3.5, Empty, Node(3.5, Empty, Empty)), Empty)     // true
