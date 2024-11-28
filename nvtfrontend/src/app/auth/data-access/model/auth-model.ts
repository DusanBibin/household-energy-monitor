export interface AuthRequestDTO{
    email: string,
    password: string
}

export interface AuthResponseDTO{
    token: string,
}

export interface SuperadminPasswordChangeDTO{
    newPassword: string,
    repeatPassword: string
}