package com.jump.standard.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.adapter.FilteringMessageListenerAdapter;

/**
 * 〈kafka消费者〉
 *
 * @author LiLin
 * @date 2020/7/17 0017
 */
public abstract class KafkaConsumer implements InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private KafkaConsumerListener kafkaConsumerListener = new KafkaConsumerListener();
    @Autowired
    private ConsumerFactory<String, byte[]> consumerFactory;
    /**
     * 主题分隔符
     */
    private final String TOPIC_SPLIT = "(,|;|:|#)";
    /**
     * 消息监听容器并发数
     */
    private int concurrency = 1;

    @Override
    public void afterPropertiesSet() throws Exception {
        //创建kafka消息容器bean
        ContainerProperties properties = new ContainerProperties(this.topic().split(TOPIC_SPLIT));
        properties.setMessageListener(getListener());
        properties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        BeanDefinitionBuilder definitionBuilder =
            BeanDefinitionBuilder.genericBeanDefinition(ConcurrentMessageListenerContainer.class);
        definitionBuilder.addConstructorArgValue(consumerFactory);
        definitionBuilder.addConstructorArgValue(properties);
        definitionBuilder.addPropertyValue("concurrency", this.concurrency);
        BeanDefinition beanDefinition = definitionBuilder.getRawBeanDefinition();
        ConfigurableApplicationContext context = (ConfigurableApplicationContext)this.applicationContext;
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry)context.getBeanFactory();
        beanFactory.registerBeanDefinition(ConcurrentMessageListenerContainer.class.getName(), beanDefinition);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void registerKafkaConsumerProcessor(String topic, KafkaConsumerProcessor kafkaConsumerProcessor) {
        kafkaConsumerListener.addKafkaConsumerProcessor(topic.split(TOPIC_SPLIT), kafkaConsumerProcessor);
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public MessageListener getListener() {
        return new FilteringMessageListenerAdapter(this.kafkaConsumerListener, consumerRecord -> {
            return this.doFilter(consumerRecord);
        }, true);
    }

    protected boolean doFilter(ConsumerRecord consumerRecord) {
        KafkaConsumerProcessor processor = kafkaConsumerListener.getKafkaConsumerProcessor(consumerRecord.topic());
        return processor != null ? processor.filter(consumerRecord) : false;
    }

    protected abstract String topic();
}