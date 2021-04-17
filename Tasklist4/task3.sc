// 3. Wykorzystaj foldBT do zdefiniowania:
//    a) sumy liczb całkowitych sumBTfold: BT[Int] => Int,
//       np.: sumBTfold(t) == 6
//    b) listy wartości pamiętanych w węzłach drzewa w obejściu:
//       - infiksowym - inorderBTfold[A](bt: BT[A]): List[A]
//       - prefiksowym - preorderBTfold[A](bt: BT[A]): List[A]
//       - postfiksowym - postorderBTfold[A](bt: BT[A]): List[A]
//       np.: inorderBTfold(t) == List(2, 3, 1)
//            preorderBTfold(t) == List(1, 2, 3)
//            postorderBTfold(t) == List(3, 2, 1)

// Tree definition from lecture:
sealed trait BT[+A]
case object Empty extends BT[Nothing]
case class Node[+A](elem:A, left:BT[A], right:BT[A]) extends BT[A]
val t = Node(1, Node(2, Empty, Node(3, Empty, Empty)), Empty)

// FoldBT from Task #2
def foldBT[A, B](f: A => (B, B) => B)(acc: B)(bt: BT[A]): B = bt match {
  case Node(node, child_left, child_right) => f(node)(foldBT(f)(acc)(child_left), foldBT(f)(acc)(child_right))
  case Empty => acc
}

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ------------------------
// -    My Extra Notes    -
// ------------------------

// Definitions from lecture:
// Note: Could be optimized.

//  def inorder[A](bt: BT[A]): List[A] =
//    bt match {
//      case Node(v,l,r) => inorder(l) ::: v::inorder(r)
//      case Empty => Nil
//    }

//  def preorder[A](bt: BT[A]): List[A] =
//    bt match {
//      case Node(v,l,r) => v::preorder(l) ::: preorder(r)
//      case Empty => Nil
//    }

//  def postorder[A](bt: BT[A]): List[A] =
//    bt match {
//      case Node(v,l,r) => postorder(l) ::: postorder(r) ::: List(v)
//      case Empty => Nil
//    }

// ----------------------------
// -      My Extra Tests      -
// ----------------------------

val tree_empty = Empty
val tree_root = Node(5, Empty, Empty)
val tree_zero = Node(4, Node(-2, Empty, Empty), Node(-2, Empty, Empty))

// -------------------------------------------------------------------------
// a)

def sumBTfold: BT[Int] => Int = bin_tree => foldBT((node: Int) => (child_left: Int, child_right: Int) => node + child_left + child_right)(0)(bin_tree)

sumBTfold(t) == 6           // true
sumBTfold(tree_empty) == 0  // true
sumBTfold(tree_root) == 5   // true
sumBTfold(tree_zero) == 0   // true

// -------------------------------------------------------------------------
// b1)

// Infix - recursively:
//    - left subtree first,
//    - then root,
//    - then right subtree
def inorderBTfold[A](bt: BT[A]): List[A] =
  foldBT((node: A) => (child_left: List[A], child_right: List[A]) => child_left ::: node :: child_right)(Nil)(bt)

inorderBTfold(t) == List(2, 3, 1)             // true
inorderBTfold(tree_empty) == List()           // true
inorderBTfold(tree_root) == List(5)           // true
inorderBTfold(tree_zero) == List(-2, 4, -2)   // true

// -------------------------------------------------------------------------
// b2)

// Prefix - recursively:
//    - root first,
//    - then left subtree,
//    - then right subtree
def preorderBTfold[A](bt: BT[A]): List[A] =
  foldBT((node: A) => (child_left: List[A], child_right: List[A]) => node :: child_left ::: child_right)(Nil)(bt)

preorderBTfold(t) == List(1, 2, 3)             // true
preorderBTfold(tree_empty) == List()           // true
preorderBTfold(tree_root) == List(5)           // true
preorderBTfold(tree_zero) == List(4, -2, -2)   // true

// -------------------------------------------------------------------------
// b3)

// Postfix - recursively:
//    - left subtree first,
//    - then right subtree,
//    - then root
def postorderBTfold[A](bt: BT[A]): List[A] =
  foldBT((node: A) => (child_left: List[A], child_right: List[A]) => child_left ::: child_right ::: List(node))(Nil)(bt)

postorderBTfold(t) == List(3, 2, 1)             // true
postorderBTfold(tree_empty) == List()           // true
postorderBTfold(tree_root) == List(5)           // true
postorderBTfold(tree_zero) == List(-2, -2, 4)   // true
