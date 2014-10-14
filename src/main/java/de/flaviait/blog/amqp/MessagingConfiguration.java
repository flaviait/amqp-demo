/**
 * (c) 2014 FLAVIA IT-Management GmbH
 */
package de.flaviait.blog.amqp;

import java.util.HashMap;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class MessagingConfiguration {

  static final String DEMO_EXCHANGE_NAME = "demo.topic";
  static final String DEMO_QUEUE_NAME = "demo.eai";

  @Bean
  public ConnectionFactory conenctionFactory() {
    final CachingConnectionFactory ccf = new CachingConnectionFactory();

    ccf.setUsername("guest");
    ccf.setPassword("guest");

    return ccf;
  }

  @Bean
  public AmqpTemplate AmqpTemplate(ConnectionFactory cf) {
    return new RabbitTemplate(cf);
  }

  @Bean
  public AmqpAdmin amqpAdmin(ConnectionFactory cf) {
    return new RabbitAdmin(cf);
  }

  @Bean
  public Exchange topicExchange(AmqpAdmin admin) {
    final TopicExchange topicExchange = new TopicExchange(DEMO_EXCHANGE_NAME);
    admin.declareExchange(topicExchange);

    return topicExchange;
  }

  @Bean
  public Queue eaiQueue(AmqpAdmin admin) {
    final Queue eaiQueue = new Queue(DEMO_QUEUE_NAME);

    admin.declareQueue(eaiQueue);

    return eaiQueue;
  }

  @Bean
  @DependsOn({ "topicExchange", "eaiQueue" })
  public Binding eaiBinding(AmqpAdmin admin) {
    final Binding binding = new Binding(DEMO_QUEUE_NAME, DestinationType.QUEUE,
                                        DEMO_EXCHANGE_NAME, "eai",
                                        new HashMap<String, Object>());

    admin.declareBinding(binding);

    return binding;
  }
}
