package com.github.nrf110.dust

import util._

object Filters {
  def apply(filter: String, value: String): String =
    filter match {
      case "h" => value.escapeHtml
      case "j" => value.escapeJs
      case "u" => value.encodeUri
      case "uc" => value.encodeUriComponent
    }
}
