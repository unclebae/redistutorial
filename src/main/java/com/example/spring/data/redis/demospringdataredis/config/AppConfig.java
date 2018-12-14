package com.example.spring.data.redis.demospringdataredis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisSocketConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;

import io.lettuce.core.ReadFrom;

@Configuration
class AppConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        /**
         * LettuceConnectionFactory 를 이용하여 LettuceConnection 인스턴스를 생성할 수 있다. 
         * narive connection 을 스레드 세이프하게 생성한다. 
         * 이는 non-blocking, non-transactional operation 을 위해 스레드 세이프하다.
         * 또한 LettucePool 을 이용하여 풀링 블록킹과 트랜잭션 커넥션을 모든 커넥션에 이용할 수 있으며, 
         * shareNativeConnection 이 false 로 설정되면 된다. 
         * 
         * Lettuce 는 Netty의 native transports 와 통합된다. 
         * 
         * 유닉스 도메인으로 커넥션 팩토리를 생성할 수 있다. 
         */
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactoryForUnixSocket() {
        return new LettuceConnectionFactory(
            new RedisSocketConfiguration("/var/run/redis.sock")
        );
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactoryUsingJedis() {
        return new JedisConnectionFactory();
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactoryUsingJedisForReal() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new JedisConnectionFactory(config);
    }

    /**
     * Write to Master and Read from Replica
     * @return
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactoryForSeperatingWriteRead() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .readFrom(ReadFrom.SLAVE)
            .build();

        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("localhost", 6379);
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    /**
     * RedisSentinel 을 이용할대 Jedis 이용하는 방법 
     * @return
     */
    @Bean
    public RedisConnectionFactory JedisConnectionFactoryForSentinel() {
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration()
            .master("mymaster")
            .sentinel("127.0.0.1", 26379)
            .sentinel("127.0.0.1", 26380);

        return new JedisConnectionFactory(sentinelConfiguration);
    }

    /**
     * RedisSentinel 을 이용할때 Lettuce 이용하는 방법 
     * @return
     */
    @Bean
    public RedisConnectionFactory lettuceConnectionFactoryForSentinel() {
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration()
            .master("mymaster")
            .sentinel("127.0.0.1", 26379)
            .sentinel("127.0.0.1", 26380)
            ;
        return new LettuceConnectionFactory(sentinelConfiguration);
    }

    /**
     * 참고, RedisSentinelConfiguration 은 또한 PropertySource 를 이용해서 설정할 수 있다. 
     * 이는 다음 프로퍼티를 설정하면 된다. 
     * 
     * spring.redis.sentinel.master 은 마스터 노드를 위한 설정 
     * spring.redis.sentinel.nodes 은 콤마로 연결된 host:port 쌍으로 지정하면된다. 
     * 
     * Sentinel 을 지정할때 다이렉트로 연결할 필요가 있을때가 있다. 
     * 이때에는 RedisConnectionFactory.getSentinelConnection() 혹은 RedisConnection.getSentinelCommands() 을
     * 이용하여 첫번째 액티브 센티넬 설정에 접근해야할 수도 있다. 
     */

     


}