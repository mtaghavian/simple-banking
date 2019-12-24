package wallet.config;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wallet.model.Transaction;
import wallet.model.enums.TransactionStatus;
import wallet.repository.TransactionRepository;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Component
public class KafkaAdapter {

    // Logger of this module
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaAdapter.class);

    @Autowired
    private TransactionRepository transactionRepository;

    // Determine if we are in the tests or not
    // We do not want to use kafka for tests
    // Alternatively we use simple blocking queue for tests
    @Autowired
    @Value("${isOnTestMode}")
    public boolean isOnTestMode;

    private KafkaProducer producer;

    private ArrayBlockingQueue<String> verifyingQueue, performingQueue;

    private Random rand = new Random(System.currentTimeMillis());

    public static enum Topic {
        performing, verifying
    }

    public void init() {
        if (isOnTestMode) {
            verifyingQueue = new ArrayBlockingQueue<String>(100);
            performingQueue = new ArrayBlockingQueue<String>(100);
            new Thread(() -> {
                while (true) {
                    try {
                        String take = verifyingQueue.take();
                        Thread.sleep(3000); // putting delay to simulate load
                        consume(Topic.verifying, take);
                    } catch (InterruptedException ex) {
                    }
                }
            }).start();
            new Thread(() -> {
                while (true) {
                    try {
                        String take = performingQueue.take();
                        Thread.sleep(3000); // putting delay to simulate load
                        consume(Topic.performing, take);
                    } catch (InterruptedException ex) {
                    }
                }
            }).start();
        } else {
            Properties p = new Properties();
            p.put("bootstrap.servers", "127.0.0.1:9092");
            p.put("acks", "all");
            p.put("retries", 0);
            p.put("batch.size", 16384);
            p.put("linger.ms", 1);
            p.put("buffer.memory", 33554432);
            p.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            p.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            producer = new KafkaProducer<>(p);

            final Properties cp = new Properties();
            cp.put("bootstrap.servers", "127.0.0.1:9092");
            cp.put("group.id", "test");
            cp.put("enable.auto.commit", "true");
            cp.put("auto.commit.interval.ms", "1000");
            cp.put("session.timeout.ms", "30000");
            cp.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            cp.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            new Thread(() -> {
                KafkaConsumer consumer = new KafkaConsumer<String, String>(cp);
                consumer.subscribe(Arrays.asList("" + Topic.verifying));
                while (true) {
                    try {
                        ConsumerRecords<String, String> records = consumer.poll(1000);
                        Thread.sleep(3000); // putting delay to simulate load
                        for (ConsumerRecord<String, String> record : records) {
                            consume(Topic.verifying, record.value());
                        }
                    } catch (InterruptedException ex) {
                    }
                }
            }).start();
            new Thread(() -> {
                KafkaConsumer consumer = new KafkaConsumer<String, String>(cp);
                consumer.subscribe(Arrays.asList("" + Topic.performing));
                while (true) {
                    try {
                        ConsumerRecords<String, String> records = consumer.poll(1000);
                        Thread.sleep(3000); // putting delay to simulate load
                        for (ConsumerRecord<String, String> record : records) {
                            consume(Topic.performing, record.value());
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }).start();
        }
    }

    public void send(Topic topic, Transaction transaction) {
        LOGGER.info("Kafka publish transaction with id: " + transaction.getId());
        if (isOnTestMode) {
            try {
                if (Topic.performing.equals(topic)) { // Verifying
                    performingQueue.put("" + transaction.getId());
                } else { // Performing
                    verifyingQueue.put("" + transaction.getId());
                }
            } catch (InterruptedException e) {
            }
        } else {
            producer.send(new ProducerRecord<String, String>("" + topic, "" + transaction.getId()));
        }
    }

    // This function consumes kafka entries
    // TODO: We should define a thread pool with several workers to distribute the load
    //       Since the veryfing and performing tasks are mainly IO operations
    //       and parallelization is desirable
    private void consume(Topic topic, String value) {
        if (Topic.performing.equals(topic)) { // Verifying
            UUID transId;
            LOGGER.info("Starting to perform transaction with id " + value);
            try {
                transId = UUID.fromString(value);
            } catch (Exception ex) {
                return;
            }
            Optional<Transaction> transOptional = transactionRepository.findById(transId);
            if (transOptional.isPresent()) {
                Transaction trans = transOptional.get();
                if (trans.getCanceled()) {
                    trans.setStatus(TransactionStatus.Canceled);
                    trans.setMsg("This transaction was canceled");
                } else {
                    if (rand.nextDouble() < 0.25) {
                        trans.setStatus(TransactionStatus.Failed);
                        trans.setMsg("Insufficient balance");
                    } else {
                        trans.setStatus(TransactionStatus.Succeeded);
                        trans.setMsg("Performing of transaction was successful!");
                    }
                    trans.setPassword(null);
                }
                try {
                    transactionRepository.save(trans);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LOGGER.info("Finished performing of transaction with id " + transId);
            }
        } else { // Performing
            UUID transId;
            LOGGER.info("Starting to verify transaction with id " + value);
            try {
                transId = UUID.fromString(value);
            } catch (Exception ex) {
                return;
            }
            Optional<Transaction> transOptional = transactionRepository.findById(transId);
            if (transOptional.isPresent()) {
                Transaction trans = transOptional.get();
                if (trans.getCanceled()) {
                    trans.setStatus(TransactionStatus.Canceled);
                    trans.setMsg("This transaction was canceled");
                } else {
                    if (rand.nextDouble() < 0.25) {
                        trans.setStatus(TransactionStatus.Failed);
                        trans.setMsg("Not valid destination");
                    } else {
                        trans.setStatus(TransactionStatus.Verified);
                        String names[] = {
                            "Ali",
                            "Masoud",
                            "Hasan",
                            "Marc",
                            "Peter",
                            "John",
                            "Sara",
                            "Fariba",
                            "Mary",
                            "Julien"
                        };
                        trans.setTargetName(names[Math.abs(rand.nextInt()) % 10]);
                    }
                }
                try {
                    transactionRepository.save(trans);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LOGGER.info("Finished verification of transaction with id " + transId);
            }
        }
    }
}
