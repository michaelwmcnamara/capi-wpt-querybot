package app.api

import java.io.{FileInputStream, File}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.json4s._
import org.json4s.native.JsonMethods._
import play.libs.Json

/**
 * Created by mmcnamara on 18/04/16.
 */
class JSONOperations() {
/*
  case class VisualsObject(pageType: String, id: String, findDate: String, headline: String, url, String, types: Seq[InteractiveElement])

  case class InteractiveElement(interactiveType: String, alt: String, canonicalUrl: String, bootUrl: String)

  def readAndParse(file: File) = {
    val stream = new FileInputStream(file)
    val mapper = new ObjectMapper()
    val newObject = mapper.readValue(json, VisualsObject)
    val json = try {
      Json.parse(stream)
    } finally {
      stream.close()
    }

    val myobject = new VisualsObject(json.get("pageType"),json.get("id"),json.get("findDate"),json.get("headline"),json.get("url",json.get("types")))
  }*/
}
