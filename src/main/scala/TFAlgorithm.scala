package com.github.gzilt.classifier.image

import org.apache.predictionio.controller.P2LAlgorithm
import org.apache.predictionio.controller.Params
import org.apache.spark.SparkContext

import scala.io.Source
import grizzled.slf4j.Logger
import java.io.File
import java.nio.file.{Files, Paths}

import sun.misc.BASE64Decoder
import java.io.{BufferedInputStream, ByteArrayOutputStream}
import java.net.URL
import java.nio.file.{Files, Paths}

import com.github.gzilt.classifier.image.model.InceptionV3

import scala.collection.mutable.ListBuffer


case class TFAlgorithmParams(
                              val inputLayer: String,
                              val outputLayer: String,
                              val modelFilename: String,
                              val imageDir: String,
                              val idToStringIdMap: String,
                              val stringIdToLabelMap: String
                            ) extends Params {

  // Case class style copy method
  def copy(
            inputLayer: String = inputLayer,
            outputLayer: String = outputLayer,
            modelFilename: String = modelFilename,
            imageDir: String = imageDir,
            idToStringIdMap: String = idToStringIdMap,
            stringIdToLabelMap: String = stringIdToLabelMap
          ): TFAlgorithmParams = {

    new TFAlgorithmParams(
      inputLayer,
      outputLayer,
      modelFilename,
      imageDir,
      idToStringIdMap,
      stringIdToLabelMap)
  }
}

class TFAlgorithm(
                   val ap: TFAlgorithmParams
                 ) extends P2LAlgorithm[PreparedData, TFModel, Query, PredictedResults] {

  @transient lazy val logger = Logger[this.type]
  private lazy val base64Decoder = new BASE64Decoder

  def train(sc: SparkContext, pd: PreparedData): TFModel = {
    new TFModel(
      ap.inputLayer,
      ap.outputLayer,
      new File(ap.modelFilename))
  }

  def predict(model: TFModel, query: Query): PredictedResults = {

    val imageBytes = query.data match {
      case Some(data) => base64Decoder.decodeBuffer(data)
      case None => {
        Files.readAllBytes(Paths.get(
          new File(ap.imageDir, query.image.getOrElse("cropped_panda.jpg"))
            .getCanonicalPath))
      }
    }

    model.predict(imageBytes, ap.idToStringIdMap, ap.stringIdToLabelMap)
  }
}

class TFModel(
               val inputLayer: String,
               val outputLayer: String,
               val serializedModel: File
             ) extends Serializable {

  @transient lazy val logger = Logger[this.type]

  def predict(
               imageBytes: Array[Byte],
               idToStringIdMap: String,
               stringIdToLabelMap: String): PredictedResults = {


    // define the model
    val model = new InceptionV3(serializedModel.getCanonicalPath, stringIdToLabelMap, idToStringIdMap)
    // initialize TensorFlowProvider
    val provider = new TensorFlowProvider(model)
    val results = model.getLabelOf(provider.run(inputLayer -> imageBytes, outputLayer).head)
    val resultList = new ListBuffer[(PredictedResult)]()
    results.foreach(item => {
      if (item.score > 0.001) resultList.append(PredictedResult(item.label, item.score))
    })
    PredictedResults(resultList)
  }
}