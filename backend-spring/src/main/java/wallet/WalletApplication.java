package wallet;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import wallet.config.HttpInterceptor;
import wallet.model.User;
import wallet.repository.UserRepository;
import wallet.util.ByteUtils;

import javax.annotation.PostConstruct;
import wallet.config.KafkaAdapter;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
public class WalletApplication implements ApplicationContextAware {

    public static final String adminUsername = "admin";

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private KafkaAdapter kafkaAdapter;

    private static ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }

    @PostConstruct
    public void starter() {
        if (userRepository.findByUsername(adminUsername) == null) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(ByteUtils.hash(adminUsername));
            admin.setFirstname("Masoud");
            admin.setLastname("Taghavian");
            userRepository.save(admin);
        }
        kafkaAdapter.init();
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        context = ac;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    @Configuration
    public class InterceptorConfig extends WebMvcConfigurerAdapter {

        @Autowired
        HttpInterceptor httpInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(httpInterceptor);
        }
    }
}
