# llocer_tarification

This library provides the methods for EV tarification.

## OCPI CDR

In order to create and fill a CDR following OCPI 2.2.1 specification, the following statement must be used:

	OcpiCdr cdr = OcpiTarification.makeCDR( tariffs, events, session ); 

where:
 - List\<OcpiTariff\> tariffs: applicable tariffs to this session
 - List\<OcppTransactionEventRequest\> events: OCPP events received from the ChargingStation
 - OcpiSession session: session with the eMSP/NAS 


