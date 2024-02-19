package com.banking.simplebankapp.config;

import com.banking.simplebankapp.dto.AccountDetails;
import com.banking.simplebankapp.dto.requests.CreateAccountRequest;
import com.banking.simplebankapp.dto.requests.DepositRequest;
import com.banking.simplebankapp.dto.requests.TransactionRequest;
import com.banking.simplebankapp.dto.requests.WithdrawRequest;
import com.banking.simplebankapp.entities.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

// This is the configuration which defines the template for request-reply mechanism against various banking actions
// such create account, make transfer, deposit, withdraw etc.
@Configuration
public class KafkaConfig {
    @Value("${kafka.group.id}")
    private String groupId;

    @Value("${kafka.reply.topic.ACCOUNT_CREATE}")
    private String replyTopic_ACCOUNT_CREATE;

    @Value("${kafka.reply.topic.ACCOUNT_FETCH}")
    private String replyTopic_ACCOUNT_FETCH;

    @Value("${kafka.reply.topic.FUND_TRANSFER}")
    private String replyTopic_FUND_TRANSFER;

    @Value("${kafka.reply.topic.FUND_DEPOSIT}")
    private String replyTopic_FUND_DEPOSIT;

    @Value("${kafka.reply.topic.FUND_WITHDRAW}")
    private String replyTopic_FUND_WITHDRAW;

    @Bean
    public ReplyingKafkaTemplate<String, CreateAccountRequest, Account> replyingCreateAccountKafkaTemplate(ProducerFactory<String, CreateAccountRequest> pf,
                                                                                                           ConcurrentKafkaListenerContainerFactory<String, Account> factory) {
        ConcurrentMessageListenerContainer<String, Account> replyContainer = factory.createContainer(replyTopic_ACCOUNT_CREATE);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public ReplyingKafkaTemplate<String, AccountDetails, Account> replyingFetchAccountKafkaTemplate(ProducerFactory<String, AccountDetails> pf,
                                                                                        ConcurrentKafkaListenerContainerFactory<String, Account> factory) {
        ConcurrentMessageListenerContainer<String, Account> replyContainer = factory.createContainer(replyTopic_ACCOUNT_FETCH);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public ReplyingKafkaTemplate<String, TransactionRequest, Boolean> replyingFundTransferKafkaTemplate(ProducerFactory<String, TransactionRequest> pf,
                                                                                                        ConcurrentKafkaListenerContainerFactory<String, Boolean> factory) {
        ConcurrentMessageListenerContainer<String, Boolean> replyContainer = factory.createContainer(replyTopic_FUND_TRANSFER);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public ReplyingKafkaTemplate<String, DepositRequest, Boolean> replyingFundDepositKafkaTemplate(ProducerFactory<String, DepositRequest> pf,
                                                                                                   ConcurrentKafkaListenerContainerFactory<String, Boolean> factory) {
        ConcurrentMessageListenerContainer<String, Boolean> replyContainer = factory.createContainer(replyTopic_FUND_DEPOSIT);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public ReplyingKafkaTemplate<String, WithdrawRequest, Boolean> replyingFundWithdrawKafkaTemplate(ProducerFactory<String, WithdrawRequest> pf,
                                                                                                    ConcurrentKafkaListenerContainerFactory<String, Boolean> factory) {
        ConcurrentMessageListenerContainer<String, Boolean> replyContainer = factory.createContainer(replyTopic_FUND_WITHDRAW);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public KafkaTemplate<String, Account> replyAccountTemplate(ProducerFactory<String, Account> pf,
                                                       ConcurrentKafkaListenerContainerFactory<String, Account> factory) {
        KafkaTemplate<String, Account> kafkaTemplate = new KafkaTemplate<>(pf);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, Boolean> replyTransactionTemplate(ProducerFactory<String, Boolean> pf,
                                                                     ConcurrentKafkaListenerContainerFactory<String, Boolean> factory) {
        KafkaTemplate<String, Boolean> kafkaTemplate = new KafkaTemplate<>(pf);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;
    }
}
