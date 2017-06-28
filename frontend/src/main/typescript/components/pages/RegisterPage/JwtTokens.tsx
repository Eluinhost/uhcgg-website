export type RegisterStartJwtToken = { username: string, exp: number }
export type RegisterErrorJwtToken = { header: string, message: string, exp: number }

export type RegisterJwtToken = RegisterStartJwtToken | RegisterErrorJwtToken

export function isRegisterErrorJwtToken(token: any): token is RegisterErrorJwtToken {
    return !!token.header;
}