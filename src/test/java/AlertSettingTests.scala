import app.api.PageWeightEmailTemplate
import app.apiutils.{ArticleDefaultAverages, PageAverageObject, PerformanceResultsObject}
import org.scalatest._

/**
 * Created by mmcnamara on 26/05/16.
 */
abstract class AlertUnitSpec extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors

class AlertSettingTests extends AlertUnitSpec with Matchers {

  val articlePerformanceAverages = new ArticleDefaultAverages("color string")  
  
  val mobileArticlespeedIndexHigh = new PerformanceResultsObject("mobileArticlespeedIndexHigh", "Android/3G", "mobileArticlespeedIndexHigh", 1, 1, 1, 1, 1, 1, articlePerformanceAverages.mobileSpeedIndex + 1, "mobileArticlespeedIndexHigh", false, false, false)
  val mobileArticletFpHigh = new PerformanceResultsObject("mobileArticletFpHigh", "Android/3G", "mobileArticletFpHigh", 2, articlePerformanceAverages.mobileTimeFirstPaintInMs + 1, 2, 2, 2, 2, 2, "mobileArticletFpHigh", false, false, false)
  val mobileArticleTfpAndSpeedIndexHigh = new PerformanceResultsObject("testResult3", "Android/3G", "testResult3", 3, articlePerformanceAverages.mobileTimeFirstPaintInMs + 1, 3, 3, 3, 3, articlePerformanceAverages.mobileSpeedIndex + 1, "testResult3", true, true, true)

  //alert description text:
  val speedIndexHighOnly = "Time till page looks loaded (SpeedIndex) is unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."
  val tFPHighOnly = "Time till page is scrollable (time-to-first-paint) is unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."
  val bothtFPandSpeedIndexAreHigh  =  "Time till page is scrollable (time-to-first-paint) and time till page looks loaded (SpeedIndex) are unusually high. Please investigate page elements below or contact <a href=mailto:\"dotcom.health@guardian.co.uk\">the dotcom-health team</a> for assistance."

  "A Mobile Performance Result with a SpeedIndex above threshold" should "contain a proper alert message" in {
    val testResult = app.App.setAlertStatus(mobileArticlespeedIndexHigh, articlePerformanceAverages)
    println(testResult.alertDescription)
    //      println(pageWeightEmail.toString())
    assert(testResult.alertStatusPageSpeed && testResult.alertDescription.contains(speedIndexHighOnly))
  }

  "A Mobile Performance Result with a timeToFirstPaint above threshold" should "contain a proper alert message" in {
    val performanceAverages = new ArticleDefaultAverages("color string")
    val testResult = app.App.setAlertStatus(mobileArticletFpHigh, articlePerformanceAverages)
    println(testResult.alertDescription)
    //      println(pageWeightEmail.toString())
    assert(testResult.alertStatusPageSpeed && testResult.alertDescription.contains(tFPHighOnly))
  }

  "A Mobile Performance Result with Both tFP and SpeedIndex above threshold" should "contain a proper alert message" in {
    val performanceAverages = new ArticleDefaultAverages("color string")
    val testResult = app.App.setAlertStatus(mobileArticletFpHigh, articlePerformanceAverages)
    println(testResult.alertDescription)
    //      println(pageWeightEmail.toString())
    assert(testResult.alertStatusPageSpeed && testResult.alertDescription.contains(bothtFPandSpeedIndexAreHigh))
  }



}
