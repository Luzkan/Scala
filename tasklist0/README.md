<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>

  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #0</strong></a>
    ·
    <a href="./tasklist0.pdf">TaskList0.pdf</a>
  </p>
</p>

---

### Disclaimer: In the task below only `head` and `tail` could be used.

### Task #1
##### Return last element of a list
```scala
@tailrec
def last[A](xs: List[A]) : A =
  if (xs == Nil) throw new NoSuchElementException("last of empty list")
  else if (xs.tail == Nil) xs.head
  else last(xs.tail)
```

``` scala
last(List(1, 9, 5, 6, 3))
last(List(5))
last(List("Hej", "3", "Ok"))
last(Nil)
last(List())
last(List(Nil))
```

##### Bonus (but illegal due to the task condition) solution
```scala
@tailrec
def last_match[A](xs: List[A]) : A = xs match {
  case h :: Nil => h
  case _ :: tail => last_match(tail)
  case _ => throw new NoSuchElementException
}
```
