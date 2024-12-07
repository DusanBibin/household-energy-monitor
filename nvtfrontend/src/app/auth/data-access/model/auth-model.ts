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

export interface RegisterRequestDTO{
    name: string,
    lastname: string,
    email: string,
    phoneNumber: string,
    password: string,
    repeatPassword: string
}