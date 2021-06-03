<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #4</strong></a>
    ·
    <a href="./tasklist4.pdf">TaskList4.pdf</a>
  </p>
</p>

---

# **Task #1**

## Write a function that sums up vertices from Binary Tree

```scala
def sumBT[A](bt: BT[Int]): Int = bt match {
  case Node(int, left, right) => int + sumBT(left) + sumBT(right)
  case Empty => 0
}
```

### _Tests_

```scala
sumBT(t) == 6           // true
sumBT(tree_empty) == 0  // true
sumBT(tree_root) == 5   // true
sumBT(tree_zero) == 0   // true
```

# **Task #2**

## Write a fold function for Binary Trees

```scala
def foldBT[A, B](f: A => (B, B) => B)(acc: B)(bt: BT[A]): B = bt match {
  case Node(node, child_left, child_right) => f(node)(foldBT(f)(acc)(child_left), foldBT(f)(acc)(child_right))
  case Empty => acc
}
```

## **Explanation:**

Take a brief look at the regular foldRight function:

```scala
def foldRight[A,B](f: A => B => B)(acc: B)(xs: List[A]): B = xs match {
  case Nil => acc
  case h::t => f (h) (foldRight (f) (acc) (t))
}
```

# **Task #3**

## Write a function which uses foldBT (from Task #2) to sum up the vertices from Binary Tree and another one that creates a list out of vertices from Binary Tree (infix, prefix, postfix)

```scala
def sumBTfold: BT[Int] => Int = bin_tree => foldBT((node: Int) => (child_left: Int, child_right: Int) => node + child_left + child_right)(0)(bin_tree)

```

```scala
def inorderBTfold[A](bt: BT[A]): List[A] =
  foldBT((node: A) => (child_left: List[A], child_right: List[A]) => child_left ::: node :: child_right)(Nil)(bt)
```

```scala
def preorderBTfold[A](bt: BT[A]): List[A] =
  foldBT((node: A) => (child_left: List[A], child_right: List[A]) => node :: child_left ::: child_right)(Nil)(bt)
```

```scala
def postorderBTfold[A](bt: BT[A]): List[A] =
  foldBT((node: A) => (child_left: List[A], child_right: List[A]) => child_left ::: child_right ::: List(node))(Nil)(bt)
```

### _Tests_

```scala
sumBTfold(t) == 6           // true
sumBTfold(tree_empty) == 0  // true
sumBTfold(tree_root) == 5   // true
sumBTfold(tree_zero) == 0   // true

inorderBTfold(t) == List(2, 3, 1)             // true
inorderBTfold(tree_empty) == List()           // true
inorderBTfold(tree_root) == List(5)           // true
inorderBTfold(tree_zero) == List(-2, 4, -2)   // true

preorderBTfold(t) == List(1, 2, 3)             // true
preorderBTfold(tree_empty) == List()           // true
preorderBTfold(tree_root) == List(5)           // true
preorderBTfold(tree_zero) == List(4, -2, -2)   // true

postorderBTfold(t) == List(3, 2, 1)             // true
postorderBTfold(tree_empty) == List()           // true
postorderBTfold(tree_root) == List(5)           // true
postorderBTfold(tree_zero) == List(-2, -2, 4)   // true
```

# **Task #4**

## Using foldBT create a functional map function

```scala
def mapBT[A, B](f: A => B)(tree: BT[A]): BT[B] =
  foldBT[A, BT[B]]((node: A) => (child_left: BT[B], child_right: BT[B]) => Node(f(node), child_left, child_right))(Empty)(tree)
```

### _Tests:_

```scala
$ mapBT[Int, Int](v => 2 * v)(t)
> Node(2, Node(4, Empty, Node(6, Empty, Empty)), Empty)
$ mapBT[Int, Int](v => 2 * v)(tree_empty)
> Empty
$ mapBT[Int, Int](v => 3 * v)(tree_root)
> Node(15, Empty, Empty)
$ mapBT[Int, Int](v => v * v)(tree_zero)
> Node(16, Node(4, Empty, Empty), Node(4, Empty, Empty))
$ mapBT[Int, Int](v => 0 * v)(tree_zero)
> Node(0, Node(0, Empty, Empty), Node(0, Empty, Empty))
$ mapBT[Int, Int](_ => 1)(tree_zero)
> Node(1, Node(1, Empty, Empty), Node(1, Empty, Empty))
$ mapBT[Int, Double](_ => 3.5)(t)
> Node(3.5, Node(3.5, Empty, Node(3.5, Empty, Empty)), Empty)
```

# **Task #5**

## Write a function to check whether a path between two vertices in a Graph exist

```scala
def pathExists[A](graph: Graph[A])(from: A, to: A): Boolean = {
  @tailrec
  def search(visited: List[A])(queue: List[A]): Boolean =
    queue match {
      case head :: tail =>
        if (visited contains head) search(visited)(tail)
        else head == to || search(head :: visited)(tail ::: (graph succ head))
      case Nil => false
    }
  search(Nil)(graph succ from)
}
```

### _Tests:_

```scala
 pathExists(g)(4, 1)  // true  [4] -> [2] -> [1]
!pathExists(g)(0, 4)  // true
!pathExists(g)(3, 0)  // true
 pathExists(g)(2,2)   // true  [2] -> [1] -> [2]
!pathExists(g)(0,0)   // true
```

## **Explanation:**

Take a brief look at the breadthSearch function given on the lecture:

```scala
def breadthSearch[A] (g: Graph[A]) (startNode: A): List[A] = {
  def search(visited: List[A]) (toVisit: List[A]): List[A] =
  toVisit match {
    case h::t => if (visited contains h) search(visited)(t)
                 else h::search(h::visited)(t ::: (g succ h))
    case Nil => Nil
  }
  search (Nil) (List(startNode))
}
```

And now here's my pathExist with comments:

```scala
def pathExists[A](graph: Graph[A])(from: A, to: A): Boolean = {
  @tailrec
  def search(visited: List[A])(queue: List[A]): Boolean =
    queue match {
      case head :: tail =>
        // Skip looking in a node that we've already been in
        if (visited contains head) search(visited)(tail)
        else
          // Checks if head is the destination node & makes recursive call:
          //    Prepending the node to visited list
          //    Appending reachable nodes to queue list
          head == to || search(head :: visited)(tail ::: (graph succ head))
      case Nil => false
    }
  // Starts with Empty List of Visited Nodes and creates
  // queue of nodes that shall be visited starting w/ "from"
  search(Nil)(graph succ from)
}
```
