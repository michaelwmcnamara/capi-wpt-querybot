import app.api.{InteractiveEmailTemplate, PageWeightEmailTemplate}
import app.apiutils.PerformanceResultsObject
import org.scalatest._

/**
 * Created by mmcnamara on 26/05/16.
 */
abstract class EmailUnitSpec extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors

class EmailTests extends EmailUnitSpec with Matchers {

  val fakeDashboardUrl = "http://www.theguardian.com/uk"
  val testResult1 = new PerformanceResultsObject("mobileArticlespeedIndexHigh", "mobileArticlespeedIndexHigh", "mobileArticlespeedIndexHigh", 1, 1, 1, 1, 1, 1, 1, "mobileArticlespeedIndexHigh", true, true, true)
  val testResult2 = new PerformanceResultsObject("mobileArticletFpHigh", "mobileArticletFpHigh", "mobileArticletFpHigh", 2, 2, 2, 2, 2, 2, 2, "mobileArticletFpHigh", true, true, true)
  val testResult3 = new PerformanceResultsObject("testResult3", "testResult3", "testResult3", 3, 3, 3, 3, 3, 3, 3, "testResult3", true, true, true)

  val testResultListEmpty = List()
  val testResultList1results = List(testResult1)
  val testResultList2results = List(testResult1, testResult2)
  val testResultList3results = List(testResult1, testResult2, testResult3)

  val emptyListText = "I'm very sorry. This email was sent in error. Please ignore."
  val singleResultText = "mobileArticlespeedIndexHigh"
  val twoResultsText = "mobileArticletFpHigh"
  val threeResultsText = "testResult3"

  "An pageWeight Email list with 0 Results" should "contain results and page elements" in {
    val pageWeightEmail = new PageWeightEmailTemplate(List(), fakeDashboardUrl)
    //      println(pageWeightEmail.toString())
    assert(pageWeightEmail.toString().contains(emptyListText))
  }

  "An pageWeight Email list with 1 Results" should "contain results and page elements" in {
    val pageWeightEmail = new PageWeightEmailTemplate(testResultList1results, fakeDashboardUrl)
    //      println(pageWeightEmail.toString())
    assert(pageWeightEmail.toString().contains(singleResultText))
  }

  "An pageWeight Email list with 2 Results" should "contain results and page elements" in {
    val pageWeightEmail = new PageWeightEmailTemplate(testResultList2results, fakeDashboardUrl)
    //      println(pageWeightEmail.toString())
    assert(pageWeightEmail.toString().contains(singleResultText) && pageWeightEmail.toString().contains(twoResultsText))
  }

  "An pageWeight Email list with 3 Results" should "contain results and page elements" in {
    val pageWeightEmail = new PageWeightEmailTemplate(testResultList3results, fakeDashboardUrl)
    //      println(pageWeightEmail.toString())
    assert(pageWeightEmail.toString().contains(singleResultText) && pageWeightEmail.toString().contains(twoResultsText) && pageWeightEmail.toString().contains(threeResultsText))
  }

  "An interactive Email list with 0 Results" should "contain results and page elements" in {
    val interactiveEmail = new InteractiveEmailTemplate(List(), fakeDashboardUrl)
    //      println(pageWeightEmail.toString())
    assert(interactiveEmail.toString().contains(emptyListText))
  }

  "An interactive Email list with 1 Results" should "contain results and page elements" in {
    val interactiveEmail = new InteractiveEmailTemplate(testResultList1results, fakeDashboardUrl)
    //      println(pageWeightEmail.toString())
    assert(interactiveEmail.toString().contains(singleResultText))
  }

  "An interactive Email list with 2 Results" should "contain results and page elements" in {
    val interactiveEmail = new InteractiveEmailTemplate(testResultList2results, fakeDashboardUrl)
    //      println(pageWeightEmail.toString())
    assert(interactiveEmail.toString().contains(singleResultText) && interactiveEmail.toString().contains(twoResultsText))
  }

  "An interactive Email list with 3 Results" should "contain results and page elements" in {
    val interactiveEmail = new InteractiveEmailTemplate(testResultList3results, fakeDashboardUrl)
    //      println(pageWeightEmail.toString())
    assert(interactiveEmail.toString().contains(singleResultText) && interactiveEmail.toString().contains(twoResultsText) && interactiveEmail.toString().contains(threeResultsText))
  }


}
