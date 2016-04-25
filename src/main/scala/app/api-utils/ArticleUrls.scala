package app.apiutils

import com.gu.contentapi.client.GuardianContentClient
import com.gu.contentapi.client.model.SearchQuery
import com.gu.contentapi.client.model.v1.ContentFields
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class ArticleUrls(key: String) {
  val testApi:String = key
  println("testApi = " + testApi)
  val contentApiClient = new GuardianContentClient(key)

/*  def getUrlsForContentType(contentType: String): List[String] = {
     contentType match {
      //case("Article") => getArticles
      case ("LiveBlog") =>  getMinByMins
      case ("Interactive") => getInteractives
      case ("Video") => getVideoPages
      case ("Audio") => getAudioPages
      case("Front") => getFronts
      case (_) => {
        val empty: List[String] = List()
        empty
      }
    }
  }*/

  def shutDown = {
    println("Closing connection to Content API")
    contentApiClient.shutdown()
  }

  def getArticles: List[(Option[ContentFields], String)] = {
    val until = DateTime.now
    val from = until.minusHours(24)

    val searchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(0)
      .orderBy("newest")
      .contentType("article")
    println("Sending query to CAPI: \n" + searchQuery.toString)

    val apiResponse = contentApiClient.getResponse(searchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    val articleContentAndUrl: List[(Option[ContentFields],String)] = for (result <- returnedResponse.results) yield {
      (result.fields, result.webUrl) }
    println("CAPI Query Success - Article: \n" + articleContentAndUrl.map(element => element._2).mkString)
    articleContentAndUrl

  }

  def getMinByMins: List[(Option[ContentFields],String)] = {
    val until = DateTime.now
    val from = until.minusHours(24)

    val searchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(0)
      .orderBy("newest")
      .tag("tone/minutebyminute")
    println("Sending query to CAPI: \n" + searchQuery.toString)

    val apiResponse = contentApiClient.getResponse(searchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    val liveBlogContentAndUrl: List[(Option[ContentFields],String)] = for (result <- returnedResponse.results) yield {
      (result.fields, result.webUrl) }
    println("CAPI Query Success - LiveBlog: \n" + liveBlogContentAndUrl.map(element => element._2).mkString)
    liveBlogContentAndUrl
  }


  def getInteractives: List[(Option[ContentFields],String)] = {
    val until = DateTime.now
    val from = until.minusHours(24)

    val searchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(1)
      .orderBy("newest")
      .contentType("interactive")
    println("Sending query to CAPI: \n" + searchQuery.toString)

    val apiResponse = contentApiClient.getResponse(searchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))

    val interactiveContentAndUrl: List[(Option[ContentFields],String)] = for (result <- returnedResponse.results) yield {
      (result.fields, result.webUrl) }
    println("CAPI Query Success - Interactives: \n" + interactiveContentAndUrl.map(element => element._2).mkString)
    interactiveContentAndUrl
  }

  def getFronts: List[(Option[ContentFields],String)] = {
    val listofFronts: List[String] = List("http://www.theguardian.com/uk"/*,
      "http://www.theguardian.com/us",
      "http://www.theguardian.com/au",
      "http://www.theguardian.com/uk-news",
      "http://www.theguardian.com/world",
      "http://www.theguardian.com/politics",
      "http://www.theguardian.com/uk/sport",
      "http://www.theguardian.com/football",
      "http://www.theguardian.com/uk/commentisfree",
      "http://www.theguardian.com/uk/culture",
      "http://www.theguardian.com/uk/business",
      "http://www.theguardian.com/uk/lifeandstyle",
      "http://www.theguardian.com/fashion",
      "http://www.theguardian.com/uk/environment",
      "http://www.theguardian.com/uk/technology",
      "http://www.theguardian.com/travel"*/)
    val emptyContentFields: Option[ContentFields] = None
    val returnList:List[(Option[ContentFields],String)] = listofFronts.map(url => (emptyContentFields, url))
    println("CAPI Query Success - Fronts: \n" + returnList.map(element => element._2).mkString)
    returnList
  }


  def getVideoPages: List[(Option[ContentFields],String)] = {
    val until = DateTime.now
    val from = until.minusHours(24)

    val searchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(20)
      .orderBy("newest")
      .contentType("video")
    println("Sending query to CAPI: \n" + searchQuery.toString)

    val apiResponse = contentApiClient.getResponse(searchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    val videoContentAndUrl: List[(Option[ContentFields],String)] = for (result <- returnedResponse.results) yield {
      (result.fields, result.webUrl) }
    videoContentAndUrl
  }

  def getAudioPages: List[(Option[ContentFields],String)] = {
    val until = DateTime.now
    val from = until.minusHours(24)

    val liveBlogSearchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(20)
      .orderBy("newest")
      .contentType("audio")
    println("Sending query to CAPI: \n" + liveBlogSearchQuery.toString)

    val apiResponse = contentApiClient.getResponse(liveBlogSearchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    val audioContentAndUrl: List[(Option[ContentFields],String)] = for (result <- returnedResponse.results) yield {
      (result.fields, result.webUrl) }
    audioContentAndUrl
  }


  /*def getContentTypeFronts: List[String] = {
    println("Creating CAPI query")
    val until = DateTime.now
    val from = until.minusHours(24)

    val FrontsSearchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showBlocks("all")
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(20)
      .orderBy("newest")
      .contentType("front")
    println("Sending query to CAPI: \n" + FrontsSearchQuery.toString)

    val apiResponse = contentApiClient.getResponse(FrontsSearchQuery)
    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    println("CAPI has returned response")
    val liveBlogUrlString: List[String] = for (result <- returnedResponse.results) yield {
      println("liveBlog result: " + result.webUrl)
      result.webUrl }
    liveBlogUrlString
  }
*/

}

