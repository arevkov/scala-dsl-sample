# Scala DSL sample

## Motivation

Sometimes it becomes cumbersome to express the logic of program just in primitives of common computer language. You might want to
have higher abstractions to declare the behavior. And that is the place where domain specific languages come into play.
There are a lot of examples of DSLs, SQL and HTML are just a few of them. They provides you the language to rule a complex
system in an easier more declarative manner. And the first step here would be to declare the grammar of language and write the compiler.

## Description

This project is an example of how to define primitive DSL on scala. It utilizes [scala-parser-combinators](https://github.com/scala/scala-parser-combinators) library to declare
the grammar of your language and analyze [abstract syntax tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree)
from given statement. To get into the theme more deeply one might want to read about [parser combinator](https://en.wikipedia.org/wiki/Parser_combinator)

## DSL syntax

Operands: (==, !=, &&, ||, +, ?)
Example:
```
< (1 == 2 || 2 == 3 ? 3 : (3 != 3 ? 4 : (5 + 6)))
> 11
```

## Build
```
$ sbt package
```

## Run
```
$ java -jar target/scala-2.11/scala-dsl-sample_2.11-1.0.jar
```

## Links

1. http://www.artima.com/pins1ed/combinator-parsing.html <br>
(an article from the creators of Scala language about how to use parser combinator technique to declare grammar on
base arithmetic operations and json parser)

2. http://debasishg.blogspot.ru/2008/04/external-dsls-made-easy-with-scala.html <br>
(author shows how to declare a dsl that describes operations on financial instruments in a more natural way)

## License

Copyright © 2016 arevkov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.