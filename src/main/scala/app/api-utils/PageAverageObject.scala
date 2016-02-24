package app.apiutils

import org.joda.time.DateTime


/**
 * Created by mmcnamara on 10/02/16.
 */

class PageAverageObject(dtfp: Int, dtdc: Int, dsdc: Int, dtfl: Int, dsfl: Int, dcflprepaid: Double, dcflpostpaid: Double, dsi: Int, dsc: Int, mtfp: Int, mtdc: Int, msdc: Int, mtfl: Int, msfl: Int, mcflprepaid: Double, mcflpostpaid: Double, msi: Int, msc: Int, resultString: String) {

  val desktopTimeFirstPaintInMs: Int = dtfp
  lazy val desktopTimeFirstPaintInSeconds: Int = desktopTimeFirstPaintInMs/1000
  val desktopTimeDocCompleteInMs: Int = dtdc
  lazy val desktopTimeDocCompleteInSeconds: Int = desktopTimeDocCompleteInMs/1000
  val desktopKBInDocComplete: Int = dsdc
  lazy val desktopMBInDocComplete: Double = roundAt(2)(desktopKBInDocComplete/1024)
  val desktopTimeFullyLoadedInMs: Int = dtfl
  lazy val desktopTimeFullyLoadedInSeconds: Int = desktopTimeFullyLoadedInMs/1000
  val desktopKBInFullyLoaded: Int = dsfl
  lazy val desktopMBInFullyLoaded: Double = roundAt(2)(desktopKBInFullyLoaded/1024)
  val desktopEstUSPrePaidCost: Double = dcflprepaid
  val desktopEstUSPostPaidCost: Double = dcflpostpaid
  val desktopSpeedIndex: Int = dsi
  val desktopSuccessCount = dsc

  val mobileTimeFirstPaintInMs: Int = mtfp
  lazy val mobileTimeFirstPaintInSeconds: Int = mobileTimeFirstPaintInMs/1000
  val mobileTimeDocCompleteInMs: Int = mtdc
  lazy val mobileTimeDocCompleteInSeconds: Int = mobileTimeDocCompleteInMs/1000
  val mobileKBInDocComplete: Int = msdc
  lazy val mobileMBInDocComplete: Double = roundAt(2)(mobileKBInDocComplete/1024)
  val mobileTimeFullyLoadedInMs: Int = mtfl
  lazy val mobileTimeFullyLoadedInSeconds: Int = mobileTimeFullyLoadedInMs/1000
  val mobileKBInFullyLoaded: Int = msfl
  lazy val mobileMBInFullyLoaded: Double = roundAt(2)(mobileKBInFullyLoaded/1024)
  val mobileEstUSPrePaidCost: Double = mcflprepaid
  val mobileEstUSPostPaidCost: Double = mcflpostpaid
  val mobileSpeedIndex: Int = msi
  val mobileSuccessCount = msc

  val formattedHTMLResultString: String = resultString
  lazy val (desktopPart,mobilePart): (String,String) = formattedHTMLResultString.splitAt(formattedHTMLResultString.indexOf("<tr>",4))
  lazy val desktopHTMLResultString: String = desktopPart
  lazy val mobileHTMLResultString: String = mobilePart

  lazy val desktopTimeFirstPaintInMs80thPercentile: Int = (desktopTimeFirstPaintInMs * 80) / 100
  lazy val desktopTimeDocCompleteInMs80thPercentile: Int = (desktopTimeDocCompleteInMs * 80) / 100
  lazy val desktopKBInDocComplete80thPercentile: Int = (desktopKBInDocComplete * 80) / 100
  lazy val desktopTimeFullyLoadedInMs80thPercentile: Int = (desktopTimeFullyLoadedInMs * 80) / 100
  lazy val desktopKBInFullyLoaded80thPercentile: Int = (desktopKBInFullyLoaded * 80) / 100
  lazy val desktopEstUSPrePaidCost80thPercentile: Double = (desktopEstUSPrePaidCost * 80) / 100
  lazy val desktopEstUSPostPaidCost80thPercentile: Double = (desktopEstUSPostPaidCost * 80) / 100
  lazy val desktopSpeedIndex80thPercentile: Int = (desktopSpeedIndex * 80) / 100
  lazy val mobileTimeFirstPaintInMs80thPercentile: Int = (mobileTimeFirstPaintInMs * 80) / 100
  lazy val mobileTimeDocCompleteInMs80thPercentile: Int = (mobileTimeDocCompleteInMs * 80) / 100
  lazy val mobileKBInDocComplete80thPercentile: Int = (mobileKBInDocComplete * 80) / 100
  lazy val mobileTimeFullyLoadedInMs80thPercentile: Int = (mobileTimeFullyLoadedInMs * 80) / 100
  lazy val mobileKBInFullyLoaded80thPercentile: Int = (mobileKBInFullyLoaded * 80) / 100
  lazy val mobileEstUSPrePaidCost80thPercentile: Double = (mobileEstUSPrePaidCost * 80) / 100
  lazy val mobileEstUSPostPaidCost80thPercentile: Double = (mobileEstUSPostPaidCost * 80) / 100
  lazy val mobileSpeedIndex80thPercentile: Int = (mobileSpeedIndex * 80) / 100


  def this() {
    this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "")
  }

  def toHTMLString: String = formattedHTMLResultString

  def roundAt(p: Int)(n: Double): Double = { val s = math pow (10, p); (math round n * s) / s}
}


class LiveBlogDefaultAverages extends PageAverageObject() {
  override val desktopTimeFirstPaintInMs: Int = 1 * 1000
  override val desktopTimeDocCompleteInMs: Int = 15 * 1000
  override val desktopKBInDocComplete: Int = 10 * 1024
  override val desktopTimeFullyLoadedInMs: Int = 20 * 1000
  override val desktopKBInFullyLoaded: Int = 15 * 1024
  override val desktopEstUSPrePaidCost: Double = 0.60
  override val desktopEstUSPostPaidCost: Double = 0.50
  override val desktopSpeedIndex: Int = 5000
  override val desktopSuccessCount = 1

  override val mobileTimeFirstPaintInMs: Int = 1 * 1000
  override val mobileTimeDocCompleteInMs: Int = 15 * 1000
  override val mobileKBInDocComplete: Int = 6 * 1024
  override val mobileTimeFullyLoadedInMs: Int = 20 * 1000
  override val mobileKBInFullyLoaded: Int = 6 * 1024
  override val mobileEstUSPrePaidCost: Double = 0.40
  override val mobileEstUSPostPaidCost: Double = 0.30
  override val mobileSpeedIndex: Int = 5000
  override val mobileSuccessCount = 1

  override val formattedHTMLResultString: String = "<tr bgcolor=\"A9BCF5\">" +
    "<td>" + DateTime.now + "</td>" +
    "<td>Desktop</td>" +
    "<td> Alerting thresholds determined by past liveblogs we have migrated</td>" +
    "<td>" + desktopTimeDocCompleteInSeconds + "s</td>" +
    "<td>" + desktopMBInDocComplete + "MB</td>" +
    "<td>$(US)" + desktopEstUSPrePaidCost + "</td>" +
    "<td>$(US)" + desktopEstUSPostPaidCost + "</td>" +
    "<td>" + desktopSpeedIndex + "</td>" +
    "<td>Predefined standards</td></tr>" +
    "<tr bgcolor=\"A9BCF5\">" +
    "<td>" + DateTime.now + "</td>" +
    "<td>Mobile</td>" +
    "<td> Yellow indicates within danger zone of threshold. Red indicates threshold has been crossed </td>" +
    "<td>" + mobileTimeDocCompleteInSeconds + "s</td>" +
    "<td>" + mobileMBInDocComplete + "MB</td>" +
    "<td>$(US)" + mobileEstUSPrePaidCost + "</td>" +
    "<td>S(US)" + mobileEstUSPostPaidCost + "</td>" +
    "<td>" + mobileSpeedIndex + "</td>" +
    "<td>Predefined standards</td></tr>"
}

class FrontsDefaultAverages extends PageAverageObject() {
  override val desktopTimeFirstPaintInMs: Int = 1 * 1000
  override val desktopTimeDocCompleteInMs: Int = 15 * 1000
  override val desktopKBInDocComplete: Int = 10 * 1024
  override val desktopTimeFullyLoadedInMs: Int = 20 * 1000
  override val desktopKBInFullyLoaded: Int = 15 * 1024
  override val desktopEstUSPrePaidCost: Double = 0.60
  override val desktopEstUSPostPaidCost: Double = 0.50
  override val desktopSpeedIndex: Int = 5000
  override val desktopSuccessCount = 1

  override val mobileTimeFirstPaintInMs: Int = 1 * 1000
  override val mobileTimeDocCompleteInMs: Int = 15 * 1000
  override val mobileKBInDocComplete: Int = 6 * 1024
  override val mobileTimeFullyLoadedInMs: Int = 20 * 1000
  override val mobileKBInFullyLoaded: Int = 6 * 1024
  override val mobileEstUSPrePaidCost: Double = 0.40
  override val mobileEstUSPostPaidCost: Double = 0.30
  override val mobileSpeedIndex: Int = 5000
  override val mobileSuccessCount = 1

  override val formattedHTMLResultString: String = "<tr bgcolor=\"A9BCF5\">" +
    "<td>" + DateTime.now + "</td>" +
    "<td>Desktop</td>" +
    "<td> Alerting thresholds determined by chosen values</td>" +
    "<td>" + desktopTimeDocCompleteInSeconds + "s</td>" +
    "<td>" + desktopMBInDocComplete + "MB</td>" +
    "<td>$(US)" + desktopEstUSPrePaidCost + "</td>" +
    "<td>$(US)" + desktopEstUSPostPaidCost + "</td>" +
    "<td>" + desktopSpeedIndex + "</td>" +
    "<td>Predefined standards</td></tr>" +
    "<tr bgcolor=\"A9BCF5\">" +
    "<td>" + DateTime.now + "</td>" +
    "<td>Mobile</td>" +
    "<td> Yellow indicates within danger zone of threshold. Red indicates threshold has been crossed </td>" +
    "<td>" + mobileTimeDocCompleteInSeconds + "s</td>" +
    "<td>" + mobileMBInDocComplete + "MB</td>" +
    "<td>$(US)" + mobileEstUSPrePaidCost + "</td>" +
    "<td>S(US)" + mobileEstUSPostPaidCost + "</td>" +
    "<td>" + mobileSpeedIndex + "</td>" +
    "<td>Predefined standards</td></tr>"
}


class GeneratedPageAverages(resultsList: List[Array[PerformanceResultsObject]]) extends PageAverageObject{
  var accumulatorDesktopTimeFirstPaint: Int = 0
  var accumulatorDesktopTimeDocComplete: Int = 0
  var accumulatorDesktopKBInDocComplete: Int = 0
  var accumulatorDesktopTimeFullyLoaded: Int = 0
  var accumulatorDesktopKBInFullyLoaded: Int = 0
  var accumulatorDesktopEstUSPrePaidCost: Double = 0
  var accumulatorDesktopEstUSPostPaidCost: Double = 0
  var accumulatorDesktopSpeedIndex: Int = 0
  var accumulatorDesktopSuccessCount = 0

  var accumulatorMobileTimeFirstPaint: Int = 0
  var accumulatorMobileTimeDocComplete: Int = 0
  var accumulatorMobileKBInDoccomplete: Int = 0
  var accumulatorMobileTimeFullyLoaded: Int = 0
  var accumulatorMobileKBInFullyLoaded: Int = 0
  var accumulatorMobileEstUSPrePaidCost: Double = 0
  var accumulatorMobileEstUSPostPaidCost: Double = 0
  var accumulatorMobileSpeedIndex: Int = 0
  var accumulatorMobileSuccessCount = 0

  var accumulatorString: String = ""

  val multipleLiveBlogs:String = "liveblogs that were migrated due to size"
  val singleLiveBlog: String = "Example of a liveblog migrated due to size"
  val noLiveBlogs: String = "All tests of migrated liveblogs failed"

  val multipleInteractives:String = "interactives with known size or performance issues"
  val singleInteractive: String = "Example of an interactive with known size or performance issues"
  val noInteractives: String = "All tests of interactives with size or performance issues have failed"

  //process result objects
  resultsList.foreach(result => {
  if (result(0).resultStatus == "Test Success") {
    accumulatorDesktopTimeFirstPaint += result(0).timeFirstPaint
    accumulatorDesktopTimeDocComplete += result(0).timeDocCompleteInMs
    accumulatorDesktopKBInDocComplete += result(0).kBInDocComplete
    accumulatorDesktopTimeFullyLoaded += result(0).timeFullyLoadedInMs
    accumulatorDesktopKBInFullyLoaded += result(0).kBInFullyLoaded
    accumulatorDesktopEstUSPrePaidCost += result(0).estUSPrePaidCost
    accumulatorDesktopEstUSPostPaidCost += result(0).estUSPostPaidCost
    accumulatorDesktopSpeedIndex += result(0).speedIndex
    accumulatorDesktopSuccessCount += 1
  }
  if (result(1).resultStatus == "Test Success") {
    accumulatorMobileTimeFirstPaint += result(1).timeFirstPaint
    accumulatorMobileTimeDocComplete += result(1).timeDocCompleteInMs
    accumulatorMobileKBInDoccomplete += result(1).kBInDocComplete
    accumulatorMobileTimeFullyLoaded += result(1).timeFullyLoadedInMs
    accumulatorMobileKBInFullyLoaded += result(1).kBInFullyLoaded
    accumulatorMobileEstUSPrePaidCost += result(1).estUSPrePaidCost
    accumulatorMobileEstUSPostPaidCost += result(1).estUSPostPaidCost
    accumulatorMobileSpeedIndex += result(1).speedIndex
    accumulatorMobileSuccessCount += 1
  }
  })

  override val desktopTimeFirstPaintInMs: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopTimeFirstPaint/accumulatorDesktopSuccessCount} else {1 * 1000}
  override val desktopTimeDocCompleteInMs: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopTimeDocComplete/accumulatorDesktopSuccessCount} else {15 * 1000}
  override val desktopKBInDocComplete: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopKBInDocComplete/accumulatorDesktopSuccessCount} else {10 * 1024}
  override val desktopTimeFullyLoadedInMs: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopTimeFullyLoaded/accumulatorDesktopSuccessCount} else {20 * 1000}
  override val desktopKBInFullyLoaded: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopKBInFullyLoaded/accumulatorDesktopSuccessCount} else {15 * 1024}
  override val desktopEstUSPrePaidCost: Double = if (accumulatorDesktopSuccessCount > 0) {roundAt(2)(accumulatorDesktopEstUSPrePaidCost/accumulatorDesktopSuccessCount)} else {60.00}
  override val desktopEstUSPostPaidCost: Double = if (accumulatorDesktopSuccessCount > 0) {roundAt(2)(accumulatorDesktopEstUSPostPaidCost/accumulatorDesktopSuccessCount)} else {50.00}
  override val desktopSpeedIndex: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopSpeedIndex/accumulatorDesktopSuccessCount} else {5000}
  override val desktopSuccessCount: Int = accumulatorDesktopSuccessCount

  override val mobileTimeFirstPaintInMs: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileTimeFirstPaint/accumulatorMobileSuccessCount} else {1 * 1000}
  override val mobileTimeDocCompleteInMs: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileTimeDocComplete/accumulatorMobileSuccessCount} else {15 * 1000}
  override val mobileKBInDocComplete: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileKBInDoccomplete/accumulatorMobileSuccessCount} else {6 * 1024}
  override val mobileTimeFullyLoadedInMs: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileTimeFullyLoaded/accumulatorMobileSuccessCount} else {20 * 1000}
  override val mobileKBInFullyLoaded: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileKBInFullyLoaded/accumulatorMobileSuccessCount} else {6 * 1024}
  override val mobileEstUSPrePaidCost: Double = if (accumulatorMobileSuccessCount > 0) {roundAt(2)(accumulatorMobileEstUSPrePaidCost/accumulatorMobileSuccessCount)} else {0.40}
  override val mobileEstUSPostPaidCost: Double = if (accumulatorMobileSuccessCount > 0) {roundAt(2)(accumulatorMobileEstUSPostPaidCost/accumulatorMobileSuccessCount)} else {0.30}
  override val mobileSpeedIndex: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileSpeedIndex/accumulatorMobileSuccessCount} else {5000}
  override val mobileSuccessCount: Int = accumulatorMobileSuccessCount

  accumulatorString = accumulatorString.concat("<tr bgcolor=\"#A9BCF5\"><td>" + DateTime.now + "</td><td>Desktop</td>")

  //add desktop averaages to return string
  if(accumulatorDesktopSuccessCount > 1){
    accumulatorString = accumulatorString.concat("<td>" + "Average of " + accumulatorDesktopSuccessCount + "pages  with recognised size issues</td>"
      + "<td>" + desktopTimeDocCompleteInMs + "s</td>"
      + "<td>" + desktopMBInFullyLoaded + "MB</td>"
      + "<td> $(US)" + desktopEstUSPrePaidCost + "</td>"
      + "<td> $(US)" + desktopEstUSPostPaidCost + "</td>"
      + "<td>" + desktopSpeedIndex + "</td>"
      + "<td>" + desktopSuccessCount + " urls Tested Successfully</td></tr>"
    )}
  else{
    if (accumulatorDesktopSuccessCount == 1) {
      accumulatorString = accumulatorString.concat("<td> Results from 1 page with recognised size issues</td>"
        + "<td>" + desktopTimeDocCompleteInMs + "s</td>"
        + "<td>" + desktopMBInFullyLoaded + "MB</td>"
        + "<td> $(US)" + desktopEstUSPrePaidCost + "</td>"
        + "<td> $(US)" + desktopEstUSPostPaidCost + "</td>"
        + "<td>" + desktopSpeedIndex + "</td>"
        + "<td>" + desktopSuccessCount + " urls Tested Successfully</td></tr>"
      )
    }
    else {
      accumulatorString = accumulatorString.concat("<td> Standard values to be used for judging page size</td>"
        + "<td>" + desktopTimeDocCompleteInMs + "s</td>"
        + "<td>" + desktopMBInFullyLoaded + "MB</td>"
        + "<td> $(US)" + desktopEstUSPrePaidCost + "</td>"
        + "<td> $(US)" + desktopEstUSPostPaidCost + "</td>"
        + "<td>" + desktopSpeedIndex + "</td>"
        + "<td>" + desktopSuccessCount + " urls Tested Successfully</td></tr>"
      )
    }
  }
  
  //add mobile averages to return string
  accumulatorString = accumulatorString.concat("<tr bgcolor=\"#A9BCF5\"><td>" + DateTime.now + "</td><td>Android/3G</td>")
  if(accumulatorMobileSuccessCount > 1){
    accumulatorString = accumulatorString.concat("<td>" + "Average of " + accumulatorDesktopSuccessCount + "pages  with recognised size issues</td>"
      + "<td>" + mobileTimeDocCompleteInMs + "s</td>"
      + "<td>" + mobileMBInFullyLoaded + "MB</td>"
      + "<td> $(US)" + mobileEstUSPrePaidCost + "</td>"
      + "<td> $(US)" + mobileEstUSPostPaidCost + "</td>"
      + "<td>" + mobileSpeedIndex + "</td>"
      + "<td>" + mobileSuccessCount + " urls Tested Successfully</td></tr>"
    )}
  else{
    if (accumulatorMobileSuccessCount == 1) {
      accumulatorString = accumulatorString.concat("<td> Results from 1 page with recognised size issues</td>"
        + "<td>" + mobileTimeDocCompleteInMs + "s</td>"
        + "<td>" + mobileMBInFullyLoaded + "MB</td>"
        + "<td> $(US)" + mobileEstUSPrePaidCost + "</td>"
        + "<td> $(US)" + mobileEstUSPostPaidCost + "</td>"
        + "<td>" + mobileSpeedIndex + "</td>"
        + "<td>" + mobileSuccessCount + " urls Tested Successfully</td></tr>"
      )
    }
    else {
      accumulatorString = accumulatorString.concat("<td> Standard values to be used for judging page size</td>"
        + "<td>" + mobileTimeDocCompleteInMs + "s</td>"
        + "<td>" + mobileMBInFullyLoaded + "MB</td>"
        + "<td> $(US)" + mobileEstUSPrePaidCost + "</td>"
        + "<td> $(US)" + mobileEstUSPostPaidCost + "</td>"
        + "<td>" + mobileSpeedIndex + "</td>"
        + "<td>" + mobileSuccessCount + " urls Tested Successfully</td></tr>"
      )
    }
  }

  override val formattedHTMLResultString: String = accumulatorString
}
