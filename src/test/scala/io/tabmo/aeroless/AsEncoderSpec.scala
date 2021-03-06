package io.tabmo.aeroless

import org.scalatest.{FlatSpec, Matchers}

class AsEncoderSpec extends FlatSpec with Matchers {

  "AsEncoder" should "encode long value" in {
    AsEncoder[Long].encode(1L) shouldBe AsValue.long(1L)
  }

  "AsEncoder" should "encode string value" in {
    AsEncoder[String].encode("foo") shouldBe AsValue.string("foo")
  }

  "AsEncoder" should "encode option value" in {
    AsEncoder[Option[String]].encode(Some("foo")) shouldBe AsValue.string("foo")
    AsEncoder[Option[String]].encode(None) shouldBe AsNull
  }

  "AsDecoder" should "decode map value" in {
    val map = Map(
      "value1" -> 1L,
      "value2" -> 2L
    )

    AsEncoder[Map[String, Long]].encode(map) shouldBe AsValue.obj(
      "value1" -> AsLong(1L),
      "value2" -> AsLong(2L)
    )
  }

  "AsEncoder" should "encode list value" in {
    AsEncoder[List[String]].encode(List("foo")) shouldBe AsValue.arr(AsValue.string("foo"))
  }

  "AsEncoder" should "encode object value" in {

    case class Contact(address: String)
    case class Person(name: String, age: Long, contact: Contact, friends: Option[List[String]])

    val encodedObject = AsEncoder[Person].encode(Person("Romain", 27, Contact("Rue de Thor"), Some(List("toto", "fifou"))))

    encodedObject shouldBe AsValue.obj(
      "name" -> AsString("Romain"),
      "age" -> AsLong(27),
      "contact" -> AsValue.obj("address" -> AsString("Rue de Thor")),
      "friends" -> AsArray(Array(AsString("toto"), AsString("fifou")))
    )
  }

  "AsEncoder" should "be contramap" in {
    val booleanEncoder = AsEncoder[Long].contramap[Boolean](b => if (b) 1L else 0L)
    booleanEncoder.encode(true) shouldBe AsValue.long(1L)
    booleanEncoder.encode(false) shouldBe AsValue.long(0L)
  }

}
