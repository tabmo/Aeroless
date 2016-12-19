package io.tabmo.aeroless

import org.scalatest.{FlatSpec, Matchers}
import scala.collection.JavaConversions._
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
}
