package fr.sewatech.arquillian.ajax;

import fr.sewatech.arquillian.ajax.backend.*;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@WebServlet(urlPatterns = "*.json")
public class JsonServlet extends HttpServlet {

    private static final String STATISTICS = "statistics";

    private TalkDAO talkDAO = TalkDAO.INSTANCE;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        System.out.println("servletPath=" + servletPath);
        Object value;
        if ("/statistics.json".equals(servletPath)) {
            value = getStatistics(req);
        } else if ("/devoxx/talk.json".equals(servletPath)) {
            value = talkDevoxx(req);
        } else if ("/mixit/talk.json".equals(servletPath)) {
            value = talkMixit(req);
        } else {
            value = null;
        }

        objectMapper.writeValue(resp.getWriter(), value);
    }

    private Collection<Talk> talkDevoxx(HttpServletRequest req) {
        String speakerSearch = req.getParameter("speakerSearch");
        String titleSearch = req.getParameter("titleSearch");

        getStatistics(req).increment(titleSearch + speakerSearch);
        return talkDAO.findAtDevoxx(titleSearch, speakerSearch);
    }

    private Collection<Talk> talkMixit(HttpServletRequest req) {
        String speakerSearch = req.getParameter("speakerSearch");
        String titleSearch = req.getParameter("titleSearch");

        getStatistics(req).increment(titleSearch + speakerSearch);
        return talkDAO.findAtMixit(titleSearch, speakerSearch);
    }

    private Statistics getStatistics(HttpServletRequest req) {
        HttpSession session = req.getSession();
        System.out.println("Getting statistics with sessionId " + session.getId());
        Statistics statistics = (Statistics) session.getAttribute(STATISTICS);
        if (statistics == null) {
            statistics = new Statistics();
            session.setAttribute(STATISTICS, statistics);
        }
        return statistics;
    }

}
