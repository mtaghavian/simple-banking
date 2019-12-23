package wallet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import wallet.model.Session;
import wallet.repository.SessionRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wallet.model.BaseResponse;
import wallet.model.enums.ResponseStatus;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

@Component
public class HttpInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerInterceptor.class);

    @Autowired
    private SessionRepository sessionRepository;

    public HttpInterceptor() {
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 3600000)
    public void clearExpiredSessions() {
        List<Session> expired = sessionRepository.findExpired(System.currentTimeMillis() - 3600000);
        sessionRepository.deleteAll(expired);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession httpSession = request.getSession();
        String uri = request.getRequestURI();
        LOGGER.info("Request: " + uri);

        Session session = sessionRepository.findById(httpSession.getId()).orElse(null);
        if (session == null) {
            session = new Session(httpSession.getId(), null, System.currentTimeMillis());
        }
        session.setLastModified(System.currentTimeMillis());
        sessionRepository.save(session);

        if (!session.isValidated()) {
            if (!("/user/signin".equals(uri) || "/user/signup".equals(uri) || "/user/signout".equals(uri))) {
                response.setContentType("application/json");
                BaseResponse r = new BaseResponse(null, ResponseStatus.NotOk, "Unauthorized access");
                ObjectMapper mapper = new ObjectMapper();
                response.getOutputStream().write(mapper.writeValueAsString(r).getBytes("UTF-8"));
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
    }
}
