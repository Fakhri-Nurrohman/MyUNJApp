package com.fakhrinurrohman.myunjapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform