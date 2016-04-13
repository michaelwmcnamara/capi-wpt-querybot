package app


import app.apiutils.PerformanceResultsObject
import org.joda.time.DateTime

/**
  * Created by Gwyn Lockett on 12/04/16.
  */

class HtmlReportBuilder(average: String, warning: String, alert: String, articleResultsUrl: String, liveBlogResultsUrl: String, interactiveResultsUrl: String, frontsResultsUrl: String) {

  val averageColor = average
  val warningColor = warning
  val alertColor = alert

  val articleResultsPage: String = articleResultsUrl
  val liveBlogResultsPage: String = liveBlogResultsUrl
  val interactiveResultsPage: String = interactiveResultsUrl
  val frontsResultsPage: String = frontsResultsUrl


  //HTML Page elements
  //Page Header
  val HTML_PAGE_HEAD: String = "<!DOCTYPE html><html lang=\"en\"><head> <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/> <title>Daily REPORT - [Performance Interactives]</title> <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\"/> <link rel=\"stylesheet\" href=\"css/style.css\"/> <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js\"></script> <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\"></script> <link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\" rel=\"stylesheet\"/> <link rel=\"stylesheet\" href=\"css/style.css\"/> <script src=\"js/script.js\"></script></head>"

  //Page Container
  val HTML_PAGE_CONTAINER: String = "<body><div id=\"container\"> <div id=\"head\"> <h1>Current performance of " +
    "today's Interactives</h1> <p>Job started at: " + DateTime.now + "</p><p><a href=http://wpt.gu-web.net/result/160330_Q9_17W/>Click here to see full results.</a></p> </div>"

  //Page Content
  val HTML_PAGE_CONTENT: String = "<div id=\"content\"> <h2>Desktop Alerts</h2> <p>The following items have been found to either take too long to load or cost too much to view on a desktop browser</p>"

  //Page Tables
  val HTML_REPORT_TABLE_HEADERS: String = "<table id=\"report\"> <thead> <tr> <th>Time Last Tested</th> <th>Test Type</th> <th>Article Url</th> <th>Time to Page Scrollable</th> <th>Time to rendering above the fold complete</th> <th>MB transferred</th> <th>Status</th> <th>Full Results Here</th> </tr></thead> <tbody>"

  //For each 'report table' row build this string:
  val HTML_REPORT_TABLE_ROWS: String = " <tr class=\"[DATA]\"> <td>[DATA]</td><td>[DATA]</td><td>[DATA]</td><td>[DATA]</td><td>[DATA]</td><td>[DATA]</td><td>[DATA]</td><td> <div class=\"arrow\"></div></td></tr>"

  val HTML_DATA_TABLE_HEADERS: String = "<tr> <td colspan=\"8\"> <table class=\"data\"> <caption>List of 5 heaviest " +
    "elements on page - Recommend reviewing these items </caption> <thead> <tr> <th>Resource</th> <th>Content Type</th> <th>Bytes Transferred</th> </tr></thead> <tbody>"

  //For each 'data table' row build this string:
  val HTML_DATA_TABLE_ROWS: String = " <tr> <td> <a href=[DATA]n>[DATA]</a> </td><td>[DATA]</td><td>[DATA]</td></tr>"

  val HTML_TABLE_END: String = " </tbody> </table>"

  //Page Footer
  val HTML_FOOTER: String = "</div><div id=\"footer\"> <p>Job completed at: [DATA]</p></div></div></body></html>"



 //HTML_PAGE_Builder
  var HTML_Results_PAGE: String = HTML_PAGE_HEAD + HTML_PAGE_CONTAINER + HTML_PAGE_CONTENT +
   HTML_REPORT_TABLE_HEADERS + HTML_REPORT_TABLE_ROWS + HTML_TABLE_END + HTML_DATA_TABLE_HEADERS + HTML_TABLE_END +
   HTML_DATA_TABLE_ROWS + HTML_FOOTER


  //Functions
  def generateHTMLRow(resultsObject: PerformanceResultsObject): String = {
    var returnString: String = ""
    //  Define new web-page-test API request and send it the url to test
    //  Add results to string which will eventually become the content of our results file

    if (resultsObject.warningStatus) {
      if (resultsObject.alertStatus) {
        println("row should be red one of the items qualifies")

        returnString = "<tr class=\"alertColor\">" + resultsObject.toHTMLSimpleTableCells() + "<div class=\"arrow\"></div></td></tr>"

      }
      else {
        println("row should be yellow one of the items qualifies")
        returnString = "<tr class=\"warningStatus\">" + resultsObject.toHTMLSimpleTableCells() + "<div class=\"arrow\"></div></td></tr>"
      }
    }
    else {
      println("all fields within size limits")
      returnString = "<tr class=\"default\">" + resultsObject.toHTMLSimpleTableCells() + "<div " +
        "class=\"arrow\"></div></td></tr>"
    }
    println(DateTime.now + " returning results string to main thread")
    println(returnString)
    returnString

  }

  def interactiveHTMLRow(resultsObject: PerformanceResultsObject): String = {
    var returnString: String = ""
    //  Define new web-page-test API request and send it the url to test
    //  Add results to string which will eventually become the content of our results file

    if (resultsObject.warningStatus) {
      if (resultsObject.alertStatus) {
        println("row should be red one of the items qualifies")
        returnString = "<tr style=\"background-color:" + alertColor + ";\">" + resultsObject.toHTMLInteractiveTableCells() + "</tr>"
      }
      else {
        println("row should be yellow one of the items qualifies")
        returnString = "<tr style=\"background-color:" + warningColor + ";\">" + resultsObject.toHTMLInteractiveTableCells() + "</tr>"
      }
    }
    else {
      println("all fields within size limits")
      returnString = "<tr>" + resultsObject.toHTMLInteractiveTableCells() + "</tr>"
    }
    println(DateTime.now + " returning results string to main thread")
    println(returnString)
    returnString

  }
  


//  def initialisePageForArticle: String = {
//    hTMLPageHeader + hTMLTitleArticle + hTMLJobStarted
//  }
//
//  def initialisePageForLiveblog: String = {
//    hTMLPageHeader + hTMLTitleLiveblog + hTMLJobStarted
//  }
//
//  def initialisePageForInteractive: String = {
//    hTMLPageHeader + hTMLTitleInteractive + hTMLJobStarted
//  }
//
//  def initialisePageForFronts: String = {
//    hTMLPageHeader + hTMLTitleFronts + hTMLJobStarted
//  }
//
//  def initialiseTable: String = {
//    hTMLSimpleTableHeaders
//  }
//
//  def interactiveTable: String = {
//    hTMLInteractiveTableHeaders
//  }




}
