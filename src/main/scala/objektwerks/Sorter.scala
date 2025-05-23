package objektwerks

object Sorter:
  given Ordering[Account] = Ordering.by[Account, String](account => account.activated).reverse
  given Ordering[Survey] = Ordering.by[Survey, String](survey => survey.released).reverse