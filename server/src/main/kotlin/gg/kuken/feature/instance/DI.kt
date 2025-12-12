package gg.kuken.feature.instance

import gg.kuken.feature.instance.entity.InstanceRepositoryImpl
import gg.kuken.feature.instance.repository.InstanceRepository
import org.koin.dsl.module

val InstancesDI =
    module {
        single<InstanceRepository>(createdAtStart = true) {
            InstanceRepositoryImpl(database = get())
        }

        single<InstanceService> {
            DockerInstanceService(
                dockerClient = get(),
                instanceRepository = get(),
                blueprintService = get(),
                identityGeneratorService = get(),
                kukenConfig = get(),
            )
        }
    }
