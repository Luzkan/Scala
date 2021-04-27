// 3. Polimorficzne leniwe drzewa binarne można zdefiniować następująco:
sealed trait lBT[+A]
case object LEmpty extends lBT[Nothing]
case class LNode[+A](elem: A, left: () => lBT[A], right: () => lBT[A]) extends lBT[A]
//    a) Napisz funkcję lBreadth: [A](ltree: lBT[A])Stream[A], tworzącą strumień zawierający
//       wszystkie wartości węzłów leniwego drzewa binarnego.
//       Wskazówka: zastosuj obejście drzewa wszerz, reprezentując kolejkę jako zwykłą listę.
//    b) Napisz funkcję lTree: (n: Int)lBT[Int] , która dla zadanej liczby naturalnej n konstruuje
//       nieskończone leniwe drzewo binarne z korzeniem o wartości n i z dwoma
//       poddrzewami lTree (2*n) oraz lTree( 2*n+1).
//    To drzewo jest przydatne do testowania funkcji z poprzedniego podpunktu.

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ------------------------
// -    My Extra Notes    -
// ------------------------

// Lazy Fibonacii w/ zip from lecture:
//    val fibs: LazyList[BigInt] = BigInt(0) #:: BigInt(1) #:: fibs.zip(fibs.tail).map(n => n._1 + n._2)

//    def foldBT[A, B](f: A => (B, B) => B)(acc: B)(bt: BT[A]): B = bt match {
//      case Node(node, child_left, child_right) => f(node)(foldBT(f)(acc)(child_left), foldBT(f)(acc)(child_right))
//      case Empty => acc

//    () => expr        // Abstract Function, value
//    (() => expr)()    // Evaluation of Expression

// ----------------------------
// -      My Extra Tests      -
// ----------------------------

// Extra Test Trees:
val tree_lecture = LNode(1, () => LNode(2, () => LEmpty, () => LNode(3, () => LEmpty, () => LEmpty)), () => LEmpty)
val tree_empty = LEmpty
val tree_root = LNode(5, () => LEmpty, () => LEmpty)
val tree_zero = LNode(4, () => LNode(-2, () => LEmpty, () => LEmpty), () => LNode(-2, () => LEmpty, () => LEmpty))


// -------------------------------------------------------------------------
// a)

def lBreadth[A](ltree: lBT[A]): LazyList [A] = {
	def lBreathInner(output: LazyList [A], tree: List[lBT[A]]): LazyList [A] =
		tree match {
			// Tree will grow up to 2 times the size of the `take n`.
			// Getting the node out of the tree into output LazyList (#::) and recursive calling on the rest of tree (merged lists)
			case LNode(node, child_left, child_right) :: tail => node #:: lBreathInner(output, tail ::: List(child_left(), child_right()))
			case LEmpty :: tail => lBreathInner(output, tail)
			case Nil => LazyList()
		}
	lBreathInner(LazyList(), List(ltree))
}

lBreadth(tree_root).toList == List(5)
lBreadth(tree_lecture).toList == List(1, 2, 3)
lBreadth(tree_zero).toList == List(4, -2, -2)
lBreadth(tree_empty).toList == Nil


// b)
//
//            [1]                                           [n]
//      [2]          [3]                      [2n]                      [2n+1]
//  [4]    [5]    [6]    [7]           [2(2n)]    [2(2n)+1]    [2(2n+1)]      [2(2n+1)+1]
// [] []  [] []  [] []  [] []         []     []  []       []  []       []    []         []

def lTree(n: Int): lBT[Int] = if (n > 0) LNode(n, () => lTree(2 * n), () => lTree(2 * n + 1)) else LEmpty

(lBreadth(lTree(1)) take 5).toList == List(1, 2, 3, 4, 5)
(lBreadth(lTree(2)) take 6).toList == List(2, 4, 5, 8, 9, 10)
(lBreadth(lTree(0))).toList == Nil




// -------------------------------------------------------------------------
// 3. a) Napisz funkcję lBreadth: [A](ltree: lBT[A])Stream[A], tworzącą strumień zawierający
//       wszystkie wartości węzłów leniwego drzewa binarnego.
//       Wskazówka: zastosuj obejście drzewa wszerz, reprezentując kolejkę jako zwykłą listę.

// a)
def lBreadth[A](ltree: lBT[A]): Stream[A] = {
  def lBeathInner(output: Stream[A], tree: List[lBT[A]]): Stream[A] =
    tree match {
      // Tree will grow up to 2 times the size of the `take n`.
      // Getting the node out of the tree into output stream (#::) and recursive calling on the rest of tree (merged lists)
      case LNode(node, child_left, child_right) :: tail => node #:: lBeathInner(output, tail ::: List(child_left(), child_right()))
      case LEmpty :: tail => lBeathInner(output, tail)
      case Nil => Stream.Empty
    }
  lBeathInner(Stream.Empty, List(ltree))
}

lBreadth(tree_root).toList == List(5)
lBreadth(tree_lecture).toList == List(1, 2, 3)
lBreadth(tree_zero).toList == List(4, -2, -2)
lBreadth(tree_empty).toList == Nil