import scala.annotation.tailrec

// 5. Dla zadanej liczby rzeczywistej a oraz dokładności  można znaleźć pierwiastek
//    trzeciego stopnia z a wyliczając kolejne przybliżenia xi tego pierwiastka (metoda Newtona-Raphsona):
//      x_{0}   = a/3   dla    a > 1
//      x_{0}   = a     dla    a ≤ 1
//      x_{i+1} = x_{i} + (a/x_{i}^2 - x_{i}) / 3
//
//    Dokładność (względna) jest osiągnięta, jeśli |x_{i}^3 - a| ≤  * |a|
//
//    Napisz efektywną (wykorzystującą rekursję ogonową) funkcję root3: Double => Double,
//    która dla zadanej liczby a znajduje pierwiastek trzeciego stopnia z dokładnością względną  =10^{-15}

val root3: Double => Double = a => {
  @tailrec
  def root3_Tailrec(a: Double, xi: Double): Double = {
    // if x_{i}^3 - a| ≤  * |a|:
    //    then relative accuracy is achieved -> returning approximation x_{i}
    if ((xi*xi*xi - a).abs <= 10e-15 * a.abs) xi
    // else calculating next approximation:
    //    x_{i+1} = x_{i} + (a/x_{i}^2 - x_{i})/3
    else root3_Tailrec(a, xi + (a/(xi*xi) - xi)/3)
  }

  // x_{0} = a/3 for a > 1
  if (a > 1) root3_Tailrec(a, a/3)
  // x_{0} = a for a ≤ 1
  else root3_Tailrec(a, a)
}

root3(125.0) == 5.0   // true
root3(64.0) == 4.0    // true
root3(8.0) == 2.0     // true
root3(0.0) == 0.0     // true
root3(-64.0) == -4.0  // true
root3(2.0) != 1.0     // true


// Update #1:
val root3: Double => Double = a => {
  @tailrec
  def root3_Tailrec(xi: Double): Double = {
    // if:
    //    x_{i}^3 - a| ≤  * |a|
    // then relative accuracy is achieved -> returning approximation x_{i}
    if ((xi*xi*xi - a).abs <= 10e-15 * a.abs) xi
    // else calculating next approximation:
    //    x_{i+1} = x_{i} + (a/x_{i}^2 - x_{i})/3
    else root3_Tailrec(xi + (a/(xi*xi) - xi)/3)
  }

  root3_Tailrec(if (a > 1) a/3 else a)
}