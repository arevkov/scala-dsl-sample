import scala.io.Source
import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by antonrevkov@gmail.com on 27.07.16.
  */
object SimpleREPL extends StandardTokenParsers {

  //**************************************************
  // Types and util functions
  //**************************************************

  case class Node(op: Any, args: List[Node] = List.empty)

  def collapse: (SimpleREPL.~[Node, List[SimpleREPL.~[String, Node]]]) => Node = {
    case x ~ xs => xs.foldLeft(x)((b, a) => Node(a._1, List(a._2, b)))
  }

  //**************************************************
  // BNF / Grammar
  //**************************************************

  lexical.reserved +=("+", "==", "!=", "?", ":", "&&", "||", "null", "true", "false")
  lexical.delimiters +=("(", ")")
  lexical.delimiters ++= lexical.reserved

  def literal: Parser[Node] =
    numericLit ^^ (x => Node(x.toLong)) |
      stringLit ^^ (Node(_)) |
      "null" ^^ (x => Node(null)) |
      "true" ^^ (x => Node(true)) |
      "false" ^^ (x => Node(false))

  def parenthesis: Parser[Node] = literal | "(" ~> logical <~ ")" | "(" ~> ternary <~ ")"

  def ternary: Parser[Node] = logical ~ "?" ~ logical ~ ":" ~ logical ^^ { case x ~ "?" ~ y ~ ":" ~ z => Node("?", List(x, y, z)) }

  def logical: Parser[Node] = logical2 ~ rep("&&" ~ logical2 | "||" ~ logical2) ^^ collapse

  def logical2: Parser[Node] = additive ~ rep("==" ~ additive | "!=" ~ additive) ^^ collapse

  def additive: Parser[Node] = parenthesis ~ rep("+" ~ parenthesis) ^^ collapse

  //**************************************************
  // Code Generator
  //**************************************************

  def compile(root: Node): () => Any = {
    val args = root.args.map(compile)
    root.op match {
      case "==" => () => args.head() == args(1)()
      case "!=" => () => args.head() != args(1)()
      case "+" => () => {
        val v1 = args.head()
        val v2 = args(1)()
        v1 match {
          case s: String => s + v2.asInstanceOf[String]
          case l: Long => l + v2.asInstanceOf[Long]
        }
      }
      case "?" => () => if (args.head().asInstanceOf[Boolean]) args(1)() else args(2)()
      case "&&" => () => args.head().asInstanceOf[Boolean] && args(1)().asInstanceOf[Boolean]
      case "||" => () => args.head().asInstanceOf[Boolean] || args(1)().asInstanceOf[Boolean]
      case null => () => null
      case true => () => true
      case false => () => false
      case s: String => () => s
      case n: Long => () => n
      case _ => throw new RuntimeException(s"syntax error - unknown operator \'${root.op}\'")
    }
  }

  //**************************************************
  // Entry point
  //**************************************************

  def parse(expr: String): Node = {
    val tokens = new lexical.Scanner(expr)
    phrase(parenthesis)(tokens) match {
      case Success(b, _) => b
      case Failure(msg, _) => throw new RuntimeException(s"syntax failure parsing \'$expr\', cause: $msg")
      case Error(msg, _) => throw new RuntimeException(s"syntax error parsing \'$expr\', cause: $msg")
    }
  }

  def eval(expr: String): Any = {
    val ast = parse(expr)
    val fn = compile(ast)
    fn()
  }

  def main(args: Array[String]): Unit = {
    println(s"usage: read->eval->loop (you writes statement in a line, program evaluates it")
    println(s"example of statement: (1 == 2 || 2 == 3 ? 3 : (3 != 3 ? 4 : (5 + 6))) ")

    for (expr <- Source.fromInputStream(System.in).getLines()) {
      try {
        println(eval(expr))
      } catch {
        case e: RuntimeException =>
          System.err.println(s"${e.getClass.getName}: ${e.getMessage}")
        case t: Throwable =>
          t.printStackTrace()
          System.exit(1)
      }
    }
  }
}
