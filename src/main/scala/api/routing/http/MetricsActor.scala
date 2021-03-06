package api.routing.http

import akka.actor.{ Actor, ActorRefFactory, ActorSystem, Props }
import api.routing.metrics.DefaultMetricsAggregator
import spray.routing.HttpService

// $COVERAGE-OFF$

/**
  * Created by dkondratiuk on 6/18/15.
  */
class MetricsActor extends Actor with MetricRoutes with HttpService {

  implicit val system = context.system

  implicit def actorRefFactory: ActorRefFactory = context

  override def receive: Receive = runRoute(route)

}

trait MetricRoutes extends HttpService {


  lazy val Root = "graph"

  lazy val route = get {
    path(Root / "data") {
      parameters('st) { st =>
        complete(DefaultMetricsAggregator.getGraph(st))
      }
    } ~ path(Root / "ui") {
      getFromResource("WEB-INF/index.html")
    } ~ pathPrefix(Root / "ui") {
      getFromResourceDirectory("WEB-INF")
    }
  }



}



//Just extend it and bind in your spray-can boot
trait MetricsBoot {

  def system: ActorSystem

  lazy val metricsService = system.actorOf(Props[MetricsActor])
  //IO(Http) ! Http.Bind(metricsService, interface, port)
}

// $COVERAGE-ON$



