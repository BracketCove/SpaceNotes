package com.wiseassblog.domain

import com.wiseassblog.domain.repository.INoteRepository
import com.wiseassblog.domain.repository.IAuthSource

class ServiceLocator(val local: INoteRepository,
                     val remote: INoteRepository,
                     val authSource: IAuthSource)