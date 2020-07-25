package com.jump.standard.kafka.consumer.annotation;

import com.jump.standard.kafka.consumer.KafkaConsumerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * 〈消费者注解类bean加载后置处理器〉
 *
 * @author LiLin
 * @date 2020/7/15 0015
 */
@Component
public class KafkaConsumerAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerAnnotationBeanPostProcessor.class);
    private BeanFactory beanFactory;
    private BeanExpressionResolver beanExpressionResolver;
    private BeanExpressionContext beanExpressionContext;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        //初始化beanFactory及bean表达式解析器及上下文
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanExpressionResolver = ((ConfigurableListableBeanFactory)beanFactory).getBeanExpressionResolver();
            this.beanExpressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory)beanFactory, null);
        }
    }

    /**
     * spring容器 bean加载后置处理器
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //bean 是否实现 kafka处理器接口
        if (bean instanceof KafkaConsumerProcessor) {
            //获取bean是否有注解KafkaConsumer
            KafkaConsumer ann = AnnotationUtils.findAnnotation(bean.getClass(), KafkaConsumer.class);
            if (ann != null) {
                //获取注解KafkaConsumer中配置的topics
                String topics = this.resolveTopic(ann);
                LOGGER.info("解析消息处理器的topics={}", topics);
                //注册kafka消息处理器到消息监听器中
                com.jump.standard.kafka.consumer.KafkaConsumer kafkaConsumer = beanFactory.getBean(com.jump.standard.kafka.consumer.KafkaConsumer.class);
                kafkaConsumer.registerKafkaConsumerProcessor(topics, (KafkaConsumerProcessor)bean);
            }
        }
        return bean;
    }

    /**
     * 解析topic表达式
     *
     * @param ann
     * @return
     */
    private String resolveTopic(KafkaConsumer ann) {
        String topicExpression = ann.topic();
        Object value = this.resolveExpression(topicExpression);
        return (String)value;
    }

    /**
     * 解析配置表达式并进行SpEl表达式解析
     *
     * @param expression
     * @return
     */
    private Object resolveExpression(String expression) {
        return this.beanExpressionResolver.evaluate(resolve(expression), beanExpressionContext);
    }

    /**
     * 解析配置表达式
     *
     * @param expression
     * @return
     */
    private String resolve(String expression) {
        return this.beanFactory != null && this.beanFactory instanceof ConfigurableBeanFactory ?
            ((ConfigurableBeanFactory)this.beanFactory).resolveEmbeddedValue(expression) : expression;
    }

}