// 1. Na wykładzie zostały zdefiniowane drzewa binarne:

sealed trait BT[+A]
case object Empty extends BT[Nothing]
case class Node[+A](elem:A, left:BT[A], right:BT[A]) extends BT[A]
val t = Node(1, Node(2, Empty, Node(3, Empty, Empty)), Empty)

//   Napisz funkcję sumBT: BT[Int] => Int, która oblicza sumę liczb całkowitych,
//   przechowywanych w węzłach drzewa, np. sumBT(t) == 6

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ------------------------
// -    My Extra Notes    -
// ------------------------

// Visualisation:
//          [1]         // Node(1, Node(2...), Empty)
//         /   \
//       [2]   [ ]      // Node(2, Empty, Node(3...))
//      /   \
//    [ ]   [3]         // Node(3, Empty, Empty)
//         /   \
//       [ ]   [ ]

// ----------------------------
// -      My Extra Tests      -
// ----------------------------

// Extra Test Trees:
val tree_empty = Empty
val tree_root = Node(5, Empty, Empty)
val tree_zero = Node(4, Node(-2, Empty, Empty), Node(-2, Empty, Empty))

// -------------------------------------------------------------------------

def sumBT[A](bt: BT[Int]): Int = bt match {
  case Node(int, left, right) => int + sumBT(left) + sumBT(right)
  case Empty => 0
}

sumBT(t) == 6           // true
sumBT(tree_empty) == 0  // true
sumBT(tree_root) == 5   // true
sumBT(tree_zero) == 0   // true