package com.etonealbert.examenmanejo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform