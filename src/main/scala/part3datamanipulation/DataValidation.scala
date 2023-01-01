package part3datamanipulation


object DataValidation {

  /*
    Validated acts like an Either, where Left is undesired and Right is desired
    Validated has a different contract than Either.
    It's used to combine all errors using 1 value without mutations (pure FP).
  */
  import cats.data.Validated

  val validValue: Validated[String, Int] = Validated.valid(42) // "Right" in Either
  val invalidValue: Validated[String, Int] = Validated.invalid("Something went wrong.") // "Left" in Either

  val test: Validated[String, Int] = Validated.cond(42 > 39, 99, "Meaning of life is too small.")

  def testNumber(n: Int): Either[List[String], Int] = {
    /*
      n must be prime (whole number > 1 that cannot be exactly divided by any whole number other than itself and 1 (e.g. 2, 3, 5, 7, 11)
      n must be non-negative
      n must be <= to 100
      n must be even

      Left(List[String]) contains every condition that n fails. Else return Right(n)
    */
    val errors = scala.collection.mutable.ListBuffer[String]()

    List(
      ("n must be prime", (2 until n-1) exists (n % _ == 0)),
      ("n must be non-negative", n < 0),
      ("n must be <= to 100", n > 100),
      ("n must be even", n % 2 != 0)
    ).foreach(element => if (element._2) errors += element._1)

    if (errors.nonEmpty) Left(errors.toList) else Right(n)
  }

  //import cats.instances.list._
  import cats.Semigroup

  implicit val combineIntMax: Semigroup[Int] = Semigroup.instance[Int](scala.math.max)
  implicit val combineLinkedList: Semigroup[List[String]] = Semigroup.instance[List[String]](_ ::: _)

  def validateNumber(n: Int): Validated[List[String], Int] = {
    // combine belongs to Semigroup (we need combine for both List[String] and Int)
    Validated
      .cond(n % 2 == 0, n, List("n must be even"))
      .combine(Validated.cond(n > 0, n, List("n must be non-negative")))
      .combine(Validated.cond(n < 100, n, List("n must be <= to 100")))
      .combine(Validated.cond(!(2 until n-1).exists(n % _ == 0), n, List("n must be prime")))
  }


  def main(args: Array[String]): Unit = {
    //
    println(testNumber(6))
    println(validateNumber(-7))
    println(validateNumber(2))
  }
}
