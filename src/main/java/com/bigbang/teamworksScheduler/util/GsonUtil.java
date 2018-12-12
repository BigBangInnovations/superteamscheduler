package com.bigbang.teamworksScheduler.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utol class to return new instance of Gson object
 * 
 * @author Poorvi Nigotiya
 *
 */
public class GsonUtil {

	public static Gson getInstance() {
		/*return new GsonBuilder().setDateFormat(Application.properties.get("datetime.format")).serializeNulls()
				.addSerializationExclusionStrategy(new ExclusionStrategy() {
					@Override
					public boolean shouldSkipField(FieldAttributes fieldAttributes) {
						final Expose expose = fieldAttributes.getAnnotation(Expose.class);
						return expose != null && !expose.serialize();
					}

					@Override
					public boolean shouldSkipClass(Class<?> aClass) {
						return aClass.equals(CompanyLeave.class);
					}
				}).addDeserializationExclusionStrategy(new ExclusionStrategy() {
					@Override
					public boolean shouldSkipField(FieldAttributes fieldAttributes) {
						final Expose expose = fieldAttributes.getAnnotation(Expose.class);
						return expose != null && !expose.deserialize();
					}

					@Override
					public boolean shouldSkipClass(Class<?> aClass) {
						return aClass.equals(CompanyLeave.class);
					}
				}).create();*/
		
		return new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").serializeNulls().create();
	}
}
