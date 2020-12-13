package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{
  InvitationType,
  Offset,
  Size,
  UserChallengeActivityType,
  UserChallengeSummaryType
}
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{
  Invitation,
  User,
  UserChallengeActivity,
  UserChallengeSummary
}
import agh.edu.pl.filters.{ FilterEq, InvitationsFilter, UsersFilter }
import agh.edu.pl.ids.{ ChallengeId, UserId }
import agh.edu.pl.models.Email.{ scalarAlias => emailType }
import agh.edu.pl.mutations.CreateUser
import sangria.macros.derive.{ deriveObjectType, AddFields }
import sangria.schema.{
  Argument,
  BooleanType,
  Field,
  ObjectType,
  OptionType,
  StringType
}

case class GraphqlUser() extends GraphqlEntity[UserId, User] {
  override def createEntitySettings: CreateUser.type = CreateUser

  override lazy val filterSettings: UsersFilter.type = UsersFilter

  private val ChallengeIdArg: Argument[ChallengeId] =
    Argument("challengeId", ChallengeId.scalarAlias)

  override def GraphQLOutputType: ObjectType[Context, User] =
    deriveObjectType[Context, User](
      AddFields(
        Field(
          name = "activities",
          fieldType = searchResponse(UserChallengeActivityType),
          arguments = List(Size, Offset),
          resolve = c =>
            c.ctx
              .repository
              .getAll[UserChallengeActivity](
                Some(FilterEq("userId", c.value.id.value) :: Nil),
                size = c.arg(Size),
                from = c.arg(Offset)
              )
        ),
        Field(
          name = "challenges",
          fieldType = searchResponse(UserChallengeSummaryType),
          arguments = List(Size, Offset),
          resolve = c =>
            c.ctx
              .repository
              .getAll[UserChallengeSummary](
                Some(FilterEq("userId", c.value.id.value) :: Nil),
                size = c.arg(Size),
                from = c.arg(Offset)
              )
        ),
        Field(
          name = "invitations",
          fieldType = searchResponse(InvitationType),
          arguments = List(Size, Offset, InvitationsFilter.FilterArgument),
          resolve = c => {
            val filters =
              c.arg(InvitationsFilter.FilterArgument).toList.flatMap(_.filters)
            c.ctx
              .repository
              .getAll[Invitation](
                Some(FilterEq("forUser", c.value.id.value) :: filters),
                size = c.arg(Size),
                from = c.arg(Offset)
              )
          }
        ),
        Field(
          name = "participatesInChallenge",
          fieldType = BooleanType,
          arguments = List(ChallengeIdArg),
          resolve = c =>
            c.ctx
              .repository
              .getAll[UserChallengeSummary](
                Some(
                  List(
                    FilterEq("userId", c.value.id.value),
                    FilterEq("challengeId", c.arg(ChallengeIdArg).value)
                  )
                )
              )
              .map(_.total != 0)(c.ctx.ec)
        )
      )
    )

  private val EmailArg = Argument("email", emailType)

  protected def getByEmailQuery: Field[Context, Unit] = Field(
    name = s"getUserByEmail",
    fieldType = OptionType(GraphQLOutputType),
    arguments = EmailArg :: Nil,
    resolve = c => {
      val email = c.arg(EmailArg)
      val id = UserId.generateId(UserId.DeterministicId(email))
      c.ctx
        .repository
        .getById[User](id)
    }
  )

  override def queries: Seq[Field[Context, Unit]] =
    super.queries :+ getByEmailQuery

}
