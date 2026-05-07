package com.aki.akiwarmup.core.dsl

/**
 * Exception dùng để thoát nhanh khỏi một Action Group.
 */
class EndActionException : RuntimeException("Action terminated by user")
