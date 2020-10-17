package agh.edu.pl.hash

case object MD5Hash {
  def md5Hash(text: String): String =
    java
      .security
      .MessageDigest
      .getInstance("MD5")
      .digest(text.getBytes())
      .map(0xff & _)
      .map("%02x".format(_))
      .foldLeft("")(_ + _)

  def generateHash(value: Any): String =
    md5Hash(value.toString)
}
