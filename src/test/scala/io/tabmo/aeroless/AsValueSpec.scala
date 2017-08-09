package io.tabmo.aeroless

import org.scalatest.{FlatSpec, Matchers}
import scala.collection.JavaConversions._

import com.aerospike.client.Value._
import io.tabmo.aerospike.data.AerospikeRecord

class AsValueSpec extends FlatSpec with Matchers {

  "AsValue" should "well parse AerospikeRecord" in {
    val bins = Map[String, AnyRef](
      "name" -> "Romain",
      "age" -> new java.lang.Long(27),
      "contact" -> mapAsJavaMap(Map(
        "address" -> "foo bar"
      )),
      "friends" -> seqAsJavaList(Seq("toto", "fifou"))
    )

    val tested = new AerospikeRecord(bins, -1, -1)
    val expected = AsValue.obj(
      "name" -> AsString("Romain"),
      "age" -> AsLong(27),
      "contact" -> AsValue.obj("address" -> AsString("foo bar")),
      "friends" -> AsArray(Array(AsString("toto"), AsString("fifou")))
    )

    AsValue(tested) shouldBe expected
  }

  "AsValue" should "well unpack object" in {
    val value = AsValue.obj(
      "name" -> AsString("Romain"),
      "age" -> AsLong(27),
      "contact" -> AsValue.obj("address" -> AsString("Rue de Thor")),
      "friends" -> AsArray(Array(AsString("toto"), AsString("fifou")))
    )

    val binSeq = value.asObject.toSeqBins //asObject is an unsafe operation
    binSeq.map(_.name) shouldBe List("name", "age", "contact", "friends")
  }

  "AsValue" should "well unpack string value" in {
    val value = AsValue.obj(
      "name" -> AsString("Romain")
    )
    val binSeq = value.asObject.toSeqBins //asObject is an unsafe operation
    binSeq.find(_.name == "name").get.value shouldBe a[StringValue]
  }

  "AsValue" should "well unpack long value" in {
    val value = AsValue.obj(
      "age" -> AsLong(27)
    )
    val binSeq = value.asObject.toSeqBins //asObject is an unsafe operation
    binSeq.find(_.name == "age").get.value shouldBe a[LongValue]
  }

  "AsValue" should "well unpack object value" in {
    val value = AsValue.obj(
      "contact" -> AsValue.obj("address" -> AsString("Rue de Thor"))
    )
    val binSeq = value.asObject.toSeqBins //asObject is an unsafe operation
    binSeq.find(_.name == "contact").get.value shouldBe a[MapValue]
  }

  "AsValue" should "well unpack list value" in {
    val value = AsValue.obj(
      "friends" -> AsArray(Array(AsString("toto"), AsString("fifou")))
    )
    val binSeq = value.asObject.toSeqBins //asObject is an unsafe operation
    binSeq.find(_.name == "friends").get.value shouldBe a[ListValue]
  }

  "AsObject" should "read and decode node" in {
    val value = AsValue.obj(
      "age" -> AsLong(27)
    ).asObject

    value[Long]("age") shouldBe Done(27)
  }
}
