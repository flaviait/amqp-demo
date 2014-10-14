/**
 * (c) 2014 FLAVIA IT-Management GmbH
 */
package de.flaviait.blog.amqp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = MessagingConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MessagingTest {

  @Autowired
  private AmqpAdmin admin;

  @Autowired
  private AmqpTemplate template;

  @Before
  public void setUp() throws Exception {
    // purge queue contents
    admin.purgeQueue(MessagingConfiguration.DEMO_QUEUE_NAME, true);
  }

  @Test
  public void routingKeyEAI() throws InterruptedException {

    template.send(MessagingConfiguration.DEMO_EXCHANGE_NAME, "eai",
                  MessageBuilder.withBody("Message Channel".getBytes()).build());


    Thread.sleep(100);

    final Object data = template.receiveAndConvert(MessagingConfiguration.DEMO_QUEUE_NAME);
    assertThat(data, not(nullValue()));

    final String message = new String((byte[]) data);
    assertThat(message, equalTo("Message Channel"));
  }

  @Test
  public void routingKeyDP() throws InterruptedException {

    template.send(MessagingConfiguration.DEMO_EXCHANGE_NAME, "dp",
                  MessageBuilder.withBody("State Machine".getBytes()).build());


    Thread.sleep(100);

    final Object data = template.receiveAndConvert(MessagingConfiguration.DEMO_QUEUE_NAME);
    assertThat(data, nullValue());
  }
}
