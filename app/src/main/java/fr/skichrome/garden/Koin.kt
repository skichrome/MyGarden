package fr.skichrome.garden

import fr.skichrome.garden.home.HomeRepository
import fr.skichrome.garden.home.HomeRepositoryImpl
import fr.skichrome.garden.home.HomeViewModel
import fr.skichrome.garden.model.DeviceSource
import fr.skichrome.garden.model.DeviceSourceImpl
import fr.skichrome.garden.model.api.ApiService
import fr.skichrome.garden.model.api.ApiSource
import fr.skichrome.garden.model.api.ApiSourceImpl
import fr.skichrome.garden.model.buildDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun getKoinModules(): List<Module> = listOf(
    databaseModule,
    networkModule,
    mainModule
)

private val databaseModule = module {
    single { buildDatabase(androidApplication()) }
}

private val networkModule = module {
    single { ApiService.getApiService() }
    single<ApiSource> { ApiSourceImpl(get()) }
}

private val mainModule = module {
    single<DeviceSource> { DeviceSourceImpl() }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    viewModel { HomeViewModel(get()) }
}