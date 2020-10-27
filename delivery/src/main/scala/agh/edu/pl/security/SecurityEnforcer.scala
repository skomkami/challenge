package agh.edu.pl.security

import agh.edu.pl.context.Context
import sangria.execution.{ Middleware, MiddlewareQueryContext }

object SecurityEnforcer extends Middleware[Context] {
  type QueryVal = Unit

  def beforeQuery(context: MiddlewareQueryContext[Context, _, _]): Unit = ()
  def afterQuery(
      queryVal: QueryVal,
      context: MiddlewareQueryContext[Context, _, _]
    ) = ()
}
