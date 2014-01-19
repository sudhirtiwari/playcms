package playcms.admin.models

import playcms.models.Model
import org.joda.time.DateTime

trait User extends Model[User.ID] {
  def username: String
  def firstName: String
  def lastName: String
  def createdOn: DateTime
  def lastLogin: DateTime
}

object User {
  type ID = String
}