package me.enkode.todo.server.common

import akka.http.scaladsl.server.Route

trait Router {
  def routes: Seq[Route]
}
