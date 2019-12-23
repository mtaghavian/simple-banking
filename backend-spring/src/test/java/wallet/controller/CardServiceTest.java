package wallet.controller;

import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wallet.model.BaseResponse;
import wallet.model.Card;
import wallet.model.Session;
import wallet.model.User;
import wallet.repository.CardRepository;
import wallet.repository.SessionRepository;
import wallet.repository.UserRepository;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

// This is sample test which demonstrates testing one of the services used in our application
// Here we test CardService
// This test brings up the whole services and beans to make a real test
// You can perform tests by "mvn test"
@RunWith(SpringRunner.class)
@SpringBootTest
public class CardServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CardService cardService;

    private static MockHttpServletRequest httpServletRequest;

    @Before
    public void init() {
        // Creating an authorised session
        // and generating a MockHttpServletRequest
        // to be used by the service
        User user = userRepository.findAll().get(0); // Getting admin user
        UUID id = UUID.randomUUID(); // Random session id
        Session session = new Session("" + id, user, System.currentTimeMillis());
        sessionRepository.save(session);
        httpServletRequest = new MockHttpServletRequest("" + id);
    }

    @Test
    public void test() {
        // Testing create and list services
        Card card = new Card();
        card.setName("card");
        card.setNumber("2345");
        cardService.create(httpServletRequest, card);
        BaseResponse<List<Card>> listResponse = cardService.list(httpServletRequest);
        Assert.assertTrue(listResponse.getContent().size() == 1);
        Assert.assertTrue(card.getName().equals(listResponse.getContent().get(0).getName()));

        // Testing read service
        card = listResponse.getContent().get(0);
        BaseResponse<Card> readResponse = cardService.read(httpServletRequest, card);
        Assert.assertTrue(card.getName().equals(readResponse.getContent().getName()));

        // Testing update service
        card = listResponse.getContent().get(0);
        String newName = "NewName";
        card.setName(newName);
        cardService.update(httpServletRequest, card);
        listResponse = cardService.list(httpServletRequest);
        Assert.assertTrue(card.getName().equals(listResponse.getContent().get(0).getName()));

        // Testing delete service
        card = listResponse.getContent().get(0);
        cardService.delete(httpServletRequest, card);
        listResponse = cardService.list(httpServletRequest);
        Assert.assertTrue(listResponse.getContent().isEmpty());
    }
}
