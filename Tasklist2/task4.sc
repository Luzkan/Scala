import scala.annotation.tailrec

// 4. Napisz funkcję replicate: List[Int] => List[Int], która z danej listy liczb naturalnych tworzy
//    listę, w której każdy element wejściowej listy jest tyle razy powtórzony, jaką ma wartość,
//    np. replicate (List(1,0,4,-2,3)) == List(1, 4, 4, 4, 4, 3, 3, 3)

val replicate: List[Int] => List[Int] = xs => {

  // Number repeater (without for loop to keep things functional)
  def replicateRepeater(number: Int, iter: Int): List[Int] = iter match {
    // We only repeat positive numbers
    case x if x > 0 => number :: replicateRepeater(number, iter-1)
    case _ => Nil
  }

  @tailrec
  def replicateTailrec(xs: List[Int], accum: List[Int]): List[Int] = {
    xs match {
      // Return our result when finished (accumulator holds our result)
      case Nil => accum
      // Recursive call on tail and appending repeated number to accumulator
      case head :: tail => replicateTailrec(tail, accum ::: replicateRepeater(head, head))
    }
  }

  replicateTailrec(xs, Nil)
}

replicate(List(1,0,4,-2,3)) == List(1, 4, 4, 4, 4, 3, 3, 3)  // true
replicate(List(-3)) == List()                                // true
replicate(List(0)) == List()                                 // true
replicate(List(1, 1)) == List(1, 1)                          // true
replicate(List()) == List()                                  // true


// Explanation:
//    @Note: This is not tail-recursive, but easier to understand
//    val replicate:List[Int] => List[Int] = xs => {
//      def replicateRepeater(number: Int, i: Int): List[Int] = i match {
//        case x if x > 0 => number :: replicateRepeater(number, i-1)
//        case _ => Nil
//      }
//
//      xs match {
//        case Nil => Nil
//        case head :: tail => replicateRepeater(head, head) ::: replicate(tail)
//      }


// -----------------------
// Update #1:

val replicate: List[Int] => List[Int] = xs => {

  // Number repeater (without for loop to keep things functional)
  def replicateRepeater(number: Int, iter: Int): List[Int] =
    if (iter > 0) number :: replicateRepeater(number, iter-1) else Nil


  @tailrec
  def replicateTailrec(xs: List[Int], accum: List[Int]): List[Int] = {
    xs match {
      // Return our result when finished (accumulator hold the result)
      case Nil => accum
      // Recursive call on tail and appending repeated number to accumulator
      case head :: tail => replicateTailrec(tail, accum ::: replicateRepeater(head, head))
    }
  }

  replicateTailrec(xs, Nil)
}