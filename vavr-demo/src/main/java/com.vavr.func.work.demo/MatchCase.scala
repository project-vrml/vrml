package com.vavr.func.work.demo

class MatchCase {

  def glob(x: Any): Any = x match {
    case 1 | "1" | "one" => "one "
    case "two" => 2
    case s: String => "String"
    case y: Int => "Int 类型 "
    case _ => "其他"
  }

  val tuple = (1, 2, 3, 4)

  def tupleMatch(x: Any) = x match {
    case (first, second) => println(s"第一个元素：${first}  第二个元素：${second}")
    case (first, _, three, _) => println(s"第一个元素：${first}  第三个元素：${three}")
    case _ => println("没有任何匹配")
  }
}