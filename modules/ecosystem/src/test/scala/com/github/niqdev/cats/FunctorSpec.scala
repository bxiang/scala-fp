package com.github.niqdev.cats

import org.scalatest.{ Matchers, WordSpecLike }

final class FunctorSpec extends WordSpecLike with Matchers {

  "Functor" should {

    "verify examples" in {
      import cats.Functor
      import cats.instances.list.catsStdInstancesForList
      import cats.instances.option.catsStdInstancesForOption

      Functor[List].map(List(1, 2, 3, 4))(_ * 2) shouldBe List(2, 4, 6, 8)
      Functor[Option].map(Option(123))(_.toString) shouldBe Some("123")
      Functor[Option].map(Option.empty[Int])(_.toString) shouldBe None
    }

    "verify Function1" in {
      import cats.instances.function.catsStdMonadForFunction1
      import cats.syntax.functor.toFunctorOps

      val func1 = (a: Int) => a + 1
      val func2 = (a: Int) => a * 2
      val func3 = (a: Int) => a + "!"
      func1.map(func2).map(func3)(123) shouldBe "248!"
    }
  }

}
