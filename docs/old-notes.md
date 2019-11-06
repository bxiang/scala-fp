---
id: old-notes
title: Old Notes
sidebar_label: Old Notes Label
---

*What is a function literal?*

**Function literal** is a synonyms for **anonymous function**. Because functions are just ordinary Scala objects, we say that they are **first-class values**. A function literal is syntactic sugar for an object with a method called apply

```scala
val lessThan0 = (a: Int, b: Int) => a < b
val lessThan1 = (a, b) => a < b
val lessThan2 = new Function2[Int, Int, Boolean] {
  override def apply(a: Int, b: Int): Boolean = a < b
}
```

*What is a variadic function?*

A **variadic function** accepts zero or more arguments. It provides a little syntactic sugar for creating and passing a Seq of elements explicitly. The special `_*` type annotation allows to pass a Seq to a variadic method

```scala
sealed trait MyList[+A]
case object MyNil extends MyList[Nothing]
case class MyCons[+A](head: A, tail: MyList[A]) extends MyList[A]

object MyList {
  def apply[A](list: A*): MyList[A] =
    if (list.isEmpty) MyNil
    else MyCons(list.head, apply(list.tail: _*))
}

// usage
MyList(1, 2, 3, 4, 5)
```

*What is a value class?*

The [AnyVal](https://docs.scala-lang.org/overviews/core/value-classes.html) class can be used to define a **value class**, which is optimized at compile time to avoid the allocation of an instance

```scala
final case class Price(value: BigDecimal) extends AnyVal {
  def lowerThan(p: Price): Boolean = this.value < p.value
}
```

*What is autoboxing?*

The JVM defines primitive types (`boolean`, `byte`, `char`, `float`, `int`, `long`, `short` and `double`) that are *stack-allocated* rather than *heap-allocated*. When a generic type is introduced, for example, `scala.collection.immutable.List`, the JVM references an object equivalent, instead of a primitive type. For example, an instantiated list of integers would be heap-allocated objects rather than integer primitives. The process of converting a primitive to its object equivalent is called *boxing*, and the reverse process is called *unboxing*. Boxing is a relevant concern for performance-sensitive programming because boxing involves heap allocation. In performance-sensitive code that performs numerical computations, the cost of [boxing and unboxing](https://docs.oracle.com/javase/tutorial/java/data/autoboxing.html) can can create significant performance slowdowns

*What is the specialized annotation?*

**Specialization** with `@specialized` annotation, refers to the compile-time process of generating duplicate versions of a generic trait or class that refer directly to a primitive type instead of the associated object wrapper. At runtime, the compiler-generated version of the generic class (or, as it is commonly referred to, the specialized version of the class) is instantiated. This process eliminates the runtime cost of boxing primitives, which means that you can define generic abstractions while retaining the performance of a handwritten, specialized implementation although it has some [quirks](http://aleksandar-prokopec.com/2013/11/03/specialization-quirks.html)

*What is the switch annotation?*

In scenarios involving simple pattern match statements that directly match a value, using `@switch` annotation provides a warning at compile time if the switch can't be compiled to a tableswitch or lookupswitch which procides better performance, because it results in a branch table rather than a decision tree

*What is an Algebraic Data Type?*

In type theory, regular data structures can be described in terms of sums, products and recursive types. This leads to an algebra for describing data structures (and so-called algebraic data types). Such data types are common in statically typed functional languages

An **algebraic data type** (ADT) is just a data type defined by one or more data constructors, each of which may contain zero or more arguments. We say that the data type is the sum or union of its data constructors, and each data constructor is the product of its arguments, hence the name algebraic data type

Example

* these types represent a SUM type because Shape is a Circle OR a Rectangle
* Circle is a PRODUCT type because it has a radius
* Rectangle is a PRODUCT type because it has a width AND a height

```scala
sealed trait Shape
final case class Circle(radius: Double) extends Shape
final case class Rectangle(width: Double, height: Double) extends Shape
```

Sum types and product types provide the necessary abstraction for structuring various data of a domain model. Whereas sum types let model the variations within a particular data type, product types help cluster related data into a larger abstraction.

*How for-comprehensions is desugared? ([docs](https://docs.scala-lang.org/tour/for-comprehensions.html))*

```scala
// (1) works because "foreach" is defined
scala> for (i <- List(1, 2, 3)) println(i)
1
2
3

// (2) "yield" works because "map" is defined
scala> for (i <- List(1, 2, 3)) yield i*2
res2: List[Int] = List(2, 4, 6)

// (3) "if" works because "withFilter" is defined
scala> for (i <- List(1, 2, 3, 4); if i%2 == 0) yield i*2
res3: List[Int] = List(4, 8)

// (4) works because "flatMap" is defined
scala> for (i <- List(1, 2, 3, 4); j <- List(3, 4, 5, 6); if i == j) yield i
res4: List[Int] = List(3, 4)
```

*What is a Typeclass?*

A Typeclass is a programming pattern that allow to extend existing libraries with new functionality, without using traditional inheritance and without altering the original library source code using a combination of ad-hoc polymorphism, parametric polymorphism (type parameters) and implicits

*What is a Monoid?*

A Monoid is an algebraic type with 2 laws, a binary operation over that type, satisfying *associativity* and an *identity* element

* associative e.g `a + (b + c) == (a + b) + c`
* identity e.g. for sum is 0, for product is 1, for string is ""

```scala
trait Monoid[A] {
  // associativity
  // op(op(x, y), z) == op(x, op(y, z))
  def op(x: A, y: A): A

  // identity
  // op(x, zero) == op(zero, x) == x
  def zero: A
}

// example
val stringMonoid = new Monoid[String] {
  override def op(x: String, y: String): String = x + y
  override def zero: String = ""
}
```

Monoids have an intimate connection with lists and arguments of the same type, it doesn't matter if we choose `foldLeft` or `foldRight` when folding with a monoid because the laws of associativity and identity hold, hence this allows parallel computation

The real power of monoids comes from the fact that they compose, this means, for example, that if types A and B are monoids, then the tuple type (A, B) is also a monoid (called their product)

```scala
scala> List("first", "second", "third").foldLeft(stringMonoid.zero)(stringMonoid.op)
scala> List("first", "second", "third").foldRight(stringMonoid.zero)(stringMonoid.op)
res: String = firstsecondthird
```

*What is a Semigroup?*

A Semigroup is just the `combine` part of a Monoid. While many semigroups are also monoids, there are some data types for which we cannot define an empty element e.g. non-empty sequences and positive integers

```scala
trait Semigroup[A] {
  // or op
  def combine(x: A, y: A): A
}

trait Monoid[A] extends Semigroup[A] {
  // or zero
  def empty: A
}
```

*What is a Functor?*

Informally, a Functor is anything with a `map` method

```scala
// F is a higher-order type constructor or a higher-kinded type
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}
```

*What is a Monad?*

Informally, a Monad is anything with a constructor and a flatMap method. A Monad is a mechanism for *sequencing computations*, all monads are functors but the opposite is not true.

A Monad is an implementation of one of the minimal sets of monadic combinators, satisfying the laws of associativity and identity

* unit and flatMap
* unit and compose
* unit, map and join

where the above are defined

```scala
def unit[A](a: => A): F[A]
def map[A, B](ma: F[A])(f: A => B): F[B]
def flatMap[A, B](ma: F[A])(f: A => F[B]): F[B]
def compose[A, B, C](f: A => F[B], g: B => F[C]): A => F[C]
def join[A](mma: F[F[A]]): F[A]

// Identity: compose(unit, f) = f = compose(f, unit)
// Associativity: compose(compose(f, g), h) = compose(f, compose(g, h))
```

A Monad provide a context for introducing and binding variables and performing variable substitution

```scala
object Monad {
  case class Id[A](value: A) {
    def map[B](f: A => B): Id[B] =
      Id(f(value))
    def flatMap[B](f: A => Id[B]): Id[B] =
      f(value)
  }

  val idMonad: Monad[Id] = new Monad[Id] {
    override def unit[A](a: => A): Id[A] =
      Id(a)

    override def flatMap[A, B](ma: Id[A])(f: A => Id[B]): Id[B] =
      ma.flatMap(f)
  }
}

Monad.Id("hello ").flatMap(a => Monad.Id("world").flatMap(b => Monad.Id(a + b)))

for {
  a <- Monad.Id("hello ")
  b <- Monad.Id("world")
} yield a + b

res: Monad.Id[String] = Id(hello world)
```

*What is a Semigroupal?*

A Semigroupal is a type class that allows to combine contexts. In contrast to flatMap, which imposes a strict order, Semigroupal parameters are independent of one another, which gives more freedom with respect to monads

```scala
trait Semigroupal[F[_]] {
  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
}
```

*What is an Applicative?*

* all applicatives are functors
* all applicatives are a semigroupal
* all monads are applicative functors, viceversa is not true

```scala
// cats definition
trait Apply[F[_]] extends Semigroupal[F] with Functor[F] {
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    ap(map(fa)(a => (b: B) => (a, b)))(fb)
}

trait Applicative[F[_]] extends Apply[F] {
  def pure[A](a: A): F[A]
}

// red book definition
trait Applicative[F[_]] extends Functor[F] {
  // primitive combinators
  def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C]
  def unit[A](a: => A): F[A]

  // derived combinators
  def map[A, B](fa: F[A])(f: A => B): F[B] =
    map2(fa, unit(()))((a, _) => f(a))
  def traverse[A, B](as: List[A])(f: A => F[B]): F[List[B]] =
    as.foldRight(unit(List[B]()))((a, fbs) => map2(f(a), fbs)(_ :: _))
}
```

*What are some of the benefits of Functional Programming?*

* Pure functions are easier to reason about
* Function signatures are more meaningful
* Parallel/Concurrent programming is easier
* Testing is easier and pure functions lend themselves well to techniques like property-based testing
* [Other benefits](https://alvinalexander.com/scala/fp-book/benefits-of-functional-programming)

*What is an effectful computation?*

In functional programming, an effect adds some capabilities to a computation. An effect is modeled usually in the form of a **type constructor** that constructs types with these additional capabilities

* `List[A]` adds the effect of aggregation on A
* `Option[A]` adds the capability of optionality for the type A
* `Try[A]` models the effects of exceptions

*What are inhabitants of a type?*

Inhabitants of a type are values for that types. Algebraig Data Types can be thought of in terms of regular algebraic equations and its result gives the number of inhabitants

* sum types: `Either[A, B]` or `A or B` corresponds to the equation `A + B`
* products types: `(A, B)` (Tuple2) or `A and B` corresponds to the equation `A * B`
* exponentiation: `A -> B` (Function1) corresponds to the equation `B^A` e.g. `Boolean -> Boolean` is `2^2`
* the `Unit` data type corresponds to the value 1
* the `Void` data type corresponds to the value 0

*What's the difference between monomorphic and polymorphic?*

Only by knowing the types

* Given a monomorphic signature `List[Int] -> List[Int]`, there are too many possible implementations to say what the function does
* Given a polymorphic/parametrized type signature `List[A] -> List[A]` it's proven that all elements in the result appear in the input which restricts the possible implementations

<!--

*What is an IO Monad?*

```scala

```

TODO
https://stackoverflow.com/questions/6246719/what-is-a-higher-kinded-type-in-scala

Summary

* Semigroup: associativity
* Monoid: associativity + identity
* Functor: map
* Monad: any of 3 monadic laws (e.g. unit + flatMap) + associativity and identity (extends Functor)
* Applicative functor
* Traversable functor

* A function having the same argument and return type is sometimes called an endofunction

TODO Functor and Monad

* associative e.g. `x.flatMap(f).flatMap(g) == x.flatMap(a => f(a).flatMap(g))`
* monadic functions of types like `A => F[B]` are called Kleisli arrows

Laws
* Left and right identity
* Associativity
* Naturality of product

## Best practices and tips

```
# best practices
https://stackoverflow.com/questions/5827510/how-to-override-apply-in-a-case-class-companion

# type projector
https://typelevel.org/blog/2015/07/13/type-members-parameters.html

# jvm stack
https://www.artima.com/insidejvm/ed2/jvm8.html
https://alvinalexander.com/scala/fp-book/recursion-jvm-stacks-stack-frames
```

```scala
// to remember: foldLeft start from left (acc op xFirst) ==> (B, A)
// to remember: foldRight start from right (xLast op acc) ==> (A, B)
```

In Scala, all methods whose names end in : are right-associative. That is, the expression x :: xs is actually
the method call xs.::(x) , which in turn calls the data constructor ::(x,xs)

* curry function used to assist type inference when passing anonymous functions

* companion object
* a **variadic function** accepts zero or more arguments
* algebraic data type (ADT)

an API should form an algebra — that is, a collection of data types, functions over these data types, and importantly, laws or properties that express relationships between these functions

* volatile
* compare and swap
* javap
* diamond inheritance problem
* variance / covariance of type A

```scala

// + covariant
// List[Dog] is considered a subtype of List[Animal], assuming Dog is a subtype of Animal
sealed trait List[+A]
// Nothing is a subtype of all types
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]
```

object PizzaService extends PizzaService

You can’t call functions on a trait, so you need to create a concrete instance of that trait before you do anything else. This technique is common with the modular pro- gramming approach, and it’s known as “reifying” the trait. (The word reify is defined as, “Taking an abstract concept and making it concrete.”)

-->

<!--

Algebra = Operations + Types

e.g.
* create is an operation
* A and String is a type
* Db is a piece of Algebra that can be composed or extended

trait Db[F[_]] {
  def create[A]: F[String]
  def delete(id: String): F[Boolean]
}

Interpreter = "Implementation" of an Algebra

i.e. Materialization for specific type

---
they are alternatives:

(tagless) final encoding: typeclass
* ignore tagless
* final encoding: means that you can describe operations with functions

initial encoding: free monad
* you can describe operations with case classes

trait Db {
  case class Create[F[_], A](a: A)
  case class Delete[F[_]](id: String)
}

---

Tagless Final Encoding == MTL-style program composition

mtl is now is a final tagless encoding of common effects

The `mtl` (Monad Transformer library) library in haskell used to provide the concrete monad transformers types, which are now in `transformers`

Option, Either, IO are all effect/context/wrapper

-->