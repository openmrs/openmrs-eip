/*
 * Copyright (C) Amiyul LLC - All Rights Reserved
 *
 * This source code is protected under international copyright law. All rights
 * reserved and protected by the copyright holder.
 *
 * This file is confidential and only available to authorized individuals with the
 * permission of the copyright holder. If you encounter this file and do not have
 * permission, please contact the copyright holder and delete this file.
 */
package org.openmrs.eip.mysql.watcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.openmrs.eip.EIPException;

public class TestUtils {
	
	/**
	 * Gets the value of the specified field on the specified object.
	 *
	 * @param object the object
	 * @param field the field object
	 * @return the property value
	 * @param <T>
	 */
	public static <T> T getFieldValue(Object object, Field field) {
		boolean isAccessible = field.isAccessible();
		
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			
			return (T) field.get(object);
		}
		catch (Exception e) {
			throw new EIPException("Failed to get the value of the property " + field, e);
		}
		finally {
			field.setAccessible(isAccessible);
		}
	}
	
	/**
	 * Sets the value of the specified field on the specified object.
	 *
	 * @param object the object
	 * @param field the field object
	 * @param value the value to set
	 */
	public static void setFieldValue(Object object, Field field, Object value) {
		boolean isAccessible = field.isAccessible();
		
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			
			field.set(object, value);
		}
		catch (Exception e) {
			throw new EIPException("Failed to set property " + field, e);
		}
		finally {
			field.setAccessible(isAccessible);
		}
	}
	
	/**
	 * Invokes the method represented by the specified name on the specified object with the specified
	 * arguments.
	 *
	 * @param object the object
	 * @param method the method
	 * @param args the arguments to pass to the method
	 */
	public static Object invokeMethod(Object object, Method method, Object... args) {
		boolean isAccessible = method.isAccessible();
		
		try {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			
			return method.invoke(object, args);
		}
		catch (Exception e) {
			throw new EIPException(
			        "Failed to invoke method " + method.getName() + " on object of type " + object.getClass().getName(), e);
		}
		finally {
			method.setAccessible(isAccessible);
		}
	}
	
}
