package com.kvcrm.config;

import java.time.Duration;

import com.kvcrm.entity.Account;
import com.kvcrm.entity.Contact;
import com.kvcrm.entity.Organization;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.iqkv.boot.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
class CacheConfig {

  private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

  CacheConfig(CacheProperties cacheProperties) {
    final var ehcacheProperties = cacheProperties.getEhcache();
    jcacheConfiguration =
        Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Object.class,
                    Object.class,
                    ResourcePoolsBuilder.heap(ehcacheProperties.getMaxEntries()))
                .withExpiry(
                    ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(
                        ehcacheProperties.getTimeToLiveSeconds()
                    ))
                )
                .build()
        );
  }

  @Bean
  HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
    return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
  }

  @Bean
  JCacheManagerCustomizer cacheManagerCustomizer() {
    return cm -> {
      createCache(cm, Account.class.getName());
      createCache(cm, Contact.class.getName());
      createCache(cm, Organization.class.getName());
    };
  }

  private void createCache(javax.cache.CacheManager cm, String cacheName) {
    javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
    if (cache != null) {
      cache.clear();
    } else {
      cm.createCache(cacheName, jcacheConfiguration);
    }
  }
}
