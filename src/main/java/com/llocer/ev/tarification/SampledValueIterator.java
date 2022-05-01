package com.llocer.ev.tarification;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.llocer.common.Tuple2;
import com.llocer.ev.ocpp.msgs20.MeasurandEnum;
import com.llocer.ev.ocpp.msgs20.OcppMeterValue;
import com.llocer.ev.ocpp.msgs20.OcppSampledValue;
import com.llocer.ev.ocpp.msgs20.OcppTransactionEventRequest;

class SampledValueIterator implements Iterator<Tuple2<Instant,Double>> {
	private final MeasurandEnum measurand;

	final Iterator<OcppTransactionEventRequest> eventsIt;

	Iterator<OcppMeterValue> metersIt = null;
	Instant meterTimestamp = null;

	Iterator<OcppSampledValue> sampledValuesIt = null;
	Double sample = null;

	SampledValueIterator( List<OcppTransactionEventRequest> events, MeasurandEnum measurand ) {
		this.measurand = measurand;
		eventsIt = events.iterator();
	}
	
	static double getValue( OcppSampledValue sampleValue ) {
		double res = sampleValue.getValue(); 
		
		if( sampleValue.getUnitOfMeasure() != null ) {
			if( sampleValue.getUnitOfMeasure().getMultiplier() != null ) {
				res *= Math.pow( 10, sampleValue.getUnitOfMeasure().getMultiplier() );
			}

			if( sampleValue.getUnitOfMeasure().getUnit() == null ) {
				sampleValue.getUnitOfMeasure().setUnit( "Wh" ); // default
			}
			
			switch( sampleValue.getUnitOfMeasure().getUnit() ) {
			case "kWh":
				res *= 1000;
				break;
			default:
				break;
			}
		}

		return res;
	}


	@Override
	public boolean hasNext() {
		do {
			if( sample != null ) return true;

			if( sampledValuesIt != null && sampledValuesIt.hasNext() ) {
				OcppSampledValue sampledValue = sampledValuesIt.next();
				if( sampledValue.getMeasurand() == this.measurand) {
					sample = getValue( sampledValue );
				}

			} else if( metersIt != null && metersIt.hasNext() ) {
				OcppMeterValue meter = metersIt.next();
				meterTimestamp = meter.getTimestamp();
				List<OcppSampledValue> sampledValue = meter.getSampledValue();
				sampledValuesIt = ( sampledValue == null ? null : sampledValue.iterator() );

			} else if( eventsIt != null && eventsIt.hasNext() ) {
				OcppTransactionEventRequest event = eventsIt.next();
				List<OcppMeterValue> meters = event.getMeterValue();
				metersIt = ( meters == null ? null : meters.iterator() );

			} else {
				return false;

			}

		} while( true );
	}

	@Override
	public Tuple2<Instant,Double> next() {
		if( this.hasNext() ) {
			Tuple2<Instant,Double> res = new Tuple2<Instant,Double>( meterTimestamp, sample );
			sample = null;
			return res;
		}

		throw new NoSuchElementException();
	}

}
