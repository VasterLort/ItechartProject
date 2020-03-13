package by.itechart

import by.itechart.dao.Flow
import by.itechart.state.Start

object Application extends App {
  private val stateStart: Flow = Start.initFlow()
}
