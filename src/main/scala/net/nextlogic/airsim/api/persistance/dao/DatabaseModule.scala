package net.nextlogic.airsim.api.persistance.dao

import slick.jdbc.JdbcProfile


trait DatabaseModule {
  // Declare an abstract profile:
  val profile: JdbcProfile
  // Import the Slick API from the profile:
  import profile.api._
  // Write our database code here...
}
