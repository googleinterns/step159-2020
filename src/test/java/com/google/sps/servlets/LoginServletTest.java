package com.google.sps.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.LoginObject;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** */
public final class LoginServletTest {

  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private LoginServlet loginObject;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    loginObject = new LoginServlet();
  }

  @AfterEach
  public void tearDown() {
    helper.tearDown();
  }

  @Mock HttpServletRequest request;

  @Test
  public void GetResponse_TrueToken() throws IOException {
    String token =
        "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijc0NGY2MGU5ZmI1MTVhMmEwMWMxMWViZWIyMjg3MTI4NjA1NDA3MTEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiMTI1MjM3NDg4NTMtNmgzZm5oMWtwcGh0OXM4OTVpc3QyYjUyMjJpZmgydTcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxMjUyMzc0ODg1My02aDNmbmgxa3BwaHQ5czg5NWlzdDJiNTIyMmlmaDJ1Ny5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwNDEyMjY0NDUyMjA0NTM4MjIyNCIsImhkIjoic3RhbmZvcmQuZWR1IiwiZW1haWwiOiJuc3BAc3RhbmZvcmQuZWR1IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJtdUdzSkp5SVdfYUdMNV9XRUNpdXRnIiwibmFtZSI6Ik5pbmEgUHJhYmh1IiwicGljdHVyZSI6Imh0dHBzOi8vbGg2Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tLTFOQnJTa2tHZ28vQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQU1adXVja1dkcTV0dUJ2N1gxZTBSTGc5YnZkVHRmZk1ndy9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiTmluYSIsImZhbWlseV9uYW1lIjoiUHJhYmh1IiwibG9jYWxlIjoiZW4iLCJpYXQiOjE1OTcxNjM4NDQsImV4cCI6MTU5NzE2NzQ0NCwianRpIjoiMzIwN2E0YTlhYTExNjVlODQ3OGI3MWRmNzNkMWYxNTljYTVmODcwOSJ9.VYRNTHnX7O-aYGXY60H1w3-0zNrWuFoMElcWxtvUhZMW9y5s5oQakQbvHkDV0KlGsvT49TZ4a10sc2fg3NjD-G2JJXe4IrQeJC2v5rWAreqQw6owd_9CVDBr-eEohbEIb904pYWVYRlybDcqjqN8J703PVkRAlkKZNa0wt10lWMnwUtRXRs3MpmRm9YGMIAS6Kxi23BoDnni2sAY69-2DwLMYPM5_yOjnphd4CPicafPVSPg8SvXFz_e8IhEalGu80t0yxpf-751oUMOHcm44Ck2wnaZu60q5kWA_V-HbtTGqhY77EbvhL3sd9k9Vib0T52o4qmqBO-gd7rdjlMW1A";
    request = createRequest(request, /* token */ token);
    LoginObject login = loginObject.postHelper(request);

    assertEquals(login.getId(), "104122644522045382224");
    assertEquals(login.getSuccess(), true);
  }

  @Test
  public void GetResponse_FalseToken() throws IOException {
    String token = "random";
    request = createRequest(request, /* token */ token);

    LoginObject login = loginObject.postHelper(request);
    assertEquals(login.getId(), "NOT FOUND");
    assertEquals(login.getSuccess(), false);
  }

  private HttpServletRequest createRequest(HttpServletRequest request, String token) {
    request = Mockito.mock(HttpServletRequest.class);
    when(request.getParameter("token")).thenReturn(token);
    return request;
  }
}
