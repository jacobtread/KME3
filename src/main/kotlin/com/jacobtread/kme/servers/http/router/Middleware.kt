package com.jacobtread.kme.servers.http.router

import com.jacobtread.kme.servers.http.WrappedRequest

abstract class Middleware : RequestMatcher {
    override fun matches(start: Int, request: WrappedRequest): Boolean {
        return true
    }
}