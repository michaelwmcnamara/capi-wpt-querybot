package app.api

import java.io.{FileInputStream, File}

import play.libs.Json

/**
 * Created by mmcnamara on 18/04/16.
 */
class JSONOperations() {

  def readAndParse(file: File) = {
    val stream = new FileInputStream(file)
    val json = try {  Json.parse(stream) } finally { stream.close() }

  }

}
