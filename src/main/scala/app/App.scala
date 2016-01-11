package app

// note an _ instead of {} would get everything

import java.io._

import app.apiutils.{ArticleUrls, WebPageTest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GetObjectRequest, PutObjectRequest}
import com.typesafe.config.{Config, ConfigFactory}
import scala.io.Source


object App {
  def main(args: Array[String]) {
    /*  This value stops the forces the config to be read and the output file to be written locally rather than reading and writing from/to S3
    #####################    this should be set to false before merging!!!!################*/
    val iamTestingLocally = true
    /*#####################################################################################*/


    //  Define names of s3bucket, configuration and output Files
    val s3BucketName = "capi-wpt-querybot"
    val configFileName = "config.conf"
    val outputFileName = "liveBlogPerformanceData.csv"
//  Initialize results string - this will be used to acculate the results from each test so that only one write to file is needed.
    var resultsString: String = "Article Url, Time to First Paint, Time to Document Complete, kB transferred at Document Complete, Time to Fully Loaded, kB transferred at Fully Loaded, Speed Index \n"
    var contentApiKey: String = ""
    var wptBaseUrl: String = ""
    var wptApiKey: String = ""
      //  Define s3Client to all access to config file and enable uploading of results to S3 - due to scoping, this must be defined even if testing locally
      val s3Client = new AmazonS3Client()
    if(!iamTestingLocally) {
      //  Retrieve configuration from S3 bucket
      val conf = getS3Config(s3Client, s3BucketName, configFileName)
      contentApiKey = conf.getString("content.api.key")
      wptBaseUrl = conf.getString("wpt.api.baseUrl")
      wptApiKey = conf.getString("wpt.api.key")
    } else
      {
            for (line <- Source.fromFile(configFileName).getLines()){
              if (line.contains("content.api.key")){contentApiKey = line.takeRight((line.length - line.indexOf("=")) - 1) }
              if (line.contains("wpt.api.baseUrl")){wptBaseUrl = line.takeRight((line.length - line.indexOf("=")) - 1) }
              if (line.contains("wpt.api.key")){wptApiKey = line.takeRight((line.length - line.indexOf("=")) - 1) }
            }
      }

    //  Define new CAPI Query object
    val articleUrlList = new ArticleUrls(contentApiKey)
    //  Request a list of urls from Content API
    val articleUrls: List[String] = articleUrlList.getUrls
    if (articleUrls.isEmpty)
      println("no results returned")
    else {
            // Send each article URL to the webPageTest API and obtain resulting data
            val testResults: List[String] = articleUrls.map(url => testUrl(url, wptBaseUrl, wptApiKey))
            // Add results to a single string so that we only need ot write to S3 once (S3 will only take complete objects).
            resultsString = resultsString.concat(testResults.mkString)
            println("Final results: \n" + resultsString)
        }
    if (!iamTestingLocally) {
      System.out.println("Writing the following to S3:\n" + resultsString)
      s3Client.putObject(new PutObjectRequest(s3BucketName, outputFileName, createOutputFile(outputFileName, resultsString)));
    }
    else {
      val output: FileWriter = new FileWriter(outputFileName)
      println("Writing the following to local file system:\n" + resultsString)
      output.write(resultsString)
      output.close()
    }
    println(resultsString)
  }


  def getS3Config(s3Client: AmazonS3Client, bucketName: String, configFileName: String): Config = {
    //    val s3Client = new AmazonS3Client()
    val s3Object = s3Client.getObject(new GetObjectRequest(bucketName, configFileName))
    val objectData = s3Object.getObjectContent
    val configString = scala.io.Source.fromInputStream(objectData).mkString
    val conf = ConfigFactory.parseString(configString)
    conf
  }

  def createOutputFile(fileName: String, content: String): File = {
    val file: File = File.createTempFile(fileName.takeWhile(_ != '.'), fileName.dropWhile(_ != '.'))
    file.deleteOnExit()
    val writer: Writer = new OutputStreamWriter(new FileOutputStream(file))
    writer.write(content)
    writer.close()
    file
  }

  def testUrl(url: String, wptBaseUrl: String, wptApiKey: String): String = {
    var returnString: String = url + ", "
    //  Define new web-page-test API request and send it the url to test
    val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
    val webPageTestResults: webpageTest.ResultElement = webpageTest.test(url)
    //  Add results to string which will eventually become the content of our results file
    returnString = returnString.concat(webPageTestResults.toString() + "\n")
    returnString
  }
}

