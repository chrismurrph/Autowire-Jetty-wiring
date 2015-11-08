package wiring

import com.cmts.server.business.webuserservices.WebUserServerServices
import com.seasoft.alarmer.common.servicedoi.{MultigasDOI, StartupInfoDOI}
import com.seasoft.common.store.{ConnectionTypeEnum, EntityManagedDataStore}

/**
 * User: Chris
 * Date: 08/07/2015
 * Time: 4:58 AM
 */
class SmartgasServerServices(info:SmartgasInfo) {
  val serverServices = WebUserServerServices.getSingleInstance(info.beanFactory, false)
  val secureDataStore: EntityManagedDataStore = info.dsFactory.getDataStore(ConnectionTypeEnum.SECURE).asInstanceOf[EntityManagedDataStore]
  serverServices.init(secureDataStore)

  /*
   * requestGraphLine PARAMS:
   * startTimeStr: 25_08_2015__17_45_27.061
   * endTimeStr: 25_08_2015__18_45_27.061
   * %%substancesNames, size [2] List of type java.util.ArrayList containing (first element anyway) java.lang.String
   * ------------------------
   *   value: Carbon_Monoxide, class: java.lang.String
   *   value: Carbon_Dioxide, class: java.lang.String
   * ------------------------
   * displayName: Shed Tube 10
   * displayTimeStr: 25_08_2015__18_45_27.273
   */
  def requestGraphLine(startTimeStr: String,
                 endTimeStr: String,
                 physicalSubstanceNames: Seq[String],
                 displayName: String,
                 displayTimeStr: String,
                 sessionId: String):MultigasDOI = {
    import scala.collection.JavaConversions._
    serverServices.getGraphLineService.requestGraphLine(startTimeStr,endTimeStr,physicalSubstanceNames,displayName,displayTimeStr,sessionId)
  }

  def requestStartupInfo: StartupInfoDOI =
  {
    serverServices.getStartupInfoService.requestStartupInfo()
  }

  def requestTime: String =
  {
    serverServices.getTimeService.requestTime()
  }
}
