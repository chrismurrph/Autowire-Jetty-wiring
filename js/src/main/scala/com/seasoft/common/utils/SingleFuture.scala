package com.seasoft.common.utils

/**
 * User: Chris
 * Date: 31/12/2014
 * Time: 4:40 PM
 */
object SingleFuture {
  import java.util.{Timer, TimerTask}

  import scala.concurrent._
  import scala.concurrent.duration.Duration

  private val timer = new Timer

  private def makeTask[T]( body: => T )( schedule: TimerTask => Unit ): Future[T] = {
    val prom = Promise[T]()
    schedule(
      new TimerTask{
        def run() {
          try {
            prom.success( body )
          } catch {
            case ex: Throwable => prom.failure( ex )
          }
        }
      }
    )
    prom.future
  }
  def apply[T]( duration: Duration )( body: => T )(implicit ctx: ExecutionContext): Future[T] = {
    // NOTE: will throw IllegalArgumentException for infinite durations
    makeTask( body )( timer.schedule( _, duration.toMillis ) )
  }
  /*
  def apply[T]( period: Long )( body: => T )(implicit ctx: ExecutionContext): Future[T] = {
    makeTask( body )( timer.schedule( _, period ) )
  }
  def apply[T]( date: Date )( body: => T )(implicit ctx: ExecutionContext): Future[T] = {
    makeTask( body )( timer.schedule( _, date ) )
  }
  */
}