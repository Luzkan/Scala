<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #3</strong></a>
    ·
    <a href="./tasklist3.pdf">TaskList3.pdf</a>
  </p>
</p>

---

# **Task #1**

## Write a function that returns a Boolean whether an element is in a list

```scala
@tailrec
def existsA[A](xs: List[A])(p: A => Boolean): Boolean = xs match {
  case Nil => false
  case head :: tail => if(p(head)) true else existsA(tail)(p)
}

def existsB[A](xs: List[A])(p: A => Boolean): Boolean =
  xs.foldLeft(false)((acc, elem) => {println("acc:"+acc+" | elem:"+elem); acc || p(elem)})

def existsC[A](xs: List[A])(p: A => Boolean): Boolean =
  xs.foldRight(false)((elem, acc) => {println("elem:"+elem+" | acc:"+acc); acc || p(elem)})
```

### _Tests_

```scala
exists(A/B/C)(List(5, 1, 2, 3))(_ == 2)        // true
exists(A/B/C)(List(5, 1, 2, 3, 6, 7))(_ == 2)  // true
exists(A/B/C)(List("A", "B", "C"))(_ == "B")   // true
exists(A/B/C)(List(5, 1, 2, 3))(_ == 4)        // false
exists(A/B/C)(List("A", "B", "C"))(_ == 2)     // false
```

# **Task #2**

## Write a filter function with foldRight.

```scala
def filter[A](xs: List[A])(p: A => Boolean): List[A] =
  xs.foldRight[List[A]](Nil)((elem, acc) => if(p(elem)) elem :: acc else acc)
```

### _Tests_

```scala
filter(List(2,7,1,3,7,8,4,1,6,9))(_ > 3) == List(7, 7, 8, 4, 6, 9)  // true
filter(List(5, 6, 7, 8, 9))(_ < 4) == Nil                           // true
filter(List(5, 6, 7, 8, 9))(_ > 4) == List(5, 6, 7, 8, 9)           // true
filter(List(5, 6, 7, 8, 9))(_ < 7) == List(5, 6)                    // true
filter(List(-3, -2, -1, 0, 2))(_ > -2) == List(-1, 0, 2)            // true
filter(List('A', 'B', 'C', 'D'))(_ > 'B') == List('C', 'D')         // true
filter(List('a', 'b', 'A', 'B'))(_ < 'b') == List('a', 'A', 'B')    // true
filter[Char](Nil)(_ > 'b') == Nil                                   // true
```

## **Explanation:**

Folds Functions Intuition:

1. Example:

   ```scala
   List(1, 3, 8).foldLeft(100)(_ - _)
   > ((100 - 1) - 3) - 8 == 88

   List(1, 3, 8).foldRight(100)(_ - _)
   > 1 - (3 - (8 - 100)) == -94
   ```

2. Example:

   ```scala
   val donuts: Seq[String] = Seq("Plain", "Strawberry", "Glazed")
   donuts.foldRight("")((a, b) => a + " Donut " + b)

   > "Plain" + "Donut" + ("Strawberry" + "Donut" + ("Glazed" + "Donut") + ""))
   > Plain Donut Strawberry Donut Glazed Donut
   ```

3. Example:

   ```scala
   val l = List(1, 2, 3, 4, 5)

   l.foldRight(0)((elem: Int, acc: Int) => {println(acc+"+"+elem); acc + elem})
   > 0+5
   > 5+4
   > 9+3
   > 12+2
   > 14+1
   > val res0: Int = 15

   l.foldRight(List[Int]())((elem: Int, acc: List[Int]) => {println(elem+"::"+acc); elem :: acc})
   > 5::List()
   > 4::List(5)
   > 3::List(4, 5)
   > 2::List(3, 4, 5)
   > 1::List(2, 3, 4, 5)
   > val res1: List[Int] = List(7, 7, 8, 4, 6, 9)
   ```

# **Task #3**

## Write a function that removes the first found element given in as functional

```scala
def remove1[A](xs: List[A])(p: A => Boolean): List[A] =
  xs match {
    case Nil => Nil
    case head :: tail => if(p(head)) tail else head :: remove1(tail)(p)
  }


def remove2[A](xs: List[A])(p: A => Boolean): List[A] = {
  @tailrec
  def removeTailrec(xs: List[A], accum: List[A]): List[A] = xs match {
    case Nil => accum.reverse
    case head :: tail => if(p(head)) tail.reverse_:::(accum) else removeTailrec(tail, head :: accum)
  }
```

### _Tests:_

```scala
remove1(List(1, 2, 3, 2, 5))(_ == 2) == List(1, 3, 2, 5)            // true
remove1(List(1, 2, 3, 2, 5))(_ == 4) == List(1, 2, 3, 2, 5)         // true
remove1(List(1, -1, 1, -1))(_ == -1) == List(1, 1, -1)              // true
remove1(List('A', 'B', 'C', 'D'))(_ == 'C') == List('A', 'B', 'D')  // true
remove1(Nil)(_ == 0) == Nil                                         // true
remove1(List(4))(_ == 4) == Nil                                     // true

remove2(List(1, 2, 3, 2, 5))(_ == 2) == List(1, 3, 2, 5)            // true
remove2(List(1, 2, 3, 2, 5))(_ == 4) == List(1, 2, 3, 2, 5)         // true
remove2(List(1, -1, 1, -1))(_ == -1) == List(1, 1, -1)              // true
remove2(List('A', 'B', 'C', 'D'))(_ == 'C') == List('A', 'B', 'D')  // true
remove2(Nil)(_ == 0) == Nil                                         // true
remove2(List(4))(_ == 4) == Nil                                     // true
```

## **Explanation:**

This is very close to what I would like to achieve:

```scala
def remove1[A](xs: List[A])(p: A => Boolean): List[A] =
  xs.foldRight[List[A]](Nil)((elem, acc) => if(p(elem)) acc else elem :: acc)
```

All I need to do is to return just the tail after the condition is met, not recursively call repeated process on the tail -> just the tail.

```scala
def remove2[A](xs: List[A])(p: A => Boolean): List[A] = {
  @tailrec
  def removeTailrec(xs: List[A], accum: List[A]): List[A] = xs match {
    // When the input xs is empty - the accumulator holds reversed result
    case Nil => accum.reverse
    // Taking current head from input xs and if it meets condition it quits
    //    with reversed result from accumulator. Else it prepends the head
    //    to the current accumulator and continues with recursive call

    // Important Information:
    //   Q: Why do we reverse the list for instead of just appending to the end of list?
    //   A: Because append operation has time complexity of O(n)
    //      List(1, 2, 3) :+ 4     ->    List(1, 2, 3, 4)
    case head :: tail => if(p(head)) tail.reverse_:::(accum) else removeTailrec(tail, head :: accum)
  }

  removeTailrec(xs, Nil)
}
```

# **Task #4**

## Write a function that splits a list in two at a given index without double list traversing.

```scala
def splitAt[A](xs: List[A])(n: Int): (List[A], List[A]) = {
  @tailrec
  def splitAtTailrec(xs: List[A], accum: List[A], n: Int): (List[A], List[A]) = xs match {
    case Nil => (accum.reverse, Nil)
    case head :: tail => if(n > 0) splitAtTailrec(tail, head :: accum, n-1) else (accum.reverse, xs)
  }

  splitAtTailrec(xs, Nil, n)
}
```

### _Tests:_

```scala
splitAt(List('a','b','c','d','e'))(2) == (List('a', 'b'), List('c', 'd', 'e'))   // true
splitAt(List('a','b','c','d','e'))(0) == ((List(), List('a','b','c','d','e')))   // true
splitAt(List('a','b','c','d','e'))(6) == ((List('a','b','c','d','e'), List()))   // true
splitAt(List('a','b','c','d','e'))(-2) == ((List(), List('a','b','c','d','e')))  // true
splitAt(List(1, 2, 3))(2) == (List(1, 2), List(3))                               // true
splitAt[Any](Nil)(0) == (Nil, Nil)                                               // true
```

## **Explanation:**

```scala
def splitAt[A](xs: List[A])(n: Int): (List[A], List[A]) = {
  @tailrec
  def splitAtTailrec(xs: List[A], accum: List[A], n: Int): (List[A], List[A]) = xs match {
    // When the input xs is empty - the accumulator holds reversed result and
    //    because the xs is already empty can we can pass Nil as second list
    case Nil => (accum.reverse, Nil)
    // We are recursively iterating down and at each step "xs" head element is prepended
    //    to the accumulator. When iterator hits 0 or was given negative in the first place
    //    then we can return accumulator (reversed) and the rest of "xs" as second List.
    case head :: tail => if(n > 0) splitAtTailrec(tail, head :: accum, n-1) else (accum.reverse, xs)
  }

  splitAtTailrec(xs, Nil, n)
}
```
