package by.itechart.action

import by.itechart.dao.Flow

sealed trait Notice

case class SuccessfulNotice(flow: Flow, message: String = "Request was completed!!!") extends Notice

case class FailureNotice(message: String = "Error!!!") extends Notice
