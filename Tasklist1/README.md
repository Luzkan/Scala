<br />
<p align="center">
  <h2 align="center">Functional and Concurrent Programming</h2>
  <p align="center">
    <a href="../README.md">Main README.md</a>
    ·
    <a href="./README.md"><strong>Answers for Task List #1</strong></a>
    ·
    <a href="./tasklist1.pdf">TaskList1.pdf</a>
  </p>
</p>

---

### Disclaimer: In the tasks below only `head` and `tail` could be used.

### **Task #1**

##### Return the sum of the whole list

```scala
val suma: List[Double] => Double = xs =>
  if (xs == Nil) 0.0
  else xs.head + suma(xs.tail)
```

```scala
suma(Nil) == 0.0                // True
suma(List(-1, 2, 3)) == 4.0     // True
suma(List(5.6)) == 5.6          // True
suma(List(2, 3)) == 5.0         // True
suma(List(0.2, 0.6)) == 0.8     // True
```

### **Task #2**

##### Return a pair - first & last element of a list

```scala
def ends[A](xs: List[A]): (A, A) =
  if (xs == Nil) throw new NoSuchElementException("empty list")
  else if (xs.tail == List() || xs.tail == Nil)  (xs.head, xs.head)
  else if (xs.tail.tail == Nil)  (xs.head, xs.tail.head)
  else ends(xs.head :: xs.tail.tail)
```

```scala
ends(List(1, 3, 5, 6, 9)) == (1,9)                    // res0 = true
ends(List(1)) == (1,1)                                // res1 = true
ends(List("Ala", "ma", "kota")) == ("Ala", "kota")    // res2 = true
ends(Nil)                                             // empty list
```

### **Task #3**

##### Return a Boolean whether the list is sorted or not

```scala
val posortowana: List[Int] => Boolean = xs =>
  if (xs == Nil || xs.tail == Nil) true
  else (xs.head <= xs.tail.head) && posortowana(xs.tail)
```

```scala
posortowana(List(1))                    // res0 = True
posortowana(List(1, 3))                 // res1 = True
posortowana(List(1, 3, 5))              // res2 = True
posortowana(List(3, 1))                 // res3 = False
posortowana(List(3, 1, 5))              // res4 = False
posortowana(List(5, 3, 1))              // res5 = False
posortowana(List(1,3,3,5,6,7)) == true  // res6 = True
posortowana(List())                     // res7 = Empty List
```

### **Task #4**

##### Return merged string based on a given list with a given separator

```scala
val glue: (List[String], String) => String = (xs, separator) =>
  if (xs == Nil) ""
  else if (xs.tail == Nil) xs.head
  else xs.head + separator + glue(xs.tail, separator)
```

```scala
glue(List("To", "jest", "napis"), "-") == "To-jest-napis"
// res0 = True

glue(List("To", "jest", "bardzo", "dlugi", "napis"), "-") == "To-jest-bardzo-dlugi-napis"
// res1 = True

glue(Nil, "-") == ""
// res2 = True

glue(List("Wyraz"), "-") == "Wyraz"
// res3 = True

glue(List("Wyraz1", "Wyraz2"), "-") == "Wyraz1-Wyraz2"
// res4 = True
```
