package fr.skichrome.garden

import fr.skichrome.garden.androidmanager.NetManager
import fr.skichrome.garden.androidmanager.NetManagerImpl
import fr.skichrome.garden.device.DeviceRepository
import fr.skichrome.garden.device.DeviceRepositoryImpl
import fr.skichrome.garden.device.DeviceViewModel
import fr.skichrome.garden.home.HomeRepository
import fr.skichrome.garden.home.HomeRepositoryImpl
import fr.skichrome.garden.home.HomeViewModel
import fr.skichrome.garden.model.api.ApiService
import fr.skichrome.garden.model.api.ApiSource
import fr.skichrome.garden.model.api.ApiSourceImpl
import fr.skichrome.garden.model.local.*
import fr.skichrome.garden.service.DataSyncRepository
import fr.skichrome.garden.service.DataSyncRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun getKoinModules(): List<Module> = listOf(
    androidManagerModule,
    localModule,
    networkModule,
    dataSyncModule,
    mainModule,
    deviceModule
)

val androidManagerModule = module {
    single<NetManager> { NetManagerImpl(androidApplication()) }
}

private val localModule = module {
    single<DeviceSource> { DeviceSourceImpl(get()) }
    single<DeviceConfSource> { DeviceConfSourceImpl(get()) }
    single<DeviceDataSource> { DeviceDataSourceImpl(get()) }
    single { buildDatabase(androidApplication()) }
}

private val networkModule = module {
    single { ApiService.getApiService() }
    single<ApiSource> { ApiSourceImpl(get()) }
}

private val dataSyncModule = module {
    single<DataSyncRepository> { DataSyncRepositoryImpl(get(), get(), get(), get(), get()) }
}

private val mainModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get(), get()) }
    viewModel { HomeViewModel(get()) }
}

private val deviceModule = module {
    single<DeviceRepository> { DeviceRepositoryImpl(get(), get()) }
    viewModel { DeviceViewModel(get()) }
}