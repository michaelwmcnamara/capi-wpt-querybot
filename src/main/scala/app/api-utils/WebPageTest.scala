package app.apiutils

//import app.api.PerformanceResultsObject
import com.squareup.okhttp.{OkHttpClient, Request, Response}
import org.joda.time.DateTime

import scala.xml.Elem


/**
 * Created by mmcnamara on 14/12/15.
 */
class WebPageTest(baseUrl: String, passedKey: String) {

  val apiBaseUrl:String = baseUrl
  val apiKey:String = passedKey

  val wptResponseFormat:String = "xml"
  implicit val httpClient = new OkHttpClient()

  def desktopChromeCableTest(gnmPageUrl:String): PerformanceResultsObject = {
    println("Sending desktop webpagetest request to WPT API")
    val resultPage: String = sendPage(gnmPageUrl + "noads")
    println("Accessing results at: " + resultPage)
    val testResults: PerformanceResultsObject = getResults(resultPage)
    println("Results returned")
    testResults
  }

  def mobileChrome3GTest(gnmPageUrl:String, wptLocation: String): PerformanceResultsObject = {
    println("Sending mobile webpagetest request to WPT API")
    val resultPage: String = sendMobile3GPage(gnmPageUrl + "#noads", wptLocation)
    println("Accessing results at: " + resultPage)
    val testResults: PerformanceResultsObject = getResults(resultPage)
    testResults
  }

  def sendPage(gnmPageUrl:String): String = {
    println("Forming desktop webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?url=" + gnmPageUrl + "&f=" + wptResponseFormat + "&k=" + apiKey
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    println(resultPage)
    resultPage
  }

  def sendMobile3GPage(gnmPageUrl:String, wptLocation: String): String = {
    println("Forming mobile 3G webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?url=" + gnmPageUrl + "&f=" + wptResponseFormat + "&k=" + apiKey + "&mobile=1&mobileDevice=Nexus5&location=" + wptLocation + ":Chrome.3G"
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    resultPage
  }


  def getResults(resultUrl: String):PerformanceResultsObject = {
    println("Requesting url:" + resultUrl)
    val request: Request = new Request.Builder()
      .url(resultUrl)
      .get()
      .build()
    var response: Response = httpClient.newCall(request).execute()
    println("Processing response and checking if results are ready")
    var testResults: Elem = scala.xml.XML.loadString(response.body.string)
    var iterator: Int = 0
    val msmaxTime: Int = 300000
    val msTimeBetweenPings: Int = 5000
    val maxCount: Int = msmaxTime / msTimeBetweenPings
    while (((testResults \\ "statusCode").text.toInt != 200) && (iterator < maxCount)) {
      println(DateTime.now + " " + (testResults \\ "statusCode").text + " statusCode response - test not ready. " + iterator + " of " + maxCount + " attempts\n")
      Thread.sleep(msTimeBetweenPings)
      iterator += 1
      response = httpClient.newCall(request).execute()
      testResults = scala.xml.XML.loadString(response.body.string)
    }
    if (((testResults \\ "statusCode").text.toInt == 200) && ((testResults \\ "response" \ "data" \ "successfulFVRuns").text.toInt > 0)  ) {
      println("\n" + DateTime.now + " statusCode == 200: Page ready after " + ((iterator+1) * msTimeBetweenPings)/1000 + " seconds\n Refining results")
      refineResults(testResults)
    } else {
        if((testResults \\ "statusCode").text.toInt == 200) {
          println(DateTime.now + " Test results show 0 successful runs ")
          failedTestNoSuccessfulRuns(resultUrl)
        }else{
          println(DateTime.now + " Test timed out after " + ((iterator+1) * msTimeBetweenPings)/1000 + " seconds")
          failedTestTimeout(resultUrl)
        }
      }
  }

  def refineResults(rawXMLResult: Elem): PerformanceResultsObject = {
    println("parsing the XML results")
    val testUrl: String = (rawXMLResult \\ "response" \ "data" \ "testUrl").text.toString
    val firstPaint: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "firstPaint").text.toInt
    println ("firstPaint = " + firstPaint)
    val docTime: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "docTime").text.toInt
    println ("docTime = " + docTime)
    val bytesInDoc: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "bytesInDoc").text.toInt
    println ("bytesInDoc = " + bytesInDoc)
    val fullyLoadedTime: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "fullyLoaded").text.toInt
    println ("Time to Fully loaded = " + fullyLoadedTime)
    val totalbytesIn: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "bytesIn").text.toInt
    println ("Total bytes = " + totalbytesIn)
    val speedIndex: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "SpeedIndex").text.toInt
    println ("SpeedIndex = " + speedIndex)
    val status: String = "Test Success"

    println("Creating PerformanceResultsObject")
    val result: PerformanceResultsObject = new PerformanceResultsObject(testUrl, firstPaint, docTime, bytesInDoc, fullyLoadedTime, totalbytesIn, speedIndex, status, false, false)
    println("Result time doc complete: " + result.timeDocComplete)
    println("Result time bytes fully loaded: " + result.bytesInFullyLoaded)
    println("Result string: " + result.toHTMLSimpleTableCells())
    println("Returning PerformanceResultsObject")
    result
  }

  def failedTestNoSuccessfulRuns(url: String): PerformanceResultsObject = {
    val failIndicator: Int = -1
    val failComment: String = "No successful runs of test"
    val failElement: PerformanceResultsObject = new PerformanceResultsObject(url , failIndicator,failIndicator,failIndicator,failIndicator,failIndicator,failIndicator, failComment, false, false)
    failElement
  }

  def failedTestTimeout(url: String): PerformanceResultsObject = {
    val failIndicator: Int = -1
    val failComment: String = "Test request timed out"
    // set warning status as result may have timed out due to very large page
    val failElement: PerformanceResultsObject = new PerformanceResultsObject(url , failIndicator,failIndicator,failIndicator,failIndicator,failIndicator,failIndicator, failComment, true, false)
    failElement
  }


}


//todo - add url into results so we can use it in result element - makes the whole html thing easier- get from xml?
