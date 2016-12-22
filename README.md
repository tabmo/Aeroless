# Aeroless

Aeroless is an extension tools for [ReactiveAerospike](https://github.com/tabmo/ReactiveAerospike). It's build above the awesome Shapeless library.

## Installation

Add in your build.sbt

```scala
resolvers += "Tabmo Bintray" at "https://dl.bintray.com/tabmo/maven"

libraryDependencies += "io.tabmo" %% "aeroless" % "0.1"
```

## Usage

### AsValue

An AST structure is include in the library to represent Aerospike record :
 
```scala
AsValue.obj(
    "name" -> AsString("Romain"),
    "age" -> AsLong(27),
    "contact" -> AsValue.obj("address" -> AsString("Rue de Thor")),
    "friends" -> AsArray(Array(AsString("toto"), AsString("fifou")))
)
```

You can parse AerospikeRecord to AsValue :

```scala
val record: AerospikeRecord = ...
val ast: AsValue = AsValue(record)
```

And transform AsValue to Sequence of Bin like that :
```scala
val value = AsValue.obj(
    "name" -> AsString("Romain"),
    "age" -> AsLong(27),
    "contact" -> AsValue.obj("address" -> AsString("Rue de Thor")),
    "friends" -> AsArray(Array(AsString("toto"), AsString("fifou")))
)

val binSeq = value.asObject.toSeqBins //asObject is an unsafe operation
```

### Encoder / Decoder

Aeroless provide Encoder and Decoder to transform Scala object from/to raw Aerospike structure.

#### Encode

You can encode complex data type to AsValue :

```
case class Contact(address: String)
case class Person(name: String, age: Long, contact: Contact, friends: Option[List[String]])

val encodedObject = AsEncoder[Person].encode(Person("Romain", 27, Contact("Rue de Thor"), Some(List("toto", "fifou"))))

encodedObject shouldBe AsValue.obj(
  "name" -> AsString("Romain"),
  "age" -> AsLong(27),
  "contact" -> AsValue.obj("address" -> AsString("Rue de Thor")),
  "friends" -> AsArray(Array(AsString("toto"), AsString("fifou")))
)
```

You can transform encoder with the contramap method :

```scala
val booleanEncoder = AsEncoder[Long].contramap[Boolean](b => if (b) 1L else 0L)
booleanEncoder.encode(true) shouldBe AsValue.long(1L)
booleanEncoder.encode(false) shouldBe AsValue.long(0L)
```

#### Decode

...and decode the same object from AsValue :

```scala
val personAs = AsValue.obj(
    "name" -> AsString("Romain"),
    "age" -> AsLong(27),
    "contact" -> AsValue.obj("address" -> AsString("Rue de Thor")),
    "friends" -> AsArray(Array(AsString("toto"), AsString("fifou")))
)

AsDecoder[Person].decode(personAs) shouldBe Done(Person("Romain", 27, Contact("Rue de Thor"), Some(List("toto", "fifou"))))
```

You can transform decoder with the map method :
```scala
val booleanDecoder = AsDecoder[Long].map(l => if (l > 0) true else false)
booleanDecoder.decode(AsValue.long(1L)) shouldBe Done(true)
booleanDecoder.decode(AsValue.long(0L)) shouldBe Done(false)
```

You can decoder sub value of AsObject with apply method :
```scala
val value = AsValue.obj(
    "age" -> AsLong(27)
).asObject

value[Long]("age") shouldBe Done(27)
```

#### Map / Option / List

A support for this type is available. For example, just look on the tests!

