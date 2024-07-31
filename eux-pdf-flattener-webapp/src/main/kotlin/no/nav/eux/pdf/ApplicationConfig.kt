package no.nav.eux.pdf

import no.nav.eux.logging.RequestIdMdcFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
class ApplicationConfig {

    @Bean
    fun requestIdMdcFilter() = RequestIdMdcFilter()

}
