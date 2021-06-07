package com.bybutter.glesdemo.program

interface Program {
    fun doOnInit()
    fun doOnGameLoop()
    fun doOnResize(width: Int, height: Int)
}