<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #6</strong></a>
    ·
    <a href="./tasklist6.pdf">TaskList6.pdf</a>
  </p>
</p>

---

# **Task #1**

## Write a whileLoop function (without using computational effects) that takes two arguments: a condition and an expression, and accurately simulates the performance of a while loop (also syntactically). What type (and why) must the arguments and the result of the function be?

```scala
@tailrec
def whileLoop(condition: => Boolean)(body: => Unit): Unit =
  if (condition) { body; whileLoop(condition)(body) }


```

### _Tests_

```scala
$ var count = 0
$ whileLoop(count < 5) { println(count); count+= 1 }
> var count: Int = 0
> 0
> 1
> 2
> 3
> 4
```

# **Task #2**

## Write the function `lrepeat`, which for a given positive integer ki of the stream Stream `(x0, x1, x2, x3, ...)` returns a stream where each `xi` element is repeated `k` times

```scala
def lrepeat[A](k: Int)(xsl: LazyList[A]): LazyList[A] = {
	def lrepeatRepeater(number: A, iter: Int, tail: LazyList[A]): LazyList[A] =
		if (iter > 0) number #:: lrepeatRepeater(number, iter - 1, tail) else lrepeat(k)(tail)

	xsl match {
		case head #:: tail => if (k > 0) lrepeatRepeater(head, k, tail) else LazyList()
		case LazyList() => LazyList()
	}
}
```

### _Tests_

```scala
lrepeat(3) (LazyList('a','b','c','d')).force == LazyList('a', 'a', 'a', 'b', 'b', 'b', 'c', 'c', 'c', 'd', 'd', 'd')
(lrepeat (3) (LazyList.from(1)) take 12).toList == List(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4)
(lrepeat (5) (LazyList.from(2)) take 6).toList == List(2, 2, 2, 2, 2, 3)
lrepeat(2) (LazyList(1,2,1)).force == LazyList(1, 1, 2, 2, 1, 1)
(lrepeat (1) (LazyList.from(1)) take 1).toList == List(1)
lrepeat(5) (LazyList()).force == Nil
(lrepeat (0) (LazyList.from(1)) take 2).toList == Nil
(lrepeat (-1) (LazyList.from(1)) take 2).toList == Nil
(lrepeat (3) (LazyList()) take 2).toList == Nil
```

# **Task #3 a)**

## Write the function `lBreadth`, which creates a stream containing all the node values of the lazy binary tree

**Update #1:** Removed unused parameter, changed name of the second parameter to `queue`.

```scala
def lBreadth[A](ltree: lBT[A]): LazyList [A] = {
	def lBreathInner(queue: List[lBT[A]]): LazyList [A] =
		queue match {
			// Tree will grow up to 2 times the size of the `take n`.
			// Getting the node out of the tree into output LazyList (#::) and recursive calling on the rest of tree (merged lists)
			case LNode(node, child_left, child_right) :: tail => node #:: lBreathInner(tail ::: List(child_left(), child_right()))
			case LEmpty :: tail => lBreathInner(tail)
			case Nil => LazyList()
		}
	lBreathInner(List(ltree))
}
```

### _Tests_

```scala
lBreadth(tree_root).toList == List(5)           // true
lBreadth(tree_lecture).toList == List(1, 2, 3)  // true
lBreadth(tree_zero).toList == List(4, -2, -2)   // true
lBreadth(tree_empty).toList == Nil              // true
```

# **Task #3 b)**

## Write the function `lTree`: which for a given natural number `n` constructs an infinite lazy binary tree with root `n` and two subtrees `lTree (2 * n)` and `lTree (2 * n + 1)`.

```scala
def lTree(n: Int): lBT[Int] =
  if (n > 0) LNode(n, () => lTree(2 * n), () => lTree(2 * n + 1)) else LEmpty
```

### _Tests_

```scala
(lBreadth(lTree(1)) take 5).toList == List(1, 2, 3, 4, 5)      // true
(lBreadth(lTree(2)) take 6).toList == List(2, 4, 5, 8, 9, 10)  // true
(lBreadth(lTree(0))).toList == Nil                             // true
```
