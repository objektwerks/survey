package objektwerks

import Validator.*

object Validators:
  extension (value: String)
    def isEmptyOrNonEmpty: Boolean = value.isEmpty || value.nonEmpty
    def isLicense: Boolean = value.length == 36
    def isPin: Boolean = value.length == 7
    def isEmail: Boolean = value.length >= 3 && value.contains("@")

  extension (account: Account)
    def validate: Validator =
      Validator()
        .validate(account.id >= 0)(Field("Id"), Message("must be greater than or equal to 0."))
        .validate(account.license.isLicense)(Field("License"), Message("must be 36 characters in length."))
        .validate(account.email.isEmail)(Field("Email"), Message("must be at least 3 characters in length and contain 1 @ symbol."))
        .validate(account.pin.isPin)(Field("Pin"), Message("must be 7 characters in length."))
        .validate(account.activated.nonEmpty)(Field("Activated"), Message("must be non empty."))

  extension (survey: Survey)
    def validate: Validator =
      Validator()
        .validate(survey.id >= 0)(Field("Id"), Message("must be greater than or equal to 0."))
        .validate(survey.accountId > 0)(Field("AccountId"), Message("must be greater than 0."))
        .validate(survey.created.nonEmpty)(Field("Built"), Message("must be non empty."))