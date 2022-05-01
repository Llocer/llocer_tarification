# llocer_tarification

This library provides the methods for EV tarification.

## OCPI CDR

In order to create and fill a CDR following OCPI 2.2.1 specification, the following statement must be used:

	OcpiCdr cdr = OcpiTarification.fillCDR( tariffs, events, session ); 

where:
 - tariffs: List<OcpiTariff> with the applicable tariffs to this session
 - events: List<OcppTransactionEventRequest> with the OCPP events received from the ChargingStation
 - session: the OcpiSession with the eMSP/NAS 


