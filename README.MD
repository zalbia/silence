# Assignment - Silence

Divide an audio stream by silence. Mostly written in pure FP using [ZIO](https://zio.dev/).

## Getting Started

Clone and run SBT on the project root directory. Make sure to have the prerequisites below installed on your machine. 

### Prerequisites
- JDK 8
- SBT
- Scala 2.13.4

This assignment was developed on WSL 1.0 on Windows 10, version 2004. It should run on OSes supporting the JVM as I don't think there's anything OS-specific in here, but I haven't tried it myself.

### Running the Program in SBT
The program reads audio silence data from XML and ISO-8601 duration strings from user input and prints an audio file segment descriptor in JSON to standard output.

The command line arguments are in the following format:

```
<path-to-xml> <chapter-silence-duration> <partition-threshold-duration> <part-silence-duration>
```

where duration is an ISO 8601 duration string.

Here are some example arguments that I've found to yield a fairly good representation of the output.

```
sbt> run silence1.xml PT3S PT30M PT1S
```

## Tests

Tests are written using [zio-test](https://zio.dev/docs/usecases/usecases_testing). The pieces of functionality with tests are for [command line argument parsing](src/test/scala/com/github/zalbia/silence/ArgumentsSpec.scala), and the business logic of [dividing an audio stream into chapters by silence](src/test/scala/com/github/zalbia/silence/ChapterSpec.scala).

To run the tests, simply run `test` in SBT.
```
sbt> test
```

## Built With

* [zio](https://zio.dev/) - as a pure FP base library
* [zio-prelude](https://github.com/zio/zio-prelude) - for extra data structures
* [jsoniter-scala](https://github.com/plokhotnyuk/jsoniter-scala/) - for writing JSON
* [xs4s](https://github.com/ScalaWilliam/xs4s) - for reading XML input as a stream in pure FP

## Author

* **Zach Albia** - [https://github.com/zalbia](https://github.com/zalbia)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.MD) file for details
