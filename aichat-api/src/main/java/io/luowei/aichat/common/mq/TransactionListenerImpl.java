package io.luowei.aichat.common.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事物消息 Producer 事务监听器
 */
@Slf4j
@Component
@RocketMQTransactionListener()
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    private AtomicInteger transactionIndex = new AtomicInteger(0);

    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<String, Integer>();

    /**
     * 发送 prepare 消息成功此方法被回调，该方法用于执行本地事务
     *
     * @param msg 回传的消息，利用transactionId即可获取到该消息的唯一Id
     * @param arg 调用send方法时传递的参数，当send时候若有额外的参数可以传递到send方法中，这里能获取到
     * @return 返回事务状态，COMMIT：提交  ROLLBACK：回滚  UNKNOW：回调
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        //事务ID
        String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        int value = transactionIndex.getAndIncrement();
        int status = value % 3;
        assert transId != null;
        localTrans.put(transId, status);
        if (status == 0) {
            log.info("success");
            //成功，提交事务
            return RocketMQLocalTransactionState.COMMIT;
        }
        if (status == 1) {
            log.info("failure");
            //失败，回滚事务
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        log.info("unknown");
        //中间状态
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    /**
     * 检查事务状态
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        RocketMQLocalTransactionState retState = RocketMQLocalTransactionState.COMMIT;
        Integer status = localTrans.get(transId);
        if (null != status) {
            switch (status) {
                case 0:
                    retState = RocketMQLocalTransactionState.COMMIT;
                    break;
                case 1:
                    retState = RocketMQLocalTransactionState.ROLLBACK;
                    break;
                case 2:
                    retState = RocketMQLocalTransactionState.UNKNOWN;
                    break;
                default:
                    break;
            }
        }
        log.info("msgTransactionId:{},TransactionState:{},status:{}", transId, retState, status);
        return retState;
    }
}