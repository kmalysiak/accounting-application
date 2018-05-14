package pl.coderstrust.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggableDispatcherServlet extends DispatcherServlet {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  protected void doDispatch(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    if (!(response instanceof ContentCachingResponseWrapper)) {
      response = new ContentCachingResponseWrapper(response);
    }
    try {
      super.doDispatch(request, response);
    } finally {
      logger.info("\nResponse Status: " + response.getStatus()
          + " \nResponse Body: " + getResponsePayload(response));
      updateResponse(response);
    }
  }

  private String getResponsePayload(HttpServletResponse response) {
    ContentCachingResponseWrapper wrapper = WebUtils
        .getNativeResponse(response, ContentCachingResponseWrapper.class);
    if (wrapper != null) {

      byte[] buf = wrapper.getContentAsByteArray();
      if (buf.length > 0) {
        int length = Math.min(buf.length, 5120);
        try {
          return new String(buf, 0, length, wrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException ex) {
          throw new AssertionError("UTF-8 is unknown");
          //It cannot happen, unless there is something broken in JVM so i won't log that
        }
      }
    }
    return "[No Content]";
  }

  private void updateResponse(HttpServletResponse response) throws IOException {
    ContentCachingResponseWrapper responseWrapper =
        WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
    responseWrapper.copyBodyToResponse();
  }
}