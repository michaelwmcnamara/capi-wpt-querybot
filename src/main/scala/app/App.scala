package app

// note an _ instead of {} would get everything

import java.io._
import java.util

import app.api._
import app.apiutils._
import com.gu.contentapi.client.model.v1.{Office, MembershipTier, CapiDateTime, ContentFields}
import com.typesafe.config.{Config, ConfigFactory}
import org.joda.time.DateTime
import sbt.complete.Completion

import scala.collection.parallel.immutable.ParSeq
import scala.io.Source


object App {
  def main(args: Array[String]) {
    /*  This value stops the forces the config to be read and the output file to be written locally rather than reading and writing from/to S3
    #####################    this should be set to false before merging!!!!################*/
    val iamTestingLocally = false
    /*#####################################################################################*/
    println("Job started at: " + DateTime.now)
    println("Local Testing Flag is set to: " + iamTestingLocally.toString)

    //  Define names of s3bucket, configuration and output Files
    val amazonDomain = "https://s3-eu-west-1.amazonaws.com"
    val s3BucketName = "capi-wpt-querybot"
    val configFileName = "config.conf"
    val emailFileName = "addresses.conf"
    val interactiveSampleFileName = "interactivesamples.conf"

    val articleOutputFilename = "articleperformancedata.html"
    val liveBlogOutputFilename = "liveblogperformancedata.html"
    val interactiveOutputFilename = "interactiveperformancedata.html"
    val videoOutputFilename = "videoperformancedata.html"
    val audioOutputFilename = "audioperformancedata.html"
    val frontsOutputFilename = "frontsperformancedata.html"
    val combinedOutputFilename = "combinedperformancedata.html"
    val combinedDesktopFilename = "combineddesktopperformancedata.html"
    val combinedMobileFilename = "combinedmobileperformancedata.html"
    val editorialPageweightFilename = "editorialpageweightdashboard.html"
    val editorialCombinedPageweightFilename = "editorialpageweightdashboardcombined.html"
    val editorialDesktopPageweightFilename = "editorialpageweightdashboarddesktop.html"
    val editorialMobilePageweightFilename = "editorialpageweightdashboardmobile.html"

    val dotcomPageSpeedFilename = "dotcompagespeeddashboardmobile.html"

    val articleResultsUrl: String = amazonDomain + "/" + s3BucketName + "/" + articleOutputFilename
    val liveBlogResultsUrl: String = amazonDomain + "/" + s3BucketName + "/" + liveBlogOutputFilename
    val interactiveResultsUrl: String = amazonDomain + "/" + s3BucketName + "/" + interactiveOutputFilename
    val frontsResultsUrl: String = amazonDomain + "/" + s3BucketName + "/" + frontsOutputFilename

    val articleCSVName = "accumulatedArticlePerformanceData.csv"
    val liveBlogCSVName = "accumulatedLiveblogPerformanceData.csv"
    val interactiveCSVName = "accumulatedInteractivePerformanceData.csv"
    val videoCSVName = "accumulatedVideoPerformanceData.csv"
    val audioCSVName = "accumulatedAudioPerformanceData.csv"
    val frontsCSVName = "accumulatedFrontsPerformanceData.csv"

    val resultsFromPreviousTests = "resultsFromPreviousTests.csv"


    //Define colors to be used for average values, warnings and alerts
    val averageColor: String = "#d9edf7"
    //    val warningColor: String = "#fcf8e3"
    val warningColor: String = "rgba(227, 251, 29, 0.32)"
    val alertColor: String = "#f2dede"

    //initialize combinedResultsLists - these will be used to sort and accumulate test results
    // for the combined page and for long term storage file
    var combinedResultsList: List[PerformanceResultsObject] = List()
    var combinedPreviousResultsList: List[PerformanceResultsObject] = List()
    var combinedResultsLast24Hours: List[PerformanceResultsObject] = List()
    var combinedOldResults: List[PerformanceResultsObject] = List()
    var combinedRecentAlertOrLive: List[PerformanceResultsObject] = List()
    var combinedRecentStatic: List[PerformanceResultsObject] = List()

    //  Initialize results string - this will be used to accumulate the results from each test so that only one write to file is needed.
    val htmlString = new HtmlStringOperations(averageColor, warningColor, alertColor, articleResultsUrl, liveBlogResultsUrl, interactiveResultsUrl, frontsResultsUrl)
    val newhtmlString = new HtmlReportBuilder(averageColor, warningColor, alertColor, articleResultsUrl, liveBlogResultsUrl, interactiveResultsUrl, frontsResultsUrl)
    var articleResults: String = htmlString.initialisePageForLiveblog + htmlString.initialiseTable
    var liveBlogResults: String = htmlString.initialisePageForLiveblog + htmlString.initialiseTable
    var interactiveResults: String = htmlString.initialisePageForInteractive + htmlString.interactiveTable
    var frontsResults: String = htmlString.initialisePageForFronts + htmlString.initialiseTable
    var audioResults: String = htmlString.initialisePageForLiveblog + htmlString.initialiseTable
    var videoResults: String = htmlString.initialisePageForLiveblog + htmlString.initialiseTable

    //Initialize email alerts string - this will be used to generate emails

    var articleAlertList: List[PerformanceResultsObject] = List()
    var liveBlogAlertList: List[PerformanceResultsObject] = List()
    var interactiveAlertList: List[PerformanceResultsObject] = List()
    var frontsAlertList: List[PerformanceResultsObject] = List()
    var audioAlertList: List[PerformanceResultsObject] = List()
    var videoAlertList: List[PerformanceResultsObject] = List()

    // var articleCSVResults: String = ""
    //  var liveBlogCSVResults: String = ""
    // var interactiveCSVResults: String = ""
    //  var videoCSVResults: String = ""
    //  var audioCSVResults: String = ""
    //  var frontsCSVResults: String = ""



    //Create new S3 Client
    println("defining new S3 Client (this is done regardless but only used if 'iamTestingLocally' flag is set to false)")
    val s3Interface = new S3Operations(s3BucketName, configFileName, emailFileName)
    var configArray: Array[String] = Array("", "", "", "", "", "")
    var urlFragments: List[String] = List()

    //Get config settings
    println("Extracting configuration values")
    if (!iamTestingLocally) {
      println(DateTime.now + " retrieving config from S3 bucket: " + s3BucketName)
      val returnTuple = s3Interface.getConfig
      configArray = Array(returnTuple._1,returnTuple._2,returnTuple._3,returnTuple._4,returnTuple._5,returnTuple._6,returnTuple._7)
      urlFragments = returnTuple._8
    }
    else {
      println(DateTime.now + " retrieving local config file: " + configFileName)
      val configReader = new LocalFileOperations
      configArray = configReader.readInConfig(configFileName)
    }
    println("checking validity of config values")
    if ((configArray(0).length < 1) || (configArray(1).length < 1) || (configArray(2).length < 1) || (configArray(3).length < 1)) {
      println("problem extracting config\n" +
        "contentApiKey length: " + configArray(0).length + "\n" +
        "wptBaseUrl length: " + configArray(1).length + "\n" +
        "wptApiKey length: " + configArray(2).length + "\n" +
        "wptLocation length: " + configArray(3).length + "\n" +
        "emailUsername length: " + configArray(4).length + "\n" +
        "emailPassword length: " + configArray(5).length) + "\n" +
        "visuals URL length: " + configArray(6).length

      System exit 1
    }
    println("config values ok")
    val contentApiKey: String = configArray(0)
    val wptBaseUrl: String = configArray(1)
    val wptApiKey: String = configArray(2)
    val wptLocation: String = configArray(3)
    val emailUsername: String = configArray(4)
    val emailPassword: String = configArray(5)
    val visualsApiUrl: String = configArray(6)

    //obtain list of email addresses for alerting
    val emailAddresses: Array[List[String]] = s3Interface.getEmailAddresses
    val generalAlertsAddressList: List[String] = emailAddresses(0)
    val interactiveAlertsAddressList: List[String] = emailAddresses(1)

    //obtain list of interactive samples to determine average size
    //val listofLargeInteractives: List[String] = s3Interface.getUrls(interactiveSampleFileName)

    //obtain list of items previously alerted on
    val previousResults: List[PerformanceResultsObject] = {
      if (iamTestingLocally) {
        List()
      } else {
        s3Interface.getResultsFileFromS3(resultsFromPreviousTests)
      }
    }

    //split old results into recent results and old results
    val cutoffTime: Long = DateTime.now.minusHours(24).getMillis
    val resultsFromLast24Hours = for (result <- previousResults if (result.getFirstPublished >= cutoffTime) || (result.getPageLastUpdated >= cutoffTime)) yield result
    val oldResults = for (result <- previousResults if (result.getFirstPublished < cutoffTime) && (result.getPageLastUpdated < cutoffTime)) yield result

    val previousResultsToRetest: List[PerformanceResultsObject] = for (result <- resultsFromLast24Hours if (result.alertStatusPageWeight || result.getLiveBloggingNow)) yield result
    val unchangedPreviousResults: List[PerformanceResultsObject] = for (result <- resultsFromLast24Hours if (!(result.alertStatusPageWeight || result.getLiveBloggingNow))) yield result

    //validate list handling

    if (!(((previousResultsToRetest.length + unchangedPreviousResults.length) == resultsFromLast24Hours.length) && ((resultsFromLast24Hours.length + oldResults.length) == previousResults.length))) {
      println("ERROR: previous results list handling is borked!")
      println("Previous Results to retest length == " + previousResultsToRetest.length + "\n")
      println("Unchanged previous results length == " + unchangedPreviousResults.length + "\n")
      println("Results from last 24 hours length == " + resultsFromLast24Hours.length + "\n")
      println("Old results length == " + oldResults.length + "\n")
      println("Original list of previous results length == " + previousResults.length + "\n")
      if(!((previousResultsToRetest.length + unchangedPreviousResults.length) == resultsFromLast24Hours.length)){
        println("Results to test and unchanged results from last 24 hours dont add up correctly \n")
      }
      if(!((resultsFromLast24Hours.length + oldResults.length) == previousResults.length)){
        println("Results from last 24 hours and old results dont add up \n")
      }
      System.exit(1)
    }
    println("Retrieved results from file\n")
    println(previousResults.length + " results retrieved in total")
    println(resultsFromLast24Hours.length + " results for last 24 hours")
    println(previousResultsToRetest.length + " results will be retested")
    println(unchangedPreviousResults.length + " results will be listed but not tested")
    //Create Email Handler class
    val emailer: EmailOperations = new EmailOperations(emailUsername, emailPassword)

    //  Define new CAPI Query object
    val capiQuery = new ArticleUrls(contentApiKey)
    //get all content-type-lists
    val articles: List[(Option[ContentFields],String)] = capiQuery.getUrlsForContentType("Article")
    val liveBlogs: List[(Option[ContentFields],String)] = capiQuery.getUrlsForContentType("LiveBlog")
    val interactives: List[(Option[ContentFields],String)] = capiQuery.getUrlsForContentType("Interactive")
    val fronts:  List[(Option[ContentFields],String)] = capiQuery.getUrlsForContentType("Front")
    val videoPages: List[(Option[ContentFields],String)] = capiQuery.getUrlsForContentType("Video")
    val audioPages: List[(Option[ContentFields],String)] = capiQuery.getUrlsForContentType("Audio")
    println(DateTime.now + " Closing Content API query connection")
    capiQuery.shutDown

    println("CAPI call summary: \n")
    println("Retrieved: " + articles.length + " article pages")
    println("Retrieved: " + liveBlogs.length + " liveblog pages")
    println("Retrieved: " + interactives.length + " intearactive pages")
    println("Retrieved: " + fronts.length + " fronts")
    println("Retrieved: " + videoPages.length + " video pages")
    println("Retrieved: " + audioPages.length + " audio pages")
    println((articles.length + liveBlogs.length + interactives.length + fronts.length + videoPages.length + audioPages.length) + " pages returned in total")

    val combinedCapiResults = articles ::: liveBlogs ::: interactives ::: fronts
    val dedupedResultsToRetest = for (result <- previousResultsToRetest if !combinedCapiResults.map(_._2).contains(result.testUrl)) yield result
    val dedupedUnchangedResults = for (result <- unchangedPreviousResults if !combinedCapiResults.map(_._2).contains(result.testUrl)) yield result

    println("Summary of pages after comparing CAPI response with existing test results: \n")
    println(dedupedResultsToRetest.length + " pages to be tested - this includes previous alerts, liveblogs, updated pages and new CAPI results")
    println(dedupedUnchangedResults.length + " pages that will be displayed but not tested - these are unchanged pages to which we already have results")
    
    val dedupedPreviousAlertUrls: List[String] = for (result <- dedupedResultsToRetest) yield result.testUrl
    val articleUrls: List[String] = for (page <- articles) yield page._2
    val liveBlogUrls: List[String] = for (page <- liveBlogs) yield page._2
    val interactiveUrls: List[String] = for (page <- interactives) yield page._2
    val frontsUrls: List[String] = for (page <- fronts) yield page._2
    val videoUrls: List[String] = for (page <- videoPages) yield page._2
    val audioUrls: List[String] = for (page <- audioPages) yield page._2

    //get all pages from the visuals team api


    // send all urls to webpagetest at once to enable parallel testing by test agents
    val urlsToSend: List[String] = (dedupedPreviousAlertUrls ::: articleUrls ::: liveBlogUrls ::: interactiveUrls ::: frontsUrls).distinct
    println("Combined list of urls: \n" + urlsToSend)

    val resultUrlList: List[(String, String)] = getResultPages(urlsToSend, urlFragments, wptBaseUrl, wptApiKey, wptLocation)

    // build result page listeners
    // first format alerts from previous test that arent in the new capi queries
    val dedupedPreviousArticles: List[PerformanceResultsObject] = for (result <- dedupedResultsToRetest if result.getPageType.contains("Article")) yield result
    val dedupedPreviousLiveBlogs: List[PerformanceResultsObject] = for (result <- dedupedResultsToRetest if result.getPageType.contains("LiveBlog")) yield result
    val dedupedPreviousInteractives: List[PerformanceResultsObject] = for (result <- dedupedResultsToRetest if result.getPageType.contains("Interactive")) yield result

    // munge into proper format and merge these with the capi results
    val articleContentFieldsAndUrl = dedupedPreviousArticles.map(result => (Option(makeContentStub(result.headline, result.pageLastUpdated, result.liveBloggingNow)), result.testUrl))
    val liveBlogContentFieldsAndUrl = dedupedPreviousLiveBlogs.map(result => (Option(makeContentStub(result.headline, result.pageLastUpdated, result.liveBloggingNow)), result.testUrl))
    val interactiveContentFieldsAndUrl = dedupedPreviousInteractives.map(result => (Option(makeContentStub(result.headline, result.pageLastUpdated, result.liveBloggingNow)), result.testUrl))

    val combinedArticleList: List[(Option[ContentFields],String)] = articleContentFieldsAndUrl ::: articles
    val combinedLiveBlogList: List[(Option[ContentFields],String)] = liveBlogContentFieldsAndUrl ::: liveBlogs
    val combinedInteractiveList: List[(Option[ContentFields],String)] = interactiveContentFieldsAndUrl ::: interactives


    //obtain results for articles
    if (combinedArticleList.nonEmpty) {
      println("Generating average values for articles")
      val articleAverages: PageAverageObject = new ArticleDefaultAverages(averageColor)
      articleResults = articleResults.concat(articleAverages.toHTMLString)

      val articleResultsList = listenForResultPages(combinedArticleList, "article", resultUrlList, articleAverages, wptBaseUrl, wptApiKey, wptLocation, urlFragments)
      combinedResultsList = articleResultsList

      println("About to sort article results list. Length of list is: " + articleResultsList.length)
      val sortedByWeightArticleResultsList = orderListByWeight(articleResultsList)
      val sortedBySpeedArticleResultsList = orderListBySpeed(articleResultsList)
      if(sortedByWeightArticleResultsList.isEmpty || sortedBySpeedArticleResultsList.isEmpty) {
        println("Sorting algorithm for articles has returned empty list. Aborting")
        System exit 1
      }
      val articleHTMLResults: List[String] = sortedByWeightArticleResultsList.map(x => htmlString.generateHTMLRow(x))
      // write article results to string
      //Create a list of alerting pages and write to string
      articleAlertList = for (result <- sortedByWeightArticleResultsList if result.alertStatusPageWeight) yield result
      articleResults = articleResults.concat(articleHTMLResults.mkString)
      articleResults = articleResults + htmlString.closeTable + htmlString.closePage
      //write article results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing article results to S3")
        s3Interface.writeFileToS3(articleOutputFilename, articleResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(articleOutputFilename, articleResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("Article Performance Test Complete")

    } else {
      println("CAPI query found no article pages")
    }

    //obtain results for liveBlogs
    if (combinedLiveBlogList.nonEmpty) {
      println("Generating average values for liveblogs")
      val liveBlogAverages: PageAverageObject = new LiveBlogDefaultAverages(averageColor)
      liveBlogResults = liveBlogResults.concat(liveBlogAverages.toHTMLString)

      val liveBlogResultsList = listenForResultPages(combinedLiveBlogList, "liveBlog", resultUrlList, liveBlogAverages, wptBaseUrl, wptApiKey, wptLocation, urlFragments)
      combinedResultsList = combinedResultsList ::: liveBlogResultsList
      val sortedLiveBlogResultsList = orderListByWeight(liveBlogResultsList)
      if(sortedLiveBlogResultsList.isEmpty) {
        println("Sorting algorithm for Liveblogs has returned empty list. Aborting")
        System exit 1
      }
      val liveBlogHTMLResults: List[String] = sortedLiveBlogResultsList.map(x => htmlString.generateHTMLRow(x))
      // write liveblog results to string
      //Create a list of alerting pages and write to string
      liveBlogAlertList = for (result <- sortedLiveBlogResultsList if result.alertStatusPageWeight) yield result

      liveBlogResults = liveBlogResults.concat(liveBlogHTMLResults.mkString)
      liveBlogResults = liveBlogResults + htmlString.closeTable + htmlString.closePage
      //write liveblog results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing liveblog results to S3")
        s3Interface.writeFileToS3(liveBlogOutputFilename, liveBlogResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(liveBlogOutputFilename, liveBlogResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("LiveBlog Performance Test Complete")

    } else {
      println("CAPI query found no liveblogs")
    }

    if (combinedInteractiveList.nonEmpty) {
      println("Generating average values for interactives")
//      val interactiveAverages: PageAverageObject = generateInteractiveAverages(listofLargeInteractives, wptBaseUrl, wptApiKey, wptLocation, interactiveItemLabel, averageColor)
      val interactiveAverages: PageAverageObject = new InteractiveDefaultAverages(averageColor)
      interactiveResults = interactiveResults.concat(interactiveAverages.toHTMLString)

      val interactiveResultsList = listenForResultPages(combinedInteractiveList, "interactive", resultUrlList, interactiveAverages, wptBaseUrl, wptApiKey, wptLocation, urlFragments)
      combinedResultsList = combinedResultsList ::: interactiveResultsList
      val sortedInteractiveResultsList = orderListByWeight(interactiveResultsList)
      if(sortedInteractiveResultsList.isEmpty) {
        println("Sorting algorithm has returned empty list. Aborting")
        System exit 1
      }
      val interactiveHTMLResults: List[String] = sortedInteractiveResultsList.map(x => htmlString.interactiveHTMLRow(x))
      //generate interactive alert message body
      interactiveAlertList = for (result <- sortedInteractiveResultsList if result.alertStatusPageWeight) yield result
      // write interactive results to string
      interactiveResults = interactiveResults.concat(interactiveHTMLResults.mkString)
      interactiveResults = interactiveResults + htmlString.closeTable + htmlString.closePage
      //write interactive results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing interactive results to S3")
        s3Interface.writeFileToS3(interactiveOutputFilename, interactiveResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(interactiveOutputFilename, interactiveResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("Interactive Performance Test Complete")

    } else {
      println("CAPI query found no interactives")
    }

    if (frontsUrls.nonEmpty) {
      println("Generating average values for fronts")
      val frontsAverages: PageAverageObject = new FrontsDefaultAverages(averageColor)
      frontsResults = frontsResults.concat(frontsAverages.toHTMLString)

      val frontsResultsList = listenForResultPages(fronts, "front", resultUrlList, frontsAverages, wptBaseUrl, wptApiKey, wptLocation, urlFragments)
//      combinedResultsList = combinedResultsList ::: frontsResultsList
      val sortedFrontsResultsList = orderListByWeight(frontsResultsList)
      if(sortedFrontsResultsList.isEmpty) {
        println("Sorting algorithm for fronts has returned empty list. Aborting")
        System exit 1
      }
      val frontsHTMLResults: List[String] = sortedFrontsResultsList.map(x => htmlString.generateHTMLRow(x))
      //Create a list of alerting pages and write to string
      frontsAlertList = for (result <- sortedFrontsResultsList if result.alertStatusPageWeight) yield result

      // write fronts results to string
      frontsResults = frontsResults.concat(frontsHTMLResults.mkString)
      frontsResults = frontsResults + htmlString.closeTable + htmlString.closePage
      //write fronts results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing fronts results to S3")
        s3Interface.writeFileToS3(frontsOutputFilename, frontsResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(frontsOutputFilename, frontsResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("Fronts Performance Test Complete")

    } else {
      println("CAPI query found no Fronts")
    }

    val sortedByWeightCombinedResults: List[PerformanceResultsObject] = orderListByWeight(combinedResultsList ::: dedupedUnchangedResults)
    val combinedDesktopResultsList: List[PerformanceResultsObject] = for (result <- combinedResultsList if result.typeOfTest.contains("Desktop")) yield result
    val combinedMobileResultsList: List[PerformanceResultsObject] = for (result <- combinedResultsList if result.typeOfTest.contains("Android/3G")) yield result

    //Generate lists for sortByWeight combined pages

    val sortedByWeightCombinedDesktopResults: List[PerformanceResultsObject] = sortHomogenousResultsByWeight(combinedDesktopResultsList)
    val sortedCombinedByWeightMobileResults: List[PerformanceResultsObject] = sortHomogenousResultsByWeight(combinedMobileResultsList)
    val combinedHTMLResults: List[String] = sortedByWeightCombinedResults.map(x => htmlString.generateHTMLRow(x))
    val combinedDesktopHTMLResults: List[String] = sortedByWeightCombinedDesktopResults.map(x => htmlString.generateHTMLRow(x))
    val combinedMobileHTMLResults: List[String] = sortedCombinedByWeightMobileResults.map(x => htmlString.generateHTMLRow(x))

    val combinedBasicHTMLResults: List[String] = sortedByWeightCombinedResults.map(x => htmlString.generatePageWeightDashboardHTMLRow(x))
    val combinedBasicDesktopHTMLResults: List[String] = sortedByWeightCombinedDesktopResults.map(x => htmlString.generatePageWeightDashboardHTMLRow(x))
    val combinedBasicMobileHTMLResults: List[String] = sortedCombinedByWeightMobileResults.map(x => htmlString.generatePageWeightDashboardHTMLRow(x))

    val combinedResults: String = htmlString.initialisePageForCombined +
        htmlString.initialiseTable +
        combinedHTMLResults.mkString +
        htmlString.closeTable + htmlString.closePage

    val combinedDesktopResults: String = htmlString.initialisePageForCombined +
        htmlString.initialiseTable +
        combinedDesktopHTMLResults.mkString +
        htmlString.closeTable + htmlString.closePage

    val combinedMobileResults: String = htmlString.initialisePageForCombined +
        htmlString.initialiseTable +
        combinedMobileHTMLResults.mkString +
        htmlString.closeTable + htmlString.closePage

    val editorialPageWeightDashboardCombined = new PageWeightDashboardCombined(sortedByWeightCombinedResults)
    val editorialPageWeightDashboardDesktop = new PageWeightDashboardDesktop(sortedByWeightCombinedDesktopResults)
    val editorialPageWeightDashboardMobile = new PageWeightDashboardMobile(sortedCombinedByWeightMobileResults)
    val editorialPageWeightDashboard = new PageWeightDashboardTabbed(sortedByWeightCombinedResults, sortedByWeightCombinedDesktopResults, sortedCombinedByWeightMobileResults)

//  strip out errors
    val errorFreeSortedByWeightCombinedResults = for (result <- sortedByWeightCombinedDesktopResults if(result.speedIndex != -1)) yield result
//record results
    val resultsToRecord = (errorFreeSortedByWeightCombinedResults ::: oldResults).take(1000)
    val resultsToRecordCSVString: String = resultsToRecord.map(_.toCSVString()).mkString

//Generate Lists for sortBySpeed combined pages
    val sortedBySpeedCombinedResults: List[PerformanceResultsObject] = orderListBySpeed(combinedResultsList ::: dedupedUnchangedResults)
    val sortedBySpeedCombinedDesktopResults: List[PerformanceResultsObject] = sortHomogenousResultsBySpeed(combinedDesktopResultsList)
    val sortedBySpeedCombinedMobileResults: List[PerformanceResultsObject] = sortHomogenousResultsBySpeed(combinedMobileResultsList)


    val dotcomPageSpeedDashboard = new PageSpeedDashboardTabbed(sortedBySpeedCombinedResults, sortedBySpeedCombinedDesktopResults, sortedBySpeedCombinedMobileResults)

      //write combined results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing liveblog results to S3")
        s3Interface.writeFileToS3(combinedOutputFilename, combinedResults)
        s3Interface.writeFileToS3(combinedDesktopFilename, combinedDesktopResults)
        s3Interface.writeFileToS3(combinedMobileFilename, combinedMobileResults)

        s3Interface.writeFileToS3(editorialCombinedPageweightFilename, editorialPageWeightDashboardCombined.toString())
        s3Interface.writeFileToS3(editorialDesktopPageweightFilename, editorialPageWeightDashboardDesktop.toString())
        s3Interface.writeFileToS3(editorialMobilePageweightFilename, editorialPageWeightDashboardMobile.toString())
        s3Interface.writeFileToS3(editorialPageweightFilename, editorialPageWeightDashboard.toString())
        s3Interface.writeFileToS3(dotcomPageSpeedFilename, dotcomPageSpeedDashboard.toString())
        s3Interface.writeFileToS3(resultsFromPreviousTests, resultsToRecordCSVString)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccessCombined: Int = outputWriter.writeLocalResultFile(combinedOutputFilename, combinedResults)
        if (writeSuccessCombined != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
        val writeSuccessCMobile: Int = outputWriter.writeLocalResultFile(combinedDesktopFilename, combinedDesktopResults)
        if (writeSuccessCMobile != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
        val writeSuccessCDesktop: Int = outputWriter.writeLocalResultFile(combinedMobileFilename, combinedMobileResults)
        if (writeSuccessCDesktop != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
        val writeSuccessPWDC: Int = outputWriter.writeLocalResultFile(editorialPageweightFilename, editorialPageWeightDashboard.toString())
        if (writeSuccessPWDC != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
        val writeSuccessPWDD: Int = outputWriter.writeLocalResultFile(editorialDesktopPageweightFilename, editorialPageWeightDashboardDesktop.toString())
        if (writeSuccessPWDD != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
        val writeSuccessPWDM: Int = outputWriter.writeLocalResultFile(editorialMobilePageweightFilename, editorialPageWeightDashboardMobile.toString())
        if (writeSuccessPWDM != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
        val writeSuccessDCPSD: Int = outputWriter.writeLocalResultFile(dotcomPageSpeedFilename, dotcomPageSpeedDashboard.toString())
        if (writeSuccessPWDM != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
        val writeSuccessAlertsRecord: Int = outputWriter.writeLocalResultFile(resultsFromPreviousTests, resultsToRecordCSVString)
        if (writeSuccessAlertsRecord != 0) {
          println("problem writing local outputfile")
          System exit 1
        }

      }



    //check if alert items have already been sent in earlier run
    val newArticleAlertsList: List[PerformanceResultsObject] = for (result <- articleAlertList if !previousResultsToRetest.map(_.testUrl).contains(result.testUrl)) yield result
    val newLiveBlogAlertsList: List[PerformanceResultsObject] = for (result <- liveBlogAlertList if !previousResultsToRetest.map(_.testUrl).contains(result.testUrl)) yield result
    val newInteractiveAlertsList: List[PerformanceResultsObject] = for (result <- interactiveAlertList if !previousResultsToRetest.map(_.testUrl).contains(result.testUrl)) yield result
    val newFrontsAlertsList: List[PerformanceResultsObject] = for (result <- frontsAlertList if !previousResultsToRetest.map(_.testUrl).contains(result.testUrl)) yield result



    /*    if (newArticleAlertsList.nonEmpty || newLiveBlogAlertsList.nonEmpty || newFrontsAlertsList.nonEmpty) {
      println("\n\n articleAlertList contains: " + newArticleAlertsList.length + " pages")
      println("\n\n liveBlogAlertList contains: " + newLiveBlogAlertsList.length + " pages")
      println("\n\n frontsAlertList contains: " + newFrontsAlertsList.length + " pages")
      val articleAlertMessageBody: String = htmlString.generateAlertEmailBodyElement(newArticleAlertsList)
      val liveBlogAlertMessageBody: String = htmlString.generateAlertEmailBodyElement(newLiveBlogAlertsList)
      val frontsAlertMessageBody: String = htmlString.generateAlertEmailBodyElement(newFrontsAlertsList)
      println("\n\n ***** \n\n" + "Full email Body:\n" + htmlString.generalAlertFullEmailBody(articleAlertMessageBody, liveBlogAlertMessageBody, frontsAlertMessageBody))
      println("compiling and sending email")
      val emailSuccess = emailer.send(generalAlertsAddressList, htmlString.generalAlertFullEmailBody(articleAlertMessageBody, liveBlogAlertMessageBody, frontsAlertMessageBody))
      if (emailSuccess)
        println(DateTime.now + " General Alert Emails sent successfully. ")
      else
        println(DateTime.now + "ERROR: Job completed, but sending of general Alert Emails failed")
    } else {
      println("No pages to alert on. Email not sent. \n Job complete")
    }

    if (newInteractiveAlertsList.nonEmpty) {
      println("\n\n interactiveAlertList contains: " + newInteractiveAlertsList.length + " pages")

      val interactiveAlertMessageBody: String = htmlString.generateInteractiveAlertBodyElement(newInteractiveAlertsList)
      println("\n\n ***** \n\n" + "Full interactive email Body:\n" + htmlString.interactiveAlertFullEmailBody(interactiveAlertMessageBody))
      println("compiling and sending email")
      val emailSuccess = emailer.send(interactiveAlertsAddressList, htmlString.interactiveAlertFullEmailBody(interactiveAlertMessageBody))
      if (emailSuccess)
        println(DateTime.now + " Interactive Emails sent successfully. \n Job complete")
      else
        println(DateTime.now + "ERROR: Job completed, but sending of Interactve Emails failed")
    } else {
      println("No pages to alert on. Email not sent. \n Job complete")
    }*/
    val alertsToSend = newArticleAlertsList ::: newLiveBlogAlertsList ::: newInteractiveAlertsList
    if (alertsToSend.nonEmpty) {
      val emailContent = new PageWeightEmailTemplate(newArticleAlertsList ::: newLiveBlogAlertsList ::: newInteractiveAlertsList)

      val emailSuccess = emailer.send(generalAlertsAddressList, emailContent.toString())
      if (emailSuccess)
        println(DateTime.now + " General Alert Emails sent successfully. ")
      else
        println(DateTime.now + "ERROR: Job completed, but sending of general Alert Emails failed")
    }else {
      println("No pages to alert on. Email not sent. \n Job complete")
    }

  }

  def getResultPages(urlList: List[String], urlFragments: List[String], wptBaseUrl: String, wptApiKey: String, wptLocation: String): List[(String, String)] = {
    val wpt: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey, urlFragments)
    val desktopResults: List[(String, String)] = urlList.map(page => {
      (page, wpt.sendPage(page))
    })
    val mobileResults: List[(String, String)] = urlList.map(page => {
      (page, wpt.sendMobile3GPage(page, wptLocation))
    })
    desktopResults ::: mobileResults
  }

  def listenForResultPages(capiPages: List[(Option[ContentFields],String)], contentType: String, resultUrlList: List[(String, String)], averages: PageAverageObject, wptBaseUrl: String, wptApiKey: String, wptLocation: String, urlFragments: List[String]): List[PerformanceResultsObject] = {
    println("ListenForResultPages called with: \n\n" +
      " List of Urls: \n" + capiPages.map(page => page._2).mkString +
      "\n\nList of WebPage Test results: \n" + resultUrlList.mkString +
      "\n\nList of averages: \n" + averages.toHTMLString + "\n")

    val listenerList: List[WptResultPageListener] = capiPages.flatMap(page => {
      for (element <- resultUrlList if element._1 == page._2) yield new WptResultPageListener(element._1, contentType, page._1, element._2)
    })

    println("Listener List created: \n" + listenerList.map(element => "list element: \n" + "url: " + element.pageUrl + "\n" + "resulturl" + element.wptResultUrl + "\n"))

    val resultsList: ParSeq[WptResultPageListener] = listenerList.par.map(element => {
      val wpt = new WebPageTest(wptBaseUrl, wptApiKey, urlFragments)
      val newElement = new WptResultPageListener(element.pageUrl, element.pageType, element.pageFields,element.wptResultUrl)
      println("getting result for page element")
      newElement.testResults = wpt.getResults(newElement.wptResultUrl)
      println("result received\n setting headline")
      newElement.testResults.setHeadline(newElement.headline)
      println("headline set\n setting pagetype")
      newElement.testResults.setPageType(newElement.pageType)
      println("pagetype set\n setting FirstPublished")
      newElement.testResults.setFirstPublished(newElement.firstPublished)
      println("FirstPublished set\n setting LastUpdated")
      newElement.testResults.setPageLastUpdated(newElement.pageLastModified)
      println("Lastupdated set\n setting LiveBloggingNow")
      newElement.testResults.setLiveBloggingNow(newElement.liveBloggingNow.getOrElse(false))
      println("all variables set for element")
      newElement
    })
    val testResults = resultsList.map(element => element.testResults).toList
    val resultsWithAlerts: List[PerformanceResultsObject] = testResults.map(element => setAlertStatus(element, averages))

    //Confirm alert status by retesting alerting urls
    println("Confirming any items that have an alert")
    val confirmedTestResults = resultsWithAlerts.map(x => {
      if (x.alertStatusPageWeight) {
        val confirmedResult: PerformanceResultsObject = confirmAlert(x, averages, urlFragments, wptBaseUrl, wptApiKey ,wptLocation)
        confirmedResult.headline = x.headline
        confirmedResult.pageType = x.pageType
        confirmedResult.firstPublished = x.firstPublished
        confirmedResult.pageLastUpdated = x.pageLastUpdated
        confirmedResult.liveBloggingNow = x.liveBloggingNow
        confirmedResult
      }
      else
        x
    })
    confirmedTestResults
  }

  def confirmAlert(initialResult: PerformanceResultsObject, averages: PageAverageObject, urlFragments: List[String],wptBaseUrl: String, wptApiKey: String, wptLocation: String): PerformanceResultsObject = {
    val webPageTest = new WebPageTest(wptBaseUrl, wptApiKey, urlFragments)
    val testCount: Int = if (initialResult.timeToFirstByte > 1000) {
      5
    } else {
      3
    }
    println("TTFB for " + initialResult.testUrl + "\n therefore setting test count of: " + testCount)
    val AlertConfirmationTestResult: PerformanceResultsObject = setAlertStatus(webPageTest.testMultipleTimes(initialResult.testUrl, initialResult.typeOfTest, wptLocation, testCount), averages)
    AlertConfirmationTestResult
  }

  def setAlertStatus(resultObject: PerformanceResultsObject, averages: PageAverageObject): PerformanceResultsObject = {
    //  Add results to string which will eventually become the content of our results file
    if (resultObject.typeOfTest == "Desktop") {
      if (resultObject.kBInFullyLoaded >= averages.desktopKBInFullyLoaded) {
        println("PageWeight Alert Set")
        resultObject.alertDescription = "the page is too heavy. Please examine the list of embeds below for items that are unexpectedly large."
        resultObject.alertStatusPageWeight = true
      }
      else {
        println("PageWeight Alert not set")
        resultObject.alertStatusPageWeight = false
      }
      if ((resultObject.timeFirstPaintInMs >= averages.desktopTimeFirstPaintInMs) ||
          (resultObject.speedIndex >= averages.desktopSpeedIndex)) {
        println("PageSpeed alert set")
        resultObject.alertStatusPageSpeed = true
        if ((resultObject.timeFirstPaintInMs >= averages.desktopTimeFirstPaintInMs) && (resultObject.speedIndex >= averages.desktopSpeedIndex)) {
          resultObject.alertDescription = "Time till page is scrollable (time-to-first-paint) and time till page looks loaded (SpeedIndex) are unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."
        } else {
          if (resultObject.speedIndex >= averages.desktopSpeedIndex) {
            resultObject.alertDescription = "Time till page looks loaded (SpeedIndex) is unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."
          }
          else {
            resultObject.alertDescription = "Time till page is scrollable (time-to-first-paint) is unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."
          }
        }
      } else {
        println("PageSpeed alert not set")
        resultObject.alertStatusPageSpeed = false
      }
    } else {
      //checking if status of mobile test needs an alert
      if (resultObject.kBInFullyLoaded >= averages.mobileKBInFullyLoaded) {
        println("PageWeight Alert Set")
        resultObject.alertDescription = "the page is too heavy. Please examine the list of embeds below for items that are unexpectedly large."
        resultObject.alertStatusPageWeight = true
      }
      else {
        println("PageWeight Alert not set")
        resultObject.alertStatusPageWeight = false
      }
      if ((resultObject.timeFirstPaintInMs >= averages.mobileTimeFirstPaintInMs) ||
        (resultObject.speedIndex >= averages.mobileSpeedIndex)) {
        println("PageSpeed alert set")
        resultObject.alertStatusPageSpeed = true
        if ((resultObject.timeFirstPaintInMs >= averages.mobileTimeFirstPaintInMs) && (resultObject.speedIndex >= averages.mobileSpeedIndex)) {
          resultObject.alertDescription = "Time till page is scrollable (time-to-first-paint) and time till page looks loaded (SpeedIndex) are unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."
        } else {
          if (resultObject.speedIndex >= averages.mobileSpeedIndex) {
            resultObject.alertDescription = "Time till page looks loaded (SpeedIndex) is unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."
          }
          else {
            resultObject.alertDescription = "Time till page is scrollable (time-to-first-paint) is unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."
          }
        }
      } else {
        println("PageSpeed alert not set")
        resultObject.alertStatusPageSpeed = false
      }
    }
    println("Returning test result with alert flags set to relevant values")
    resultObject
  }

  def generateInteractiveAverages(urlList: List[String], wptBaseUrl: String, wptApiKey: String, wptLocation: String, urlFragments: List[String], itemtype: String, averageColor: String): PageAverageObject = {
    val setHighPriority: Boolean = true
    val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey, urlFragments)

    val resultsList: List[Array[PerformanceResultsObject]] = urlList.map(url => {
      val webPageDesktopTestResults: PerformanceResultsObject = webpageTest.desktopChromeCableTest(url, setHighPriority)
      val webPageMobileTestResults: PerformanceResultsObject = webpageTest.mobileChrome3GTest(url, wptLocation, setHighPriority)
      val combinedResults = Array(webPageDesktopTestResults, webPageMobileTestResults)
      combinedResults
    })

    val pageAverages: PageAverageObject = new GeneratedInteractiveAverages(resultsList, averageColor)
    pageAverages
  }


  def retestUrl(initialResult: PerformanceResultsObject, wptBaseUrl: String, wptApiKey: String, wptLocation: String, urlFragments: List[String]): PerformanceResultsObject = {
    val webPageTest = new WebPageTest(wptBaseUrl, wptApiKey, urlFragments)
    val testCount: Int = if (initialResult.timeToFirstByte > 1000) {
      5
    } else {
      3
    }
    println("TTFB for " + initialResult.testUrl + "\n therefore setting test count of: " + testCount)
    //   val AlertConfirmationTestResult: PerformanceResultsObject = setAlertStatusPageWeight(webPageTest.testMultipleTimes(initialResult.testUrl, initialResult.typeOfTest, wptLocation, testCount), averages)
    webPageTest.testMultipleTimes(initialResult.testUrl, initialResult.typeOfTest, wptLocation, testCount)
  }


  def orderListByWeight(list: List[PerformanceResultsObject]): List[PerformanceResultsObject] = {
    if(list.length % 2 == 0) {
      println("orderListByWeight called. \n It has " + list.length + " elements.")
      val tupleList = listSinglesToPairs(list)
      println("listSinglesToPairs returned a list of " + tupleList.length + " pairs.")
      val alertsList: List[(PerformanceResultsObject, PerformanceResultsObject)] = for (element <- tupleList if element._1.alertStatusPageWeight || element._2.alertStatusPageWeight) yield element
      val okList: List[(PerformanceResultsObject, PerformanceResultsObject)] = for (element <- tupleList if !element._1.alertStatusPageWeight && !element._2.alertStatusPageWeight) yield element

      sortByWeight(alertsList) ::: sortByWeight(okList)
    }
    else{
      println("orderListByWeight has odd number of elements. List length is: " + list.length)
      List()
    }
  }

  def orderListBySpeed(list: List[PerformanceResultsObject]): List[PerformanceResultsObject] = {
    if(list.length % 2 == 0) {
      println("orderListByWeight called. \n It has " + list.length + " elements.")
      val tupleList = listSinglesToPairs(list)
      println("listSinglesToPairs returned a list of " + tupleList.length + " pairs.")
      val alertsList: List[(PerformanceResultsObject, PerformanceResultsObject)] = for (element <- tupleList if element._1.alertStatusPageSpeed || element._2.alertStatusPageSpeed) yield element
      val okList: List[(PerformanceResultsObject, PerformanceResultsObject)] = for (element <- tupleList if !element._1.alertStatusPageSpeed && !element._2.alertStatusPageSpeed) yield element

      sortBySpeed(alertsList) ::: sortBySpeed(okList)
    }
    else{
      println("orderListByWeight has odd number of elements. List length is: " + list.length)
      List()
    }
  }

  def sortHomogenousResultsByWeight(list: List[PerformanceResultsObject]): List[PerformanceResultsObject] = {
    val alertsResultsList: List[PerformanceResultsObject] = for (result <- list if result.alertStatusPageWeight) yield result
    val okResultsList: List[PerformanceResultsObject] = for (result <- list if !result.alertStatusPageWeight) yield result
    val sortedAlertList: List[PerformanceResultsObject] = alertsResultsList.sortWith(_.bytesInFullyLoaded > _.bytesInFullyLoaded)
    val sortedOkList: List[PerformanceResultsObject] = okResultsList.sortWith(_.bytesInFullyLoaded > _.bytesInFullyLoaded)
    sortedAlertList ::: sortedOkList
  }

  def sortHomogenousResultsBySpeed(list: List[PerformanceResultsObject]): List[PerformanceResultsObject] = {
    val alertsResultsList: List[PerformanceResultsObject] = for (result <- list if result.alertStatusPageSpeed) yield result
    val okResultsList: List[PerformanceResultsObject] = for (result <- list if !result.alertStatusPageSpeed) yield result
    val sortedAlertList: List[PerformanceResultsObject] = alertsResultsList.sortWith(_.speedIndex > _.speedIndex)
    val sortedOkList: List[PerformanceResultsObject] = okResultsList.sortWith(_.speedIndex > _.speedIndex)
    sortedAlertList ::: sortedOkList
  }
  
  def sortByWeight(list: List[(PerformanceResultsObject,PerformanceResultsObject)]): List[PerformanceResultsObject] = {
    if(list.nonEmpty){
      val sortedTupleList = sortTupleListByWeight(list)
      makeList(sortedTupleList)
    }
    else {
        println("sortByWeight has noElements in list. Passing back empty list")
        List()
    }
  }

  def sortBySpeed(list: List[(PerformanceResultsObject,PerformanceResultsObject)]): List[PerformanceResultsObject] = {
    if(list.nonEmpty){
      val sortedTupleList = sortTupleListBySpeed(list)
      makeList(sortedTupleList)
    }
    else {
      println("sortByWeight has noElements in list. Passing back empty list")
      List()
    }
  }



  def listSinglesToPairs(list: List[PerformanceResultsObject]): List[(PerformanceResultsObject, PerformanceResultsObject)] = {
    if (list.nonEmpty && list.length % 2 == 0) {
      println("list Singles to pairs has " + list.length + " perf results objects.")
      val tupleList = makeTuple(List((list.head, list.tail.head)), list.tail.tail)
      println("makeTuple called - returned a list of " + tupleList.length + "pairs")
      tupleList
    }
    else {
      println("listSinglesToPairs has been passed an empty or odd number of elements: list has " + list.length + "elements" )
      makeTuple(List((list.head, list.tail.head)), list.tail.tail)
    }
  }

  def makeTuple(tupleList: List[(PerformanceResultsObject, PerformanceResultsObject)], restOfList: List[PerformanceResultsObject]): List[(PerformanceResultsObject, PerformanceResultsObject)] = {
    println("maketuple function here: tuple list has: " + tupleList.length + " elements.\n" + "               and the rest of list has: " + restOfList.length + " elements remaining.")
    if (restOfList.isEmpty) {
      tupleList
    }
    else {
      if (restOfList.length < 2) {
        println("make tuple has odd number of items in list"); tupleList
      }
      else {
        makeTuple(tupleList ::: List((restOfList.head, restOfList.tail.head)), restOfList.tail.tail)
      }
    }
  }


  def makeList(tupleList: List[(PerformanceResultsObject,PerformanceResultsObject)]): List[PerformanceResultsObject] = {
    val fullList: List[PerformanceResultsObject] = tupleList.flatMap(a => List(a._1,a._2))
    fullList
  }

  def sortTupleListByWeight(list: List[(PerformanceResultsObject,PerformanceResultsObject)]): List[(PerformanceResultsObject,PerformanceResultsObject)] = {
    list.sortWith{(leftE:(PerformanceResultsObject, PerformanceResultsObject),rightE:(PerformanceResultsObject, PerformanceResultsObject)) =>
      leftE._1.bytesInFullyLoaded + leftE._2.bytesInFullyLoaded > rightE._1.bytesInFullyLoaded + rightE._2.bytesInFullyLoaded}
  }

  def sortTupleListBySpeed(list: List[(PerformanceResultsObject,PerformanceResultsObject)]): List[(PerformanceResultsObject,PerformanceResultsObject)] = {
    list.sortWith{(leftE:(PerformanceResultsObject, PerformanceResultsObject),rightE:(PerformanceResultsObject, PerformanceResultsObject)) =>
      leftE._1.speedIndex + leftE._2.speedIndex > rightE._1.speedIndex + rightE._2.speedIndex}
  }


  def makeContentStub(passedHeadline: Option[String], passedLastModified: Option[CapiDateTime], passedLiveBloggingNow: Option[Boolean]): ContentFields = {
    val contentStub = new ContentFields {override def newspaperEditionDate: Option[CapiDateTime] = None

      override def internalStoryPackageCode: Option[Int] = None

      override def displayHint: Option[String] = None

      override def legallySensitive: Option[Boolean] = None

      override def creationDate: Option[CapiDateTime] = None

      override def shouldHideAdverts: Option[Boolean] = None

      override def wordcount: Option[Int] = None

      override def thumbnail: Option[String] = None

      override def liveBloggingNow: Option[Boolean] = passedLiveBloggingNow

      override def showInRelatedContent: Option[Boolean] = None

      override def internalComposerCode: Option[String] = None

      override def lastModified: Option[CapiDateTime] = passedLastModified

      override def byline: Option[String] = None

      override def isInappropriateForSponsorship: Option[Boolean] = None

      override def commentable: Option[Boolean] = None

      override def trailText: Option[String] = None

      override def internalPageCode: Option[Int] = None

      override def main: Option[String] = None

      override def body: Option[String] = None

      override def productionOffice: Option[Office] = None

      override def newspaperPageNumber: Option[Int] = None

      override def shortUrl: Option[String] = None

      override def publication: Option[String] = None

      override def secureThumbnail: Option[String] = None

      override def contributorBio: Option[String] = None

      override def firstPublicationDate: Option[CapiDateTime] = None

      override def isPremoderated: Option[Boolean] = None

      override def membershipAccess: Option[MembershipTier] = None

      override def scheduledPublicationDate: Option[CapiDateTime] = None

      override def starRating: Option[Int] = None

      override def hasStoryPackage: Option[Boolean] = None

      override def headline: Option[String] = passedHeadline

      override def commentCloseDate: Option[CapiDateTime] = None

      override def internalOctopusCode: Option[String] = None

      override def standfirst: Option[String] = None
    }
  contentStub
  }

  def jodaDateTimetoCapiDateTime(time: DateTime): CapiDateTime = {
    new CapiDateTime {
      override def dateTime: Long = time.getMillis
    }
  }

}


