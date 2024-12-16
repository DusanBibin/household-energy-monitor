
export interface PartialUserData{
  name?: string,
  lastname?: string,
  email?: string,
  role?: Role
}



export interface ResponseMessage{
  message: string;
}

export interface ResponseData{
  isError: boolean,
  data?: any,
  error?: ResponseMessage
}

export interface Role{
  authority: string;
}

export interface JwtPayload{
  id:number,
  sub: string,
  exp: number,
  role: Role[]
}