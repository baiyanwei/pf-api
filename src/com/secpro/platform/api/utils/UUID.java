package com.secpro.platform.api.utils;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class UUID {
	
	private static	EthernetAddress nic = EthernetAddress.fromInterface();
	private static	TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(nic);	
	
	
	public static String generateID(){
		return uuidGenerator.generate().toString();
	}
}
