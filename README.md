# soapWeatherServer

For tests:

Integration:
curl -X POST --header "content-type: application/json" -d '{"name":"Vitebsk"}' http://localhost:8080/integration/temperature
 
SOAP:
curl --header "content-type: text/xml" -d @request.xml http://localhost:8080/temperature
