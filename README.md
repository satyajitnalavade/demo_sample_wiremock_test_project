# Sample Wiremock test project

If you dont want to use mock rest service server when you're using rest template but you want to simulate a real server, you might want to try using Wiremock.

Wiremock allows you to start a real HTTP server and simulate requests. To use it, you add the Wiremock standalone dependency and then you can add a JUnit Wiremock rule. Once this is done, you define stubs.

this service will actually make a real remote call. So, we're gonna call get vehicle details. If we jump into that code, you can see it's doing a rest template call. The URL will be localhost and the Wiremock port and then Wiremock will respond with the appropriate JSON. The nice thing about Wiremock is it allows you to mock real interactions but also simulate failures very easily


Using Wiremock

Add wiremock-standalone dependency
Use WireMockRule
Define Stubs
	