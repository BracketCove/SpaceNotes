package com.wiseassblog.domain

import com.wiseassblog.domain.repository.IAuthRepository

class UserServiceLocator(val authRepository: IAuthRepository)