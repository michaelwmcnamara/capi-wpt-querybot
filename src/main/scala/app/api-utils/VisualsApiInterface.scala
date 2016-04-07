package app.apiutils

import com.squareup.okhttp.{OkHttpClient, Response, Request}
import org.joda.time.DateTime

import scala.xml.Elem

import argonaut._, Argonaut._
import play.api.libs.json._
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax


/**
 * Created by mmcnamara on 30/03/16.
 */
class VisualsApiInterface(url: String) {

  val apiUrl: String = url



}
case class VisualsApiRecord(id: String, webPublicationDate: DateTime, findDate: DateTime, headline:String, url: String, types: Seq[VisualTypes]){


 object VisualsApiRecord {
   implicit val userFormat = Json.Format[VisualsApiRecord]
   val id: String = id
   val webPublicationDate: DateTime = webPublicationDate
   val findDate: DateTime = findDate
   val headline: String = headline
   val url: String = url
   val types: Seq[VisualTypes] = types

 }
}

case class VisualTypes(typeName: String, altName: String){

  object VisualTypes{
    implicit val userFormat = Json.Format[VisualTypes]
    val typeName:String = typeName
    val altName: String = altName
  }
}