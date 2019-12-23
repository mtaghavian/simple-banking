package wallet.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wallet.model.BaseResponse;
import wallet.model.Card;
import wallet.model.User;
import wallet.model.enums.ResponseStatus;
import wallet.repository.CardRepository;
import wallet.repository.SessionRepository;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Component
public class CardServiceImpl implements CardService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CardRepository cardRepository;

    private User getCurrentUser(HttpSession httpSession) {
        return sessionRepository.findById(httpSession.getId()).get().getUser();
    }

    @Override
    public BaseResponse<List<Card>> list(HttpServletRequest request) {
        User user = getCurrentUser(request.getSession());
        List<Card> cards = cardRepository.findAllByUserId(user.getId());
        Collections.sort(cards);
        return new BaseResponse<>(cards, ResponseStatus.Ok, null);
    }

    @Override
    public BaseResponse<Card> create(HttpServletRequest request, Card card) {
        User user = getCurrentUser(request.getSession());
        card.setUser(user);
        // Validate
        String problem = Card.validate(card);
        if (problem == null) {
            cardRepository.save(card);
            return new BaseResponse<>(card, ResponseStatus.Ok, null);
        } else {
            return new BaseResponse<>(card, ResponseStatus.NotOk, problem);
        }
    }

    @Override
    public BaseResponse<Card> read(HttpServletRequest request, Card card) {
        Optional<Card> findById = cardRepository.findById(card.getId());
        if (findById.isPresent()) {
            return new BaseResponse<>(findById.get(), ResponseStatus.Ok, null);
        } else {
            return new BaseResponse<>(null, ResponseStatus.NotOk, "Card was not found");
        }
    }

    @Override
    public BaseResponse<Card> update(HttpServletRequest request, Card card) {
        User user = getCurrentUser(request.getSession());
        card.setUser(user);
        /* Fetch from database */
        Optional<Card> findById = cardRepository.findById(card.getId());
        if (findById.isPresent()) {
            Card dbCard = findById.get();
            // Copy contents of card
            Card.copy(card, dbCard);
            // Validate
            String problem = Card.validate(dbCard);
            if (problem == null) {
                cardRepository.save(dbCard);
                return new BaseResponse<>(dbCard, ResponseStatus.Ok, null);
            } else {
                return new BaseResponse<>(dbCard, ResponseStatus.NotOk, problem);
            }
        } else {
            return new BaseResponse<>(null, ResponseStatus.NotOk, "Card was not found");
        }
    }

    @Override
    public BaseResponse<Card> delete(HttpServletRequest request, Card card) {
        cardRepository.deleteById(card.getId());
        return new BaseResponse<>(card, ResponseStatus.Ok, null);
    }
}
