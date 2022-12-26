package com.study.mykoin.core.fiis.http

import com.study.mykoin.core.fiis.http.controller.FiiController
import com.study.mykoin.core.fiis.http.controller.ProfileController
import kotlinx.coroutines.FlowPreview
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration {

    @Bean
    fun entryRoutes(entryController: FiiController, profileController: ProfileController) = coRouter {
        "/api/fiis".nest {
            accept(MediaType.APPLICATION_JSON).nest {

                GET("/entry", entryController::getEntry)

                contentType(MediaType.APPLICATION_JSON).nest {
                    POST("/entry", entryController::postEntry)
                }

                contentType(MediaType.APPLICATION_JSON).nest {
                    POST("/profile", profileController::createProfile)
                }
            }
        }
    }
}
