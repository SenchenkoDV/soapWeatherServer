# soapWeatherServer

For tests:

Integration:
curl -X POST --header "content-type: application/json" -d '{"name":"Minsk"}' http://localhost:8080/service
 
SOAP:
curl --header "content-type: text/xml" -d @request.xml http://localhost:8080/ws
