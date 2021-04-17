import scala.annotation.tailrec

// 5. Na wykładzie zostały zdefiniowane grafy:
sealed trait Graphs[A]
case class Graph[A](succ: A => List[A]) extends Graphs[A]

val g = Graph((i: Int) =>
  i match {
    case 0 => List(3)
    case 1 => List(0, 2, 4)
    case 2 => List(1)
    case 3 => List(5)
    case 4 => List(0, 2)
    case 5 => List(3)
    case n => throw new NoSuchElementException(s"Graph g: node $n doesn't exist")
  })

//    Napisz funkcję: pathExists[A](g: Graph[A])(from: A, to: A): Boolean
//    sprawdzającą, czy istnieje droga pomiędzy zadanymi wierzchołkami grafu.
//    np. dla poniższego grafu g:
//       pathExists(g)(4, 1)
//      !pathExists(g)(0, 4)
//      !pathExists(g)(3, 0)
//       pathExists(g)(2,2)
//      !pathExists(g)(0,0)

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

// ------------------------
// -    My Extra Notes    -
// ------------------------

// Breadth Search given on the lecture with my explanations
def breadthSearch[A] (g: Graph[A]) (startNode: A): List[A] = {
  def search(visited: List[A]) (queue: List[A]): List[A] =
    queue match {
      case head :: tail =>
        // Skip looking in a node that we've already been in
        if (visited contains head) search(visited)(tail)
        else {
          // Prepends node to the result and makes a recursive call:
          //    Prepending the node to visited list
          //    Appending the reachable nodes to queue list
          head :: search(head :: visited)(tail ::: (g succ head))
        }
      case Nil => Nil
    }

  // Starts with Empty List of Visited Nodes and creates
  // queue of nodes that shall be visited starting w/ startNode
  search (Nil) (List(startNode))
}

// -------------------------------------------------------------------------

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

 pathExists(g)(4, 1)  // true  [4] -> [2] -> [1]
!pathExists(g)(0, 4)  // true
!pathExists(g)(3, 0)  // true
 pathExists(g)(2,2)   // true  [2] -> [1] -> [2]
!pathExists(g)(0,0)   // true
