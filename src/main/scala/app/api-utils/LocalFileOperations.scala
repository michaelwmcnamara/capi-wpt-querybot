package app.apiutils

import java.io.FileWriter

import org.joda.time.DateTime

import scala.io.Source


/**
 * Created by mmcnamara on 10/02/16.
 */
class LocalFileOperations {

  def readInConfig(fileName: String):Array[String] = {
    var contentApiKey: String = ""
    var wptBaseUrl: String = ""
    var wptApiKey: String = ""
    var wptLocation: String = ""
    var emailUsername: String = ""
    var emailPassword: String = ""

    println(DateTime.now + " retrieving local config file: " + fileName)
    for (line <- Source.fromFile(fileName).getLines()) {
      if (line.contains("content.api.key")) {
        println("capi key found")
        contentApiKey = line.takeRight((line.length - line.indexOf("=")) - 1)
      }
      if (line.contains("wpt.api.baseUrl")) {
        println("wpt url found")
        wptBaseUrl = line.takeRight((line.length - line.indexOf("=")) - 1)
      }
      if (line.contains("wpt.api.key")) {
        println("wpt api key found")
        wptApiKey = line.takeRight((line.length - line.indexOf("=")) - 1)
      }
      if (line.contains("wpt.location")) {
        println("wpt location found")
        wptLocation = line.takeRight((line.length - line.indexOf("=")) - 1)
      }
      if (line.contains("email.username")) {
        println("email username found")
        emailUsername = line.takeRight((line.length - line.indexOf("=")) - 1)
      }
      if (line.contains("email.password")) {
        println("email password found")
        emailPassword = line.takeRight((line.length - line.indexOf("=")) - 1)
      }
    }
    Array(contentApiKey, wptBaseUrl, wptApiKey, wptLocation, emailUsername, emailPassword)
  }


  def writeLocalResultFile(outputFileName: String, results: String): Int = {
    val output: FileWriter = new FileWriter(outputFileName)
    println(DateTime.now + " Writing the following to local file " + outputFileName + ":\n" + results)
    output.write(results)
    output.close()
    println(DateTime.now + " Writing to file: " + outputFileName + " complete. \n")
    0
  }

}
