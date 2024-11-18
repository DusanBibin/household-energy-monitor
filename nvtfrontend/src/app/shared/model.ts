export interface ErrorMessage{
  message: string;
}

export interface ResponseData{
  isError: boolean,
  data?: any,
  error?: ErrorMessage
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