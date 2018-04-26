package com.github.gzilt.classifier.image.model

import java.nio.file.{Files, Paths}
import scala.io.Source

class InceptionV3(graphPath: String, humanLabelPath: String, labelMapPath: String) extends TensorFlowModel with Labelable {

  private val codeLabelSeq: Array[(String, String)] = {

    // ex: n02510455 => ["giant panda", "panda", "panda bear", ...]
    val labelMap = Source.fromFile(humanLabelPath).getLines.toList
      .map(_.split("\\s+", 2))
      .map { case Array(s, l) => (s.trim, l.trim) }
      .toMap

    // ex: 169 => ["giant panda", "panda", "panda bear", ...]
    val indexToCode = Source.fromFile(labelMapPath).getLines.toList
      .dropWhile(_.trim.startsWith("#"))
      .grouped(4)
      .map { grouped =>
        val targetClass = grouped(1).split(":")(1).trim.toInt
        val targetClassString = grouped(2).split(":")(1).trim.stripPrefix("\"").stripSuffix("\"")
        (targetClass, (targetClassString, labelMap(targetClassString)))
      }
      .toMap
      .withDefault(_ => "" -> "")

    Array.tabulate(indexToCode.keys.max + 1)(indexToCode.apply)
  }

  override def getBytes: Array[Byte] =
    Files.readAllBytes(Paths.get(graphPath))

  override def getLabelOf(tensor: Array[Float], limit: Int): Seq[Label] = {
    val all = tensor.zip(codeLabelSeq).map { case (score, (code, label)) =>
      Label(code, label, score)
    }
    all.sortBy(-_.score).take(limit).toSeq
  }

}
