package io.tabmo.aeroless

trait AsDecoder[A] {
  self =>
  def decode[V <: AsValue](v: V): Result[A]

  def map[B](f: A => B): AsDecoder[B] = new AsDecoder[B] {
    override def decode[V <: AsValue](v: V): Result[B] = self.decode(v).map(f)
  }
}

object Result {
  def apply[A](a: A): Result[A] = Done(a)

  def fromOption[A](opt: Option[A]) = opt match {
    case Some(a) => Done(a)
    case None => Failed
  }
}

sealed abstract class Result[+A] {
  def map[B](f: A => B): Result[B]

  def flatMap[B](f: A => Result[B]): Result[B]

  def toOption: Option[A]
}
case class Done[A](a: A) extends Result[A] {
  override def map[B](f: (A) => B): Result[B] = Done(f(a))

  override def flatMap[B](f: (A) => Result[B]): Result[B] = f(a)

  override def toOption: Option[A] = Some(a)
}
case object Failed extends Result[Nothing] {
  override def map[B](f: (Nothing) => B): Result[B] = Failed

  override def flatMap[B](f: (Nothing) => Result[B]): Result[B] = Failed

  override def toOption: Option[Nothing] = None
}

object AsDecoder {
  import shapeless._
  import shapeless.labelled._

  def instance[A](f: AsValue => Result[A]): AsDecoder[A] = new AsDecoder[A] {
    override def decode[V <: AsValue](v: V): Result[A] = f(v)
  }

  implicit val longDecoder: AsDecoder[Long] = instance {
    case AsLong(v) => Done(v)
    case _ => Failed
  }

  implicit val stringDecoder: AsDecoder[String] = instance {
    case AsString(v) => Done(v)
    case _ => Failed
  }

  implicit def mapDecoder[K, V](implicit evK: AsDecoder[K], evV: AsDecoder[V]): AsDecoder[Map[K, V]] = instance {
    case AsObject(kv) => kv.toList.foldRight(Result(List[(K, V)]())) { case ((k, v), acc) =>
      acc match {
        case Done(s) => evK.decode(AsString(k)).flatMap(k => evV.decode(v).map((k, _) :: s))
        case Failed => Failed
      }
    }.map(_.toMap)
    case _ => Failed
  }

  implicit def listDecoder[A](implicit ev: AsDecoder[A]): AsDecoder[List[A]] = instance {
    case AsArray(seq) => seq.foldRight(Result(List[A]())){ case (e, acc) =>
      acc match {
         case Done(s) => ev.decode(e).map(_ :: s)
        case Failed => Failed
      }
    }
    case _ => Failed
  }

  implicit def optionDecoder[A](implicit ev: AsDecoder[A]): AsDecoder[Option[A]] = instance {
    case AsNull => Done(None)
    case e: AsValue => ev.decode(e).map(Some(_))
  }

  implicit val hnilDecoder: AsDecoder[HNil] = instance(_ => Done(HNil))

  implicit def hlistDecoder[K <: Symbol, H, T <: HList](
    implicit witness: Witness.Aux[K],
    hDecoder: Lazy[AsDecoder[H]],
    tDecoder: Lazy[AsDecoder[T]]
  ): AsDecoder[FieldType[K, H] :: T] = instance {
    case o@AsObject(kv) => {
      val fieldName = witness.value.name
      val headField = hDecoder.value.decode(kv.getOrElse(fieldName, AsNull))
      val tailFields = tDecoder.value.decode(o)
      (headField, tailFields) match {
        case (Done(h), Done(t)) => Done(field[K](h) :: t)
        case _ => Failed
      }
    }
    case _ => Failed
  }

  implicit def objectDecoder[A, Repr <: HList](
    implicit gen: LabelledGeneric.Aux[A, Repr],
    hlistDecoder: AsDecoder[Repr]
  ): AsDecoder[A] = instance { v =>
    hlistDecoder.decode(v).map(gen.from)
  }

  def apply[A](implicit ev: AsDecoder[A]): AsDecoder[A] = ev
}
