// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.responders.run;

import util.RegexTestCase;
import fitnesse.FitNesseContext;
import fitnesse.components.SocketDealer;
import fitnesse.http.MockRequest;
import fitnesse.http.MockResponseSender;
import fitnesse.http.Response;
import fitnesse.testsystems.fit.SimpleSocketSeeker;
import fitnesse.testutil.FitNesseUtil;

public class SocketCatchingResponderTest extends RegexTestCase {
  private SocketDealer dealer;
  private SimpleSocketSeeker seeker;
  private MockResponseSender sender;
  private SocketCatchingResponder responder;
  private FitNesseContext context;
  private MockRequest request;

  public void setUp() throws Exception {
    seeker = new SimpleSocketSeeker();
    sender = new MockResponseSender();
    responder = new SocketCatchingResponder();
    context = FitNesseUtil.makeTestContext();
    dealer = context.socketDealer;
    request = new MockRequest();
  }

  public void tearDown() throws Exception {
  }

  public void testSuccess() throws Exception {
    int ticket = dealer.seekingSocket(seeker);
    request.addInput("ticket", ticket + "");
    Response response = responder.makeResponse(context, request);
    response.sendTo(sender);

    assertEquals("", sender.sentData());
  }

  public void testMissingSeeker() throws Exception {
    request.addInput("ticket", "123");
    Response response = responder.makeResponse(context, request);
    response.sendTo(sender);

    assertHasRegexp("There are no clients waiting for a socket with ticketNumber 123", sender.sentData());
    assertTrue(sender.isClosed());
    assertEquals(404, response.getStatus());
  }


}
