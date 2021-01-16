package com.example.android.ballCorral.gameSimulation

interface MobileEntity {
    fun travel(dt: Float)
    fun reactToCollisions(collisionGrid: CollisionGrid)
}