package gg.kuken.feature.remoteConfig

import org.koin.dsl.module

val RemoteConfigDI =
    module {
        single<RemoteConfigRepository>(createdAtStart = true) {
            RemoteConfigRepository(database = get())
        }

        factory<RemoteConfigService> {
            RemoteConfigService(
                remoteConfigRepository = get(),
            )
        }
    }
