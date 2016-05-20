package app.apiutils

import com.squareup.okhttp.{OkHttpClient, Response, Request}

import scala.xml.Elem

import argonaut._, Argonaut._


/**
 * Created by mmcnamara on 30/03/16.
 */
class VisualsApiInterface(url: String) {

  val apiUrl: String = url
  implicit val httpClient = new OkHttpClient()
/*
  def getTodaysPages(): List[String] = {
    println("Requesting result url:" + apiUrl)
    val request: Request = new Request.Builder()
      .url(apiUrl)
      .get()
      .build()
    val response: Response = httpClient.newCall(request).execute()

    val responseJSON: Json = argonaut.Json.
    val myResponse



  }
*/

}
