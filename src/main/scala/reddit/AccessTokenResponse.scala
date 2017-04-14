package reddit

case class AccessTokenResponse(access_token: String, token_type: String, expires_in: Int, scope: String)
