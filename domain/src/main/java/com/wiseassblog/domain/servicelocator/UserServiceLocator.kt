package com.wiseassblog.domain.servicelocator

import com.wiseassblog.domain.repository.IAuthRepository

class UserServiceLocator(val authRepository: IAuthRepository)