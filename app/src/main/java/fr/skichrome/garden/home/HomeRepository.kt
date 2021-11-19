package fr.skichrome.garden.home

import fr.skichrome.garden.model.DeviceSource

interface HomeRepository
{
}

class HomeRepositoryImpl(private val deviceSource: DeviceSource) : HomeRepository
{
}