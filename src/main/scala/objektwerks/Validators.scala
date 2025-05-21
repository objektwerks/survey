package objektwerks

object Validators:
  extension (value: String)
    def isEmptyOrNonEmpty: Boolean = value.isEmpty || value.nonEmpty
    def isLicense: Boolean = value.length == 36
    def isPin: Boolean = value.length == 7
    def isEmail: Boolean = value.length >= 3 && value.contains("@")